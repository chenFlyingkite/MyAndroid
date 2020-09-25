package com.flyingkite.android;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.recyclerview.RVAdapter;

import androidx.recyclerview.widget.RecyclerView;

public class TextAdapter extends RVAdapter<String, TextAdapter.TextVH, TextAdapter.ItemListener> implements Loggable {
    public interface ItemListener extends RVAdapter.ItemListener<String, TextVH> {

    }

    @Override
    public TextVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextVH(inflateView(parent, R.layout.view_text));
    }

    @Override
    public void onBindViewHolder(TextVH holder, int position) {
        super.onBindViewHolder(holder, position);
        String s = itemOf(position);
        holder.text.setText(s);
    }

    public static class TextVH extends RecyclerView.ViewHolder implements Loggable {
        private TextView text;
        public TextVH(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.itemText);
        }
    }

    /*
    //https://developer.android.com/training/data-storage/room/?hl=zh-tw
    @Entity
    private class A {
        @PrimaryKey
        int x;

        @ColumnInfo(name = "aaa", collate = 2)
        String y;
    }

    @Dao
    private interface DAO {
        @Query("SELECT * from A")
        List<A> getAs();
    }
    */
}
