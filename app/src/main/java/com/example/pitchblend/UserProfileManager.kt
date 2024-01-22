package com.example.pitchblend

object UserProfileManager {
    private var userProfile: UserProfile? = null

    fun saveUserProfile(profile: UserProfile) {
        userProfile = profile
    }

    fun getUserProfile(): UserProfile? {
        return userProfile
    }
}