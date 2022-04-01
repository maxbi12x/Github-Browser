package com.example.githubbrowser.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.githubbrowser.responsemodels.gettingdata

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "RepoDatabase" // Database name
        private const val TABLE_REPO = "RepoTable" // Table Name
        private const val KEY_DATA = "_id"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_REPO_TABLE = ("CREATE TABLE " + TABLE_REPO + "("

                + KEY_DATA + " TEXT PRIMARY KEY)")
        db?.execSQL(CREATE_REPO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_REPO")
        onCreate(db)
    }

    fun addRepo(owner: String, repo: String): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_DATA, "$owner+$repo")
        val result = db.insert(TABLE_REPO, null, contentValues)
        db.close()
        return result
    }


    @SuppressLint("Range")
    fun getRepo(): ArrayList<gettingdata> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val repoList: ArrayList<gettingdata> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_REPO" // Database select query

        val db = this.readableDatabase

        //  try {
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val r = gettingdata(cursor.getString(cursor.getColumnIndex(KEY_DATA)))
                repoList.add(r)
            } while (cursor.moveToNext())
            //  }
            db.close()
            cursor.close()
        }
        return repoList
    }

    fun deleteRepo(owner: String, repo: String, context: Context): Int {
        val db = this.writableDatabase
        val del = "${owner}+${repo}"
        Toast.makeText(context, del, Toast.LENGTH_SHORT).show()
        val ar: Array<String?> = arrayOf(del)
        val result = db.delete("RepoTable ", "_id =?", ar)
        db.close()
        return result

    }
}





