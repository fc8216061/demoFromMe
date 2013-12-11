package com.lele.drag;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

import com.lele.drag.adapter.GridViewAdapter;
import com.lele.drag.util.Configure;
import com.lele.drag.view.DragGridView;

public class MainActivity extends Activity {
    private DragGridView gridView;
    public static final int NUM_COLUMNS = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        Configure.init(this);
        gridView = (DragGridView) findViewById(R.id.gridview);
        gridView.setNumColumns(NUM_COLUMNS);
        ArrayList<String> l = new ArrayList<String>();
        for (int i = 0; i < 9; i++) {
            l.add("" + i);
        }
        GridViewAdapter adapter = new GridViewAdapter(MainActivity.this, l);
        gridView.setAdapter(adapter);
    }
}
