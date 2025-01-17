package com.yasinmaden.ecommerceapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.yasinmaden.ecommerceapp.navigation.RootNavigationGraph
import com.yasinmaden.ecommerceapp.ui.theme.MyappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyappTheme {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
    }

}