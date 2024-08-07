package com.example.suicanfcreader.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.example.suicanfcreader.viewModel.TopScreenViewModel

@Composable
fun TopScreen(
    topScreenViewModel: TopScreenViewModel
) {

    val nfcData = topScreenViewModel.nfcData.observeAsState("")
    val isDataRefreshed = topScreenViewModel.isDataRefreshed.observeAsState(false)
    val scrollState = rememberScrollState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isDataRefreshed.value && nfcData.value.isNotEmpty()) {
            Button(modifier = Modifier
                .padding(vertical = 60.dp), onClick = {
                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(nfcData.value))
            }) {
                Text("Copy NFC Data")
            }
        }
        Text(text = "NFC Screen")
        Text(text = "NFC Data: ${nfcData.value}")
    }
}
