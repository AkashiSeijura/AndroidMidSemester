package io.github.dekabrsky.androidMidSemester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.dekabrsky.androidMidSemester.presentation.navigation.MatchesNavRoot
import io.github.dekabrsky.androidMidSemester.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MatchesNavRoot()
            }
        }
    }
}
