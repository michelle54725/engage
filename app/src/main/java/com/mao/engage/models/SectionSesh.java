package com.mao.engage.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//Serializable allows object -> string (Firebase will do smth). need getter and setters
public class SectionSesh implements Serializable {
    public String a_start;
    public String b_end;
    public String ta_key;
    public String section_id;
    public String ref_key;
    public int magic_key;
    public double threshold = 5.0;
    public boolean isTakingAttendance = false;

    public double getThreshold() {
        return threshold;
    }

    public boolean isTakingAttendance() {
        return isTakingAttendance;
    }


    public String getA_start() {
        return a_start;
    }

    public String getB_end() {
        return b_end;
    }

    public String getTa_key() {
        return ta_key;
    }

    public String getSection_id() {
        return section_id;
    }

    public String getRef_key() {
        return ref_key;
    }

    public int getMagic_key() {
        return magic_key;
    }

    public Map<String, String> getUser_ids() {
        return user_ids;
    }

    public ArrayList<ArrayList<Integer>> getSaved_slider_vals() {
        return saved_slider_vals;
    }

    Map<String, String> user_ids;
    ArrayList<ArrayList<Integer>> saved_slider_vals;

    public SectionSesh() {
    }


    public SectionSesh(String a_start, String b_end,
                       String ta_key, String section_id,
                       String ref_key, int magic_key,
                       ArrayList<String> user_ids) {
        // fed by constructor
        this.a_start = a_start;
        this.b_end = b_end;
        this.ta_key = ta_key;
        this.section_id = section_id;
        this.ref_key = ref_key;

        this.magic_key = magic_key;
        // manual init
        this.user_ids = new HashMap<>();
        for (int i = 0; i < user_ids.size(); i++)
        {
            this.user_ids.put("bob", user_ids.get(i));
        }
        this.saved_slider_vals = new ArrayList<>();
    }

    public SectionSesh(String a_start, String b_end,
                       String ta_key, String section_id,
                       String ref_key, int magic_key,
                       Map<String, String> user_ids) {
        // fed by constructor
        this.a_start = a_start;
        this.b_end = b_end;
        this.ta_key = ta_key;
        this.section_id = section_id;
        this.ref_key = ref_key;

        this.magic_key = magic_key;
        // manual init
        this.user_ids = user_ids;
        this.saved_slider_vals = new ArrayList<>();
    }

}
