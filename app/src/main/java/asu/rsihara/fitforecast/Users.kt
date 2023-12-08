package asu.rsihara.fitforecast

import android.util.Log
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Users(
    var username: String = "",
    var age: String = "",
    var height: String = "",
    var weight: String = "",
    var goal_weight: String = "",
    var health: String = "",
    var calories: String = "",
    var exercise: String = "",
    var number_days: String = "",
    var cDiabetes: String = "",
    var cHypertension: String = "",
    var cHeartDisease: String = "",
    var cAsthma: String = "",
    var cObesity: String = "",
    var cArthritis: String = "",
    var cDepression: String = "",
    var cKidneyDisease: String = "",
    var cOsteoporosis: String = "",
    var cMigraine: String = ""
) {
    companion object {
        const val USERS_CHILD = "users"
    }
    class Users()
        // properties and methods
        // Function to map the object to a Map for Firebase
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "age" to age,
            "height" to height,
            "weight" to weight,
            "goal_weight" to goal_weight,
            "health" to health,
            "calories" to calories,
            "exercise" to exercise,
            "number_days" to number_days
        )
    }

}