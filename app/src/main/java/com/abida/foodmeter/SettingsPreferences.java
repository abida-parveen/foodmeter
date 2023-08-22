package com.abida.foodmeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsPreferences {

    private Context context;

    private static final String LOGIN_REPORT = "login_report";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_NAME = "user_name";
    private static double breakfast = 0.0;
    private static double lunch = 0.0;
    private static double dinner = 0.0;
    private static double burned = 0.0;


    public SettingsPreferences(Context context){
        this.context= context;
    }

    public void setBreakfast(double meal) {
        breakfast = meal;
    }
    public void setLunch(double meal) {
        lunch = meal;
    }
    public void setDinner(double meal) {
        dinner = meal;
    }
    public void setBurnedCal(double meal) {
        burned = meal;
    }

    public double getTotalCount(){
        return (breakfast+lunch+dinner);
    }

    public double getBurned(){
        return burned;
    }

    public double getNetCount(){
        double result = getTotalCount()-getBurned();
        return result;
    }

    public void setLoggedIn( boolean isLoggedIn) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(LOGIN_REPORT, isLoggedIn);
        prefEditor.apply();
    }public boolean isUserLoggedIn() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(LOGIN_REPORT,false);
    }


    public void setUserEmail(String userEmail) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putString(USER_EMAIL, userEmail);
        prefEditor.apply();
    }public String getUserEmail() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(USER_EMAIL,"null");
    }


    public void setUserName(String userName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putString(USER_NAME, userName);
        prefEditor.apply();
    }public String getUserName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(USER_NAME,"null");
    }
}
