package asu.rsihara.fitforecast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.abs

class UserInputActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferenceCalories: DatabaseReference
    private lateinit var databaseReferenceCaloriesPredict: DatabaseReference
    private lateinit var editTextUsername: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextGoalWeight: EditText
    private lateinit var editTextGoalDays: EditText
    private lateinit var editTextCaloriesIntake: EditText
    private lateinit var checkBoxHealth: CheckBox
    private lateinit var checkBoxCalorie: CheckBox
    private lateinit var checkBoxExercise: CheckBox
    var actualcalories: Double = 0.0
    var predictedCalories: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_input)

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("my_databaseUser")
        databaseReferenceCalories = database.getReference("my_databaseCalories")
        databaseReferenceCaloriesPredict = database.getReference("calories")

        editTextUsername = findViewById<EditText>(R.id.editText_PersonName)
        editTextAge = findViewById<EditText>(R.id.editText_Age)
        editTextHeight = findViewById<EditText>(R.id.editText_Height)
        editTextWeight = findViewById<EditText>(R.id.editText_Weight)
        editTextGoalWeight = findViewById<EditText>(R.id.editText_GoalWeight)
        editTextGoalDays = findViewById<EditText>(R.id.editText_GoalDays)
        editTextCaloriesIntake = findViewById<EditText>(R.id.editText_CaloriesIntake)

        checkBoxHealth = findViewById<CheckBox>(R.id.CheckBox_Health)
        checkBoxCalorie = findViewById<CheckBox>(R.id.CheckBox_Calorie)
        checkBoxExercise = findViewById<CheckBox>(R.id.CheckBox_Exercise)
        val submitButton: Button = findViewById(R.id.MaterialButton_Submit)
        val submitPredict: Button = findViewById(R.id.MaterialButton_Predict)
        val formButton: Button = findViewById(R.id.MaterialButton_Diseases)
        val username = intent.getStringExtra("username")

        if (username != null) {
            databaseReference.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    if (user != null) {
                        editTextUsername.setText(user.username)
                        editTextAge.setText(user.age)
                        editTextHeight.setText(user.height)
                        editTextWeight.setText(user.weight)
                        editTextGoalWeight.setText(user.goal_weight)
                        editTextGoalDays.setText(user.number_days)
                        checkBoxHealth.isChecked = user.health == "Yes"
                        checkBoxCalorie.isChecked = user.calories == "Yes"
                        checkBoxExercise.isChecked = user.exercise == "Yes"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInputActivity", "Failed to read user data", error.toException())
                }
            })
        }

        submitButton.setOnClickListener {
            saveUserData()
            savecalories()
            }

        formButton.setOnClickListener {
            val intent = Intent(this, Diseases_Activity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        // Set click listener for the "Predict Calories" Button
        submitPredict.setOnClickListener {
            // Retrieve values from the "calories" node
            databaseReferenceCaloriesPredict.addListenerForSingleValueEvent(object : ValueEventListener {
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
                    val currentWeight = editTextWeight.text.toString().toDouble()
                    val goalWeight = editTextGoalWeight.text.toString().toDouble()
                    val days = editTextGoalDays.text.toString().toInt()


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
                    predictedCalories = slope * nextDay + intercept

                    // Output: Predicted calorie value for the (n+1)th day
//                    println("Predicted Calories for Day $nextDay: $predictedCalories")

                    // val predictedCalories = model.predict()
                    //actual calories prediction
                    val weightLossTarget = goalWeight - currentWeight
                    val caloriesToLoseOneKg = 7500
                    val caloriesPerDayForOneKgLoss = caloriesToLoseOneKg / days
                    actualcalories = (weightLossTarget * 7500) / days + 1500 // 1500 for normal daily expenditure
                    println("Actual Calories in raghu page: $actualcalories")
                    println("Predicted Calories in raghu page: $predictedCalories ")

                    // Move the Intent creation and startActivity inside onDataChange
                    val intent = Intent(this@UserInputActivity, CalorieActivity::class.java)
                    intent.putExtra("actualcalories", actualcalories)
                    intent.putExtra("predictedCalories", predictedCalories)
                    startActivity(intent)

                    startActivity(intent)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error, if any
                }
            })
        }

    }

    private fun saveUserData() {

        val username = editTextUsername.text.toString()
        val age = editTextAge.text.toString()
        val height = editTextHeight.text.toString()
        val weight = editTextWeight.text.toString()
        val goalWeight = editTextGoalWeight.text.toString()
        val numberDays = editTextGoalDays.text.toString()

        val healthChecked = checkBoxHealth.isChecked
        val calorieChecked = checkBoxCalorie.isChecked
        val exerciseChecked = checkBoxExercise.isChecked

        if (username.isNotEmpty()) {
            // Create a User object with the retrieved data
            val user = Users(
                username = username,
                age = age,
                height = height,
                weight = weight,
                goal_weight = goalWeight,
                number_days = numberDays,
                health = if (healthChecked) "Yes" else "No",
                calories = if (calorieChecked) "Yes" else "No",
                exercise = if (exerciseChecked) "Yes" else "No"
                )

            // Reference to the location in the database with the username as the key
            val userReference = databaseReference.child(username)

            // Save user values to the database
            userReference.updateChildren(user.toMap()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
//                    finish()
                } else {
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savecalories(){
        val editTextCaloriesIntake = editTextCaloriesIntake.text.toString()
        if (editTextCaloriesIntake.isNotEmpty()) {
            // Append the calories value to the "calories" node in the database
            databaseReferenceCaloriesPredict.push().setValue(editTextCaloriesIntake)
        }
    }

}