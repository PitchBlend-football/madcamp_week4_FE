package com.example.pitchblend

data class UserProfile(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val username: String,
    val phoneNumber: String,
    val nickname: String,
    val selectedTeamId: Int,
    var teamName: String,
    val logoUrl: String
) {
    init {
        // 객체가 생성될 때 초기화 블록에서 로그 출력
        println("UserProfile created - " +
                "AccessToken: $accessToken, " +
                "RefreshToken: $refreshToken, " +
                "Email: $email, " +
                "Username: $username, " +
                "PhoneNumber: $phoneNumber, " +
                "Nickname: $nickname, " +
                "SelectedTeamId: $selectedTeamId, " +
                "LogoUrl: $logoUrl")
    }

    // teamName을 초기화하는 메소드 추가
    fun initializeTeamName(name: String) {
        // 객체 생성 이후에 teamName을 초기화하는 메소드
        println("Initializing TeamName: $name")
        // 다른 초기화 로직을 추가할 수 있음
    }

    fun saveTeamName(name: String) {
        teamName = name
        // 저장 로직 추가
    }
}