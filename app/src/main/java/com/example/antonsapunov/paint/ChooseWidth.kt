package com.example.antonsapunov.paint

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class ChooseWidth: DialogFragment(){

    var width: Float = 0f
    var completionHandler: ((Float) -> Unit)? = null

    companion object {
        fun newInstance(currentWidth: Float, completionHandler: (Float) -> Unit): ChooseWidth {
            val dialog = ChooseWidth()
            dialog.width = currentWidth
            dialog.completionHandler = completionHandler
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.choose_width, container, false)
        val seekBar = view?.findViewById<SeekBar>(R.id.seek_bar)
        val textView = view?.findViewById<TextView>(R.id.textView)
        seekBar?.max = 49
        seekBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView?.text = "Stroke Width: ${progress+1}"
                width = (progress+1).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBar?.progress = (width-1).toInt()
        view?.findViewById<Button>(R.id.cancelButton)?.setOnClickListener({ v ->
            dismiss()
        })
        view?.findViewById<Button>(R.id.okButton)?.setOnClickListener({ v ->
            completionHandler?.invoke(width)
            dismiss()
        })
        return view
    }

}