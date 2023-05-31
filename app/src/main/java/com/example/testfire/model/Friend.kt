package com.example.testfire.model

data class Friend(
    val email : String? = null,
    val name : String? = null,
    var profileImageUrl : String? = null,
    val uid : String? = null,
    var location: String?=null,
    val dog: Dog? = null
)

data class Dog(
    val dclass: String? = null, //견종
    val dage: String? = null,   // 나이
    val dsex: String? = null,  // 성별
    val dcharacter:String?=null    //성격
)