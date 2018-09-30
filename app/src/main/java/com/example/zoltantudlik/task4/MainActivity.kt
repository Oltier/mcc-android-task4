package com.example.zoltantudlik.task4

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var pickPhotoButton: Button
    private lateinit var barCodeTextView: TextView
    private lateinit var peopleTextView: TextView
    private lateinit var smileTextView: TextView
    private lateinit var eyesTextView: TextView
    private lateinit var imageView: ImageView

    private val RESULT_LOAD_IMG = 1
    private val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pickPhotoButton = findViewById(R.id.btnPickPhoto)
        barCodeTextView = findViewById(R.id.txtBarcode)
        peopleTextView = findViewById(R.id.txtNumPeople)
        smileTextView = findViewById(R.id.txtSmile)
        eyesTextView = findViewById(R.id.txtEyes)
        imageView = findViewById(R.id.imageView)

        checkPermissions()

        pickPhotoButton.setOnClickListener { _ ->
            loadImageFromGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(selectedImage,
                    filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            val image = BitmapFactory.decodeFile(picturePath)

            imageView.setImageBitmap(image)

            val faceDetector = FaceDetector(this)
            faceDetector.detectFaces(image)

            val barcodeDetector = BarcodeDetector(this)
            barcodeDetector.detectBarcode(image)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_READ_EXTERNAL_STORAGE)
        }
    }

    private fun loadImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RESULT_LOAD_IMG)
    }

    fun setFaceInfo(faces: Int, isSmiling: Boolean, hasEyes: Boolean) {
        peopleTextView.text = faces.toString()
        smileTextView.text = if(isSmiling) getText(R.string.yes) else getText(R.string.no)
        eyesTextView.text = if(hasEyes) getText(R.string.yes) else getText(R.string.no)
    }

    fun setBarcodeInfo(containsBarcode: Boolean) {
        barCodeTextView.text = if(containsBarcode) getText(R.string.yes) else getText(R.string.no)
    }
}
