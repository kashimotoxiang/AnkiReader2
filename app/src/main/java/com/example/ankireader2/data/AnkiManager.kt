package com.example.ankireader2.data

import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import com.example.ankireader2.model.Deck
import com.example.ankireader2.model.Model
import com.example.ankireader2.model.Note
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.File

class AnkiManager {
    private val ANKI_HOME_PATH: String = Environment.getExternalStorageDirectory().absolutePath +
            File.separatorChar + "AnkiDroid" + File.separatorChar
    private val ANKI_DB_PATH: String = ANKI_HOME_PATH + "collection.anki2"
    var mDecks = HashMap<String, Deck>()
    var mModels = HashMap<Long, Model>()
    private fun openAnkiDb(): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(ANKI_DB_PATH, null)
    }


    init {
        val db = openAnkiDb()
        val sql = "select * from col"
        val c = db.rawQuery(sql, null)

        while (c.moveToNext()) {
            // decks
            try {
                val decksJson = c.getString(c.getColumnIndex("decks"))
                val jsonObject = JSONObject(decksJson)
                val iter = jsonObject.keys()
                while (iter.hasNext()) {
                    val idStr = iter.next()
                    val name = jsonObject.getJSONObject(idStr).getString("name")
                    val id = idStr.toLong()
                    mDecks[name] = Deck(id, name)
                }

                // models
                val json = c.getString(c.getColumnIndex("models"))
                val modelarray = JSONObject(json)
                val ids: JSONArray = modelarray.names()
                for (i in 0 until ids.length()) {
                    val id: String = ids.getString(i)
                    val o = modelarray.getJSONObject(id)
                    mModels[o.getLong("id")] = Model(o.getLong("id"), o)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        c.close()
        db.close()
    }


    fun getNotesByDeckId(decks: MutableList<Long>): MutableList<Note> {
        val notes: MutableList<Note> = ArrayList()
        val db = openAnkiDb()
        val sb = decks.joinToString(" , ")
        val sql =
            ("""select n.id,n.tags,n.mid,  c.due,c.type, flds from notes as n, cards as c where c.nid=n.id  and c.did in ($sb ) order by c.type desc""")
        val c = db.rawQuery(sql, null)
        while (c.moveToNext()) {
            val id = c.getLong(c.getColumnIndex("id"))
            val tags = c.getString(c.getColumnIndex("tags"))
            var due = c.getLong(c.getColumnIndex("due"))
            if (due<10000000){
                due+=7983021722
            }
            val type = c.getInt(c.getColumnIndex("type"))
            val mid = c.getLong(c.getColumnIndex("mid"))
            val fields = c.getString(c.getColumnIndex("flds")).split("\\x1f".toRegex())
            val fieldsMap = HashMap<String, String>()

            for ((k, v) in mModels[mid]!!.fields.zip(fields)) {
                fieldsMap[k] = Jsoup.parse(v).text()
            }
            notes.add(Note(id, tags, due, type, fieldsMap))
        }
        c.close()
        db.close()
        return notes
    }

}
