package com.jm.scancodedemo.utlis

import android.content.Context
import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object  JsonUtils {
    fun getJson(fileName: String, context: Context): String {
        val stringBuilder = StringBuilder()
        try {
            val assetManager: AssetManager = context.assets
            val bf = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName), "UTF-8"
                )
            )
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

}