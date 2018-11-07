package com.mao.engage;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

//Serializable allows object -> string (Firebase will do smth). need getter and setters
public class SectionSesh implements Serializable {
    String a_start;
    String b_end;
    String ta_name;
    String section_id;
    String ref_key;
    int magic_key;
    ArrayList<String> user_ids;
    ArrayList<ArrayList<Integer>> saved_slider_vals;

    public SectionSesh() {
    }


    public SectionSesh(String a_start, String b_end,
                       String ta_name, String section_id,
                       String ref_key, int magic_key,
                       ArrayList<String> user_ids) {
        // fed by constructor
        this.a_start = a_start;
        this.b_end = b_end;
        this.ta_name = ta_name;
        this.section_id = section_id;
        this.ref_key = ref_key;

        this.magic_key = magic_key;
        // manual init
        this.user_ids = user_ids;
        this.saved_slider_vals = new ArrayList<>();
    }

    public String getref_key() {
        return ref_key;
    }

    public void setref_key(String ref_key) {
        this.ref_key = ref_key;
    }

    public String geta_start() {
        return a_start;
    }

    public void seta_start(String a_start) {
        a_start = a_start;
    }

    public String getb_end() {
        return b_end;
    }

    public void setb_end(String b_end) {
        b_end = b_end;
    }

    public String getta_name() {
        return ta_name;
    }

    public void setta_name(String ta_name) {
        this.ta_name = ta_name;
    }

    public String getsection_id() {
        return section_id;
    }

    public void setsection_id(String section_id) {
        this.section_id = section_id;
    }

//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }

    public int getmagic_key() {
        return magic_key;
    }

    public void setmagic_key(int magic_key) {
        this.magic_key = magic_key;
    }

    public ArrayList<String> getuser_ids() {
        return user_ids;
    }

    public void setuser_ids(ArrayList<String> user_ids) {
        this.user_ids = user_ids;
    }

    public ArrayList<ArrayList<Integer>> getsaved_slider_vals() {
        return saved_slider_vals;
    }

    public void setsaved_slider_vals(ArrayList<ArrayList<Integer>> saved_slider_vals) {
        this.saved_slider_vals = saved_slider_vals;
    }
}
