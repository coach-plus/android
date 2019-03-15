package com.mathandoro.coachplus.api.Request;

public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String newPasswordRepeat;

    public UpdatePasswordRequest(String oldPassword, String newPassword, String newPasswordRepeat) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordRepeat = newPasswordRepeat;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordRepeat() {
        return newPasswordRepeat;
    }

    public boolean isValid(){
        return (!this.oldPassword.equals("") && !this.newPassword.equals("") && this.newPassword.equals(this.newPasswordRepeat));
    }
}
