package com.example.zoltantudlik.task4

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions


class FaceDetector(activity: MainActivity) {
    private var detector: FirebaseVisionFaceDetector
    private val treshold = 0.5f
    private val mainActivity: MainActivity

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(FirebaseVisionFaceDetectorOptions.FAST_MODE)
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setTrackingEnabled(false)
                .build()

        mainActivity = activity
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
    }

    fun detectFaces(bitmap: Bitmap) {
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)

        detector.detectInImage(image)
                .addOnSuccessListener { faces ->
                    Log.d("MCC", "Detected faces: ${faces.size}")
                    faces.forEach { face ->
                        Log.d("MCC", "Smiling chance: ${face.smilingProbability}")
                        Log.d("MCC", "Eyes left chance: ${face.rightEyeOpenProbability}")
                        Log.d("MCC", "Eyes right chance: ${face.leftEyeOpenProbability}")
                    }

                    val smilingAvg = faces
                            .asSequence()
                            .map { face -> face.smilingProbability }
                            .average()

                    val eyesAvg = faces
                            .asSequence()
                            .map { face -> (face.leftEyeOpenProbability + face.rightEyeOpenProbability) / 2 }
                            .average()

                    val isSmiling = smilingAvg >= treshold
                    val hasEyes = eyesAvg >= treshold

                    mainActivity.setFaceInfo(faces.size, isSmiling, hasEyes)
                }
                .addOnFailureListener { error ->
                    Log.e("MCC", "Error during face recognition", error)
                }
    }
}