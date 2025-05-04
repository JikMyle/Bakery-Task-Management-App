package com.example.bakerytaskmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bakerytaskmanagementapp.ui.staff.StaffScreen
import com.example.bakerytaskmanagementapp.ui.theme.BakeryTaskManagementAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BakeryTaskManagementAppTheme {
                StaffScreen()
            }
        }
    }
}