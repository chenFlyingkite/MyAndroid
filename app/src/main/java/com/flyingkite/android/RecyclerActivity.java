package com.flyingkite.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.flyingkite.library.Say;
import com.flyingkite.library.widget.CenterScroller;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        initRecycler();
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
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(adapter);
    }
}
