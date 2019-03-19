package com.flyingkite.library.widget;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public abstract class RVSelectAdapter<T,
        VH extends RecyclerView.ViewHolder,
        TListener extends RVAdapter.ItemListener<T, VH>>
        extends RVAdapter<T, VH ,TListener> {

    protected List<Integer> selectedIndices = new ArrayList<>();

    public boolean hasSelection() {
         return false;
    }

    public T super_itemOf(int index) {
        return super.itemOf(index);
    }

    @Override
    public T itemOf(int index) {
        if (hasSelection()) {
            if (index < 0 || selectedIndices.size() <= index) {
                return null;
            } else {
                return super.itemOf(selectedIndices.get(index));
            }
        } else {
            return super.itemOf(index);
        }
    }

    @Override
    public int getItemCount() {
        if (hasSelection()) {
            return selectedIndices.size();
        } else {
            return super.getItemCount();
        }
    }
}
