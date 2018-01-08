package com.example.antonsapunov.paint

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val paint by bind<Button>(R.id.paint)
    private val camera by bind<Button>(R.id.camera)
    private val gallery by bind<Button>(R.id.gallery)

    private var permissionListener: (() -> Unit)? = null
    private lateinit var cameraImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paint.setOnClickListener { startActivity(PaintActivity.newIntent(this)) }
        camera.setOnClickListener { takePictureFromCamera() }
        gallery.setOnClickListener { chooseImageFromGallery() }
    }

    companion object {
        val GALLERY = 1
        val CAMERA = 2
        val PERMISSION_CODE = 3
        val IMAGE_URI = "imageUri"
    }
    private fun takePictureFromCamera() {
        checkPermission {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            val uri = createUri()
            if (uri != null && intent.resolveActivity(packageManager) != null) {
                cameraImageUri = uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, CAMERA)
            } else Toast.makeText(this, "Can`t open camera :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA -> {
                    startActivity(PaintActivity.newIntent(this, cameraImageUri, "camera"))
                }
                GALLERY -> {
                    if (resultIntent != null) {
                        startActivity(PaintActivity.newIntent(this, resultIntent.data))
                    } else Toast.makeText(applicationContext, "Can`t open choosen picture :(", Toast.LENGTH_LONG).show()
                }
                else -> throw IllegalArgumentException("Unsupported request, might be one of CAMERA or GALLERY!")
            }
        }
    }

    private fun createUri(): Uri? {
        val imageFile: File?

        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/Paint")
        if (!directory.exists()) directory.mkdir()
        try {
            imageFile = File.createTempFile("IMG_", ".jpg", directory)
            return FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID, imageFile)
        } catch (exception: IOException) {
            Toast.makeText(applicationContext, "Can`t take picture :(", Toast.LENGTH_LONG).show()
        }
        return null

    }

    private fun checkPermission(listener: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listener.invoke()
        } else {
            val permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionListener = listener
                requestPermission()
            } else listener.invoke()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionListener?.invoke()
            permissionListener = null
        } else {
            Snackbar.make(window.decorView, "App needs permission to write to storage", Snackbar.LENGTH_LONG)
                    .setAction("Try again") {
                        requestPermission()
                    }.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putParcelable(IMAGE_URI, cameraImageUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val parcelable: Parcelable? = savedInstanceState?.getParcelable(IMAGE_URI)
        if (parcelable != null) cameraImageUri = parcelable as Uri
    }

}
