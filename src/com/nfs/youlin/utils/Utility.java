package com.nfs.youlin.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {  
    public static void setListViewHeightBasedOnChildren(ListView listView) {  
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();
            Loger.i("youlin","666666666666666----->"+listItem.getMeasuredHeight()+" wjkjwakfjdkl---->"+listAdapter.getCount());
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        Loger.i("youlin","777777777777777777777----->"+params.height);
        listView.setLayoutParams(params);  
    }  
}  
