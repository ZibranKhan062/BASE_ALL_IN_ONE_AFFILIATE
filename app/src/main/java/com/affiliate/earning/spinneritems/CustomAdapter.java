package com.affiliate.affiliate.spinneritems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.affiliate.affiliate.R;

public class CustomAdapter extends BaseAdapter {

    Context context;

    String[] productNames;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext,  String[] productNames) {
        this.context = applicationContext;
        this.productNames = productNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {


        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        TextView names = (TextView) view.findViewById(R.id.textView);

        names.setText(productNames[i]);
        return view;
    }
}