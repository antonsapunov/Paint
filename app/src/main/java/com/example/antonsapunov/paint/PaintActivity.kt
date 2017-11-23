package com.example.antonsapunov.paint

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_paint.*

class PaintActivity : AppCompatActivity() {

    private val drawingView by bind<DrawingView>(R.id.drawing_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)

        modeMove.setOnClickListener { _ ->
            drawingView.tool = Tool.MOVE
            menu.menuIconView.setImageResource(R.drawable.cursor_pointer)
            menu.close(true)
        }
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


        val pickedImage = intent.data
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

    companion object {
        fun newIntent(context: Context, uri: Uri? = null): Intent {
            val intent = Intent(context, PaintActivity::class.java)
            intent.data = uri
            return intent
        }
    }

}
