package com.example.suicanfcreader

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.suicanfcreader.viewModel.TopScreenViewModel
import com.example.suicanfcreader.viewModel.TopScreenViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var topScreenViewModel: TopScreenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        topScreenViewModel = ViewModelProvider(this, TopScreenViewModelFactory(this))[TopScreenViewModel::class.java]

        setContent {
            SuicaNFCReaderApp(this@MainActivity, topScreenViewModel)
        }

        lifecycleScope.launch {
            topScreenViewModel.nfcData.observe(this@MainActivity) { data ->
                // Handle the NFC data
                println("NFC Data: $data")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        topScreenViewModel.enableNfcForegroundDispatch(this)
    }

    override fun onPause() {
        super.onPause()
        topScreenViewModel.disableNfcForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        topScreenViewModel.handleNfcIntent(intent,this)
    }
}