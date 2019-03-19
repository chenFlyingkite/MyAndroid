package com.flyingkite.library.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

public interface ViewCreatorUtil {
    default View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        if (parent == null) {
            return null;
        } else {
            return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }
    }
}
