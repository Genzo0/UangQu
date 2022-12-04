package com.example.contohtugasakhir

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText

class AddViewModel : ViewModel() {

    private var labelIncome= ""
    private var labelOutcome = ""
    private var amountIncome = ""
    private var amountOutcome = ""
    private var descriptionIncome = ""
    private var descriptionOutcome = ""
    private var dateIncome = ""
    private var dateOutcome = ""

    fun setLabelIncome(label : String){
        labelIncome = label
    }

    fun getLabelIncome() : String {
        return labelIncome
    }

    fun setLabelOutcome(label : String){
        labelOutcome = label
    }

    fun getLabelOutcome(): String{
        return labelOutcome
    }

    fun setAmountIncome(amount : String){
        amountIncome = amount
    }

    fun getAmountIncome(): String{
        return amountIncome
    }

    fun setAmountOutcome(amount : String){
        amountOutcome = amount
    }

    fun getAmountOutcome(): String{
        return amountOutcome
    }

    fun setDescriptionIncome(description : String){
        descriptionIncome = description
    }

    fun getDescriptionIncome(): String{
        return descriptionIncome
    }

    fun setDescriptionOutcome(description : String){
        descriptionOutcome = description
    }

    fun getDescriptionOutcome(): String{
        return descriptionOutcome
    }
}