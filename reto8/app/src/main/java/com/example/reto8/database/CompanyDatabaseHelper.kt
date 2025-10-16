package com.example.reto8.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.reto8.model.Company
import com.example.reto8.model.CompanyClassification

class CompanyDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "companies.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_COMPANIES = "companies"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_WEBSITE = "website"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PRODUCTS_SERVICES = "products_services"
        private const val COLUMN_CLASSIFICATION = "classification"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_COMPANIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_WEBSITE TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PRODUCTS_SERVICES TEXT NOT NULL,
                $COLUMN_CLASSIFICATION TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPANIES")
        onCreate(db)
    }

    fun insertCompany(company: Company): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_WEBSITE, company.website)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_PRODUCTS_SERVICES, company.productsServices)
            put(COLUMN_CLASSIFICATION, company.classification.name)
        }

        val id = db.insert(TABLE_COMPANIES, null, values)
        db.close()
        return id
    }

    fun getAllCompanies(): List<Company> {
        val companies = mutableListOf<Company>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_COMPANIES,
            null, null, null, null, null,
            "$COLUMN_NAME ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                companies.add(cursorToCompany(it))
            }
        }

        db.close()
        return companies
    }

    fun getCompanyById(id: Long): Company? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_COMPANIES,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        val company = if (cursor.moveToFirst()) {
            cursorToCompany(cursor)
        } else {
            null
        }

        cursor.close()
        db.close()
        return company
    }

    fun updateCompany(company: Company): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_WEBSITE, company.website)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_PRODUCTS_SERVICES, company.productsServices)
            put(COLUMN_CLASSIFICATION, company.classification.name)
        }

        val result = db.update(
            TABLE_COMPANIES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(company.id.toString())
        )

        db.close()
        return result
    }

    fun deleteCompany(id: Long): Int {
        val db = writableDatabase
        val result = db.delete(
            TABLE_COMPANIES,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return result
    }

    fun searchCompanies(name: String?, classification: CompanyClassification?): List<Company> {
        val companies = mutableListOf<Company>()
        val db = readableDatabase

        val selection = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        if (!name.isNullOrBlank()) {
            selection.add("$COLUMN_NAME LIKE ?")
            selectionArgs.add("%$name%")
        }

        if (classification != null) {
            selection.add("$COLUMN_CLASSIFICATION = ?")
            selectionArgs.add(classification.name)
        }

        val selectionString = if (selection.isNotEmpty()) {
            selection.joinToString(" AND ")
        } else {
            null
        }

        val cursor = db.query(
            TABLE_COMPANIES,
            null,
            selectionString,
            if (selectionArgs.isNotEmpty()) selectionArgs.toTypedArray() else null,
            null, null,
            "$COLUMN_NAME ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                companies.add(cursorToCompany(it))
            }
        }

        db.close()
        return companies
    }

    private fun cursorToCompany(cursor: Cursor): Company {
        return Company(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            website = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEBSITE)),
            phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
            productsServices = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTS_SERVICES)),
            classification = CompanyClassification.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSIFICATION))
            )
        )
    }
}
