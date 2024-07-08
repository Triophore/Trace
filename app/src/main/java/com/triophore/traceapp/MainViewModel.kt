package com.triophore.traceapp

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.triophore.graph.SineWave.getSineWave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val _plotValues = MutableSharedFlow<List<Double>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val plotValues = _plotValues.asSharedFlow()

    fun sendValues(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            while (true) {
                delay(1.seconds)
                _plotValues.tryEmit(getSineWave())
            }
        }
    }

    fun createValues() {
        val pi = 3.14159
        var data = listOf<Double>()
        val freq = 1
        val step = 0.01
        var t = 0.0
        var flag = false
        GlobalScope.launch {
            while (isActive) {
                //delay(1.microseconds)
                data = data + (kotlin.math.sin(2 * pi * freq * t))
                t += step
                if (t.isPerfectInt(step) && !flag) {
                    flag = true
                    println("testtest: $data")
                    createAndWriteFileExternal(context, "data.txt", "$data")
                    data = listOf<Double>()
                    t = 0.0
                }
                yield()
            }
        }
    }

    private fun createAndWriteFileExternal(context: Context, filename: String, data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val destPath: String = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath ?: ""
                val folder = File("$destPath/Folder")
                if (!folder.exists()) {
                    folder.mkdirs()
                    println("file created")
                } else
                    println("file exists")


                val file = File(folder, filename)
                file.writeText(data)
            } catch (e: IOException) {
                // Handle the exception
                e.printStackTrace()
            }
        }
    }

//    fun createFolderInAppStorage(context: Context, folderName: String): File? {
//        val appFilesDir = context.filesDir
//        val folder = File(appFilesDir, folderName)
//        return if (!folder.exists()) {
//            folder.mkdirs()
//            folder  // Return the folder object if created
//        } else {
//            null // Return null if folder already exists or couldn't be created
//        }
//    }
}

private fun Double.isPerfectInt(precision: Double): Boolean {
    println("testtest :::: ${this - this.toInt().toDouble()}  :::: ${precision} :::: ${this - this.toInt().toDouble() < precision}")
    return this - this.toInt().toDouble() < precision
}
