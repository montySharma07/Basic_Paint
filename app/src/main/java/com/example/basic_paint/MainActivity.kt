package com.example.basic_paint

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var drawingView:DrawingView?=null
    private var mcolorSelected:ImageButton?=null
    private var mImageView:ImageView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView=findViewById<DrawingView>(R.id.drawing_view)
        drawingView!!.changeBrushSize(10.toFloat())
        val ll:LinearLayout=findViewById(R.id.llcolor)
        mcolorSelected=ll[1]as ImageButton
        val changeBrush:ImageView=findViewById(R.id.image)
        changeBrush.setOnClickListener {
            showSizeChanger()
        }
        val gallery:ImageView=findViewById(R.id.gallery)
        gallery.setOnClickListener {
            if(isReadStorageAllowed()){
                val pickPhotoIntent=Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotoIntent, GALLERY)
            }
            else{
                requestPremissionRequest()
            }
        }
        val undo=findViewById<ImageView>(R.id.undo)
        undo.setOnClickListener {
            drawingView!!.onClickUndo()
        }
        val saveButton:ImageView=findViewById(R.id.save)
        saveButton.setOnClickListener {
            if(isReadStorageAllowed()){
                BitmapAsyncTask(getBitmapFormView(findViewById(R.id.fl))).execute()
            }else{
                requestPremissionRequest()
            }
        }

    }

    private fun getBitmapFormView(view: View):Bitmap{
        val returnedBitmap=Bitmap.createBitmap(view.width,view.height,
                                            Bitmap.Config.ARGB_8888)
        val canvas=Canvas(returnedBitmap)
        val bg=view.background
        if(bg!=null){
            bg.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return  returnedBitmap
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

    private fun requestPremissionRequest(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this,"the permission granted",Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@MainActivity,"permission granted for storage",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@MainActivity,"Oops permission not granted",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mImageView=findViewById(R.id.imageView)
        if(requestCode== Activity.RESULT_OK && requestCode== GALLERY && data!!.data !=null){
            try {
                mImageView!!.visibility=View.VISIBLE
                mImageView!!.setImageURI(data.data)
            }catch (e :Exception){
                e.printStackTrace()
            }
        }
    }


    private fun isReadStorageAllowed():Boolean{
        val result=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result==PackageManager.PERMISSION_GRANTED
    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap): AsyncTask<Any, Void, String>() {

        private lateinit var mProgressDialog: Dialog

        override fun doInBackground(vararg params: Any?): String {
            var result=""
            if(mBitmap!=null){
                try {
                    val bytes=ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                    val f=File(externalCacheDir!!.absoluteFile.toString()
                            +File.separator+"KidDrawingApp"
                            +System.currentTimeMillis()/1000+".png")
                    val fos=FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    fos.close()
                    result=f.absolutePath
                }catch (e:Exception){
                    result=""
                    e.printStackTrace()
                }
            }
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            if(!result!!.isEmpty()){
                Toast.makeText(this@MainActivity,"File Saved Successfully",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@MainActivity,"Error Happened while saving the file",Toast.LENGTH_SHORT).show()
            }
            MediaScannerConnection.scanFile(this@MainActivity,
                                        arrayOf(result),null){
                path,uri-> val shareIntent=Intent()
                shareIntent.action= Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
                shareIntent.type="image/png"
                startActivity(
                        Intent.createChooser(
                                shareIntent,"share"
                        )
                )
            }

        }

        private fun showProgressDialog(){
            mProgressDialog= Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.custom_progress)
            mProgressDialog.show()
        }
        private fun cancelProgressDialog(){
            mProgressDialog.dismiss()
        }


    }

    companion object{
        private const val STORAGE_PERMISSION_CODE=1
        private const val GALLERY=2
    }

}