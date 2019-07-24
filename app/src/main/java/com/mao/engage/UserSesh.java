package com.mao.engage;

import java.io.Serializable;

//Serializable allows object -> string (Firebase will do smth). need getter and setters
public class UserSesh implements Serializable {
    private String user_id;
    private String username;
    private int slider_val;
    private int magic_key;
    private String section_ref_key;
    private boolean isPresent;
    private boolean isStudent;

    private static UserSesh instance = null;

    private UserSesh() {}

    public UserSesh(String user_id, String username,
                    int magic_key, String section_ref_key) {
        // fed by constructor
        this.user_id = user_id;
        this.username = username;
        this.magic_key = magic_key;
        this.section_ref_key = section_ref_key;
        // manual init
        int slider_val = 0; // dynamic
    }

    public static UserSesh getInstance() {
        if (instance == null) {
            instance = new UserSesh("", "", -1, null);
        }
        instance.isStudent = true;
        return instance;
    }

    public void save_slider_vals() {
        //TODO: still need this?
    }

    public String getUser_id() {
        return user_id;
    }

    public String getInstanceUser_id() {
        return instance.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
        instance.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getInstanceUsername() {
        return instance.username;
    }

    public void setUsername(String username) {
        this.username = username;
        instance.username = username;
    }

    public int getSlider_val() {
        return slider_val;
    }

    public int getInstanceSlider_val() {
        return instance.slider_val;
    }

    public void setSlider_val(int slider_val) {
        this.slider_val = slider_val;
        instance.slider_val = slider_val;
    }

    public int getMagic_key() {
        return magic_key;
    }

    public int getInstanceMagic_key() {
        return instance.magic_key;
    }

    public void setMagic_key(int magic_key) {
        this.magic_key = magic_key;
        instance.magic_key = magic_key;
    }

    public String getSection_ref_key() {
        return section_ref_key;
    }

    public String getInstanceSection_ref_key() {
        return instance.section_ref_key;
    }

    public void setSection_ref_key(String section_ref_key) {
        this.section_ref_key = section_ref_key;
        instance.section_ref_key = section_ref_key;
    }

    public void setPresent(boolean status) {
        this.isPresent = status;
        instance.isPresent = status;
    }

    public boolean checkAttendanceStatus() {
        return isPresent;
    }

    public void setIsStudent(boolean isStudent) {
        instance.isStudent = isStudent;
    }

    public boolean checkIsStudent() {
        return instance.isStudent;
    }

}