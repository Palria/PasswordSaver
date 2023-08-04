package com.palria.passwordsaver;

import java.io.Serializable;
import java.util.ArrayList;

public class PasswordDataModel implements Serializable {
    String passwordId;
    String passwordTitle;
    String passwordDescription;
    String dateAdded;
    boolean isUploadedOnline;
    String password;
    String pin;
    String token;
    String code;
    String phone;
    String email;
    String itemTitles;
    String itemValues;

    public PasswordDataModel(String passwordId, String passwordTitle, String passwordDescription, String dateAdded,
                             boolean isUploadedOnline,String password,String pin,String token,String code,String phone,String email,String itemTitles,String itemValues){
                            this.passwordId = passwordId;
                            this.passwordTitle = passwordTitle;
                            this.passwordDescription = passwordDescription;
                            this.dateAdded = dateAdded;
                            this.isUploadedOnline = isUploadedOnline;
                            this.password = password;
                            this.pin = pin;
                            this.token = token;
                            this.code = code;
                            this.phone = phone;
                            this.email = email;
                            this.itemTitles = itemTitles;
                            this.itemValues = itemValues;
                        }

    public String getPasswordId() {
        return passwordId;
    }

    public String getPasswordTitle() {
        return passwordTitle;
    }

    public String getPasswordDescription() {
        return passwordDescription;
    }
    public String getDateAdded() {
        return dateAdded;
    }

    public boolean isUploadedOnline() {
        return isUploadedOnline;
    }

    public String getPassword() {
        return password;
    }

    public String getPin() {
        return pin;
    }

    public String getToken() {
        return token;
    }

    public String getCode() {
        return code;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getItemTitles() {
        return itemTitles;
    }

    public String getItemValues() {
        return itemValues;
    }
}
