package com.crud.todoapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontManager {

    private static Typeface currentTypeface;
    private static float currentFontSize;
    private static int currentColour;

    public static  void setCurrentFontSize(float textSize) {
        currentFontSize = textSize;
    }

    public static void setCurrentTypeface(Typeface typeface) {
        currentTypeface = typeface;
    }

    public static void setCurrentColour(int color) { currentColour = color;}
    public static void applyFontToView(Context context, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                applyFontToView(context, child);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTypeface(currentTypeface);
        }
    }

    public static void applyTextSizeToView(View view) {

        if (view instanceof TextView) {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, currentFontSize);
        } else if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                final View childView = viewGroup.getChildAt(i);

                applyTextSizeToView(childView);
            }
        }
    }

    public static int getCurrentColour() { return currentColour; }
    public static Typeface getCurrentTypeface() {
        return currentTypeface;
    }

    public static float getCurrentFontSize() {
        return currentFontSize;
    }
}
