package com.mao.engage.callback

import android.util.Log
import java.lang.Exception

import com.mao.engage.teacher.StudentLoginActivity
/**
 * A generic class used to add callback support for Firebase operations.
 * Enforces synchronous and sequential execution of the program upon
 * invoking a Firebase call. Should really only be utilized app
 * cannot continue to function normally without the response from
 * Firebase as the call is naturally asynchronous.
 *
 * Please see [StudentLoginActivity] for a concrete usage of this class.
 */
class CallbackManager<T> {
    /**
     * This method handles when the data has been successfully returned
     * from Firebase. You should override it in the child class of the
     * [CallbackManager] by passing in the callback data coming from
     * Firebase and an action that you desire on the data passed back.
     * @param callbackData the data received from Firebase
     * @param callbackAction the action you will take given [callbackData]
     */
    fun onSuccess(callbackData: T, callbackAction: (input: T) -> Unit) {
        callbackAction(callbackData)
    }

    /**
     * This method handles when some exceptions occur during the fetching
     * of the data from Firebase. You don't have to add any new functionality
     * to this particular method in the child class unless necessary.
     * @param callbackError the exception that occurs when fetching data
     */
    fun onFailure(callbackError: Exception) {
        Log.e("Exception", callbackError.toString());
    }
}