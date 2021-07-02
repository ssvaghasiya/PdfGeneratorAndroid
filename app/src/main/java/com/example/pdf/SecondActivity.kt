package com.example.pdf

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream


class SecondActivity : AppCompatActivity() {
    private val TAG = "PdfCreatorActivity"
    private val REQUEST_CODE_ASK_PERMISSIONS = 111
    private var pdfFile: File? = null
    var imgdownload: ImageView? = null
    var MyList1: ArrayList<GiftitemPOJO>? = null
    var giftitemPOJO: GiftitemPOJO? = null
    var context: Context? = null
    var name: GiftitemPOJO? = null
    var price: GiftitemPOJO? = null
    var url: GiftitemPOJO? = null
    var type: GiftitemPOJO? = null
    var date: GiftitemPOJO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        imgdownload = findViewById(R.id.downloadpdf)
        context = this
        giftitemPOJO = GiftitemPOJO()
        val doLOgin = DoLOgin()
        doLOgin.execute()
        imgdownload?.setOnClickListener {
            try {
                createPdfWrapper()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: DocumentException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    private fun createPdfWrapper() {
        val hasWriteStoragePermission = ActivityCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel(
                        "You need to allow access to Storage"
                    ) { dialog, which ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                REQUEST_CODE_ASK_PERMISSIONS
                            )
                        }
                    }
                    return
                }
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
            }
            return
        } else {
            createPdf()
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    private fun createPdf() {
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
        if (!docsFolder.exists()) {
            docsFolder.mkdir()
            Log.i(TAG, "Created a new directory for PDF")
        }
        val pdfname = "GiftItem.pdf"
        pdfFile = File(docsFolder.absolutePath, pdfname)
        val output: OutputStream = FileOutputStream(pdfFile)
        val document = Document(PageSize.A4)
        val table = PdfPTable(floatArrayOf(3f, 3f, 3f, 3f, 3f))
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        table.defaultCell.fixedHeight = 50f
        table.totalWidth = PageSize.A4.width
        table.widthPercentage = 100f
        table.defaultCell.verticalAlignment = Element.ALIGN_MIDDLE
        table.addCell("Name")
        table.addCell("Price")
        table.addCell("Type")
        table.addCell("URL")
        table.addCell("Date")
        table.headerRows = 1
        val cells = table.getRow(0).cells
        for (j in cells.indices) {
            cells[j].backgroundColor = BaseColor.GRAY
        }
        for (i in MyList1!!.indices) {
            name = MyList1!![i]
            type = MyList1!![i]
            date = MyList1!![i]
            url = MyList1!![i]
            price = MyList1!![i]
            val namen = name!!.item_name
            val pricen = price!!.item_price
            val daten = date!!.CreatedAt
            val typen = type!!.Item_type_code
            val urln = url!!.Item_URL
            table.addCell(namen.toString())
            table.addCell(pricen.toString())
            table.addCell(typen.toString())
            table.addCell(urln.toString())
            table.addCell(daten!!.substring(0, 10))
        }
        //        System.out.println("Done");
        PdfWriter.getInstance(document, output)
        document.open()
        val f = Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE)
        val g = Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE)
        document.add(Paragraph("Pdf Data \n\n", f))
        document.add(Paragraph("Pdf File Through Itext", g))
        document.add(table)
        //        for (int i = 0; i < MyList1.size(); i++) {
//            document.add(new Paragraph(String.valueOf(MyList1.get(i))));
//        }
        document.close()
        Log.e("safiya", MyList1.toString())
        previewPdf()
    }

    private fun previewPdf() {
        val packageManager = context!!.packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = "application/pdf"
        val list: List<*> =
            packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.size > 0) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val uri = FileProvider.getUriForFile(
                context!!,
                context!!.applicationContext.packageName + ".provider",
                pdfFile!!
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf")
            context!!.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "Download a PDF Viewer to see the generated PDF",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    inner class DoLOgin : AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(objects: Array<Any?>): Any? {
            try {
//                val con: Connection = connectionClass.CONN()
//                if (con == null) {
//                } else {
//                    val query = "select * from youtable"
//                    val statement: Statement = con.createStatement()
//                    val rs: ResultSet = statement.executeQuery(query)
//                    MyList1 = ArrayList<GiftitemPOJO>()
//                    while (rs.next()) {
//                        giftitemPOJO?.item_name = rs.getString("item_name")
//                        giftitemPOJO?.item_price = rs.getString("item_price")
//                        giftitemPOJO?.Item_URL = rs.getString("Item_URL")
//                        giftitemPOJO?.Item_type_code = rs.getString("Item_type_code")
//                        giftitemPOJO?.CreatedAt = rs.getString("CreatedAt")
//                        MyList1?.add(giftitemPOJO!!)
//                        giftitemPOJO = GiftitemPOJO()
//                    }
//                }
                MyList1 = ArrayList<GiftitemPOJO>()
                giftitemPOJO?.item_name = "air conditionar"
                giftitemPOJO?.item_price = "25000"
                giftitemPOJO?.Item_URL = "xyz.com"
                giftitemPOJO?.Item_type_code = "123456789"
                giftitemPOJO?.CreatedAt = "27/08/2000"
                MyList1?.add(giftitemPOJO!!)
                MyList1?.add(giftitemPOJO!!)
            } catch (e: Exception) {
            }
            return null
        }

        override fun onPostExecute(o: Any?) {
            super.onPostExecute(o)
        }
    }
}