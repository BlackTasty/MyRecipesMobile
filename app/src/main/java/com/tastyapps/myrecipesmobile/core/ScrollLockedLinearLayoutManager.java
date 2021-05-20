package com.tastyapps.myrecipesmobile.core;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ScrollLockedLinearLayoutManager extends LinearLayoutManager {
    public ScrollLockedLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
