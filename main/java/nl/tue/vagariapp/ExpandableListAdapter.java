package nl.tue.vagariapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by S. Albert on 18-4-2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private final LayoutInflater layoutInflater;
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<Picture>> _listDataChild;


    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, ArrayList<Picture>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public Picture getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        String album = _listDataHeader.get(groupPosition);


        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        ArrayList<Picture> gridlist = new ArrayList<Picture>();

        for (int j = 0; j < _listDataChild.get(album).size(); j++) {
            gridlist.add(_listDataChild.get(album).get(j));
        }

        GridView gridView = (GridView) convertView
                .findViewById(R.id.lblListItem);


        ImageAdapter imgAdapter = new ImageAdapter(_context, gridlist);
        gridView.setAdapter(imgAdapter);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_group, null);

        }


        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}