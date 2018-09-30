package com.example.zoltantudlik.task4

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.zoltantudlik.task4.R.id.image


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
            val mainActivity = this
            val requestListener = object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    Log.e("MCC", "Error loading bitmap", e)
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    val faceDetector = FaceDetector(mainActivity)
                    faceDetector.detectFaces(resource!!)

                    val barcodeDetector = BarcodeDetector(mainActivity)
                    barcodeDetector.detectBarcode(resource)

                    return false
                }

            }


            Glide.with(this)
                    .load(selectedImage) // Uri of the picture
                    .into(imageView);

            Glide.with(this)
                    .asBitmap()
                    .load(selectedImage)
                    .apply(RequestOptions().override(800, 600))
                    .listener(requestListener)
                    .submit()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_READ_EXTERNAL_STORAGE)
        }
    }

    private fun loadImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, RESULT_LOAD_IMG)
    }

    fun setFaceInfo(faces: Int, isSmiling: Boolean, hasEyes: Boolean) {
        peopleTextView.text = faces.toString()
        smileTextView.text = if (isSmiling) getText(R.string.yes) else getText(R.string.no)
        eyesTextView.text = if (hasEyes) getText(R.string.yes) else getText(R.string.no)
    }

    fun setBarcodeInfo(containsBarcode: Boolean) {
        barCodeTextView.text = if (containsBarcode) getText(R.string.yes) else getText(R.string.no)
    }
}
