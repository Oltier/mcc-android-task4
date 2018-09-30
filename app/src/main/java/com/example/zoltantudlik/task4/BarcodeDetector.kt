package com.example.zoltantudlik.task4

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage


class BarcodeDetector(activity: MainActivity) {

    private val mainActivity: MainActivity = activity
    private val detector: FirebaseVisionBarcodeDetector

    init {
        val options: FirebaseVisionBarcodeDetectorOptions = FirebaseVisionBarcodeDetectorOptions
                .Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                .build()

        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
    }

    fun detectBarcode(barcode: Bitmap) {
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(barcode)

        detector.detectInImage(image)
                .addOnSuccessListener { barcodes ->
                    Log.d("MCC", "Barcodes: ${barcodes.size}")
                    if(barcodes.size > 0) {
                        mainActivity.setBarcodeInfo(true)
                    } else {
                        mainActivity.setBarcodeInfo(false)
                    }
                }
                .addOnFailureListener{ error ->
                    Log.e("MCC", "Error during barcode scanning", error)
                }
    }
}