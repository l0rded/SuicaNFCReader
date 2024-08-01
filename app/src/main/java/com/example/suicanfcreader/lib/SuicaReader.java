package com.example.suicanfcreader.lib;

import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SuicaReader {

    public int termId;
    public int procId;
    public int year;
    public int month;
    public int day;
    public String kind;
    public int remain;
    public int seqNo;
    public int reasion;
    public int inStation;
    public int inLine;
    public int outStation;
    public int outLine;

    public String device;
    public String action;

    public static final SparseArray<String> DEVICE_LIST = new SparseArray<>();
    public static final SparseArray<String> ACTION_LIST = new SparseArray<>();

    public SuicaReader(){}

    public static SuicaReader parse(byte[] res, int off) {
        SuicaReader self = new SuicaReader();
        self.init(res, off);
        return self;
    }

    private void init(byte[] res, int off) {
        this.termId = res[off];
        this.procId = res[off+1];
        //2-3: ??
        int mixInt = toInt(res, off, 4,5);
        this.year  = (mixInt >> 9) & 0x07f;
        this.month = (mixInt >> 5) & 0x00f;
        this.day   = mixInt & 0x01f;

        if (isShopping(this.procId)) {
            this.kind = "物販";
        } else if (isBus(this.procId)) {
            this.kind = "バス";
        } else {
            this.kind = res[off+6] < 0x80 ? "JR" : "公営/私鉄" ;
        }

        this.inLine = toInt(res, off, 6);         // 6: 出線区
        this.inStation = toInt(res, off, 7);      // 7: 入駅
        this.outLine = toInt(res, off, 8);        // 8: 出線区
        this.outStation = toInt(res, off, 9);     // 9: 出駅
        this.remain  = toInt(res, off, 11,10);    // 10-11: 残高 (little endian)
        this.seqNo   = toInt(res, off, 12,13,14); // 12-14: 連番
        this.reasion = res[off+15];               // 15: リージョン

        this.device = DEVICE_LIST.get(this.termId);
        this.action = ACTION_LIST.get(this.procId);
    }

    private int toInt(byte[] res, int off, int... idx) {
        int num = 0;
        for (int j : idx) {
            num = num << 8;
            num += ((int) res[off + j]) & 0x0ff;
        }
        return num;
    }
    private boolean isShopping(int procId) {
        return procId == 70 || procId == 73 || procId == 74 || procId == 75 || procId == 198 || procId == 203;
    }
    private boolean isBus(int procId) {
        return procId == 13|| procId == 15|| procId ==  31|| procId == 35;
    }

    public static byte[] readWithoutEncryption(byte[] idm, int size)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

        bout.write(0);
        bout.write(0x06);
        bout.write(idm);
        bout.write(1);
        bout.write(0x0f);
        bout.write(0x09);
        bout.write(size);
        for (int i = 0; i < size; i++) {
            bout.write(0x80);
            bout.write(i);
        }

        byte[] msg = bout.toByteArray();
        msg[0] = (byte) msg.length;
        return msg;
    }

    static {
        DEVICE_LIST.put(3 , "精算機");
        DEVICE_LIST.put(4 , "携帯型端末");
        DEVICE_LIST.put(5 , "車載端末");
        DEVICE_LIST.put(7 , "券売機");
        DEVICE_LIST.put(8 , "券売機");
        DEVICE_LIST.put(9 , "入金機");
        DEVICE_LIST.put(18 , "券売機");
        DEVICE_LIST.put(20 , "券売機等");
        DEVICE_LIST.put(21 , "券売機等");
        DEVICE_LIST.put(22 , "改札機");
        DEVICE_LIST.put(23 , "簡易改札機");
        DEVICE_LIST.put(24 , "窓口端末");
        DEVICE_LIST.put(25 , "窓口端末");
        DEVICE_LIST.put(26 , "改札端末");
        DEVICE_LIST.put(27 , "携帯電話");
        DEVICE_LIST.put(28 , "乗継精算機");
        DEVICE_LIST.put(29 , "連絡改札機");
        DEVICE_LIST.put(31 , "簡易入金機");
        DEVICE_LIST.put(70 , "VIEW ALTTE");
        DEVICE_LIST.put(72 , "VIEW ALTTE");
        DEVICE_LIST.put(199 , "物販端末");
        DEVICE_LIST.put(200 , "自販機");

        ACTION_LIST.put(1 , "運賃支払(改札出場)");
        ACTION_LIST.put(2 , "チャージ");
        ACTION_LIST.put(3 , "券購(磁気券購入)");
        ACTION_LIST.put(4 , "精算");
        ACTION_LIST.put(5 , "精算 (入場精算)");
        ACTION_LIST.put(6 , "窓出 (改札窓口処理)");
        ACTION_LIST.put(7 , "新規 (新規発行)");
        ACTION_LIST.put(8 , "控除 (窓口控除)");
        ACTION_LIST.put(13 , "バス (PiTaPa系)");
        ACTION_LIST.put(15 , "バス (IruCa系)");
        ACTION_LIST.put(17 , "再発 (再発行処理)");
        ACTION_LIST.put(19 , "支払 (新幹線利用)");
        ACTION_LIST.put(20 , "入A (入場時オートチャージ)");
        ACTION_LIST.put(21 , "出A (出場時オートチャージ)");
        ACTION_LIST.put(31 , "入金 (バスチャージ)");
        ACTION_LIST.put(35 , "券購 (バス路面電車企画券購入)");
        ACTION_LIST.put(70 , "物販");
        ACTION_LIST.put(72 , "特典 (特典チャージ)");
        ACTION_LIST.put(73 , "入金 (レジ入金)");
        ACTION_LIST.put(74 , "物販取消");
        ACTION_LIST.put(75 , "入物 (入場物販)");
        ACTION_LIST.put(198 , "物現 (現金併用物販)");
        ACTION_LIST.put(203 , "入物 (入場現金併用物販)");
        ACTION_LIST.put(132 , "精算 (他社精算)");
        ACTION_LIST.put(133 , "精算 (他社入場精算)");
    }

}