package com.lele.drag.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lele.drag.MainActivity;
import com.lele.drag.R;
import com.lele.drag.util.Configure;


public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> lstDate;
    private TextView txtAge;
    public int gonePosition;
    private boolean isMoving;
    private int rows;

    public GridViewAdapter(Context mContext, List<String> list) {
        this.context = mContext;
        lstDate = list;
        rows = (lstDate.size()-1) / 3 +1; 
    }

    @Override
    public int getCount() {
        return lstDate.size();
    }

    @Override
    public Object getItem(int position) {
        return lstDate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void exchange(int startPosition, int endPosition,int gonePosition) {
        this.gonePosition = gonePosition;
        Log.e(" ---- ", "    gonePosition = " + gonePosition);
        Object startObject = getItem(startPosition);
        if(startPosition < endPosition){
            lstDate.add(endPosition + 1, (String) startObject);
            lstDate.remove(startPosition);
        }else{
            lstDate.add(endPosition,(String)startObject);
            Log.e("---", "endPosition = " + endPosition);
            lstDate.remove(startPosition + 1);
            Log.e("----", "   startPosition = " + startPosition);
            Log.e("----", "   lstDate.length = " + lstDate.size());
        }
    }

    public void setMovingState(boolean isMoving){
        this.isMoving = isMoving;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.griditem_main, null);
        txtAge = (TextView) convertView.findViewById(R.id.txt_userAge);
        txtAge.setText("Item" + lstDate.get(position));
        if (isMoving && position == gonePosition){
            Log.e(" --- ", "    isMoving    ");
            convertView.setVisibility(View.INVISIBLE);
        }
        AbsListView.LayoutParams params = new AbsListView.LayoutParams((int)(Configure.screenWidth/MainActivity.NUM_COLUMNS - 40*Configure.screenDensity),
                (int)(Configure.screenHeight/rows - 40*Configure.screenDensity));
        convertView.setLayoutParams(params);
        return convertView;
    }

}
