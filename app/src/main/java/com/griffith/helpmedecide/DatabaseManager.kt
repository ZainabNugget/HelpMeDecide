package com.griffith.helpmedecide

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val DATABASE_NAME = "HMD_DATABASE"
const val TABLE_NAME = "USER_LISTS"
const val COLUMN_ID = "id"
const val COLUMN_NAME = "name"
const val ITEMS = "ListItems"
class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //Add a query
        val query = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $ITEMS TEXT NOT NULL
            )
        """
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addList(name : String, items : String){
        val db = this.writableDatabase //get the datatbase
        //put all items as a string seperated by a :
        //? not sure if it would work
        val values = ContentValues()
        // on below line we are passing all values
        // along with its key and value pair.
        values.put(COLUMN_NAME, name)
        values.put(ITEMS, items)
        db.insert(TABLE_NAME, null, values)
        db.close() //close after we're done
    }
    fun getAllLists(): List<Pair<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_NAME, ITEMS),
            null,
            null,
            null,
            null,
            null
        )
        val lists = mutableListOf<Pair<String, String>>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val items = getString(getColumnIndexOrThrow(ITEMS))
                lists.add(Pair(name, items))
            }
            close()
        }
        db.close()
        return lists
    }


}