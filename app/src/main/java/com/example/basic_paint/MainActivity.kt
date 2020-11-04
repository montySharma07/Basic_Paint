package com.example.basic_paint

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView:DrawingView?=null
    private var mcolorSelected:ImageButton?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView=findViewById<DrawingView>(R.id.drawing_view)
        drawingView!!.changeBrushSize(10.toFloat())
        val ll:LinearLayout=findViewById(R.id.llcolor)
        mcolorSelected=ll[1]as ImageButton
//        mcolorSelected!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.palletpressed))

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
    fun paintClicked(view: View){

        if(view!==mcolorSelected){
            val imageButton=view as ImageButton
            val colorTag=imageButton.tag.toString()
            drawingView!!.setcolor(colorTag)
            imageButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.palletpressed))
        }
        mcolorSelected!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet))
        mcolorSelected=view as ImageButton
    }

}