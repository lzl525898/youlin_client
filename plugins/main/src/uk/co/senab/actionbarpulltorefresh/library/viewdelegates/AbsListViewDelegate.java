/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.senab.actionbarpulltorefresh.library.viewdelegates;

import android.R;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

/**
 * FIXME
 */
public class AbsListViewDelegate implements ViewDelegate {

    public static final Class[] SUPPORTED_VIEW_CLASSES =  { AbsListView.class };

    @Override
    public int isReadyForPullhyy(View view, final float x, final float y) {
        int ready = -1;
        
        // First we check whether we're scrolled to the top
        AbsListView absListView = (AbsListView) view;
        int listviewfirstvisibleposition = absListView.getFirstVisiblePosition();
        int listviewlastvisibleposition = absListView.getLastVisiblePosition();
        int listviewchildcount = absListView.getCount();
        final View firstVisibleChild = absListView.getChildAt(0);
        final View lastvisibleChild = absListView.getChildAt(listviewlastvisibleposition-listviewfirstvisibleposition);
        Log.d("hyytest", "AbsListViewDelegate instence isReadyForPull "+absListView.getCount());
        if (listviewchildcount == 0) {
            ready = 1;
            Log.d("hyytest", "111111ready="+ready);
        } else if(listviewfirstvisibleposition == 0 && (listviewlastvisibleposition == listviewchildcount-1)){
//        	if(firstVisibleChild.getTop()>= absListView.getPaddingTop()||lastvisibleChild.getBottom()<= absListView.getBottom()){
//        		Log.d("hyytest", "!childbottom="+lastvisibleChild.getBottom()+"Paddingbottom="+absListView.getBottom());
//        		Log.d("hyytest", "!childtop="+firstVisibleChild.getTop()+"Paddingtop="+absListView.getPaddingTop());
//        		ready = 0;
//            	Log.d("hyytest", "111111ready="+ready);
//        	}
//        } else if(listviewfirstvisibleposition == 0 && (listviewlastvisibleposition == listviewchildcount-1)){
        	if(firstVisibleChild.getTop()>= absListView.getPaddingTop() && lastvisibleChild.getBottom()<= absListView.getBottom()){
                //		Log.d("hyytest", "!childtop="+firstVisibleChild.getTop()+"Paddingtop="+absListView.getPaddingTop());
                ready = 6;  //����
                Log.d("hyytest", "111111ready="+ready);
            }else if(firstVisibleChild.getTop()>= absListView.getPaddingTop()){
        //		Log.d("hyytest", "!childtop="+firstVisibleChild.getTop()+"Paddingtop="+absListView.getPaddingTop());
        		ready = 4;  //����
            	Log.d("hyytest", "111111ready="+ready);
        	}else if(lastvisibleChild.getBottom()<= absListView.getBottom()){
        //		Log.d("hyytest", "!childbottom="+lastvisibleChild.getBottom()+"Paddingbottom="+absListView.getBottom());
        		ready = 5;  //����
            	Log.d("hyytest", "111111ready="+ready);
        	}
        }else if (listviewfirstvisibleposition == 0) {
           // Log.d("hyytest", "childtop="+firstVisibleChild.getTop()+"Paddingtop="+absListView.getPaddingTop());
             if(firstVisibleChild != null && firstVisibleChild.getTop() >= absListView.getPaddingTop()){
            	 ready = 2;//����
            }
            Log.d("hyytest", "222222ready="+ready);
        }
        //Log.d("hyytest", "LastVisiblePosition"+absListView.getLastVisiblePosition());
        else if(listviewlastvisibleposition == (listviewchildcount-1)){ 
        	//Log.d("test1", "childbottom="+lastvisibleChild.getBottom()+"PaddingBottom="+absListView.getBottom());
        	 if(lastvisibleChild != null && lastvisibleChild.getBottom()<= absListView.getBottom()){
        		 ready = 3;//����
        	}
        	Log.d("hyytest", "333333ready="+ready);
        }
       // absListView.getLastVisiblePosition() == absListView.getCount()
        // Then we have to check whether the fas scroller is enabled, and check we're not starting
        // the gesture from the scroller
//        if (ready >= 0 && absListView.isFastScrollEnabled() && isFastScrollAlwaysVisible(absListView)) {
//            switch (getVerticalScrollbarPosition(absListView)) {
//                case View.SCROLLBAR_POSITION_RIGHT:
//                    if(x < absListView.getRight() - absListView.getVerticalScrollbarWidth()){
//                    	ready =  4;
//                    }
//                    Log.d("hyytest", "!!!!!!!SCROLLBAR_POSITION_RIGHT ready="+ready);
//                    break;
//                case View.SCROLLBAR_POSITION_LEFT:
//                    if(x > absListView.getVerticalScrollbarWidth()){
//                    	ready = 5;
//                    }
//                    Log.d("hyytest", "!!!!!!!SCROLLBAR_POSITION_LEFT ready="+ready);
//                    break;
//            }
//        }
        
        return ready;
    }

    int getVerticalScrollbarPosition(AbsListView absListView) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                CompatV11.getVerticalScrollbarPosition(absListView) :
                Compat.getVerticalScrollbarPosition(absListView);
    }

    boolean isFastScrollAlwaysVisible(AbsListView absListView) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                CompatV11.isFastScrollAlwaysVisible(absListView) :
                Compat.isFastScrollAlwaysVisible(absListView);
    }

    static class Compat {
        static int getVerticalScrollbarPosition(AbsListView absListView) {
            return View.SCROLLBAR_POSITION_RIGHT;
        }
        static boolean isFastScrollAlwaysVisible(AbsListView absListView) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static class CompatV11 {
        static int getVerticalScrollbarPosition(AbsListView absListView) {
            return absListView.getVerticalScrollbarPosition();
        }
        static boolean isFastScrollAlwaysVisible(AbsListView absListView) {
            return absListView.isFastScrollAlwaysVisible();
        }
    }

	@Override
	public boolean isReadyForPull(View view, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}
}
