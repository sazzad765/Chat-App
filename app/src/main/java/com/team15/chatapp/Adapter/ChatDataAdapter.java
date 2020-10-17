package com.team15.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import com.team15.chatapp.R;

import java.util.ArrayList;

public class ChatDataAdapter extends BaseAdapter {
    ArrayList array_list;
    Context context;

    public ChatDataAdapter(Context context, ArrayList array_list) {
        this.context = context;
        this.array_list = array_list;
    }


    @Override
    public int getCount() {
        return array_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int count = position+1;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.list_layout, null);

        ArrayList list= (ArrayList) array_list.get(position);
        TextView txtListSl = view.findViewById(R.id.txtListSl);
        TextView txtListTitle = view.findViewById(R.id.txtListTitle);
        txtListSl.setText("SL: "+count);
        txtListTitle.setText(list.get(1).toString());

        return view;
    }
}