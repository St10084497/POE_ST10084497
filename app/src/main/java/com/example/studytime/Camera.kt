package com.example.studytime

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage

import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class Camera : AppCompatActivity() {

    var tvTitle: TextView? = null

    var imgViewCamera: ImageView? = null

    var btnTakePic: Button? = null
    private var storageRef: StorageReference? =null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        tvTitle = findViewById(R.id.textView)

        imgViewCamera = findViewById(R.id.imageView)

        btnTakePic = findViewById(R.id.btnSavepic)
        storageRef= FirebaseStorage.getInstance().reference
        btnTakePic?.setOnClickListener {
            captureOnClick()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            val bm = data?.extras?.get("data") as Bitmap?
            imgViewCamera?.setImageBitmap(bm)

            //conversation to byte array
            val baos = ByteArrayOutputStream()
            bm?.compress(Bitmap.CompressFormat.JPEG,100,baos)
            val imageData : ByteArray =baos.toByteArray()

            //generate a unique file anme
            val filename = "${System.currentTimeMillis()}"

            //push the image after formatting to the db
            val imageRef = storageRef?.child(filename)
            val uploadTask = imageRef?.putBytes(imageData)
            uploadTask?.addOnSuccessListener {
                Toast.makeText(this@Camera,"Image saved to the db", Toast.LENGTH_SHORT)
                val intent = Intent(this@Camera, Dataset::class.java)
                startActivity(intent)
            }?.addOnFailureListener{
                Toast.makeText(this@Camera,"Failed to save image",Toast.LENGTH_SHORT)
            }//failure listerner end

        }catch (ex:java.lang.Exception){
            Toast.makeText(this@Camera,"Pic not saved",Toast.LENGTH_SHORT).show()
        }//catch ends
    }//method ends
    //function to capture the on click
    private fun captureOnClick(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,0)
    }
}







