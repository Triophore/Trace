package com.triophore.traceapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.triophore.trace.Trace
import com.triophore.traceapp.ui.theme.TraceAppTheme


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            TraceAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Plotter(modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .clip(MaterialTheme.shapes.large),
                        data = viewModel.plotValues.collectAsState(initial = listOf()).value)
                }
            }
        }
        viewModel.sendValues(lifecycleScope)
    }
}

@Composable
fun Plotter(modifier: Modifier = Modifier, data: List<Double>) {
    Trace(
        modifier = modifier,
        verticalScale = 0.8f,
        horizontalScale = 1,
        sampleRate = 100.0,
        data = data,
        backgroundColor = Color.Gray
    )
}

@Preview(showBackground = true)
@Composable
fun PlotterPreview() {
    val viewModel = MainViewModel(application = Application())
    TraceAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Plotter(modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(innerPadding)
                .clip(MaterialTheme.shapes.large),
                data = viewModel.plotValues.collectAsState(initial = listOf()).value)
        }
    }
}