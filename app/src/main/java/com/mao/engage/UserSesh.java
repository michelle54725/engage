package com.mao.engage;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;

//Serializable allows object -> string (Firebase will do smth). need getter and setters
public class UserSesh implements Serializable {
    String user_id;
    String username;
    int slider_val;
    int magic_key;
    String section_ref_key;

    public UserSesh() {
    }

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

    public void save_slider_vals() {
        //TODO: still need this?
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSlider_val() {
        return slider_val;
    }

    public void setSlider_val(int slider_val) {
        this.slider_val = slider_val;
    }

    public int getMagic_key() {
        return magic_key;
    }

    public void setMagic_key(int magic_key) {
        this.magic_key = magic_key;
    }

    public String getSection_ref_key() {
        return section_ref_key;
    }

    public void setSection_ref_key(String section_ref_key) {
        this.section_ref_key = section_ref_key;
    }
}