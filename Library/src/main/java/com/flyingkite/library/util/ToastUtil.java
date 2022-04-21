package com.flyingkite.library.util;

import android.widget.Toast;

import androidx.annotation.StringRes;

public interface ToastUtil extends ContextOwner {
    default void showToast(@StringRes int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_LONG).show();
    }

    default void showToastShort(@StringRes int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
    }

    default void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    default void showToastShort(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }
}
