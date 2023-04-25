package com.example.pilltracker

data class Medicine(
    val productNdc: String,
    val genericName: String,
    val brandName: String,
    val activeIngredients: MutableList<ActiveIngredient>,
    val packaging: MutableList<Packaging>,
    val marketingExpirationDate: String
) {
    data class ActiveIngredient(
        val name: String,
        val strength: String
    )

    data class Packaging(
        val packageNdc: String,
        val description: String,
        val marketingStartDate: String,
        val sample: Boolean
    )
}