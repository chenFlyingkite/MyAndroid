package com.flyingkite.android;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.mediastore.MediaStoreTester;
import com.flyingkite.library.recyclerview.Library;
import com.flyingkite.library.recyclerview.RVAdapter;
import com.flyingkite.library.recyclerview.RVSelectAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerActivity extends BaseActivity {

    private TextView parentFolder;

    private RVA rva = new RVA();
    private Library<TRA> diskLib;
    private TicTac2 clock = new TicTac2();
    private File parent;
    private String state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        initRecycler();
        initRecycler2();

        init();

        initDisk();
    }

    @Override
    protected String[] neededPermissions() {
        return new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
    }

    private void init() {
        findViewById(R.id.test).setOnClickListener((v) -> {
            new MediaStoreTester(getApplicationContext()).test();
        });
        findViewById(R.id.disk).setOnClickListener((v) -> {
            File root = Environment.getExternalStorageDirectory();
            logE("root = %s", root);
            fileList(root);
        });
        parentFolder = findViewById(R.id.parentFolder);
    }

    private void fileList(File f) {
        logE("fileList = %s", f);
        parent = f;
        updateFile();

        List<File> all = new ArrayList<>();
        long ms = -1;
        int dn = -1;
        int fn = -1;
        int n = -1;
        if (f != null) {
            clock.tic();
            String[] a = f.list();
            ms = clock.tac("File listed %s", f);
            if (a != null) {
                fn = dn = 0;
                n = a.length;
                logE("%s items", a.length);
                for (int i = 0; i < a.length; i++) {
                    File fi = new File(f, a[i]);
                    String k = fi.getAbsolutePath();
                    logE("#%s : %s", i, fi);
                    all.add(fi);
                    if (fi.isFile()) {
                        fn++;
                    } else {
                        dn++;
                    }
                }
            }
        }
        state = String.format("%sms %s items = %s D + %s F for %s", ms, n, dn, fn, f);
        diskLib.adapter.setDataList(all);
        diskLib.adapter.notifyDataSetChanged();
        updateFile();
    }

    private void updateFile() {
        parentFolder.setText(state);
    }

    private void initRecycler() {
        Library<TextAdapter> textLib = new Library<>(findViewById(R.id.recycler));
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("#" + i);
        }
        TextAdapter adapter = new TextAdapter();
        adapter.setDataList(list).setItemListener(new TextAdapter.ItemListener() {
            @Override
            public void onClick(String item, TextAdapter.TextVH holder, int position) {
                textLib.adapter.scroller.scrollToCenter(position);
                logE("item = %s, #%s", item, position);
                rva.setX(position + 1);
                adapter.notifyDataSetChanged();
            }
        });
        textLib.setViewAdapter(adapter);
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

    private void initDisk() {
        diskLib = new Library<>(findViewById(R.id.recyclerDisk), true);
        List<File> ans = new ArrayList<>();
        TRA ta = new TRA();
        ta.setItemListener(new TRA.ItemListener() {
            @Override
            public void onClick(File item, TRA.VH holder, int position) {
                logE("Disk #%s, %s", position, item);
                fileList(item);
            }
        });
        ta.setDataList(ans);
        diskLib.setViewAdapter(ta);
    }

    @Override
    public void onBackPressed() {
        File root = Environment.getExternalStorageDirectory();
        boolean isRoot = root.getAbsolutePath().equals(parent.getAbsolutePath());
        isRoot = false;
        if (!isRoot) {
            fileList(parent.getParentFile());
            return;
        }
        super.onBackPressed();
    }

    private static class TRA extends RVAdapter<File, TRA.VH, TRA.ItemListener> implements Loggable {

        private interface ItemListener extends RVAdapter.ItemListener<File, TRA.VH> {

        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(inflateView(parent, R.layout.view_text2));
        }

        @Override
        public void onBindViewHolder(VH vh, int position) {
            super.onBindViewHolder(vh, position);
            File f = itemOf(position);
            String s = String.format("%s : %s", position, f.getName());
            int tc = Color.BLACK;
            if (f.isFile()) {
                tc = Color.BLUE;
            }
            vh.msg.setText(s);
            vh.msg.setTextColor(tc);
        }

        private static class VH extends RecyclerView.ViewHolder {
            private TextView msg;
            public VH(@NonNull View v) {
                super(v);
                msg = v.findViewById(R.id.textMsg);
            }
        }
    }

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
