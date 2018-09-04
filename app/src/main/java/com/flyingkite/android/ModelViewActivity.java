package com.flyingkite.android;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.widget.Library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelViewActivity extends FragmentActivity implements Loggable {

    private Library<TextAdapter> textLib1;
    private Library<TextAdapter> textLib2;

    private MVM m1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modv);

        initLibs();
        obs();
    }

    private List<String> strs = new ArrayList<>();

    private void initLibs() {
        textLib1 = new Library<>(findViewById(R.id.recycler));
        strs.clear();
        for (int i = 0; i < 20; i++) {
            strs.add("# " + i);
        }
        TextAdapter a = new TextAdapter();
        a.setDataList(strs);
        a.setItemListener(new TextAdapter.ItemListener() {
            @Override
            public void onClick(String item, TextAdapter.TextVH holder, int position) {
                logE("Remove #%s -> %s", position, item);
                if (position < 0) return;
                strs.remove(position);
                m1.line.setValue(strs);
                textLib1.adapter.notifyItemRemoved(position);
            }
        });
        textLib1.setViewAdapter(a);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetList();
        m1.line.setValue(strs);
        textLib1.adapter.notifyDataSetChanged();
    }

    private void resetList() {
        strs.clear();
        for (int i = 0; i < 20; i++) {
            strs.add("# " + i);
        }
    }

    private void obs() {
        m1 = ViewModelProviders.of(this).get(MVM.class);
        m1.line.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                logE("m1 = %s", strings);
                TextView t = findViewById(R.id.modvText);
                t.setText(_fmt("m1 = %s", strings));
            }
        });
        m1.line.setValue(strs);
    }
}

class MVM extends ViewModel {
    public MutableLiveData<List<String>> line = new MutableLiveData<>();

    public MVM() {
        line.setValue(Arrays.asList("M1", "M2"));
    }
}
