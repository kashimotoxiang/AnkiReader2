package com.example.ankireader2.model

import org.json.JSONObject

data class Model(var id: Long, var json: JSONObject) {
    var name: String = json.getString("name")
    var fields=ArrayList<String>()

    init {
        val fieldsJson = json.getJSONArray("flds")
        for (i in 0 until fieldsJson.length()) {
            val item = fieldsJson.getJSONObject(i)
            fields.add(item.getString("name"))
        }
    }

}