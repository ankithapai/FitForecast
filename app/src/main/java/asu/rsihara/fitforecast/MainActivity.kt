package asu.rsihara.fitforecast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferenceCalories: DatabaseReference

    private val UserNames = listOf("Andrew")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("my_databaseUser")
        databaseReferenceCalories = database.getReference("my_databaseCalories")

        val UsernameET = findViewById<EditText>(R.id.EditText_Username)
        val PasswordET = findViewById<EditText>(R.id.EditText_Password)
        val CaloriesIntakeET = findViewById<EditText>(R.id.EditText_CaloriesIntake)
        val loginButton: Button = findViewById(R.id.MaterialButton_LogIn)
        val SubmitButton: Button = findViewById(R.id.MaterialButton_Submit)

        loginButton.setOnClickListener {
            val UsernameInput = UsernameET.text.toString()
            val PasswordInput = PasswordET.text.toString()
            if (UsernameInput in UserNames && PasswordInput == "admin") {
                // Code to execute when the button is clicked
                val intent = Intent(this, UserInputActivity::class.java)
                intent.putExtra("username", UsernameInput)
                startActivity(intent)
            }else {
                 Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_SHORT).show()
            }
        }

        SubmitButton.setOnClickListener {
            val caloriesIntakeInput = CaloriesIntakeET.text.toString()
            val usernameInput = UsernameET.text.toString()
            if (usernameInput in UserNames) {
                if (caloriesIntakeInput.isNotEmpty()) {
                    // Get the current date in the desired format
                    val currentDate =
                        SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(Date())

                    // Reference to the location in the database with the username as the key
                    val userReference = databaseReferenceCalories.child(usernameInput)

                    // Reference to the location under the username with the current date as the key
                    val dateReference = userReference.child(currentDate)

                    // Save calories value to the database
                    dateReference.setValue(caloriesIntakeInput.toInt())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Calories saved successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Code to redirect to another activity (UserInputActivity in this case)
//                        val intent = Intent(this, UserInputActivity::class.java)
//                        startActivity(intent)
                            } else {
                                Toast.makeText(this, "Failed to save calories", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Enter Calories amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, " User is not registred.", Toast.LENGTH_SHORT).show()
            }
        }

    }

}