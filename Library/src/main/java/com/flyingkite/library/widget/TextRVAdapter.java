package com.flyingkite.library.widget;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.R;

public class TextRVAdapter extends RVAdapter<String, TextRVAdapter.ButtonVH, TextRVAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<String, ButtonVH> {
        //void onDelete(String data, ButtonVH vh, int position);
    }

    private int vhLayout = holderLayout();
    private int idText = itemId();

    @LayoutRes
    protected int holderLayout() {
        return R.layout.view_square_image;
    }

    @IdRes
    protected int itemId() {
        return R.id.itemIcon;
    }

    @NonNull
    @Override
    public ButtonVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        initCenterScroller(parent);
        return new ButtonVH(inflateView(parent, vhLayout));
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonVH vh, int position) {
        super.onBindViewHolder(vh, position);
        String msg = itemOf(position);
        vh.text.setText(msg);
    }

    public class ButtonVH extends RecyclerView.ViewHolder {
        private TextView text;

        public ButtonVH(View v) {
            super(v);
            text = v.findViewById(idText);
        }
    }
}
