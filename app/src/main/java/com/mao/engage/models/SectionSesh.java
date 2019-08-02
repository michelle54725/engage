/*
    How Engage represents a unique section session.
    Parameters are defined by the teacher before starting a new section except for
    isTakingAttendance, user_ids, and saved_slider_vals, which are dynamic during the session.

    Etymology: "Section" refers to a course discussion section.
 */

package com.mao.engage.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Serializing allows us to pass our custom object (i.e. SectionSesh) in and out of Firebase
public class SectionSesh implements Serializable {

    public String ref_key;
    public int magic_key;
    private String a_start;
    private String b_end;
    private String ta_key;
    private String section_name;

    private boolean isTakingAttendance = false;
    private Map<String, String> user_ids;
    //[Mao: (not using this)] private ArrayList<ArrayList<Integer>> saved_slider_vals;

    public SectionSesh(String a_start, String b_end, String ta_key, String section_name,
                       String ref_key, int magic_key, ArrayList<String> user_ids) {
        // fed by constructor
        this.a_start = a_start;
        this.b_end = b_end;
        this.ta_key = ta_key;
        this.section_name = section_name;
        this.ref_key = ref_key;
        this.magic_key = magic_key;

        // manual initialization
        this.user_ids = new HashMap<>();
        for (int i = 0; i < user_ids.size(); i++) {
            this.user_ids.put("bob", user_ids.get(i));
        }
        //[Mao: (not using this)] this.saved_slider_vals = new ArrayList<>();
    }

    //Serializable needs a void constructor
    public SectionSesh() {
    }

    // getters
    public String getRef_key() { return ref_key; }
    public int getMagic_key() { return magic_key; }
    public String getA_start() { return a_start; }
    public String getB_end() { return b_end; }
    public String getTa_key() { return ta_key; }
    public String getSection_name() { return section_name; }
    public boolean getIsTakingAttendance() { return isTakingAttendance; }
    public Map<String, String> getUser_ids() { return user_ids; }
    //[Mao: (not using this)]public ArrayList<ArrayList<Integer>> getSaved_slider_vals() { return saved_slider_vals; }


    // setters
    public void setRef_key(String ref_key) { this.ref_key = ref_key; }
    public void setMagic_key(int magic_key) { this.magic_key = magic_key; }
    public void setA_start(String a_start) { this.a_start = a_start; }
    public void setB_end(String b_end) { this.b_end = b_end; }
    public void setTa_key(String ta_key) { this.ta_key = ta_key; }
    public void setSection_name(String section_name) { this.section_name = section_name; }
    public void setIsTakingAttendance(boolean takingAttendance) { isTakingAttendance = takingAttendance; }
    public void setUser_ids(Map<String, String> user_ids) { this.user_ids = user_ids; }
    //[Mao: (not using this)]public void setSaved_slider_vals(ArrayList<ArrayList<Integer>> saved_slider_vals) { this.saved_slider_vals = saved_slider_vals; }
}
