package com.example.ankireader2.utils

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import androidx.annotation.RequiresApi
import java.util.*

class TtsUtils(private val context: Context) {
    private var ttsEN: TextToSpeech? = null
    private var ttsCN: TextToSpeech? = null

    init {
        ttsEN = TextToSpeech(context, OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                ttsEN?.language = Locale.US
                ttsEN?.setPitch(1.0f) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                ttsEN?.setSpeechRate(1.25f)
            }
        })
        ttsCN = TextToSpeech(context, OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                ttsCN?.language = Locale.CHINA
                ttsCN?.setPitch(1.0f) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                ttsCN?.setSpeechRate(3f)
            }
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun speakEN(text: String) {
        if (ttsEN != null) {
            ttsEN!!.speak(text, TextToSpeech.QUEUE_ADD, null, null)
            while (ttsEN!!.isSpeaking) {
                Thread.sleep(100)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speakCN(text: String) {
        if (ttsCN != null) {
            ttsCN!!.speak(text, TextToSpeech.QUEUE_ADD, null, null)
            while (ttsCN!!.isSpeaking) {
                Thread.sleep(100)
            }
        }
    }

    fun stopAll() {
        ttsCN?.stop()
        ttsEN?.stop()
    }

    companion object {
        private var singleton: TtsUtils? = null
        fun getInstance(context: Context): TtsUtils {
            if (singleton == null) {
                synchronized(TtsUtils::class.java) {
                    if (singleton == null) {
                        singleton = TtsUtils(context)
                    }
                }
            }
            return singleton as TtsUtils
        }
    }


}