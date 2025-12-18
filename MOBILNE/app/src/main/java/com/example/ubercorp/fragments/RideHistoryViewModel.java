package com.example.ubercorp.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RideHistoryViewModel extends ViewModel {

    private final MutableLiveData<String> searchText;

    public RideHistoryViewModel(){
        searchText = new MutableLiveData<>();
    }

    public LiveData<String> getText(){return searchText;}
}
