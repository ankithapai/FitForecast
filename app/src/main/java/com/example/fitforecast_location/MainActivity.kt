package com.example.fitforecast_location

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val TAG = "MyActivity"

class MainActivity : ComponentActivity() {
    companion object {
        private const val CALENDAR_PERMISSION_REQUEST_CODE = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gapsButton: Button
    private lateinit var locationTextView: TextView
    private lateinit var button: Button
    data class CalendarEvent(val id: Long, val startTime: Long, val endTime: Long)
    private lateinit var buttonhc1: Button
    private lateinit var buttonhc2: Button
    private lateinit var buttonhc3: Button
    private lateinit var buttona1: Button
    private lateinit var buttona2: Button
    private lateinit var buttona3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext, "x" )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //locationTextView = findViewById(R.id.locationTextView)
        checkLocationPermission()
        //button = findViewById(R.id.button)
        buttonhc1 = findViewById(R.id.buttonhc1)
        buttonhc2 = findViewById(R.id.buttonhc2)
        buttonhc3 = findViewById(R.id.buttonhc3)
        buttona1 = findViewById(R.id.buttona1)
        buttona2 = findViewById(R.id.buttona2)
        buttona3 = findViewById(R.id.buttona3)
        button.setOnClickListener {
            if (checkCalendarPermissions()) {
                val appointmentTime = getAppointmentTime()
                addEventToCalendar(appointmentTime)
            } else {
                requestCalendarPermissions()
            }
        }
        //gapsButton = findViewById(R.id.button4)
        findGapsInCalendar() // Call this method to find gaps and update the button text
        setButtonClickListener(buttonhc1)
        setButtonClickListener(buttonhc2)
        setButtonClickListener(buttonhc3)
        setButtonClickListener(buttona1)
        setButtonClickListener(buttona2)
        setButtonClickListener(buttona3)

    }
    private fun findGapsInCalendar() {
        if (checkCalendarPermissions()) {
            val events = mutableListOf<CalendarEvent>()
            val gaps = mutableListOf<String>()
            // Define the time range for today
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val todayEnd = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val uri: Uri = CalendarContract.Events.CONTENT_URI
            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
            )

            // Filter events to include only those within today's date
            val selection = "(${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTEND} <= ?)"
            val selectionArgs = arrayOf(todayStart.toString(), todayEnd.toString())

            val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)
            cursor?.use {
                while (it.moveToNext()) {
                    val event = CalendarEvent(
                        it.getLong(0),  // Event ID
                        it.getLong(1),  // Start time
                        it.getLong(2)   // End time
                    )
                    events.add(event)
                }
            }

            // Sort events by start time
            events.sortBy { it.startTime }

            val gapsInfo = StringBuilder()
            if (events.isNotEmpty()) {
                val firstEvent = events.first()
                val lastEvent = events.last()

                // Iterate through the events to find gaps
                for (i in 0 until events.size - 1) {
                    val gapStart = events[i].endTime
                    val gapEnd = events[i + 1].startTime

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val gapStartDate = Date(gapStart)
                    val gapEndDate = Date(gapEnd)
                    val formattedGapStart = dateFormat.format(gapStartDate)
                    val formattedGapEnd = dateFormat.format(gapEndDate)


                    // Check if the gap falls between the first and last events
                    if (gapStart < gapEnd && gapStart >= firstEvent.startTime && gapEnd <= lastEvent.endTime) {
                        val gapDuration = gapEnd - gapStart
                        val gapDurationMinutes = gapDuration / (60 * 1000) % 60
                        val gapDurationHours = gapDuration / (60 * 60 * 1000) % 24

                        gapsInfo.forEachIndexed { index, gap ->
                            if (index < 3) {
                                gaps.add(gap.toString())
                            }
                        }
                    }
                }
            }

            runOnUiThread {
                updateGapButtons(gaps)
            }

        } else {
            requestCalendarPermissions()
        }
    }
    private fun setButtonClickListener(button: Button) {
        button.setOnClickListener {
            if (checkCalendarPermissions()) {
                val eventDetails = button.text.toString()
                val appointmentTime = getAppointmentTime()
                addEventToCalendar(appointmentTime, eventDetails)
            } else {
                requestCalendarPermissions()
            }
        }
    }

    private fun addEventToCalendar(appointmentTime: Calendar, eventTitle: String) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, appointmentTime.timeInMillis)
            put(CalendarContract.Events.DTEND, appointmentTime.timeInMillis + 60 * 60 * 1000) // 1 hour duration
            put(CalendarContract.Events.TITLE, eventTitle) // Set event title
            put(CalendarContract.Events.DESCRIPTION, "Event Description: $eventTitle")
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)?.also {
            Toast.makeText(this, "$eventTitle added to calendar", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show()
    }
    private fun updateGapButtons(gaps: List<String>) {
        val buttons = listOf(buttona1, buttona2, buttona3)
        gaps.forEachIndexed { index, gap ->
            if (index < buttons.size) {
                buttons[index].text = gap
            }
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Log.d(TAG, "Inside getLocation")
                findNearesthospital(location)
            } else {
                Log.d(TAG, "Inside getLocation location is null")
                locationTextView.text = "Location is null"
            }
        }
    }

    private fun findNearesthospital(location: Location) {
        Log.d(TAG, "Inside findNearestRestaurants")
        val apiKey = "AIzaSyCJsvNlrpjMuPep1ijjZ6NYyTfquh1w9YU" // Replace with your actual API key
        val radius = 10000// Radius in meters
        val locationStr = "${location.latitude},${location.longitude}"
        val type = "hospital"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationStr&radius=$radius&type=$type&key=$apiKey"

        // Use OkHttp client to make a request
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    locationTextView.text = "Error: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        locationTextView.text = "Response not successful: HTTP ${response.code}"
                    }
                    return
                }

                val responseData = response.body?.string()
                if (responseData == null) {
                    runOnUiThread {
                        locationTextView.text = "No response received from the server"
                    }
                    return
                }

                val jsonObject = JSONObject(responseData)
                val results = jsonObject.getJSONArray("results")

                if (results.length() > 0) {
                    val stringBuilder = StringBuilder()
                    for (i in 0 until 5) {
                        val hospital = results.getJSONObject(i)
                        val name = hospital.getString("name")
                        val address = hospital.getString("vicinity")
                        val hospitalLocation = hospital.getJSONObject("geometry").getJSONObject("location")
                        val hospitalLat = hospitalLocation.getDouble("lat")
                        val hospitalLng = hospitalLocation.getDouble("lng")

                        val distance = calculateDistance(location.latitude, location.longitude, hospitalLat, hospitalLng)
                        val formattedDistance = String.format("%.2f", distance)
                        stringBuilder.append("$name, Address: $address, Distance: $formattedDistance km\n")

                        val providers = mutableListOf<String>()
                        for (i in 0 until minOf(3, results.length())) {
                            val provider = results.getJSONObject(i).getString("name")
                            providers.add(provider)
                        }

                        runOnUiThread {
                            updateProviderButtons(providers)
                        }
                    }
                } else {
                    runOnUiThread {
                        locationTextView.text = "No nearby hospitals found"
                    }
                }
            }
        })
    }
    private fun updateProviderButtons(providers: List<String>) {
        val buttons = listOf(buttonhc1, buttonhc2, buttonhc3)
        providers.forEachIndexed { index, provider ->
            if (index < buttons.size) {
                buttons[index].text = provider
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            Log.d(TAG, "Inside checkLocationPermission")
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocation()
                } else {
                    Log.d(TAG, "Location permission denied")
                    locationTextView.text = "Location permission denied"
                }
            }
            CALENDAR_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Calendar permission granted, you can proceed with accessing the calendar
                    findGapsInCalendar()
                } else {
                    // Calendar permission denied
                    Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getAppointmentTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, 1) // Add 2 weeks
        calendar.set(Calendar.HOUR_OF_DAY, 8) // Set time to 8 AM
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar
    }

    private fun addEventToCalendar(appointmentTime: Calendar) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, appointmentTime.timeInMillis)
            put(CalendarContract.Events.DTEND, appointmentTime.timeInMillis + 60 * 60 * 1000) // 1 hour duration
            put(CalendarContract.Events.TITLE, "Health Check-up")
            put(CalendarContract.Events.DESCRIPTION, "Health Check-up: https://maps.app.goo.gl/KFuLggP2ewh9KefZA")
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)?.also {
            Toast.makeText(this, "Appointment added to calendar", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "Failed to add appointment", Toast.LENGTH_SHORT).show()
    }

    private fun checkCalendarPermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCalendarPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
            CALENDAR_PERMISSION_REQUEST_CODE
        )
    }


}
