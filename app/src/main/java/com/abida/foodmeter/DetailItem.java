package com.abida.foodmeter;

public class DetailItem {

    private double calories;
    private double caloriesBurned;
    private double netCalories;
    private String time;        // Mon, Tue, or Week1, Week2

    public DetailItem(){}

    public DetailItem(double caloriesUsed, double caloriesBurned, double netCalories, String time) {
        this.calories = caloriesUsed;
        this.caloriesBurned = caloriesBurned;
        this.netCalories = netCalories;
        this.time = time;
    }

    public double getCaloriesUsed() {
        return calories;
    }

    public void setCaloriesUsed(double caloriesUsed) {
        this.calories = caloriesUsed;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public double getNetCalories() {
        return netCalories;
    }

    public void setNetCalories(double netCalories) {
        this.netCalories = netCalories;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
