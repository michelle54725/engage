package com.mao.engage;

import android.os.Build;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mao.engage.utils.eventlisteners.TeacherChangeEventListener;
import com.mao.engage.utils.eventlisteners.UserCreateEventListener;
import com.mao.engage.utils.eventlisteners.UserSeshChangeListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class FirebaseUtils {
    // Hardcoded instance variables (that should not be hardcoded)
    final static String USER_ID = "user_id_1";
    final static String USERNAME = "Michelle Mao";
    final static String START = "2018-12-31-2000";
    final static String END = "2018-12-31-2200";
    final static String TA_NAME = "John Denero";
    final static String SECTION_ID = "CS70134A";
    final static int MAGICKEY = 420;

    private static DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    private static DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");
    private static DatabaseReference mTeachersRef = FirebaseDatabase.getInstance().getReference("/Teachers");
    private static DatabaseReference mMagicKeysRef = FirebaseDatabase.getInstance().getReference("/MagicKeys");

    //Local variables as copy of Database
    public static HashMap<String, String> allUsers = new HashMap<>(); // K: user_id (device key); V: section_ref_key
    public static HashSet<String> allTeachers = new HashSet<>(); // device keys (DB reference key)
    public static HashMap<String, Integer> sectionSliders = new HashMap<>(); // K: user_id; v: sliderVal;
    public static HashMap<String, Boolean> sectionAttendance = new HashMap<>(); // K: user_id; v: True if present, False if absent;

    public static HashMap<String, String> existingSections = new HashMap<>(); //K: section_name; V: section_ref;
    public static HashMap<String, HashMap>  sectionMap = new HashMap<>(); //K: section ref key; V: new Hashmap of MagicKeys, section_names, sectionSliders2.0
    static int counter = 0; //counter for attendance [not sure if necessary]

    /*
        Removes self (user) from local databases
     */
    public static void removeUser(String ref_key, String userId) {
        Log.d("TEST", "remove USER method in firebase; ref key: " + ref_key + "  user id   " + userId);
        FirebaseDatabase.getInstance().getReference("/Sections").child(ref_key).child("user_ids").child(userId).removeValue();
    }
    /*
        setSectionListener called in StartActivity
        Retrieves section data from Firebase to update a HashMap<String section_ref_key, Hashmap<String x, String y>>
     */

    // Find SectionSesh corresponding to User's MagicWord
    // Add UserID to corresponding Section's user_ids list
    public static void createUser(final UserSesh user) {
        Log.d("TEST", "in findSectionWithUser" + mSectionRef.getKey());
        UserCreateEventListener userCreateEventListener
                = new UserCreateEventListener(user);
        mSectionRef.addListenerForSingleValueEvent(userCreateEventListener);
    }

    public static void updateTeacher(String name, String sectionRefKey, String sectionID) {
        Log.d("TEST", "updating Teacher w device ID " + getPsuedoUniqueID());
        DatabaseReference mRef = mTeachersRef.child(getPsuedoUniqueID());
        mRef.child("name").setValue(name);
        mRef.child("existingSections").child(sectionRefKey).setValue(sectionID);
    }

    // Returns a HashMap of k: user_id, v: user_name for a specific section
    public static HashMap<String, String> getUserNames(String sectionId) {
        HashMap<String, String> listOfUsers = new HashMap<>();
        Map<String, Object> hashyMap = sectionMap.get(sectionId);
        try {
            Map<String, String> usersInSection = (Map) hashyMap.get("user_ids");
        } catch (NullPointerException e) {
            listOfUsers.put("null", "No Students,a");
            return listOfUsers;
        }

        Map<String, String> usersInSection = (Map) hashyMap.get("user_ids");

        for (String key : usersInSection.keySet()) {
            String[] nameAndId = new String[]{usersInSection.get(key), key};
            listOfUsers.put(key, usersInSection.get(key));
            Log.d("TEST", "getUserNames " + usersInSection.get(key) + " " + key);
        }

        return listOfUsers;
    }

    public static void setUserListener() {
        mUsersRef.addChildEventListener(new UserSeshChangeListener());
    }


    public static void setTeacherListener() {
        mTeachersRef.addChildEventListener(new TeacherChangeEventListener());
    }

    public static boolean teacherIsInDB() {
        Log.d("TEST", "in teacherIsInDB method...");
        Log.d("TEST", "teacherIsInDB RESULT: " + allTeachers.contains(getPsuedoUniqueID()));

        return allTeachers.contains(getPsuedoUniqueID());
    }

    public static String getMySection() {
        Log.d("TEST", "getMySection: " + allUsers.get(getPsuedoUniqueID()));
        return allUsers.get(getPsuedoUniqueID());
    }

    /**
     * Return pseudo unique ID
     * @return ID
     */
    public static String getPsuedoUniqueID()
    {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" +
                (Build.BOARD.length() % 10)
                + (Build.BRAND.length() % 10)
                + (Build.CPU_ABI.length() % 10)
                + (Build.DEVICE.length() % 10)
                + (Build.MANUFACTURER.length() % 10)
                + (Build.MODEL.length() % 10)
                + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their phone, there will be a duplicate entry
        String serial = null;
        try
        {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        catch (Exception e)
        {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}