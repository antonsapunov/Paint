package com.example.antonsapunov.paint

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.NavUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_paint.*
import org.xdty.preference.colorpicker.ColorPickerDialog
import java.io.File

class PaintActivity : AppCompatActivity() {

    private val drawingView by bind<DrawingView>(R.id.drawing_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)

        modePath.setOnClickListener { _ ->
            drawingView.tool = Tool.PATH
            menu.menuIconView.setImageResource(R.drawable.marker)
            menu.close(true)
        }
        modeRect.setOnClickListener { _ ->
            drawingView.tool = Tool.RECT
            menu.menuIconView.setImageResource(R.drawable.checkbox_blank_outline)
            menu.close(true)
        }
        modeCircle.setOnClickListener { _ ->
            drawingView.tool = Tool.CIRCLE
            menu.menuIconView.setImageResource(R.drawable.checkbox_blank_circle_outline)
            menu.close(true)
        }

        if (intent.getStringExtra(URI) != "null" && intent.getStringExtra(CAMERA_FLAG) != "camera") {
            // Let's read picked image data - its URI
            val pickedImage = Uri.parse(intent.getStringExtra(URI))
            // Let's read picked image path using content resolver
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(pickedImage!!, filePath, null, null, null)
            cursor!!.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]))

            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(imagePath, options)
            drawingView.drawImage(bitmap)
            cursor.close()
        }
        if (intent.getStringExtra(URI) != "null" && intent.getStringExtra(CAMERA_FLAG) == "camera") {
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/Paint")
            val photo = directory.listFiles().last()
            val photoUri = photo.absolutePath

            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(photoUri, options)
            drawingView.drawImage(bitmap)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.redactor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.actionPickColor -> {
                chooseColor()
            }
            R.id.actionPickSize -> {
                pickSize()
            }
            R.id.actionSave -> {
                saveImage()
            }
            R.id.actionShare -> {
                shareImage()
            }
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
            }
        }
        return true
    }

    private fun chooseColor() {
        val mColors = resources.getIntArray(R.array.my_rainbow)

        val dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                mColors,
                drawingView.color,
                4,
                ColorPickerDialog.SIZE_SMALL,
                true
        )

        dialog.setOnColorSelectedListener { color ->
            drawingView.color = color
        }

        dialog.show(fragmentManager, "color_dialog_test")
    }

    private fun pickSize() {
        ChooseWidth.newInstance(drawingView.strokeWidth, {width -> drawingView.strokeWidth = width}).show(fragmentManager,"F")
    }

    private fun shareImage() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val path = saveImage()
        if (path != null) {
            val uri = Uri.parse(path)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/jpeg"
            startActivity(shareIntent)
        }
    }

    private fun saveImage(): String? {
        val name = "draw" + System.currentTimeMillis() + ".jpg"
        val image = drawingView.getImage()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        return MediaStore.Images.Media.insertImage(
                contentResolver, image, name,
                name)
    }

    override fun onBackPressed() {
        drawingView.undo()
    }

    companion object {
        val URI = "URI"
        val CAMERA_FLAG = "CAMERA_FLAG"
        fun newIntent(context: Context, uri: Uri? = null, cameraFlag: String = ""): Intent {
            val intent = Intent(context, PaintActivity::class.java)
            intent.putExtra(URI, uri.toString())
            intent.putExtra(CAMERA_FLAG, cameraFlag)
            return intent
        }
    }

}
