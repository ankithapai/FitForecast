package asu.rsihara.fitforecast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlin.random.Random

class CalorieActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie)
        val myCalorieView = findViewById<TextView>(R.id.calorieIntake)
        val myCarbsView = findViewById<TextView>(R.id.carbs_split_text)
        val myProteinView = findViewById<TextView>(R.id.protein_split_text)
        val myFatView = findViewById<TextView>(R.id.fat_split_text)
//        val exerciseView = findViewById<TextView>(R.id.exerciseSuggestion)
        val myRunView = findViewById<TextView>(R.id.run_split_text)
        val myLiftView = findViewById<TextView>(R.id.lift_split_text)
        val myYogaView = findViewById<TextView>(R.id.yoga_split_text)
        val Pred_Calories = findViewById<TextView>(R.id.Pred_Calories)

        val receivedActualCalories = intent.getDoubleExtra("actualcalories", 0.0)
        val receivedPredictedCalories = intent.getDoubleExtra("predictedCalories", 0.0)

        val actualCalories = receivedActualCalories.toInt().toDouble()
        val predictedCalories = receivedPredictedCalories.toInt().toDouble()

        myCalorieView.text = "Calories to be consumed: $actualCalories"
        Pred_Calories.text = "Predicted Calories: $predictedCalories"

        println("Actual Calories: $actualCalories")
        println("Predicted Calories: $predictedCalories ")

        findViewById<Button>(R.id.button_suggestions).setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.bulk).setOnClickListener {
            val randomCalories = actualCalories
            val calcCarb = ((randomCalories * 0.45) / randomCalories * 100).toInt()
            val calcProtein = ((randomCalories * 0.35) / randomCalories * 100).toInt()
            val calcFat = ((randomCalories * 0.20) / randomCalories * 100).toInt()
            val run = 1
            val lift = 3
            val yoga = 2
            myCalorieView.text = "Calories to be consumed: $randomCalories"
            Pred_Calories.text = "Predicted Calories: $predictedCalories"
            myCarbsView.text = "$calcCarb%"
            myProteinView.text = "$calcProtein%"
            myFatView.text = "$calcFat%"
            myRunView.text = "$run hr"
            myLiftView.text = "$lift hr"
            myYogaView.text = "$yoga hr"

            // Set the progress of each ProgressBar
            findViewById<ProgressBar>(R.id.carb_progress).progress = calcCarb
            findViewById<ProgressBar>(R.id.protein_progress).progress = calcProtein
            findViewById<ProgressBar>(R.id.fat_progress).progress = calcFat

            findViewById<ProgressBar>(R.id.run_progress).progress = run
            findViewById<ProgressBar>(R.id.lift_progress).progress = lift
            findViewById<ProgressBar>(R.id.yoga_progress).progress = yoga
        }

        findViewById<Button>(R.id.lean).setOnClickListener {
            val randomCalories = actualCalories
            val calcCarb = ((randomCalories * 0.35) / randomCalories * 100).toInt()
            val calcProtein = ((randomCalories * 0.35) / randomCalories * 100).toInt()
            val calcFat = ((randomCalories * 0.30) / randomCalories * 100).toInt()
            val run = 2
            val lift = 1
            val yoga = 2
            myCalorieView.text = "Calories to be consumed: $randomCalories"
            Pred_Calories.text = "Predicted Calories: $predictedCalories"
            myCarbsView.text = "$calcCarb%"
            myProteinView.text = "$calcProtein%"
            myFatView.text = "$calcFat%"
//            exerciseView.text = "Weight 0.2 hours, " +
//                    "running 1.0 hours, " +
//                    "yoga 1.0 hours"
            myRunView.text = "$run hr"
            myLiftView.text = "$lift hr"
            myYogaView.text = "$yoga hr"

            // Set the progress of each ProgressBar
            findViewById<ProgressBar>(R.id.carb_progress).progress = calcCarb
            findViewById<ProgressBar>(R.id.protein_progress).progress = calcProtein
            findViewById<ProgressBar>(R.id.fat_progress).progress = calcFat

            findViewById<ProgressBar>(R.id.run_progress).progress = run
            findViewById<ProgressBar>(R.id.lift_progress).progress = lift
            findViewById<ProgressBar>(R.id.yoga_progress).progress = yoga
        }
        findViewById<Button>(R.id.maintain).setOnClickListener {
            val randomCalories = actualCalories
            val calcCarb = ((randomCalories * 0.45) / randomCalories * 100).toInt()
            val calcProtein = ((randomCalories * 0.20) / randomCalories * 100).toInt()
            val calcFat = ((randomCalories * 0.35) / randomCalories * 100).toInt()

            val run = 1
            val lift = 2
            val yoga = 1
            myCalorieView.text = "Calories to be consumed: $randomCalories"
            Pred_Calories.text = "Predicted Calories: $predictedCalories"
            myProteinView.text = "$calcProtein%"
            myFatView.text = "$calcFat%"

            myRunView.text = "$run hr"
            myLiftView.text = "$lift hr"
            myYogaView.text = "$yoga hr"

            // Set the progress of each ProgressBar
            findViewById<ProgressBar>(R.id.carb_progress).progress = calcCarb
            findViewById<ProgressBar>(R.id.protein_progress).progress = calcProtein
            findViewById<ProgressBar>(R.id.fat_progress).progress = calcFat

            findViewById<ProgressBar>(R.id.run_progress).progress = run
            findViewById<ProgressBar>(R.id.lift_progress).progress = lift
            findViewById<ProgressBar>(R.id.yoga_progress).progress = yoga
        }
    }

}