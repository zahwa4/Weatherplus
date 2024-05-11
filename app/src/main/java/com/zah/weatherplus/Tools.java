package com.zah.weatherplus;

import android.content.Context;
import android.widget.Toast;

public class Tools {
    static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
