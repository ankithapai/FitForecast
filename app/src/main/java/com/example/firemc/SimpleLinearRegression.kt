package com.example.firemc
//
//class SimpleLinearRegression {
//    private var slope: Double = 0.0
//    private var intercept: Double = 0.0
//
//    fun train(caloriesConsumed: List<Double>, currentWeight: Double, goalWeight: Double, days: Int) {
//        val x = (1..days).map { it.toDouble() }.toDoubleArray()
//        val y = caloriesConsumed.toDoubleArray()
//
//        val sumX = x.sum()
//        val sumY = y.sum()
//        val sumXY = x.zip(y).map { it.first * it.second }.sum()
//        val sumX2 = x.map { it * it }.sum()
//
//        slope = (days * sumXY - sumX * sumY) / (days * sumX2 - sumX * sumX)
//        intercept = (sumY - slope * sumX) / days
//    }
//
//    fun predict(): Double {
//        return slope + intercept
//    }
//}
