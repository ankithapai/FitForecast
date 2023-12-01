package com.example.firemc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*

//
//class MainActivity : AppCompatActivity() {
//    private lateinit var database: FirebaseDatabase
//    private lateinit var myRef: DatabaseReference
//    private lateinit var textView: EditText
//    private lateinit var button: Button
//    private lateinit var predictButton: Button
//    private lateinit var resultTextView: TextView
//    private lateinit var currentWeightEditText: EditText
//    private lateinit var goalWeightEditText: EditText
//    private lateinit var daysEditText: EditText
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        database = FirebaseDatabase.getInstance()
//        myRef = database.getReference("calories")
//
//        // Find TextView and Buttons by their IDs
//        textView = findViewById<EditText>(R.id.textedit)
//        button = findViewById(R.id.button)
//        predictButton = findViewById(R.id.predictButton)
//        resultTextView = findViewById(R.id.resultTextView)
//        currentWeightEditText = findViewById(R.id.currentWeightEditText)
//        goalWeightEditText = findViewById(R.id.goalWeightEditText)
//        daysEditText = findViewById(R.id.daysEditText)
//
//
//        // Set click listener for the "Enter Calories" Button
//        button.setOnClickListener {
//            // Logic for what happens when the "Enter Calories" button is clicked
//            val enteredCalories = textView.text.toString()
//            if (enteredCalories.isNotEmpty()) {
//                // Append the calories value to the "calories" node in the database
//                myRef.push().setValue(enteredCalories)
//
//                // Print entered calories to the console (you can remove this if not needed)
//                println("Entered Calories: $enteredCalories")
//            }
//        }
//
//        // Set click listener for the "Predict Calories" Button
//        predictButton.setOnClickListener {
//            val enteredCalories = textView.text.toString()
//            if (enteredCalories.isNotEmpty()) {
//                //val caloriesConsumed = enteredCalories.split(",").map { it.trim().toDouble() }
//                val caloriesConsumed = listOf(1200.0, 1300.0, 1400.0, 1500.0, 1600.0, 1700.0, 1800.0, 1900.0, 2000.0, 2100.0, 2200.0, 2300.0, 2400.0, 2500.0, 2600.0, 2700.0, 2800.0, 2900.0, 3000.0, 3100.0,7000.0)
//
//                //val caloriesConsumed = defaultCalorieValues
//
//                val currentWeight = currentWeightEditText.text.toString().toDouble()
//                val goalWeight = goalWeightEditText.text.toString().toDouble()
//                val days = daysEditText.text.toString().toInt()
//
//                val model = SimpleLinearRegression()
//                model.train(caloriesConsumed, currentWeight, goalWeight, days)
//
//                val predictedCalories = model.predict()
//
//                // Display the predicted calories in a TextView
//                resultTextView.text = "Predicted Calories: $predictedCalories"
//            }
//        }
//    }
//}
class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var textView: EditText
    private lateinit var button: Button
    private lateinit var predictButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var currentWeightEditText: EditText
    private lateinit var goalWeightEditText: EditText
    private lateinit var daysEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("calories")

        // Find TextView and Buttons by their IDs
        textView = findViewById<EditText>(R.id.textedit)
        button = findViewById(R.id.button)
        predictButton = findViewById(R.id.predictButton)
        resultTextView = findViewById(R.id.resultTextView)
        currentWeightEditText = findViewById(R.id.currentWeightEditText)
        goalWeightEditText = findViewById(R.id.goalWeightEditText)
        daysEditText = findViewById(R.id.daysEditText)

        // Set click listener for the "Enter Calories" Button
        button.setOnClickListener {
            // Logic for what happens when the "Enter Calories" button is clicked
            val enteredCalories = textView.text.toString()
            if (enteredCalories.isNotEmpty()) {
                // Append the calories value to the "calories" node in the database
                myRef.push().setValue(enteredCalories)

                // Print entered calories to the console (you can remove this if not needed)
                println("Entered Calories: $enteredCalories")
            }
        }

        // Set click listener for the "Predict Calories" Button
        predictButton.setOnClickListener {
            // Retrieve values from the "calories" node
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val caloriesConsumed = mutableListOf<Double>()

                    for (childSnapshot in dataSnapshot.children) {
                        val calorieValueString = childSnapshot.getValue(String::class.java)

                        try {
                            val calorieValue = calorieValueString?.toDouble()
                            calorieValue?.let {
                                caloriesConsumed.add(it)
                            }
                        } catch (e: NumberFormatException) {
                            // Handle the case where the value cannot be converted to a double
                            // You may want to log an error or take appropriate action
                            e.printStackTrace()
                        }
                    }

                    // Continue with the rest of the code
                    val currentWeight = currentWeightEditText.text.toString().toDouble()
                    val goalWeight = goalWeightEditText.text.toString().toDouble()
                    val days = daysEditText.text.toString().toInt()

                    //val model = SimpleLinearRegression()
                    //model.train(caloriesConsumed, currentWeight, goalWeight, days)
                    // Input: List of calorie values for n days
                    //val caloriesConsumed = listOf(1000.0, 2000.0, 3000.0 /*... add more values as needed */)

                    // Calculate the linear regression parameters
                    val n = caloriesConsumed.size
                    val sumX = (1..n).sum().toDouble()
                    val sumY = caloriesConsumed.sum()
                    val sumXY = (1..n).zip(caloriesConsumed).sumBy { (x, y) -> (x * y).toInt() } // Convert x to Double
                    val sumXSquare = (1..n).sumBy { it * it }.toDouble()

                    val slope = (n * sumXY - sumX * sumY) / (n * sumXSquare - sumX * sumX)
                    val intercept = (sumY - slope * sumX) / n

                    // Predict calorie value for the (n+1)th day
                    val nextDay = n + 1
                    val predictedCalories = slope * nextDay + intercept

                    // Output: Predicted calorie value for the (n+1)th day
                    println("Predicted Calories for Day $nextDay: $predictedCalories")

                   // val predictedCalories = model.predict()
                    //actual calories prediction
                    val weightLossTarget = currentWeight - goalWeight
                    val caloriesToLoseOneKg = 7500
                    val caloriesPerDayForOneKgLoss = caloriesToLoseOneKg / days
                    val actualcalories = (weightLossTarget * 7500) / days - 1500 // 1500 for normal daily expenditure
                    println("Actual Calories: $actualcalories")

                    // Display the predicted calories in a TextView
                    resultTextView.text = "Predicted Calories: $predictedCalories, Actual Calories: $actualcalories"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error, if any
                }
            })
        }
    }
}