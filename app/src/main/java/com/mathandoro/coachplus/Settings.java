package com.mathandoro.coachplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.mathandoro.coachplus.models.getMyUser;
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
    protected String USER = "USER";
    protected String ACTIVE_TEAM_ID = "ACTIVE_TEAM_ID";
    protected String DEVICE_ID = "DEVICE_ID";
    protected String EMAIL_VERIFICATION = "EMAIL_VERIFICATION";

    protected SharedPreferences preferences;

    public Settings(Context context){
        this.context = context;
        Log.d("package-name", context.getPackageName());
        this.preferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void startSession(String token, ReducedUser user, boolean emailConfirmationRequired){
        this.setToken(token);
        this.setMyUser(user);
        this.setEmailVerificationRequired(emailConfirmationRequired);
    }

    public void confirmEmailVerification() {
        this.setEmailVerificationRequired(false);
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

    public void setMyUser(ReducedUser user){
        this.setObject(USER, user);
    }

    public getMyUser getUser(){
        return this.getObject(USER, getMyUser.class);
    }

    public String getToken(){
        return this.getString(TOKEN);
    }

    public boolean isEmailVerificationRequired(){
        return this.getBoolean(EMAIL_VERIFICATION, false);
    }

    public String getDeviceId() {
        return this.getString(DEVICE_ID);
    }

    public void setDeviceId(String deviceId) {
        this.setString(DEVICE_ID, deviceId);
    }

    protected void setEmailVerificationRequired(boolean required){
        this.setBoolean(EMAIL_VERIFICATION, required);
    }

    protected void setBoolean(String name, boolean value){
        this.preferences.edit().putBoolean(name, value).apply();
    }

    protected void setString(String name, String value){
        this.preferences.edit().putString(name, value).apply();
    }

    protected String getString(String name) {
        return preferences.getString(name, null);
    }

    protected boolean getBoolean(String name, boolean defaultValue) {
        return preferences.getBoolean(name, defaultValue);
    }

    private void setToken(String token) {
        this.setString(TOKEN, token);
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
