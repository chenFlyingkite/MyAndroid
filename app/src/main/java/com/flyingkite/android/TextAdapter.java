package com.flyingkite.android;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.widget.RVAdapter;

import androidx.recyclerview.widget.RecyclerView;

public class TextAdapter extends RVAdapter<String, TextAdapter.TextVH, TextAdapter.ItemListener> implements Loggable {
    public interface ItemListener extends RVAdapter.ItemListener<String, TextVH> {

    }

    private int x = 0;

    @Override
    public TextVH onCreateViewHolder(ViewGroup parent, int viewType) {
        logE("+ type%s", viewType);
        return new TextVH(inflateView(parent, R.layout.view_text), x++);
    }

    @Override
    public void onBindViewHolder(TextVH holder, int position) {
        super.onBindViewHolder(holder, position);
        String s = itemOf(position);
        holder.text.setText(s);
        logE("~ #%s, A = %s, L = %s", position, holder.getAdapterPosition(), holder.getLayoutPosition());
    }

    public static class TextVH extends RecyclerView.ViewHolder implements Loggable {
        private TextView text;
        private int xy;
        public TextVH(View itemView, int x) {
            super(itemView);
            text = itemView.findViewById(R.id.itemText);
            h.sendEmptyMessage(0);
            xy = x;
        }

        @Override
        public String LTag() {
            return "VH #" + xy + " #" + getAdapterPosition();
        }

        Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //logE("LP = %s", getLayoutPosition());
                logE("%s", TextVH.this.toString());

                int a = getAdapterPosition();
                int l = getLayoutPosition();
                if (a < 0) {
                    logE("NEG A = %s", a);
                }
                if (l < 0) {
                    logE("NEG L = %s", l);
                }
                if (a != l) {
                    logE("NEG A != L");
                }
                sendEmptyMessageDelayed(msg.what, 2000);
            }
        };
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
