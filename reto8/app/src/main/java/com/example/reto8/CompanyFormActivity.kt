package com.example.reto8

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reto8.database.CompanyDAO
import com.example.reto8.databinding.ActivityCompanyFormBinding
import com.example.reto8.model.Company
import com.example.reto8.model.CompanyClassification

class CompanyFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompanyFormBinding
    private lateinit var companyDAO: CompanyDAO
    private var companyToEdit: Company? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        companyDAO = CompanyDAO(this)

        setupUI()
        loadCompanyData()
    }

    private fun setupUI() {
        // Setup classification dropdown
        val classifications = CompanyClassification.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classifications)
        binding.actvClassification.setAdapter(adapter)
        binding.actvClassification.threshold = 1 // Show suggestions after typing 1 character
        binding.actvClassification.setOnClickListener {
            binding.actvClassification.showDropDown()
        }

        // Setup click listeners
        binding.btnSave.setOnClickListener { saveCompany() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun loadCompanyData() {
        val companyId = intent.getLongExtra(EXTRA_COMPANY_ID, -1L)

        if (companyId != -1L) {
            // Edit mode
            binding.tvFormTitle.text = "Editar Empresa"
            binding.btnSave.text = "Actualizar"

            companyToEdit = companyDAO.getCompanyById(companyId)
            companyToEdit?.let { company ->
                binding.etCompanyName.setText(company.name)
                binding.etWebsite.setText(company.website)
                binding.etPhone.setText(company.phone)
                binding.etEmail.setText(company.email)
                binding.etProductsServices.setText(company.productsServices)
                binding.actvClassification.setText(company.classification.displayName, false)
            }
        }
    }

    private fun saveCompany() {
        if (validateForm()) {
            val company = createCompanyFromForm()

            val result = if (companyToEdit != null) {
                // Update existing company
                val updatedCompany = company.copy(id = companyToEdit!!.id)
                companyDAO.updateCompany(updatedCompany)
            } else {
                // Insert new company
                companyDAO.insertCompany(company) > 0
            }

            if (result) {
                val message = if (companyToEdit != null) "Empresa actualizada exitosamente" else "Empresa agregada exitosamente"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar la empresa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Clear previous errors
        binding.etCompanyName.error = null
        binding.etWebsite.error = null
        binding.etPhone.error = null
        binding.etEmail.error = null
        binding.etProductsServices.error = null
        binding.actvClassification.error = null

        // Validate company name
        if (binding.etCompanyName.text.toString().trim().isEmpty()) {
            binding.etCompanyName.error = "El nombre de la empresa es requerido"
            isValid = false
        }

        // Validate website
        val website = binding.etWebsite.text.toString().trim()
        if (website.isEmpty()) {
            binding.etWebsite.error = "La URL de la página web es requerida"
            isValid = false
        } else if (!isValidUrl(website)) {
            binding.etWebsite.error = "Ingrese una URL válida"
            isValid = false
        }

        // Validate phone
        if (binding.etPhone.text.toString().trim().isEmpty()) {
            binding.etPhone.error = "El teléfono es requerido"
            isValid = false
        }

        // Validate email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.etEmail.error = "El email es requerido"
            isValid = false
        } else if (!isValidEmail(email)) {
            binding.etEmail.error = "Ingrese un email válido"
            isValid = false
        }

        // Validate products and services
        if (binding.etProductsServices.text.toString().trim().isEmpty()) {
            binding.etProductsServices.error = "Los productos y servicios son requeridos"
            isValid = false
        }

        // Validate classification
        val classificationText = binding.actvClassification.text.toString().trim()
        if (classificationText.isEmpty()) {
            binding.actvClassification.error = "La clasificación es requerida"
            isValid = false
        } else if (CompanyClassification.values().none { it.displayName == classificationText }) {
            binding.actvClassification.error = "Seleccione una clasificación válida"
            isValid = false
        }

        return isValid
    }

    private fun createCompanyFromForm(): Company {
        val classificationText = binding.actvClassification.text.toString().trim()
        val classification = CompanyClassification.values().find { it.displayName == classificationText }
            ?: CompanyClassification.CONSULTING

        return Company(
            name = binding.etCompanyName.text.toString().trim(),
            website = binding.etWebsite.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            productsServices = binding.etProductsServices.text.toString().trim(),
            classification = classification
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches() ||
               url.startsWith("http://") ||
               url.startsWith("https://")
    }

    companion object {
        const val EXTRA_COMPANY_ID = "extra_company_id"
    }
}
