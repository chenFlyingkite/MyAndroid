package flyingkite.playground;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import flyingkite.library.android.log.Loggable;
import flyingkite.library.androidx.TicTac2;
import flyingkite.library.androidx.mediastore.MediaStoreTester;
import flyingkite.library.androidx.recyclerview.Library;
import flyingkite.library.androidx.recyclerview.RVAdapter;
import flyingkite.library.androidx.recyclerview.RVSelectAdapter;

public class RecyclerActivity extends BaseActivity {

    private TextView parentFolder;

    private RVA rva = new RVA();
    private Library<TRA> diskLib;
    private TicTac2 clock = new TicTac2();
    private File parent;
    private String state;

    private Library<TileAdapter> tiles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        initRecycler();
        initRecycler2();

        init();

        initDisk();
        initColorTile();
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

    private void sort(String[] a) {
        if (a == null) return;

        Arrays.sort(a, (x, y) -> {
            return x.compareTo(y);
        });
    }

    // Android 11 cannot list file in emulated/storage/0 ?
    // Mis-list the file of emulated/storage/0/a.txt
    // Access : Allowed to manage all files,
    // allowed to access media only, not allowed
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
            sort(a);
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

    private void initColorTile() {
        tiles = new Library<>(findViewById(R.id.recyclerScroll), false); // vertical or horizontal
        List<String> li = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            li.add("" + i);
        }
        TileAdapter a = new TileAdapter();
        a.setDataList(li);
        tiles.setViewAdapter(a);

        seekbars[0] = new SeekbarKit(findViewById(R.id.scrollParentX)) {
            @Override
            public String getTextDisplay(SeekbarKit me) {
                return _fmt("PX = %02d", me.seekBar.getProgress());
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                scrollPXYCXY(5);
            }
        };
        seekbars[1] = new SeekbarKit(findViewById(R.id.scrollParentY)) {
            @Override
            public String getTextDisplay(SeekbarKit me) {
                return _fmt("PY = %02d", me.seekBar.getProgress());
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                scrollPXYCXY(5);
            }
        };
        seekbars[2] = new SeekbarKit(findViewById(R.id.scrollChildX)) {
            @Override
            public String getTextDisplay(SeekbarKit me) {
                return _fmt("CX = %02d", me.seekBar.getProgress());
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                scrollPXYCXY(5);
            }
        };
        seekbars[3] = new SeekbarKit(findViewById(R.id.scrollChildY)) {
            @Override
            public String getTextDisplay(SeekbarKit me) {
                return _fmt("CY = %02d", me.seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                scrollPXYCXY(5);
            }
        };
    }
    private void scrollPXYCXY(int pos) {
        int px = seekbars[0].seekBar.getProgress();
        int py = seekbars[1].seekBar.getProgress();
        int cx = seekbars[2].seekBar.getProgress();
        int cy = seekbars[3].seekBar.getProgress();
        logE("tilesCenter.scrollToPercent(%s, %s, %s, %s, %s, true)", pos, px, cx, py, cy);
        tilesCenter.scrollToPercent(pos, px, cx, py, cy, true);
    }
    private SeekbarKit[] seekbars = new SeekbarKit[4];
    // tested and OK
    private CenterScroller tilesCenter = new CenterScroller() {
        @Override
        public RecyclerView getRecyclerView() {
            return tiles.recyclerView;
        }
    };

    private class TileAdapter extends TextAdapter {
        @Override
        public TextVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TextVH(inflateView(parent, R.layout.view_tile));
        }

        private int[] colors = {0x44888888, 0x44cc0000, 0x4400cc00, 0x440000cc,
                0x44cccc00, 0x4400cccc, 0x44cc00cc, 0x44dddddd};

        @Override
        public void onBindViewHolder(TextVH vh, int position) {
            super.onBindViewHolder(vh, position);
            vh.itemView.setBackgroundColor(colors[position % colors.length]);
        }
    }

    private static class TRA extends RVAdapter<File, TRA.VH, TRA.ItemListener> implements Loggable {

        private interface ItemListener extends RVAdapter.ItemListener<File, TRA.VH> {

        }

        private TicTac2 tt = new TicTac2();

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(inflateView(parent, R.layout.view_text2));
        }

        private Set<Long> free = new HashSet<>();
        private Set<Long> total = new HashSet<>();
        private Set<Long> used = new HashSet<>();

        @Override
        public void onBindViewHolder(VH vh, int position) {
            super.onBindViewHolder(vh, position);
            tt.tic();
            File f = itemOf(position);
            long fr = f.getFreeSpace();
            free.add(fr);
            long to = f.getTotalSpace();
            total.add(to);
            long us = f.getUsableSpace();
            used.add(us);
            // hint, not guarantee
            logE("free = %s, total = %s, used = %s", free, total, used);
            String sp = String.format("F %s\nT %s\nU %s", fr, to, us);
            String s = sp + "\n";
            s = "";
            long leng = f.length();
            String len = fileSize(leng);
            if (f.isDirectory()) {
                int sub = -1;
                String[] fl = f.list();
                if (fl != null) {
                    sub = fl.length;
                }
                s += String.format("%s : %s (%s items) %s", position, f.getName(), sub, len);
            } else {
                s += String.format("%s : %s %s", position, f.getName(), len);
            }
            int tc = Color.BLACK;
            if (f.isFile()) {
                tc = Color.BLUE;
            }
            tt.tac("#onBind %s : %s", position, f);
            vh.msg.setText(s);
            vh.msg.setTextColor(tc);
        }

        @Deprecated
        private static String fileSize(long z) {
            long b = z % 1024;
            long kb = z / 1024;
            long mb = kb / 1024;
            long gb = mb / 1024;
            if (gb > 0) {
                double val = gb + mb / 1024.0;
                return String.format(Locale.US, "%.2f GB", val);
            } else if (mb > 0) {
                double val = mb + kb / 1024.0;
                return String.format(Locale.US, "%.2f MB", val);
            } else if (kb > 0) {
                double val = kb + b / 1024.0;
                return String.format(Locale.US, "%.2f KB", val);
            } else {
                return String.format(Locale.US, "%3d Bytes", b);
            }
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
