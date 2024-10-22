package com.example.weatherapp

data class Sys(
    var country: String,
    var id: Int,
    var sunrise: Int,
    var sunset: Int,
    var type: Int
)