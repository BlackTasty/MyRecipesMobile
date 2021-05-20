package com.tastyapps.myrecipesmobile.core.events;

public interface OnClientConnectedEventListener {
    void onConnected();

    void onFail(Throwable ex);
}
