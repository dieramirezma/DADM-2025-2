package com.example.reto8.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reto8.R
import com.example.reto8.model.Company

class CompanyAdapter(
    private var companies: List<Company>,
    private val onEditClick: (Company) -> Unit,
    private val onDeleteClick: (Company) -> Unit
) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCompanyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val tvClassification: TextView = itemView.findViewById(R.id.tvClassification)
        val tvWebsite: TextView = itemView.findViewById(R.id.tvWebsite)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvProductsServices: TextView = itemView.findViewById(R.id.tvProductsServices)
        val btnEdit: View = itemView.findViewById(R.id.btnEdit)
        val btnDelete: View = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companies[position]

        holder.tvCompanyName.text = company.name
        holder.tvClassification.text = company.classification.displayName
        holder.tvWebsite.text = company.website
        holder.tvPhone.text = company.phone
        holder.tvEmail.text = company.email
        holder.tvProductsServices.text = company.productsServices

        // Set click listeners
        holder.btnEdit.setOnClickListener { onEditClick(company) }
        holder.btnDelete.setOnClickListener { onDeleteClick(company) }
    }

    override fun getItemCount(): Int = companies.size

    fun updateCompanies(newCompanies: List<Company>) {
        companies = newCompanies
        notifyDataSetChanged()
    }
}
