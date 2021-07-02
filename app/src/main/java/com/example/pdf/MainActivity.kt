package com.example.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var btnCreate: Button? = null
    var editText: EditText? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCreate = findViewById<View>(R.id.create) as Button
        editText = findViewById<View>(R.id.edittext) as EditText
        btnCreate?.setOnClickListener { createPdf(editText!!.text.toString()) }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun createPdf(sometext: String) {
        // create a new document
        val document = PdfDocument()
        // crate a page description
        var pageInfo = PageInfo.Builder(300, 600, 1).create()
        // start a page
        var page = document.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        var paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(50F, 50F, 30F, paint)
        paint.color = Color.BLACK
        canvas.drawText(sometext, 80F, 50F, paint)
        //canvas.drawt
        // finish the page
        document.finishPage(page)
        // draw text on the graphics object of the page
        // Create Page 2
        pageInfo = PageInfo.Builder(300, 600, 2).create()
        page = document.startPage(pageInfo)
        canvas = page.canvas
        paint = Paint()
        paint.color = Color.BLUE
        canvas.drawCircle(100F, 100F, 100F, paint)
        document.finishPage(page)
        // write the document content
        val directory_path = Environment.getExternalStorageDirectory().path + "/mypdf/"
        val file = File(directory_path)
        if (!file.exists()) {
            file.mkdirs()
        }
        val targetPdf = directory_path + "test-2.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("main", "error " + e.toString())
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show()
        }
        // close the document
        document.close()
    }
}