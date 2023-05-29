package com.example.testfire

data class Friend(
    val email : String? = null,
    val name : String? = null,
    var profileImageUrl : String? = null,
    val uid : String? = null,
    val dog: Dog? = null
)

data class Dog(
    val dclass: String? = null,
    val dage: String? = null,
    val dweight: String? = null
)