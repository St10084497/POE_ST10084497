package com.example.studytime

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Dataset : AppCompatActivity() {


    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var nameEditText: EditText

    private lateinit var startDateButton: Button

    private lateinit var startTimeButton: Button

    private lateinit var endDateButton: Button

    private lateinit var endTimeButton: Button

    private lateinit var descriptionEditText: EditText

   private lateinit var imageView: ImageView

    private lateinit var captureImageButton: Button

    private lateinit var submitButton: Button

    private lateinit var picButton: Button


    private var capturedImage: Bitmap? = null

    private lateinit var categorySpinner: Spinner

    private val defaultCategoryIndex = 0 // Declare the variable for the default category index

    private lateinit var readButton: Button

    private lateinit var database: DatabaseReference

    private var startDate: Date? = null

    private var startTime: Date? = null

    private var endDate: Date? = null

    private var endTime: Date? = null
    private var alertDialog : AlertDialog? = null //ui to place data on

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dataset)

        picButton=findViewById(R.id.btnPic)

        nameEditText = findViewById(R.id.nameEditText)
        readButton = findViewById(R.id.readButton)
        startDateButton = findViewById(R.id.startDateButton)

        startTimeButton = findViewById(R.id.startTimeButton)

        endDateButton = findViewById(R.id.endDateButton)

        endTimeButton = findViewById(R.id.endTimeButton)

        descriptionEditText = findViewById(R.id.descriptionEditText)

        submitButton = findViewById(R.id.submitButton)

        database = FirebaseDatabase.getInstance().reference

        startDateButton.setOnClickListener { showDatePicker(startDateListener) }

        startTimeButton.setOnClickListener { showTimePicker(startTimeListener) }

        endDateButton.setOnClickListener { showDatePicker(endDateListener) }

        endTimeButton.setOnClickListener { showTimePicker(endTimeListener) }



        categorySpinner = findViewById(R.id.categorySpinner)

        val categories =
//add the subjects for studying in category
            listOf("Mathematics", "English", "Life Orientation","Physics") // Replace with your desired categories

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        categorySpinner.adapter = categoryAdapter

        categorySpinner.setSelection(defaultCategoryIndex) // Set the default selection using the variable
        submitButton.setOnClickListener {
            val selectedItem = categorySpinner.selectedItem as String
            val taskName = nameEditText.text.toString()
            val taskDesc= descriptionEditText.text.toString()

            //optioonal;
            if (taskName.isEmpty())
            {
                nameEditText.error ="Please enter a value"
                return@setOnClickListener
            }
            if (taskDesc.isEmpty())
            {
                descriptionEditText.error ="Please enter a value"
                return@setOnClickListener
            }
            saveToFirebase(selectedItem,taskName,taskDesc)

        }
        readButton.setOnClickListener {
            val startDate = startDate?:return@setOnClickListener
            val endDate = endDate?:return@setOnClickListener
            fetchEntriesFromFirebase(startDate,endDate)
        }
        picButton.setOnClickListener{
            val intent = Intent(this@Dataset, Camera::class.java)
            startActivity(intent)
        }


    }//on create ends


    private fun showDatePicker(dateSetListener: OnDateSetListener)
    {
        //yy/mm/dd
        val calendar =Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //show up
        val datePickerDialog =DatePickerDialog(this,dateSetListener,year,month,day)
        datePickerDialog.show()

    }

    private fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener)
    {
        val calendar= Calendar.getInstance()
        val hour =calendar.get(Calendar.HOUR_OF_DAY)
        val minute =calendar.get(Calendar.MINUTE)
        val timePickerDialog =TimePickerDialog(this,timeSetListener,hour,minute,true)
        timePickerDialog.show()


    }




    private val startDateListener =

        DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->

            val selectedCalendar = Calendar.getInstance()

            selectedCalendar.set(year, month, day)

            startDate = selectedCalendar.time



            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val selectedDateString = dateFormat.format(startDate!!)

            startDateButton.text = selectedDateString

        }//ends

    private val startTimeListener =

        TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->

            val selectedCalendar = Calendar.getInstance()

            selectedCalendar.time = startDate

            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)

            selectedCalendar.set(Calendar.MINUTE, minute)

            startTime = selectedCalendar.time



            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val selectedTimeString = timeFormat.format(startTime!!)

            startTimeButton.text = selectedTimeString

        }// ends

    private val endDateListener =

        DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->

            val selectedCalendar = Calendar.getInstance()

            selectedCalendar.set(year, month, day)

            endDate = selectedCalendar.time



            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val selectedDateString = dateFormat.format(endDate!!)

            endDateButton.text = selectedDateString

        } //ends



    private val endTimeListener =

        TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->

            val selectedCalendar = Calendar.getInstance()

            selectedCalendar.time = endDate

            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)

            selectedCalendar.set(Calendar.MINUTE, minute)

            endTime = selectedCalendar.time



            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val selectedTimeString = timeFormat.format(endTime!!)

            endTimeButton.text = selectedTimeString

        } //ends
    private fun saveToFirebase(item: String, taskName:String?, taskDesc: String?)
    {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        //for the 2 dates and 2 times
        val startDateString = startDateButton.text.toString()
        val startTimeString = startTimeButton.text.toString()
        val endDateString= endDateButton.text.toString()
        val endTimeString= endTimeButton.text.toString()
        //parsing data
        val startDate = dateFormat.parse(startDateString)
        val startTime = timeFormat.parse(startTimeString)
        val endDate = dateFormat.parse(endDateString)
        val endTime = timeFormat.parse(endTimeString)

        //calcs
        val totalTimeInMillis =
            endDate.time -startDate.time+endTime.time-startTime.time
        val totalMinutes = totalTimeInMillis /(1000*60)
        val totalHours = totalMinutes/60
        val minutesRemaining = totalMinutes % 60

        //format to pass into the db
        val totalTimeString = String.format( Locale.getDefault(),
            "%02d:%02d",totalHours,minutesRemaining)

        //key value to pass into firebase

        val key = database.child("items").push().key
        if (key!= null)
        {
            val task = TaskModel(taskName, taskDesc,startDateString,startTimeString,endDateString,endTimeString,totalTimeString)
            database.child("items").child(key).setValue(task)
                .addOnFailureListener {
                    Toast.makeText(this,"Date  notsaved to fireBase",Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Toast.makeText(this,"Date saved to fireBase",Toast.LENGTH_SHORT).show()
                    readButton.isVisible=true
                }
        }

    }//save to firebase
    private fun fetchEntriesFromFirebase(startDate: Date,endDate: Date)
    {
        val query =database.child("items")
        //  .orderByChild("startDateString")
        //.startAt(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startDate))
        //.endAt(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDate))

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
                Toast.makeText(this@Dataset,"Failed to read entry ",Toast.LENGTH_SHORT ).show()
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


}
data class TaskModel(

    var taskName: String? = null,

    var taskDesc: String? = null,

    var startDateString: String? = null,

    var startTimeString: String? = null,

    var endDateString: String? = null,

    var endTimeString: String? = null,

    var totalTimeString: String? = null

)