package com.ujwal.roomdemo

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ujwal.roomdemo.db.Subscriber
import com.ujwal.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {

    //val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()

    val saveOrUpdateBtn = MutableLiveData<String>()
    val clearAllOrDeleteBtn = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateBtn.value = "Save"
        clearAllOrDeleteBtn.value = "Clear All"
    }

    fun getSavedSubscribers() = liveData {
        repository.subscribers.collect {
            emit(it)
        }
    }

    fun saveOrUpdate() {
        if (inputName.value == null){
            statusMessage.value = Event("Please Enter Subscriber's Name!")
        } else if (inputEmail.value == null){
            statusMessage.value = Event("Please Enter Subscriber's Email!")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please Enter Correct Email Address!")
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    private fun insert(subscriber: Subscriber) = viewModelScope.launch(IO) {
        val newRowId = repository.insert(subscriber)
        withContext(Main) {
            if (newRowId > -1) {
                statusMessage.value = Event("Subscriber inserted successfully! $newRowId")
            } else {
                statusMessage.value = Event("Error occurred!")
            }
        }
    }

    private fun update(subscriber: Subscriber) = viewModelScope.launch(IO) {
        val numberOfRows = repository.update(subscriber)
        withContext(Main) {
            if (numberOfRows > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateBtn.value = "Save"
                clearAllOrDeleteBtn.value = "Clear All"
                statusMessage.value = Event("$numberOfRows Rows Updated Successfully!")
            } else {
                statusMessage.value = Event("Error occurred!")
            }
        }
    }

    private fun delete(subscriber: Subscriber) = viewModelScope.launch(IO) {
        val numberOfRowsDeleted = repository.delete(subscriber)
        withContext(Main) {
            if (numberOfRowsDeleted > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateBtn.value = "Save"
                clearAllOrDeleteBtn.value = "Clear All"
                statusMessage.value = Event("$numberOfRowsDeleted rows deleted successfully!")
            } else {
                statusMessage.value = Event("Error occurred!")
            }
        }
    }

    private fun clearAll() = viewModelScope.launch(IO) {
        val allRowsDeleted = repository.deleteAll()
        withContext(Main) {
            if (allRowsDeleted > 0) {
                statusMessage.value = Event("All $allRowsDeleted Subscribers deleted successfully!")
            } else {
                statusMessage.value = Event("Error occurred!")
            }
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateBtn.value = "Update"
        clearAllOrDeleteBtn.value = "Delete"
    }
}