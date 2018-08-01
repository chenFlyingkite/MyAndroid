package com.flyingkite.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.Say;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.widget.CenterScroller;
import com.flyingkite.library.widget.Library;
import com.flyingkite.library.widget.RVSelectAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        initRecycler();
        initRecycler2();
    }

    private void initRecycler() {
        RecyclerView recycler = findViewById(R.id.recycler);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("#" + i);
        }
        CenterScroller scroller = new CenterScroller() {
            @Override
            public RecyclerView getRecyclerView() {
                return recycler;
            }
        };
        TextAdapter adapter = new TextAdapter();
        adapter.setDataList(list).setItemListener(new TextAdapter.ItemListener() {
            @Override
            public void onClick(String item, TextAdapter.TextVH holder, int position) {
                scroller.smoothScrollToCenter(position);
                Say.Log("item = %s, #%s", item, position);
                rva.setX(position + 1);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(adapter);
    }

    private void initRecycler2() {
        List<String> ss = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            ss.add("" + i);
        }
        Library<RVA> lib = new Library<>(findViewById(R.id.recycler2));

        rva.setDataList(ss);
        lib.setViewAdapter(rva);
    }
    private RVA rva = new RVA();

    private static class RVA
            extends RVSelectAdapter<String, RVA.RVAH, RVA.ItemListener>
            implements Loggable
    {

        public boolean sel = false;
        private int x = 1;

        @NonNull
        @Override
        public RVAH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RVAH(inflateView(parent, R.layout.view_text));
        }

        @Override
        public boolean hasSelection() {
            return sel;
        }

        @Override
        public void onBindViewHolder(RVAH holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.text.setText(itemOf(position));
        }

        public void setX(int nx) {
            sel = nx != 1;
            selectedIndices.clear();
            int n = dataList.size();
            for (int i = 0; i < n; i++) {
                String si = itemOfSuper(i);
                int p = Integer.parseInt(si);
                log("#%s : %s => p = %s, nx = %s", i, si, p, nx);
                if (p % nx == 0) {
                    selectedIndices.add(i);
                }
            }
            log("selIndex = %s", selectedIndices);
            notifyDataSetChanged();
        }


        public interface ItemListener extends RVSelectAdapter.ItemListener<String, RVAH> {

        }



        public class RVAH extends RecyclerView.ViewHolder {

            private TextView text;

            public RVAH(View v) {
                super(v);
                text = v.findViewById(R.id.itemText);
            }
        }
    }
}
