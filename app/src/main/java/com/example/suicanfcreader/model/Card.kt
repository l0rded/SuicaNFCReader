package com.example.suicanfcreader.model

import android.content.Context
import com.example.suicanfcreader.lib.SuicaReader

data class Card(
    var date: String? = null,
    var number: String? = null,
    var payment: String? = null,
    var kind: String? = null,
    var device: String? = null,
    var action: String? = null,
    var inLine: String? = null,
    var inStation: String? = null,
    var outLine: String? = null,
    var outStation: String? = null,
    var balance: String? = null,
    var inCompany: String? = null,
    var outCompany: String? = null
) {
    companion object {
        fun getCard(context: Context?, felica: SuicaReader): Card {
            val card = Card().apply {
                date = "${2000 + felica.year}年${felica.month}月${felica.day}日"
                number = felica.seqNo.toString()
                payment = ""
                kind = felica.kind
                device = felica.device
                action = felica.action

                // Retrieve station details safely
                val inStationDetails = context?.let {
                    Station.getStation(it, felica.inLine, felica.inStation)
                }
                inLine = inStationDetails?.lineName ?: "-"
                inStation = inStationDetails?.stationName ?: "-"
                inCompany = inStationDetails?.company ?: "-"

                val outStationDetails = context?.let {
                    Station.getStation(it, felica.outLine, felica.outStation)
                }
                outLine = outStationDetails?.lineName ?: "-"
                outStation = outStationDetails?.stationName ?: "-"
                outCompany = outStationDetails?.company ?: "-"

                balance = felica.remain.toString()
            }
            return card
        }
    }
}
