package com.example.testfire.model

import java.time.Month
import java.time.MonthDay
import java.time.Year

data class walkNmemo(
    var walk:Int?=null,
    var memo: String? ="",
    val year: Int=0,
    val month: Int=0,
    val monthDay: Int=0

)
