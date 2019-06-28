package com.unibg.app3dsat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unibg.app3dsat.R;

import java.util.ArrayList;

/**
 * Class: ListViewAdapter
 */
public class ListViewAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final ArrayList<Patient> arraylist;

    /**
     * Constructor: ListViewAdapter
     *
     * @param context
     * @param arraylist
     */
    public ListViewAdapter(Context context, ArrayList<Patient> arraylist) {
        inflater = LayoutInflater.from(context);
        this.arraylist = arraylist;
    }

    /**
     * Method: getCount
     *
     * @return int
     */
    @Override
    public int getCount() {
        return arraylist.size();
    }

    /**
     * Method: getItem
     *
     * @param position
     * @return Patient
     */
    @Override
    public Patient getItem(int position) {
        return arraylist.get(position);
    }

    /**
     * Method: getItemId
     *
     * @param position
     * @return long
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Method: getView
     *
     * @param position
     * @param view
     * @param parent
     * @return View
     */
    @Nullable
    @SuppressLint("InflateParams")
    public View getView(final int position, @Nullable View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);

            // Locate the TextViews in listview_item.xml
            holder.name = view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Set the results into TextViews
        holder.name.setText(arraylist.get(position).toString());
        return view;
    }

    /**
     * Class: ViewHolder
     */
    class ViewHolder {
        TextView name;
    }
}