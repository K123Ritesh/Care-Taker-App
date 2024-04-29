package com.example.care_taker


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream

class MainActivity : FlutterActivity() {

    private val CHANNEL = "screenshot_channel"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "takeScreenShot") {
                takeScreenShot(result)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun takeScreenShot(result: MethodChannel.Result) {
        val activity = this
        val window = activity.window
        val view = window.decorView.rootView

        val surfaceView = getSurfaceView(view) ?: return

        val surface = surfaceView.holder.surface

        if (!surface.isValid) return

        val bitmap: Bitmap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

            PixelCopy.request(surfaceView, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    val byteArray = bitmapToByteArray(bitmap)
                    result.success(byteArray)
                } else {
                    result.error("PIXEL_COPY_FAILED", "PixelCopy failed", null)
                }
            }, Handler(Looper.getMainLooper()))

        } else {
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
            val byteArray = bitmapToByteArray(bitmap)
            result.success(byteArray)
        }
    }

    private fun getSurfaceView(view: View): SurfaceView? {
        var surfaceView: SurfaceView? = null
        traverseView(view) { v -> if (v is SurfaceView) surfaceView = v }
        return surfaceView
    }

    private fun traverseView(view: View, callback: (View) -> Unit) {
        callback(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                traverseView(view.getChildAt(i), callback)
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
