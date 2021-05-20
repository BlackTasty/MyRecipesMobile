package com.tastyapps.myrecipesmobile.core.viewmodel;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class LoginViewModel extends BaseObservable {
    private String address = "127.0.0.1";
    private String username = "";
    private String password = "";
    private boolean connectButtonEnabled = false;

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        updateConnectButtonEnabled();
        notifyPropertyChanged(BR.username);
        Log.d("LoginViewModel", "Username: " + username);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        updateConnectButtonEnabled();
        notifyPropertyChanged(BR.password);
        Log.d("LoginViewModel", "Password: " + password);
    }

    @Bindable
    public boolean isConnectButtonEnabled() {
        return connectButtonEnabled;
    }

    private void updateConnectButtonEnabled() {
        connectButtonEnabled = username.length() > 0 && password.length() > 0;
        Log.d("LoginViewModel", "Button enabled: " + connectButtonEnabled);
        notifyPropertyChanged(BR.connectButtonEnabled);
    }
}
