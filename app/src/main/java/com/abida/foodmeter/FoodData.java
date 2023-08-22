package com.abida.foodmeter;

public class FoodData {

    String uid;


    private int caloriesUsed;
    private int caloriesBurned;
    private String userEmail;
    private String foodTime;// Breakfast, Lunch, Dinner

    public FoodData(){}

    public FoodData(String uid, int caloriesUsed, int caloriesBurned, String userEmail, String foodTime) {
        this.uid = uid;
        this.caloriesUsed = caloriesUsed;
        this.caloriesBurned = caloriesBurned;
        this.userEmail = userEmail;
        this.foodTime = foodTime;
    }

    public int getCaloriesUsed() {
        return caloriesUsed;
    }

    public void setCaloriesUsed(int caloriesUsed) {
        this.caloriesUsed = caloriesUsed;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFoodTime() {
        return foodTime;
    }

    public void setFoodTime(String foodTime) {
        this.foodTime = foodTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object object){

        if (object instanceof FoodData){
            FoodData p = (FoodData) object;
            return this.uid.equals(p.getUid());
        }
        else return false;
    }

}
