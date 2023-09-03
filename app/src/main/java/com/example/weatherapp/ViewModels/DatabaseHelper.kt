package com.example.weatherapp.ViewModels
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "city_database.db"

        private const val TABLE_NAME = "city_table"
        private const val COLUMN_CITY_NAME = "city_name"
        private const val COLUMN_CITY_CODE = "city_code"

        private const val TABLE_SETTINGS = "settings_table"
        private const val COLUMN_CURRENT_CITY = "current_city"
        private const val COLUMN_SELECTED_LANGUAGE = "selected_language"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createCityTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_CITY_NAME TEXT PRIMARY KEY, $COLUMN_CITY_CODE INTEGER)"
        val createSettingsTableQuery = "CREATE TABLE $TABLE_SETTINGS ($COLUMN_CURRENT_CITY TEXT, $COLUMN_SELECTED_LANGUAGE TEXT)"

        db?.execSQL(createCityTableQuery)
        db?.execSQL(createSettingsTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    // 添加城市和代码到数据库
    fun addCity(cityName: String, cityCode: Int) {
        val values = ContentValues()
        values.put(COLUMN_CITY_NAME, cityName)
        values.put(COLUMN_CITY_CODE, cityCode)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // 获取所有城市和代码的哈希表
    fun getAllCities(): HashMap<String, Int> {
        val citiesMap = HashMap<String, Int>()
        val query = "SELECT * FROM $TABLE_NAME"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val cityIndex = cursor.getColumnIndex(COLUMN_CITY_NAME)
                val codeIndex = cursor.getColumnIndex(COLUMN_CITY_CODE)

                if (cityIndex >= 0 && codeIndex >= 0){
                    val cityName = cursor.getString(cityIndex)
                    val cityCode = cursor.getInt(codeIndex)

                    citiesMap[cityName] = cityCode
                }

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return citiesMap
    }

    // 设置当前城市
    fun setCurrentCity(cityName: String) {
        /*
        val values = ContentValues()
        values.put(COLUMN_CURRENT_CITY, cityName)

        val db = this.writableDatabase
        db.delete(TABLE_SETTINGS, null, null)
        db.insert(TABLE_SETTINGS, null, values)
        db.close()*/
        val values = ContentValues()
        values.put(COLUMN_CURRENT_CITY, cityName)

        val db = this.writableDatabase
        val existingRecords = db.query(TABLE_SETTINGS, null, null, null, null, null, null)

        if (existingRecords.moveToFirst()) {
            db.update(TABLE_SETTINGS, values, null, null)
        } else {
            db.insert(TABLE_SETTINGS, null, values)
        }

        existingRecords.close()
        db.close()
    }

    // 获取当前城市
    fun getCurrentCity(): String? {
        var currentCity: String? = null
        val query = "SELECT * FROM $TABLE_SETTINGS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            //currentCity = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENT_CITY))
            val currentCityIndex = cursor.getColumnIndex(COLUMN_CURRENT_CITY)
            if (currentCityIndex >= 0) {
                currentCity = cursor.getString(currentCityIndex)
            }
        }

        cursor.close()
        db.close()

        return currentCity
    }

    // 设置选择的语言
    fun setSelectedLanguage(language: String) {
        /*val values = ContentValues()
        values.put(COLUMN_SELECTED_LANGUAGE, language)

        val db = this.writableDatabase
        db.delete(TABLE_SETTINGS, null, null)
        db.insert(TABLE_SETTINGS, null, values)
        db.close()*/
        val values = ContentValues()
        values.put(COLUMN_SELECTED_LANGUAGE, language)

        val db = this.writableDatabase
        val existingRecords = db.query(TABLE_SETTINGS, null, null, null, null, null, null)

        if (existingRecords.moveToFirst()) {
            db.update(TABLE_SETTINGS, values, null, null)
        } else {
            db.insert(TABLE_SETTINGS, null, values)
        }

        existingRecords.close()
        db.close()
    }

    // 获取选择的语言
    fun getSelectedLanguage(): String? {
        var selectedLanguage: String? = null
        val query = "SELECT * FROM $TABLE_SETTINGS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val selectedLanguageIndex = cursor.getColumnIndex(COLUMN_SELECTED_LANGUAGE)
            if (selectedLanguageIndex >= 0) {
                selectedLanguage = cursor.getString(selectedLanguageIndex)
            }
        }

        cursor.close()
        db.close()

        return selectedLanguage
    }
}
