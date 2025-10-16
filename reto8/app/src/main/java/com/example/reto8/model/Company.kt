package com.example.reto8.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Company(
    val id: Long = 0,
    val name: String,
    val website: String,
    val phone: String,
    val email: String,
    val productsServices: String,
    val classification: CompanyClassification
) : Parcelable

enum class CompanyClassification(val displayName: String) {
    CONSULTING("Consultoría"),
    CUSTOM_DEVELOPMENT("Desarrollo a la medida"),
    SOFTWARE_FACTORY("Fábrica de software"),
    CONSULTING_CUSTOM("Consultoría y Desarrollo a la medida"),
    CONSULTING_FACTORY("Consultoría y Fábrica de software"),
    CUSTOM_FACTORY("Desarrollo a la medida y Fábrica de software"),
    ALL("Consultoría, Desarrollo a la medida y Fábrica de software")
}
