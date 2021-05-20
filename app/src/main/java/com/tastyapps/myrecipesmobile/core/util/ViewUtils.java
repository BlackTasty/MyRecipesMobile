package com.tastyapps.myrecipesmobile.core.util;

import android.graphics.Rect;
import android.view.View;

public class ViewUtils {
    public static int getVisibilityPercents(View view) {
        final Rect currentViewRect = new Rect();
        int percents = 100;
        int height = (view == null || view.getVisibility() != View.VISIBLE) ? 0 : view.getHeight();
        if (height == 0) {
            return 0;
        }
        view.getLocalVisibleRect(currentViewRect);
        if(viewIsPartiallyHiddenTop(currentViewRect)){
            // view is partially hidden behind the top edge
            percents = (height - currentViewRect.top) * 100 / height;
        } else if(viewIsPartiallyHiddenBottom(currentViewRect, height)){
            percents = currentViewRect.bottom * 100 / height;
        }
        return percents;
    }

    private static boolean viewIsPartiallyHiddenBottom(Rect currentViewRect, int height) {
        return currentViewRect.bottom > 0 && currentViewRect.bottom < height;
    }

    private static boolean viewIsPartiallyHiddenTop(Rect currentViewRect) {
        return currentViewRect.top > 0;
    }
}
