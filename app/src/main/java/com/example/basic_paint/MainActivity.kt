package com.example.basic_paint

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private var drawingView:DrawingView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView=findViewById<DrawingView>(R.id.drawing_view)
        drawingView!!.changeBrushSize(10.toFloat())
        val changeBrush:ImageView=findViewById(R.id.image)
        changeBrush.setOnClickListener {
            showSizeChanger()
        }

    }

    private fun showSizeChanger(){
        val brushDialog=Dialog(this)
        brushDialog.setContentView(R.layout.brush_size_dialog)
        brushDialog.setTitle("choose Size")

        val smallbtn=brushDialog.findViewById<ImageView>(R.id.small)
        val medbtn=brushDialog.findViewById<ImageView>(R.id.medium)
        val largebtn=brushDialog.findViewById<ImageView>(R.id.large)

        smallbtn.setOnClickListener {
            drawingView!!.changeBrushSize(10.toFloat())
            brushDialog.dismiss()
        }
        medbtn.setOnClickListener {
            drawingView!!.changeBrushSize(20.toFloat())
            brushDialog.dismiss()
        }
        largebtn.setOnClickListener {
            drawingView!!.changeBrushSize(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()

    }

}