package com.mao.engage;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    //Local variables as copy of Database
    static HashMap<String, String> allUsers = new HashMap<>(); // K: user_id (device key); V: section_ref_key
    static HashSet<String> allTeachers = new HashSet<>(); // device keys (DB reference key)
    static HashMap<String, Integer> sectionSliders = new HashMap<>(); // K: user_id; v: slider;
    static HashMap<String, String> existingSections = new HashMap<>(); //K: section_name; V: section_ref;
    static HashMap<String, HashMap> sectionMap = new HashMap<>(); //K: section ref key; V: new Hashmap of MagicKeys, section_names, and what else?
    static HashMap<String, HashMap> userMap = new HashMap<>(); //K : section ref key V: hashmap of ( K: user id ) and (V: name).



    /*
        Section listener called in StartActivity
        Retrieves section data from Firebase to update a HashMap<String section_ref_key, Hashmap<String x, String y>>
     */
    public static void setSectionListener() {
        mSectionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String section_ref_key = dataSnapshot.getKey();
                Log.d("TEST", "[new Section Child]: " + section_ref_key);
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                HashMap<String, String> hashyMap = new HashMap<>();
                for(DataSnapshot child : children) {
                    if (!(child.getKey().equals("user_ids"))) {
                        hashyMap.put(child.getKey(), child.getValue().toString());
                        Log.d("TEST", child.getKey() + " " + child.getValue());
                    } else {
                        setUserIdListener(section_ref_key);
                        Log.d("TEST", "USER IDS key " + child.getKey() + " value " + child.getValue());
                    }
                }
                Log.d("TEST: ", "SECTION ITEMS added");
                sectionMap.put(section_ref_key, hashyMap);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setUserIdListener(String sectionRefKey) {
        final String sectionref = sectionRefKey;
        mSectionRef.child(sectionRefKey).child("user_ids").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();

                HashMap<String, String> hashyMap = new HashMap<>();
                for(DataSnapshot child : users) {
                    hashyMap.put(child.getKey(), child.getValue().toString());
                    Log.d("TEST", "User ids" + child.getKey() + " " + child.getValue());
                }

                userMap.put(sectionref, hashyMap);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //returns arraylist of existing sections for a user
    public static ArrayList<String> getExistingSections() {
        ArrayList<String> existingList = new ArrayList<>();
        for (String key : existingSections.keySet()) {
            existingList.add(key);
        }
        return existingList;
    }

    public static HashMap<String, String> getExistingSectionsHashMap() {
        return existingSections;
    }

    public static double getThreshold(String refKey) {
        Log.d("TEST: ", "getThreshold called" + Double.parseDouble(sectionMap.get(refKey).get("threshold").toString()));
        return Double.parseDouble(sectionMap.get(refKey).get("threshold").toString());
    }

    public static void changeThresholdVal(String refKey, double threshold) {
        //TODO:doesn't update the firebase, just updates local values -- ask if that is that ok?
        Log.d("TEST: ", "changeThresholdVal called");
        sectionMap.get(refKey).put("threshold", threshold);
    }

    public static long getMagicKey(String refKey) {
        String s = sectionMap.get(refKey).get("magic_key").toString();
        Log.d("TEST", s);
        //Long.parseLong(sectionMap.get(refKey).get("magic_key").toString())
        return Long.parseLong(s);
    }

    /*
        Currently when called, has a HashMap get null object reference error even when called on magic_key
     */
    public static String getStartTime(String refKey) {
        String s = sectionMap.get(refKey).get("a_start").toString();
        Log.d("TEST", s);
        return s.substring(s.length() - 7);
    }

    public static String getEndTime(String refKey) {
        String s = sectionMap.get(refKey).get("b_end").toString();
        Log.d("TEST", s);
        return s.substring(s.length() - 7);
    }

    //adds existing section information to hashmap
    public static void setExistingSectionsListener(String userID) {
        Log.d("TEST: ", "setExistingSections Called");
        mTeachersRef.child(userID).child("existingSections").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //SectionSesh section = dataSnapshot.getValue(SectionSesh.class);
                //existingSections.put(section.section_id, section);
                String section_id = dataSnapshot.getKey();
                String section_ref = dataSnapshot.getValue(String.class);
                existingSections.put(section_ref, section_id);
                Log.d("TEST: ", "EXISTING SECTIONS added");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    public static ArrayList<Integer> getSliderVals(String refKey) {
//
//    }

    // Add a section child in SectionSesh
    public static void createSection(SectionSesh section) {
        Log.d("TEST", "in FirebaseUtils.createSection...");
        mSectionRef.child(section.ref_key).setValue(section);
        //sectionsMagicKey.put(section.ref_key, section.magic_key);
        //Log.d("TEST", "sectionsMagicKey key: " + sectionsMagicKey.keySet() + " value: " + sectionsMagicKey.values());
        FirebaseDatabase.getInstance().getReference("/MagicKeys").child("" + section.getMagic_key()).setValue(section.getRef_key());

        // a Listener on a Section's user_ids to maintain local sectionSliders HashMap
        mSectionRef.child(section.ref_key).child("user_ids").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // **REMOVED b/c shouldn't be in createSection. Would not allow us to resume section
                //Log.d("TEST", "LISTENER SAYS copying user to local sectionSliders: " + dataSnapshot.getKey());
                //String user_id = dataSnapshot.getKey();
                //sectionSliders.put(user_id, 50); // default slider = 50
                //setSliderListener(user_id);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Someone changed their name

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TEST", "removing user from local sectionSliders: " + dataSnapshot.getKey());
                sectionSliders.remove(dataSnapshot.getKey());
                String user_id = dataSnapshot.getKey();
                // TODO: stop Listener?
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void createSection(String start, String end, String ta_name, String section_id,
                                     String key, int magic_word) {
    }


    // Find SectionSesh corresponding to User's MagicWord
    // Add UserID to corresponding Section's user_ids list
    public static void createUser(final UserSesh user) {
        Log.d("TEST", "in findSectionWithUser" + mSectionRef.getKey());
        mSectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("TEST", "in OnDataChange w MW: " + String.valueOf(user.getMagic_key()));
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SectionSesh section = snapshot.getValue(SectionSesh.class);
                        if (section.getMagic_key() == user.getMagic_key()) {
                            Log.d("TEST", "\n[FOUND MATCH] " + "\nmagic key: " + section.getMagic_key() + "; \n" + "ref key: " + section.getRef_key());
                            // Reflect change in section_ref_key in both DB and UserSesh object
                            user.setSection_ref_key(section.getRef_key());
                            mUsersRef.child(user.getUser_id()).setValue(user);

                            DatabaseReference userIDref = mSectionRef.child(section.getRef_key()).child("user_ids");
                            Map<String, Object> userUpdates = new HashMap<>();
                            userUpdates.put(user.getUser_id(), user.getUsername());
                            userIDref.updateChildren(userUpdates);
                        }
                    }
                } else {
                    Log.d("TEST-FAIL", "dataSnapshot DNE");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TEST-FAIL", "failed to read value");
            }
        });
    }

    public static void updateTeacher(String name, String sectionRefKey, String sectionID) {
        Log.d("TEST", "updating Teacher w device ID " + getPsuedoUniqueID());
        DatabaseReference mRef = mTeachersRef.child(getPsuedoUniqueID());
        mRef.child("name").setValue(name);
        mRef.child("existingSections").child(sectionRefKey).setValue(sectionID);
    }

    public static void setSliderVal(String user_id, final int value) {
        String key = allUsers.get(user_id);
        Log.d("TEST", "Attempting to write " + value + " to " + user_id + "...");
        if (key != null) {
            mUsersRef.child(user_id).child("slider_val").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TEST", "New slider wrote to DB: " + value);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TEST", "New slider wrote to DB: " + "FAILED");
                        }
                    });
        }
    }

    private static void setSliderListener(final String user_id) {
        // creates Listener for UserSessions's slider_val to update sectionSliders HashMap
        mUsersRef.child(user_id).child("slider_val").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (sectionSliders.containsKey(user_id)) {
                        Log.d("TEST", "\nreading '" + user_id + "'; \n with slider val: " + dataSnapshot.getValue());
                        sectionSliders.put(user_id, Integer.valueOf(dataSnapshot.getValue().toString()));

                        // For testing purposes
                        Log.d("PRINT_TEST", "\n Printing contents of sectionSliders Hashmap...");
                        for (String user : sectionSliders.keySet()) {
                            Integer value = sectionSliders.get(user);
                            Log.d("PRINT_TEST", user + ": " + value.toString());
                        }

                    } else {
                        Log.d("TEST", "ERROR: user_id not found in sectionSlider HashMap");
                    }
                } else {
                    Log.d("TEST", "deleting Listener for " + user_id);
                    mUsersRef.child(user_id).child("slider_val").removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void setUserListener() {
        mUsersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                UserSesh newUser = dataSnapshot.getValue(UserSesh.class);
                Log.d("TEST", "[new User Child] \n" + newUser.getUser_id() + "\n" + newUser.getSection_ref_key());
                allUsers.put(newUser.getUser_id(), newUser.getSection_ref_key());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                UserSesh newUser = dataSnapshot.getValue(UserSesh.class);
                Log.d("TEST", "[deleting User Child] \n" + newUser.getUser_id() + "\n" + newUser.getSection_ref_key());
                allUsers.remove(newUser.getUser_id());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public static void setTeacherListener() {
        mTeachersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String id = dataSnapshot.getKey();
                Log.d("TEST", "[new Teacher Child] \n" + id);
                allTeachers.add(id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getKey();
                Log.d("TEST", "[deleting Teacher Child] \n" + id);
                allTeachers.remove(id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static boolean teacherIsInDB() {
        Log.d("TEST", "in teacherIsInDB method...");
        Log.d("TEST", "teacherIsInDB RESULT: " + allTeachers.contains(getPsuedoUniqueID()));

        return allTeachers.contains(getPsuedoUniqueID());
    }

    public static String getMySection() {
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