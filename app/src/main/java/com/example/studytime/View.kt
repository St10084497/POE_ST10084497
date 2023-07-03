package com.example.studytime

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.*
import java.util.*

class View : AppCompatActivity() {

    private lateinit var readButton: Button
    private lateinit var database: DatabaseReference
    private var alertDialog : AlertDialog? = null //ui to place data on
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        readButton = findViewById(R.id.readButton)
        database = FirebaseDatabase.getInstance().reference
        readButton.setOnClickListener {

            fetchEntriesFromFirebase()
        }

    }

    private fun fetchEntriesFromFirebase()
    {
        val query =database.child("items")


        query.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries : MutableList<TaskModel> = mutableListOf()
                for (entrySnapshot in snapshot.children){
                    val entry = entrySnapshot.getValue(TaskModel::class.java)
                    entry?.let { entries.add(it) }
                }
                //add alert db-- and pass into it
                showEntriesAlert(entries)
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@View,"Failed to read entry ", Toast.LENGTH_SHORT ).show()
            }
        })

    }//fetch the entries


    private fun showEntriesAlert(entries: List<TaskModel>)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("TimeSheet Entries")
        if (entries.isEmpty())
        {
            builder.setMessage("No Entries Found")
        }else
        {
            val entryTexts = entries.mapIndexed{index, entry  ->
                "Entry ${index +1} \n "+
                        "Task Name: ${entry.taskName}\n"+
                        "Task Desc: ${entry.taskDesc}\n"+
                        "Start Date:${entry.startDateString}\n"+
                        "Start Time: ${entry.startTimeString}\n"+
                        "End Date: ${entry.endDateString}\n"+
                        "End Time: ${entry.endTimeString}\n"+
                        "Total Time: ${entry.totalTimeString}\n\n"
            }
            val entriesText = entryTexts.reduce { acc, entryText -> acc + entryText  }

            builder.setMessage(entriesText)

        }
        builder.setPositiveButton("OK "){dialog,_ ->
            dialog.dismiss()
        }

        alertDialog= builder.create()
        alertDialog?.show()

    }

}//class ends


