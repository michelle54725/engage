package com.engage.education

import com.engage.education.teacher.findSectionBot
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class Bot {
    companion object {
        lateinit var mUID: String
        lateinit var mUser: UserSesh
        val mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections")
        val mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions")


        @JvmStatic fun createNewUser(magicWord: String?) {
            // create new UserSesh & store in DB
            mUID = generatePsuedoUniqueID()
            val AlphaNumericString = ("abcdefghijklmnopqrstuvxyz")

            // create StringBuffer size of AlphaNumericString
            val sb = StringBuilder(36)
            sb.append("bot-")
            for (i in 0 until 3) {
                val index = (AlphaNumericString.length * Math.random()).toInt()
                sb.append(AlphaNumericString[index])
            }
            val mUsername = sb.toString()
            mUser = UserSesh(mUID, mUsername,
                    Integer.valueOf(magicWord), null)
            // setting the UserConfig variables for global access
            UserConfig.username = mUsername
            UserConfig.userID = mUID
            UserConfig.userType = UserConfig.UserType.STUDENT
            findSectionBot(mUser)


        }


        fun generatePsuedoUniqueID(): String {
            //example: 00000000-5e4c-8274-ffff-ffffef05ac4a
            // chose a Character random from this String
            val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz")

            // create StringBuffer size of AlphaNumericString
            val sb = StringBuilder(36)
            for (i in 0 until 8) {
                val index = (AlphaNumericString.length * Math.random()).toInt()
                sb.append(AlphaNumericString[index])
            }
            sb.append("-")
            for (i in 0 until 4) {
                val index = (AlphaNumericString.length * Math.random()).toInt()
                sb.append(AlphaNumericString[index])
            }
            sb.append("-")
            for (i in 0 until 4) {
                val index = (AlphaNumericString.length * Math.random()).toInt()
                sb.append(AlphaNumericString[index])
            }
            sb.append("-")
            for (i in 0 until 4) {
                val index = (AlphaNumericString.length * Math.random()).toInt()
                sb.append(AlphaNumericString[index])
            }
            sb.append("-")
            for (i in 0 until 12) {
                val index = (AlphaNumericString.length * Math.random()).toInt()
                sb.append(AlphaNumericString[index])
            }
            return sb.toString()
        }
    }
}
