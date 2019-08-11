package com.mao.engage

class UserConfig {
    enum class UserType {
        STUDENT,
        TEACHER,
        ADMINISTRATOR
    }
    companion object {
        var sectionReferenceKey: String? = null
        var userType: UserType? = UserType.STUDENT
        var username: String? = null
        var userID: String? = null

        fun initialize(
                refKey: String,
                type: UserType,
                name: String,
                uID: String
        ) {
            sectionReferenceKey = refKey;
            userType = type
            username = name
            userID = uID
        }
    }
}