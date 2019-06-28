package com.unibg.app3dsat.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.unibg.app3dsat.R;

/**
 * Class: MyExpandableListAdapter
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private final SparseArray<Group> groups;
    @NonNull
    private final LayoutInflater inflater;

    /**
     * Constructor: MyExpandableListAdapter
     *
     * @param act
     * @param groups
     */
    public MyExpandableListAdapter(Activity act, SparseArray<Group> groups) {
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    /**
     * Method: getChild
     *
     * @param groupPosition
     * @param childPosition
     * @return Object
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    /**
     * Method: getChildId
     *
     * @param groupPosition
     * @param childPosition
     * @return long
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    /**
     * Method: getChildView
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return View
     */
    @Nullable
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, @Nullable View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }

        text = convertView.findViewById(R.id.textView1);
        text.setText(children);

        return convertView;
    }

    /**
     * Method: getChildrenCount
     *
     * @param groupPosition
     * @return int
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    /**
     * Method: getGroup
     *
     * @param groupPosition
     * @return Object
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    /**
     * Method: getGroupCount
     *
     * @return int
     */
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    /**
     * Method: getGroupId
     *
     * @param groupPosition
     */
    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    /**
     * Method: getGroupView
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return View
     */
    @Nullable
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, @Nullable View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }

        Group group = (Group) getGroup(groupPosition);

        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);

        return convertView;
    }

    /**
     * Method: hasStableIds
     *
     * @return boolean
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Method: isChildSelectable
     *
     * @return boolean
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}