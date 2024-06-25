package com

import android.app.Person
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {
    private var _searchText = MutableStateFlow("")
    var searchText = _searchText.asStateFlow()
    private var _isSearch = MutableStateFlow(false)
    var isSearch = _isSearch.asStateFlow()

    private val _persons = MutableStateFlow(allPersons)

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    val persons = searchText.combine(_persons) { text, persons ->
        if (text.isBlank()) {
            persons
        } else {
            persons.filter {
                it.doesMatchSearchQuery(text)
            }
        }

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000
        ),
        _persons.value
    )
}

data class Persons(
    var firstName: String,
    var lastName: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombination = listOf(
            "$firstName$lastName",
            "$firstName $lastName",
            "${firstName.first()} ${lastName.first()}"
        )
        return matchingCombination.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allPersons = listOf(
    Persons(
        "Mohammad",
        "taheri"
    ),
    Persons("vahid", "Taheri")
)