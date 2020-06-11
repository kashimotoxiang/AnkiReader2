package com.example.ankireader2

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ankireader2.play.PlayerService
import com.github.florent37.runtimepermission.kotlin.askPermission


class MainActivity : AppCompatActivity() {
   private lateinit var playService: PlayerService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cardFront = findViewById<TextView>(R.id.card_front)
        val cardBack = findViewById<TextView>(R.id.card_back)

        val mSeekBar = findViewById<SeekBar>(R.id.seekBar)
        var seekBarTextView = findViewById<TextView>(R.id.seekBarText)

        askPermission() {
            Toast.makeText(
                this@MainActivity, "权限申请成功",
                Toast.LENGTH_SHORT
            ).show()
        }.onDeclined { e ->
            Toast.makeText(
                this@MainActivity, "权限申请失败，程序无法运行",
                Toast.LENGTH_SHORT
            ).show()
        }

        playService= PlayerService(this) { cardFrontText, cardBackText,currentProgress:Int,barMaximum: Int->
            mSeekBar.progress = currentProgress
            mSeekBar.max = barMaximum
            cardFront.text = cardFrontText.toString();
            cardBack.text = cardBackText.toString();
        }

        mSeekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {

                playService.currentProgress=progress
                seekBarTextView.text =
                    """${Integer.toString(progress)}:${Integer.toString(mSeekBar.max)}"""
                if(playService.notes.isNotEmpty()){
                    cardFront.text = playService.notes[progress].fields["中文释义"]!!.toString();
                    cardBack.text =  playService.notes[progress].fields["中文释义"]!!.toString();
                }


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }

    fun playBtnClick(v: View) {
        if (playService.isPlaying()) {
            // Stop
            playService.pause()
        } else {
            // Start
            playService.play()
        }
    }


}
