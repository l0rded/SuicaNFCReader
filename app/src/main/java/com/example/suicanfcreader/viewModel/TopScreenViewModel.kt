package com.example.suicanfcreader.viewModel

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.suicanfcreader.lib.SuicaReader
import com.example.suicanfcreader.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TopScreenViewModel(
    context: Context
) : ViewModel() {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)
    private val _nfcData = MutableLiveData<String>()
    val nfcData: LiveData<String> get() = _nfcData
    private val _showNoNfcDialog = MutableLiveData<Boolean>()

    fun enableNfcForegroundDispatch(activity: Activity) {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val nfcIntentFilter = arrayOf(
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                )

                val pendingIntent =
                    PendingIntent.getActivity(
                        activity,
                        0,
                        Intent(
                            activity,
                            activity.javaClass
                        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_MUTABLE
                    )
                adapter.enableForegroundDispatch(activity, pendingIntent, nfcIntentFilter, null)
            } else {
                _showNoNfcDialog.postValue(true)
            }
        }
    }

    fun disableNfcForegroundDispatch(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    fun handleNfcIntent(intent: Intent?,context: Context) {
        intent?.let {
            if (intent.action in listOf(
                    NfcAdapter.ACTION_TAG_DISCOVERED,
                    NfcAdapter.ACTION_TECH_DISCOVERED,
                    NfcAdapter.ACTION_NDEF_DISCOVERED
                )
            ) {
                val tag = it.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                tag?.let {
                    viewModelScope.launch {
                        val data = readTagData(tag, context)
                        _nfcData.postValue(data)
                    }
                }
            }
        }
    }

    private suspend fun readTagData(tag: Tag,context: Context): String = withContext(Dispatchers.IO) {
        val sb = StringBuilder()
        val id = tag.id
        try {
            val felica = NfcF.get(tag)
            felica.connect()
            val req = SuicaReader.readWithoutEncryption(id, 10)
            val res: ByteArray = felica.transceive(req)
            felica.close()
            sb.append(fromData(res, context))
        } catch (e: Exception) {
            sb.append("Error reading NFC tag: ${e.message}")
        }
        sb.toString()
    }


    private fun fromData(data: ByteArray, context: Context):String {

        val size: Int = data[12].toInt()
        val sb = StringBuilder()
        for (i in 0 until size) {
            val felica = SuicaReader.parse(data, 13 + i * 16)
            val card: Card = Card.getCard(context, felica)
            if (i < size - 1) {

                sb.appendLine("=== %02d ===".format(i))
                sb.appendLine("端末種: %s".format(card.device?: ""))
                sb.appendLine("処理: %s".format(card.kind?: ""))
                sb.appendLine("日付:%s".format(card.date?: ""))
                sb.appendLine("入線区: %s-%s".format(card.inCompany?: "", card.inLine?: ""))
                sb.appendLine("入駅順: %s".format(card.inStation?: ""))
                sb.appendLine("出線区: %s-%s".format(card.outCompany?: "", card.outCompany?: ""))
                sb.appendLine("出駅順: %s".format(card.outStation?: ""))
                sb.appendLine("残高: %s".format(card.balance?: ""))
                sb.appendLine("BIN: ")
                sb.appendLine(data.joinToString(" ") { "%02x".format(it) })
            }
        }
        return sb.toString()
    }
}



class TopScreenViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TopScreenViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
