package com.example.studytime

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import java.util.Timer
import kotlin.collections.ArrayList

class Timer : AppCompatActivity() {

    private lateinit var tvTime: TextView

    private lateinit var tvLapTime: TextView

    private lateinit var btnStartStop: Button

    private lateinit var btnReset: Button

    private lateinit var btnLap: Button

    private lateinit var chart: LineChart

    private var lapNumber = 1

    private var lapTimes: MutableList<Long> = ArrayList()

    private lateinit var handler: Handler

    private lateinit var runnable: Runnable

    private var isRunning = false
    private   lateinit var imggif: ImageView

    private var startTime: Long = 0

    private var elapsedTime: Long = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        tvTime = findViewById(R.id.tvTime)

        tvLapTime = findViewById(R.id.tvlapTime)

        btnStartStop = findViewById(R.id.btnstartStop)

        btnReset = findViewById(R.id.btnReset)

        btnLap = findViewById(R.id.btnLap)

        chart = findViewById(R.id.chart)

        imggif = findViewById(R.id.imageView5)

        // Set up the chart properties

        chart.description.isEnabled = true

        chart.setDrawGridBackground(true)

        chart.axisLeft.isEnabled = true

        chart.axisRight.isEnabled = true

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM



        handler = Handler()

        runnable = object : Runnable {

            override fun run() {

                elapsedTime = System.currentTimeMillis() - startTime

                tvTime.text = formatTime(elapsedTime)

                handler.postDelayed(this, 100)

            } // run ends

        } // runable ends
    }//on create

    fun startStopWatch(view: View) {

        if (!isRunning) {

            isRunning = true

            startTime = System.currentTimeMillis()

            handler.postDelayed(runnable, 0)

            btnStartStop.text = "STOP"
            tvTime.setTextColor(getColor( R.color.Green))
            imggif.isVisible=true
            btnLap.isVisible=true
            //load gif
            Glide.with(this).load(R.drawable.splash).into(imggif)

            val runsplash = Timer()
            val ShowSplash: TimerTask = object : TimerTask() {
                override fun run() {
                    finish()

                }
            }
            runsplash.schedule(ShowSplash,System.currentTimeMillis())

            lapNumber = 1

            lapTimes.clear()

            tvLapTime.text = ""

        } else {

            stopStopWatch()

        }

    }


    private fun formatTime(elapsedTime: Long): String {

        val hours = (elapsedTime / (1000 * 60 * 60)).toInt()

        val minutes = (elapsedTime / (1000 * 60) % 60).toInt()

        val seconds = (elapsedTime / 1000 % 60).toInt()

        val milliseconds = (elapsedTime % 1000).toInt()

        return String.format(

            Locale.getDefault(),

            "%02d:%02d:%02d:%03d",

            hours, minutes, seconds, milliseconds

        ) // string format ends

    } //method ends

    private fun stopStopWatch() {

        isRunning = false

        handler.removeCallbacks(runnable)

        btnStartStop.text = "START"
        tvTime.setTextColor(getColor( R.color.white))
        imggif.isVisible=false
        btnLap.isVisible=false
        val lapTime = System.currentTimeMillis() - startTime

        lapTimes.add(lapTime)

        val lapTimeString = "Lap: $lapNumber : " + formatTime(lapTime)

        tvLapTime.text = "${tvLapTime.text}\n$lapTimeString"

    }


    fun resetWatch(view: View) {

        isRunning = false

        handler.removeCallbacks(runnable)

        tvTime.text = "00:00:00:00"

        btnStartStop.text = "START"
        tvTime.setTextColor(getColor( R.color.white))
        imggif.isVisible=false
        btnLap.isVisible=false
        lapNumber = 1

        lapTimes.clear()

        tvLapTime.text = ""
        chart.clear()

    } // reset ends

    fun lapButton(view: View) {

        if (isRunning) {

            val lapTime = System.currentTimeMillis() - startTime

            lapTimes.add(lapTime)

            val lapTimeString = "Task  $lapNumber: " + formatTime(lapTime)

            tvLapTime.text = "${tvLapTime.text}\n$lapTimeString"

            lapNumber++

            val entry = Entry(lapNumber.toFloat(), lapTime.toFloat())

            val entries: MutableList<Entry> = ArrayList()

            entries.add(entry)

            val lineDataSet = LineDataSet(entries, "Lap times")

            lineDataSet.setDrawIcons(false)

            lineDataSet.color = Color.RED

            // lineDataSet.circleColor = Color.RED

            lineDataSet.lineWidth = 3f

            lineDataSet.circleRadius = 5f

            lineDataSet.valueTextSize = 9f

            lineDataSet.setDrawValues(false)

            val lineData: LineData? = chart.data

            if (lineData == null) {

                val newLineData = LineData(lineDataSet)

                chart.data = newLineData

            } else {

                lineData.addDataSet(lineDataSet)

                chart.notifyDataSetChanged()

            }

            chart.setVisibleXRangeMaximum(5f)

            chart.moveViewToX((lapNumber - 5).toFloat())

        }


    }
}