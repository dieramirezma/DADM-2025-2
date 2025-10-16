package com.example.reto8

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reto8.adapter.CompanyAdapter
import com.example.reto8.database.CompanyDAO
import com.example.reto8.databinding.ActivityMainBinding
import com.example.reto8.model.Company
import com.example.reto8.model.CompanyClassification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var companyDAO: CompanyDAO
    private lateinit var companyAdapter: CompanyAdapter
    private var allCompanies: List<Company> = emptyList()

    private val companyFormLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadCompanies() // Reload the list after adding/editing
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        companyDAO = CompanyDAO(this)

        setupUI()
        setupRecyclerView()
        loadCompanies()
    }

    private fun setupUI() {
        // Setup classification dropdown
        val classifications = listOf("Todas") + CompanyClassification.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classifications)
        binding.actvClassification.setAdapter(adapter)
        binding.actvClassification.threshold = 1 // Show suggestions after typing 1 character
        binding.actvClassification.setOnClickListener {
            binding.actvClassification.showDropDown()
        }

        // Setup click listeners
        binding.fabAddCompany.setOnClickListener { openCompanyForm() }
        binding.btnSearch.setOnClickListener { searchCompanies() }
        binding.btnClearFilters.setOnClickListener { clearFilters() }

        // Setup text watchers for real-time search
        binding.etSearchName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchCompanies()
            }
        })

        binding.actvClassification.setOnItemClickListener { _, _, position, _ ->
            searchCompanies()
        }
    }

    private fun setupRecyclerView() {
        companyAdapter = CompanyAdapter(
            companies = emptyList(),
            onEditClick = { company -> openCompanyForm(company) },
            onDeleteClick = { company -> showDeleteConfirmation(company) }
        )

        binding.rvCompanies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = companyAdapter
        }
    }

    private fun loadCompanies() {
        allCompanies = companyDAO.getAllCompanies()
        updateCompaniesList(allCompanies)
    }

    private fun searchCompanies() {
        val searchName = binding.etSearchName.text.toString().trim()
        val classificationText = binding.actvClassification.text.toString().trim()

        val classification = if (classificationText.isEmpty() || classificationText == "Todas") {
            null
        } else {
            CompanyClassification.values().find { it.displayName == classificationText }
        }

        val filteredCompanies = if (searchName.isEmpty() && classification == null) {
            allCompanies
        } else {
            companyDAO.searchCompanies(
                if (searchName.isEmpty()) null else searchName,
                classification
            )
        }

        updateCompaniesList(filteredCompanies)
    }

    private fun clearFilters() {
        binding.etSearchName.setText("")
        binding.actvClassification.setText("", false)
        updateCompaniesList(allCompanies)
    }

    private fun updateCompaniesList(companies: List<Company>) {
        companyAdapter.updateCompanies(companies)

        // Show/hide empty state
        if (companies.isEmpty()) {
            binding.rvCompanies.visibility = android.view.View.GONE
            binding.llEmptyState.visibility = android.view.View.VISIBLE
        } else {
            binding.rvCompanies.visibility = android.view.View.VISIBLE
            binding.llEmptyState.visibility = android.view.View.GONE
        }
    }

    private fun openCompanyForm(company: Company? = null) {
        val intent = Intent(this, CompanyFormActivity::class.java)
        company?.let {
            intent.putExtra(CompanyFormActivity.EXTRA_COMPANY_ID, it.id)
        }
        companyFormLauncher.launch(intent)
    }

    private fun showDeleteConfirmation(company: Company) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro de que desea eliminar la empresa \"${company.name}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteCompany(company)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteCompany(company: Company) {
        if (companyDAO.deleteCompany(company.id)) {
            Toast.makeText(this, "Empresa eliminada exitosamente", Toast.LENGTH_SHORT).show()
            loadCompanies() // Reload the list
        } else {
            Toast.makeText(this, "Error al eliminar la empresa", Toast.LENGTH_SHORT).show()
        }
    }
}