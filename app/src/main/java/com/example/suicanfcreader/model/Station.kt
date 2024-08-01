package com.example.suicanfcreader.model

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

data class Station(
    var lineName: String = "",
    var stationName: String = "",
    var company: String = ""
) {
    companion object {
        fun getStation(context: Context, lineCode: Int, stationCode: Int): Station {
            val station = Station()

            try {
                context.assets.open("StationCode.csv").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { br ->
                        br.lineSequence().forEach { line ->
                            val tokens = line.split(",").map { it.trim() }
                            if (tokens.size >= 6 && tokens[1] == lineCode.toString() && tokens[2] == stationCode.toString()) {
                                station.company = tokens[3]
                                station.lineName = tokens[4]
                                station.stationName = tokens[5]
                                return station
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return station
        }
    }
}
