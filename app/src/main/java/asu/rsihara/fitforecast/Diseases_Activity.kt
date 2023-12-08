package asu.rsihara.fitforecast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Diseases_Activity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferenceCalories: DatabaseReference

    private lateinit var ratingBarDiabetes: RatingBar
    private lateinit var ratingBarHypertension: RatingBar
    private lateinit var ratingBarHeartDisease: RatingBar
    private lateinit var ratingBarAsthma: RatingBar
    private lateinit var ratingBarObesity: RatingBar
    private lateinit var ratingBarArthritis: RatingBar
    private lateinit var ratingBarDepression: RatingBar
    private lateinit var ratingBarKidneyDisease: RatingBar
    private lateinit var ratingBarOsteoporosis: RatingBar
    private lateinit var ratingBarMigraine: RatingBar
    private lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diseases)

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("my_databaseUser")
        databaseReferenceCalories = database.getReference("my_databaseCalories")

        ratingBarDiabetes = findViewById(R.id.RatingBar_Diabetes)
        ratingBarHypertension = findViewById(R.id.RatingBar_Hypertension)
        ratingBarHeartDisease = findViewById(R.id.RatingBar_Heart_Disease)
        ratingBarAsthma = findViewById(R.id.RatingBar_Asthma)
        ratingBarObesity = findViewById(R.id.RatingBar_Obesity)
        ratingBarArthritis = findViewById(R.id.RatingBar_Arthritis)
        ratingBarDepression = findViewById(R.id.RatingBar_Depression)
        ratingBarKidneyDisease = findViewById(R.id.RatingBar_Kidney_Disease)
        ratingBarOsteoporosis = findViewById(R.id.RatingBar_Osteoporosis)
        ratingBarMigraine = findViewById(R.id.RatingBar_Migraine)
        val submitButton: Button = findViewById(R.id.MaterialButton_Submit)

        username = intent.getStringExtra("username").toString()
        // Check if the username exists in the database
        val userReference = databaseReference.child(username)

        userReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result?.getValue(Users::class.java)
                if (user != null) {
                    ratingBarDiabetes.rating = user.cDiabetes?.toFloat() ?: 0.0f
                    ratingBarHypertension.rating = user.cHypertension?.toFloat() ?: 0.0f
                    ratingBarHeartDisease.rating = user.cHeartDisease?.toFloat() ?: 0.0f
                    ratingBarAsthma.rating = user.cAsthma?.toFloat() ?: 0.0f
                    ratingBarObesity.rating = user.cObesity?.toFloat() ?: 0.0f
                    ratingBarArthritis.rating = user.cArthritis?.toFloat() ?: 0.0f
                    ratingBarDepression.rating = user.cDepression?.toFloat() ?: 0.0f
                    ratingBarKidneyDisease.rating = user.cKidneyDisease?.toFloat() ?: 0.0f
                    ratingBarOsteoporosis.rating = user.cOsteoporosis?.toFloat() ?: 0.0f
                    ratingBarMigraine.rating = user.cMigraine?.toFloat() ?: 0.0f
                }
            }
        }

        submitButton.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        var cDiabetes = ratingBarDiabetes.rating.toString()
        var cHypertension = ratingBarHypertension.rating.toString()
        var cHeartDisease = ratingBarHeartDisease.rating.toString()
        var cAsthma = ratingBarAsthma.rating.toString()
        var cObesity = ratingBarObesity.rating.toString()
        var cArthritis = ratingBarArthritis.rating.toString()
        var cDepression = ratingBarDepression.rating.toString()
        var cKidneyDisease = ratingBarKidneyDisease.rating.toString()
        var cOsteoporosis = ratingBarOsteoporosis.rating.toString()
        var cMigraine = ratingBarMigraine.rating.toString()

        // Create a map to store only the fields you want to update
        val userUpdates = mapOf(
            "cDiabetes" to cDiabetes,
            "cHypertension" to cHypertension,
            "cHeartDisease" to cHeartDisease,
            "cAsthma" to cAsthma,
            "cObesity" to cObesity,
            "cArthritis" to cArthritis,
            "cDepression" to cDepression,
            "cKidneyDisease" to cKidneyDisease,
            "cOsteoporosis" to cOsteoporosis,
            "cMigraine" to cMigraine
        )

        // Reference to the location in the database with the username as the key
        val userReference = databaseReference.child(username)

        // Update only the specified fields in the database
        userReference.updateChildren(userUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
