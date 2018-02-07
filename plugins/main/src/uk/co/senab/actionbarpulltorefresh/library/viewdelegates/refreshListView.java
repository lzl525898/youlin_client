package uk.co.senab.actionbarpulltorefresh.library.viewdelegates;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class refreshListView extends ListView{

	private float originalposition;
	private float currentposition;
	private  boolean parentdo;
	public boolean getrefreshstate(){
		return parentdo;
	}
	public refreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		originalposition = ev.getY();
		Log.d("hyytest", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+originalposition);
		if(parentdo == true){
			parentdo = false;
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		currentposition = ev.getY();
		Log.d("hyytest",
				"**************************rstVisiblePosition"
						+ this.getFirstVisiblePosition());
		Log.d("hyytest", "**************************currentposition="
				+ currentposition);


			switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE: {
    			if ((this.getFirstVisiblePosition() == 0
    					&& this.getChildAt(0).getTop() == this.getPaddingTop() && (currentposition - originalposition) > 8)) {

    				super.onTouchEvent(ev);
    				Log.d("hyytest", "babababa");
    				parentdo = true;
//    				onInterceptTouchEvent(ev);
    				return true;
    			} else{
    				return super.onTouchEvent(ev);
    			}
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {

                break;
            }
        }
			return true;
	}

}
