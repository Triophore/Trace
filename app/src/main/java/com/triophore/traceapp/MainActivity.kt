package com.triophore.traceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.triophore.trace.Trace
import com.triophore.traceapp.ui.theme.TraceAppTheme


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TraceAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Trace(
                        modifier = Modifier.clip(MaterialTheme.shapes.large),
                        verticalScale = 0.8f,
                        horizontalScale = 1,
                        sampleRate = 100.0,
                        data = viewModel.plotValues.collectAsState(initial = listOf()).value,
                        backgroundColor = Color.Blue
                    )
                }
            }
        }
        viewModel.sendValues(lifecycleScope)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TraceAppTheme {
        Greeting("Android")
    }
}