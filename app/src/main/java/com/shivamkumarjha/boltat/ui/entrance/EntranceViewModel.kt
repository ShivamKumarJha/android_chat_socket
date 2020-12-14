package com.shivamkumarjha.boltat.ui.entrance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shivamkumarjha.boltat.R

class EntranceViewModel : ViewModel() {
    private val _entranceForm = MutableLiveData<EntranceFormState>()
    val entranceFormState: LiveData<EntranceFormState> = _entranceForm

    fun nameListener(name: String) {
        if (name.isEmpty() || name.length < 3) {
            _entranceForm.value =
                EntranceFormState(nameError = R.string.invalid_username, isDataValid = false)
        } else {
            _entranceForm.value = EntranceFormState(isDataValid = true)
        }
    }
}