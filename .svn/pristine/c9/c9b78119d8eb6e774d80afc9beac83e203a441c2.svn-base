package com.srcoop.android.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

import com.srcoop.android.activity.R;

/**
 * Created by Jiahuiming on 14-11-23.
 */
public class SpecialAdapter extends SimpleAdapter {
    private int[] background= new int[]{R.drawable.list_item_background_o,R.drawable.list_item_background_e};

    public SpecialAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){


        View view = super.getView(position, convertView, parent);
        int backgroundPos = position%background.length;
        view.setBackgroundResource(background[backgroundPos]);
        return view;

    }

}
