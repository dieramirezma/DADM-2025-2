package com.example.reto8.database

import android.content.Context
import com.example.reto8.model.Company
import com.example.reto8.model.CompanyClassification

class CompanyDAO(context: Context) {
    private val dbHelper = CompanyDatabaseHelper(context)

    fun insertCompany(company: Company): Long {
        return dbHelper.insertCompany(company)
    }

    fun getAllCompanies(): List<Company> {
        return dbHelper.getAllCompanies()
    }

    fun getCompanyById(id: Long): Company? {
        return dbHelper.getCompanyById(id)
    }

    fun updateCompany(company: Company): Boolean {
        return dbHelper.updateCompany(company) > 0
    }

    fun deleteCompany(id: Long): Boolean {
        return dbHelper.deleteCompany(id) > 0
    }

    fun searchCompanies(name: String?, classification: CompanyClassification?): List<Company> {
        return dbHelper.searchCompanies(name, classification)
    }
}
