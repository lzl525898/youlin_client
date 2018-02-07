package com.nfs.youlin.signcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;

import java.util.Date;

/**
 * @Title: MonthSignView
 * @Package cn.wecoder.signcalendar.library
 * @Description: 一个月日期签到视图
 * @Author Jim
 * @Date 15/10/20
 * @Time 下午2:50
 * @Version
 */
public class MonthSignView extends LinearLayout {
    private SignCalendar signCalendar;
    private MonthSignData mMonthSignData;
    private Context mContext;

    public MonthSignView(Context context) {
        this(context, null);
    }

    public MonthSignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 设置签到日历
     * @param signCalendar 签到日历
     */
    public void setSignCalendar(SignCalendar signCalendar) {
        this.signCalendar = signCalendar;
    }

    /**
     * 设置每月视图的签到数据
     * @param monthSignData 每月签到数据
     */
    public void setMonthSignData(MonthSignData monthSignData) {
        mMonthSignData = monthSignData;
        generateChildViews();
    }

    /**
     * 生成视图
     */
    private void generateChildViews() {
        int year = mMonthSignData.getYear();
        int month = mMonthSignData.getMonth();

        int monthDays = DateUtil.getDateNum(year, month);
        Loger.d("test", "year="+year+"month="+month+"monthDays="+monthDays);
        int earlymonthDays = DateUtil.getDateNum(year, month-1);
        if( month-1<0)
        	earlymonthDays = DateUtil.getDateNum(year-1, 11);
        Date firstDay = new Date(year - 1900, month, 1);
        Date lastDay = new Date(year - 1900, month, monthDays);
        int weekday = firstDay.getDay();  //星期几
        int showDays = monthDays + weekday;
        int allshowday = 42;
//        int showRows = (int) Math.ceil(showDays / 7.0);
        int showRows = 6;  //固定显示最多行数
        int showFlag = 0;
        for(int i=0; i<showRows; i++) {
            LinearLayout weekContainer = new LinearLayout(mContext);
            weekContainer.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams weekContainerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            weekContainerParams.topMargin = (int) mContext.getResources().getDimension(R.dimen.cal_date_v_margin);
            //cal_date_v_margin 每行的上下间距 
            weekContainerParams.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.cal_date_v_margin);
            weekContainer.setLayoutParams(weekContainerParams);

            int width = (int) mContext.getResources().getDimension(R.dimen.cal_date_width);
            int height = (int) mContext.getResources().getDimension(R.dimen.cal_date_height);

            for(int j=0; j<7; j++) {
                LinearLayout weekDayContainer = new LinearLayout(mContext);
                weekDayContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
                weekDayContainer.setGravity(Gravity.CENTER);
                if(showFlag >= weekday && showFlag < showDays) {
                    DateSignView currentMonthDayView = new DateSignView(mContext);
                    int currentDayIndex = showFlag - weekday + 1;
                    Date currentDay = new Date(year - 1900, month, currentDayIndex);
                    boolean isSigned = mMonthSignData.isDaySigned(currentDay);
                    currentMonthDayView.setSignCalendar(signCalendar);
                    currentMonthDayView.setSignData(firstDay,lastDay,currentDay, isSigned);
                    LinearLayout.LayoutParams currentMonthDayViewParams = new LinearLayout.LayoutParams(width, height);
                    currentMonthDayView.setLayoutParams(currentMonthDayViewParams);
                    weekDayContainer.addView(currentMonthDayView);
                }else if(showFlag < weekday ){
//                    LinearLayout otherMonthDayView = new LinearLayout(mContext);
//                    LinearLayout.LayoutParams otherMonthDayViewParams = new LinearLayout.LayoutParams(width, height);
//                    otherMonthDayView.setLayoutParams(otherMonthDayViewParams);
//                    weekDayContainer.addView(otherMonthDayView);
                	  DateSignView otherMonthDayView = new DateSignView(mContext);
                	  otherMonthDayView.setSignCalendar(signCalendar);
                	  int currentDayIndex = earlymonthDays-weekday + showFlag+1;
                	  Date currentDay;
                	  if(month-2<0)
                		  currentDay = new Date(year-1 - 1900, 12-1, currentDayIndex);
                	  else
                		  currentDay = new Date(year - 1900, month-1, currentDayIndex);
                	  otherMonthDayView.setSignData(firstDay,lastDay,currentDay, false);
                	  LinearLayout.LayoutParams otherMonthDayViewParams = new LinearLayout.LayoutParams(width, height);
                    otherMonthDayView.setLayoutParams(otherMonthDayViewParams);
                    weekDayContainer.addView(otherMonthDayView);
                }else if(showFlag >= showDays && showFlag < allshowday){
                	DateSignView otherMonthDayView = new DateSignView(mContext);
              	  otherMonthDayView.setSignCalendar(signCalendar);
              	  int currentDayIndex = showFlag - showDays+1;
              	  Date currentDay = new Date(year - 1900, month+1, currentDayIndex);
              	  otherMonthDayView.setSignData(firstDay,lastDay,currentDay, false);
              	  LinearLayout.LayoutParams otherMonthDayViewParams = new LinearLayout.LayoutParams(width, height);
                  otherMonthDayView.setLayoutParams(otherMonthDayViewParams);
                  weekDayContainer.addView(otherMonthDayView);
                }
                weekContainer.addView(weekDayContainer);
                showFlag++;
            }
            addView(weekContainer);
        }
    }
}
