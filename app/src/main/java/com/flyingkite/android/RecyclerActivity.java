package com.flyingkite.android;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import flyingkite.log.L;
import flyingkite.math.ChiSquarePearson;
import flyingkite.math.ChiSquareTable;
import flyingkite.math.DiscreteSample;

public class RecyclerActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        initRecycler();
        initRecycler2();
    }

    private void chiTest() {
        int draws = 20;
        DiscreteSample s = new DiscreteSample(8);
        double[] pdf = new double[]{0.025, 0.1, 0.1, 0.155, 0.155, 0.155, 0.155, 0.155};
        s.setPdf(pdf);
        for (int i = 0; i < 20; i++) {
            s.clearSample();
            s.drawSample(draws);
            s.evalObservePdf();
            boolean acc = ChiSquarePearson.acceptH0(s, ChiSquareTable.getChiTailArea(7, ChiSquareTable._100));
            L.log("#%s ->\n%s\n => Chi %s\n-----\n", i, s, acc ? "accept" : "reject");
        }
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
                adapter.notifyDataSetChanged();
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
                String si = super_itemOf(i);
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
