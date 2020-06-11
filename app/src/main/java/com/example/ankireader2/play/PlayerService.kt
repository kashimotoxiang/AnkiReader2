package com.example.ankireader2.play

import android.content.Context
import com.example.ankireader2.data.AnkiManager
import com.example.ankireader2.model.Note
import com.example.ankireader2.utils.TtsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep


enum class PlayState {
    NORMAL, PLAYING, PAUSED
}

class PlayerService(private val context: Context, val viewCallbacks: (Any, Any, Int, Int) -> Unit) {
    private var currentState: PlayState = PlayState.NORMAL
    private var tts: TtsUtils = TtsUtils.getInstance(context)
    private var ankiManager: AnkiManager = AnkiManager()
    var currentProgress = 0
    private var totalNotes = 0
    private var currentThread: Unit? = null
    var notes = mutableListOf<Note>()

    init {
        val enDeck = ankiManager.mDecks["01日清【英语】::单词"]
        notes = ankiManager.getNotesByDeckId(mutableListOf<Long>(enDeck!!.id))
        require(notes.size != 0) { "牌组识别为空!" }
        totalNotes = notes.size
        notes.sortBy { it.due }
    }

    fun play() {
        currentState = PlayState.PLAYING
        currentThread = Thread(Runnable {
            while (currentState === PlayState.PLAYING) {
                currentProgress %= totalNotes
                playAudio(notes[currentProgress])
                currentProgress += 1
                sleep(200)
            }
        }).start()
    }

    fun pause() {
        currentState = PlayState.PAUSED
        tts.stopAll()
    }

    fun isPlaying(): Boolean {
        return currentState == PlayState.PLAYING
    }

    private fun playAudio(note: Note) {
        val cn = note.fields["中文释义"]!!
        val en = note.fields["英语单词"]!!
        try {
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            coroutineScope.launch {
                viewCallbacks(cn, en, currentProgress, totalNotes)
            }
        } catch (e: Exception) {
            println("error")
        }
        for (j in 1..2) {
            tts.speakCN(cn);
            tts.speakEN(en);
        }
    }
}


