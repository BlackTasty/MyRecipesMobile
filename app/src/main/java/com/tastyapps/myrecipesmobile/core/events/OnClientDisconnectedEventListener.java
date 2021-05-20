package com.tastyapps.myrecipesmobile.core.events;

public interface OnClientDisconnectedEventListener {
    void onDisconnected();

    void onFail(Throwable ex);
}
