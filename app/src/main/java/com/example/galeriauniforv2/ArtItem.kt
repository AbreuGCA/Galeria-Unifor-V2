package com.example.galeriauniforv2

data class ArtItem(
    val title: String = "",
    val artist: String = "",
    val creationDate: String = "",
    val description: String = "",
    val imageBase64: String = ""
) {
    var isButtonsVisible: Boolean = false
}