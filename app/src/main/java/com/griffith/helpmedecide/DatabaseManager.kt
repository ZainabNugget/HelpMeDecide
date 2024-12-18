package com.griffith.helpmedecide
/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
//set up the information
const val DATABASE_NAME = "HMD_DATABASE"
const val TABLE_NAME = "USER_LISTS"
const val COLUMN_ID = "id"
const val COLUMN_NAME = "name"
const val ITEMS = "ListItems"
const val TAG = "Tag"
class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //Add a query
        val query = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $ITEMS TEXT NOT NULL,
                $TAG TEXT NOT NULL
            )
        """
        //execute the query
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addList(name : String, items : String, tag : String){
        val db = this.writableDatabase //get the datatbase
        //put all items as a string seperated by a : for simplicity
        val values = ContentValues()
        // on below line we are passing all values
        // along with its key and value pair.
        values.put(COLUMN_NAME, name)
        values.put(ITEMS, items)
        values.put(TAG, tag)
        db.insert(TABLE_NAME, null, values)
        db.close() //close after we're done
    }

    //get all lists based on tag : Wheel or RollDice
    fun getAllLists(tag:String): List<Pair<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_NAME, ITEMS, TAG),
            null,
            null,
            null,
            null,
            null
        )
        //prep the lists variable
        val lists = mutableListOf<Pair<String, String>>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val items = getString(getColumnIndexOrThrow(ITEMS))
                val tagL = getString(getColumnIndexOrThrow(TAG))
                if(tagL.equals(tag)){
                    lists.add(Pair(name, items))
                } else {
                    continue;
                }
            }
            close()
        }
        db.close()
        return lists
    }


}