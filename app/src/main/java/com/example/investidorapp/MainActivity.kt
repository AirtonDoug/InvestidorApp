package com.example.investidorapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investidorapp.ui.theme.InvestidorAppTheme
import com.example.investidorapp.ui.view.InvestidorScreen
import com.example.investidorapp.viewmodel.InvestimentosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES .TIRAMISU) {
            ActivityCompat .requestPermissions(
                this,
               arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
        setContent {
            val viewModel: InvestimentosViewModel = viewModel()
            InvestidorScreen (viewModel)
        }
    } }