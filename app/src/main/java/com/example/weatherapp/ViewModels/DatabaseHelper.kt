package com.example.weatherapp.ViewModels
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "mydatabase.db"
        private const val TABLE_NAME = "settings"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CURRENT_CITY = "current_city"
        private const val COLUMN_SELECTED_LANGUAGE = "selected_language"
        private const val COLUMN_CITY_NAME = "city_name"
        private const val COLUMN_CITY_CODE = "city_code"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CURRENT_CITY TEXT, $COLUMN_SELECTED_LANGUAGE TEXT, $COLUMN_CITY_NAME TEXT, $COLUMN_CITY_CODE INTEGER)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTable)
        onCreate(db)
    }
    fun getCurrentCity(): String? {
        val query = "SELECT $COLUMN_CURRENT_CITY FROM $TABLE_NAME WHERE $COLUMN_ID = 1"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        var currentCity: String? = null
        if (cursor.moveToFirst()) {
            //currentCity = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENT_CITY))
            val columnIndex = cursor.getColumnIndex(COLUMN_CURRENT_CITY)
            if (columnIndex >= 0) {
                currentCity = cursor.getString(columnIndex)
            }
        }
        cursor.close()
        //db.close()
        return currentCity
    }
    fun setCurrentCity(city: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CURRENT_CITY, city)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf("1"))
        //db.close()
    }
    fun getSelectedLanguage(): String? {
        val query = "SELECT $COLUMN_SELECTED_LANGUAGE FROM $TABLE_NAME WHERE $COLUMN_ID = 1"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        var selectedLanguage: String? = null
        if (cursor.moveToFirst()) {
            //selectedLanguage = cursor.getString(cursor.getColumnIndex(COLUMN_SELECTED_LANGUAGE))
            val columnIndex = cursor.getColumnIndex(COLUMN_SELECTED_LANGUAGE)
            if (columnIndex >= 0) {
                selectedLanguage = cursor.getString(columnIndex)
            }
        }
        cursor.close()
        //db.close()
        return selectedLanguage
    }
    fun setSelectedLanguage(language: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SELECTED_LANGUAGE, language)
        }
        //db.update(TABLE_NAME, values, "$COLUMN_ID = 1", null)
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf("1"))
        //db.close()
    }
    fun getCityCodes(): HashMap<String, Int> {
        val query = "SELECT $COLUMN_CITY_NAME, $COLUMN_CITY_CODE FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        val cityCodes = HashMap<String, Int>()
        while (cursor.moveToNext()) {
            val columnIndexCityName = cursor.getColumnIndex(COLUMN_CITY_NAME)
            val columnIndexCityCode = cursor.getColumnIndex(COLUMN_CITY_CODE)
            if (columnIndexCityName >= 0 && columnIndexCityCode >= 0) {
                val cityName = cursor.getString(columnIndexCityName)
                val cityCode = cursor.getInt(columnIndexCityCode)
                cityCodes[cityName] = cityCode
            }
        }
        cursor.close()
        //db.close()
        return cityCodes
    }
    fun setCityCode(cityName: String, cityCode: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CITY_NAME, cityName)
            put(COLUMN_CITY_CODE, cityCode)
        }
        db.insert(TABLE_NAME, null, values)
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf("1"))
        //db.close()
    }
}
