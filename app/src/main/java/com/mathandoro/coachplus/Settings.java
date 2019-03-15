package com.mathandoro.coachplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.ReducedUser;

/**
 * Created by dominik on 26.03.17.
 */

public class Settings {
    protected Context context;

    public static int TEAM_ICON_SIZE = 128;
    public static int USER_ICON_SIZE = 128;
    public static int TEAM_ICON_LARGE = 512;

    protected String TOKEN = "TOKEN";
    protected String EMAIL = "EMAIL";
    protected String PASSWORD = "PASSWORD";
    protected String FIRSTNAME = "FIRSTNAME";
    protected String LASTNAME = "LASTNAME";
    protected String USER = "USER";
    protected String ACTIVE_TEAM_ID = "ACTIVE_TEAM_ID";

    protected SharedPreferences preferences;

    public Settings(Context context){
        this.context = context;
        Log.d("package-name", context.getPackageName());
        this.preferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
    }

    protected void setString(String name, String value){
        this.preferences.edit().putString(name, value).apply();
    }

    protected String getString(String name){
        return preferences.getString(name, null);
    }

    public String getToken(){
        return this.getString(TOKEN);
    }

    public void setToken(String token) {
        this.setString(TOKEN, token);
    }

    public String getEmail() {
        return this.getString(EMAIL);
    }

    public void setEmail(String email) {
        this.setString(EMAIL, email);
    }

    public String getPassword() {
        return this.getString(PASSWORD);
    }

    public void setPassword(String password) {
        this.setString(PASSWORD, password);
    }

    public void setActiveTeamId(String teamId){
        this.setString(ACTIVE_TEAM_ID, teamId);
    }

    public String getActiveTeamId(){
        return this.getString(ACTIVE_TEAM_ID);
    }

    public void reset() {
        this.preferences.edit().clear().apply();
    }

    public void setUser(ReducedUser user){
        this.setObject(USER, user);
    }

    public MyReducedUser getUser(){
        return this.getObject(USER, MyReducedUser.class);
    }

    private void setObject(String name, Object object){
        Gson gson = new Gson();
        String json = gson.toJson(object);
        this.setString(name, json);
    }

    private <T> T getObject(String name, Class<T> clazz){
        String json = this.getString(name);
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
