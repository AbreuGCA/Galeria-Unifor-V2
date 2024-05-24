package com.example.galeriauniforv2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import java.io.File
import java.util.Locale

class ArtDetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art_detail)


        // Inicializar TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        // Configurar OnClickListener para o botão de falar
        findViewById<Button>(R.id.buttonSpeak).setOnClickListener {
            speakDescription()
        }

        val title = intent.getStringExtra("TITLE")
        val artist = intent.getStringExtra("ARTIST")
        val creationDate = intent.getStringExtra("CREATION_DATE")
        val description = intent.getStringExtra("DESCRIPTION")
        val imagePath = intent.getStringExtra("IMAGE_PATH")

        findViewById<TextView>(R.id.detailTitle).text = title
        findViewById<TextView>(R.id.detailArtist).text = artist
        findViewById<TextView>(R.id.detailCreationDate).text = creationDate
        findViewById<TextView>(R.id.detailDescription).text = description

        if (imagePath != null) {
            val imgFile = File(imagePath)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                findViewById<ImageView>(R.id.detailImageView).setImageBitmap(myBitmap)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos do TextToSpeech
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Configurar o idioma para Português do Brasil
            val localeBrazil = Locale("pt", "BR")
            val result = textToSpeech.setLanguage(localeBrazil)

            // Verificar se a configuração da linguagem foi bem-sucedida
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "Language not supported")
            }
        } else {
            Log.e("TextToSpeech", "Initialization failed")
        }
    }

    private fun speakDescription() {
        val description = intent.getStringExtra("DESCRIPTION")
        if (!description.isNullOrEmpty()) {
            textToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}


