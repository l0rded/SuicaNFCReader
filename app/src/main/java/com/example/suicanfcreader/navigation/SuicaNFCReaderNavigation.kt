package com.example.suicanfcreader.navigation

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.suicanfcreader.view.screens.TopScreen
import com.example.suicanfcreader.viewModel.TopScreenViewModel
import com.example.suicanfcreader.viewModel.TopScreenViewModelFactory

/**
 * Provides the navigation in the app.
 */
@Composable
fun SuicaNFCReaderNavigation(
    navController: NavHostController,
    context: Context,
    modifier: Modifier,
    viewModel: TopScreenViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TopScreen.route
    ) {
        composable(Screen.TopScreen.route) {
             TopScreen(viewModel)
        }
    }
}