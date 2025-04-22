package com.example.cashwire.data

import android.content.Context
import android.content.SharedPreferences
import com.example.cashwire.models.User
import com.google.gson.Gson

class UserRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        TransactionRepository.PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        const val KEY_USER = "key_user"
        const val KEY_IS_LOGGED_IN = "key_is_logged_in"

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // Get current user data
    fun getCurrentUser(): User {
        val json = sharedPreferences.getString(KEY_USER, null)
        return if (json != null) {
            try {
                gson.fromJson(json, User::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                createDefaultUser()
            }
        } else {
            createDefaultUser()
        }
    }

    // Save user data
    fun saveUser(user: User) {
        user.updatedAt = System.currentTimeMillis()
        val json = gson.toJson(user)
        sharedPreferences.edit().putString(KEY_USER, json).apply()
    }

    // Update specific user field
    fun updateUserName(name: String) {
        val user = getCurrentUser()
        user.name = name
        saveUser(user)
    }

    fun updateUserEmail(email: String) {
        val user = getCurrentUser()
        user.email = email
        saveUser(user)
    }

    fun updateUserPhone(phone: String?) {
        val user = getCurrentUser()
        user.phoneNumber = phone
        saveUser(user)
    }

    // Authentication methods
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, true)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun logout() {
        // Clear login status but preserve user data
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    // Create a default user if none exists
    private fun createDefaultUser(): User {
        val user = User(
            name = "SakithLiyanage", // Updated user login
            email = "sakith1@gmail.com",
            currency = "LKR"
        )
        saveUser(user)
        return user
    }
}