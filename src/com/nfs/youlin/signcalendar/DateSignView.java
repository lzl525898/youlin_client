package com.nfs.youlin.signcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nfs.youlin.R;
import java.util.Date;

/**
 * @Title: DateSignView
 * @Package cn.wecoder.signcalendar.library
 * @Description: 一天签到视图
 * @Author Jim
 * @Date 15/10/20
 * @Time 下午2:50
 * @Version
 */
public class DateSignView extends LinearLayout {
    private boolean signed;
    private Date date = new Date();
    private Date currentmonthfirstday = new Date();
    private Date currentmonthlastday = new Date();
    private TextView mDayView;
    private TextView mSignView;
    private Context mContext;
    private SignCalendar signCalendar;

    public DateSignView(Context context) {
        this(context, null);
    }

    public DateSignView(Context context, AttributeSet attrs) {
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
     * 得到日期
     * @return 日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 得到签到状态
     * @return 签到状态
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * 设置每日签到视图的数据
     * @param date 日期
     * @param signed 签到状态
     */
    public void setSignData(Date firstdate,Date lastdate,Date date, boolean signed) {
        this.date = date;
        this.currentmonthfirstday = firstdate;
        this.currentmonthlastday = lastdate;
        this.signed = signed;
        generateChildViews();
    }

    /**
     * 生成视图
     */
    private void generateChildViews() {
        int day = date.getDate();
        TextView dayView = new TextView(mContext);
        TextView signView = new TextView(mContext);
        dayView.setGravity(Gravity.CENTER);
        signView.setGravity(Gravity.CENTER); // hyy
        dayView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.cal_day_text_size));
        signView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.cal_sign_text_size));
        Date nowDate = new Date();
        if(signCalendar.getToday() != null) {
            nowDate = signCalendar.getToday();
        }
        int result = -1;
        if(DateUtil.compareDateDay(date, currentmonthfirstday) == -1 || DateUtil.compareDateDay(date, currentmonthlastday) == 1){
        	result = 1;
        }else if(DateUtil.compareDateDay(date, nowDate) == 0){
        	result = 0;
        }
//        int	result = DateUtil.compareDateDay(date, nowDate); //1.同一天返回0；2.之前返回－1；3.之后返回1
//        int result = DateUtil.compareDateDay(date, new Date(nowDate.getYear() - 1900, nowDate.getMonth(), DateUtil.getDateNum(nowDate.getYear(), nowDate.getMonth()-1)));
        if(result == 1) {
            dayView.setText(day + "");
            dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unreach_day));
            dayView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.cal_unday_text_size));
//            signView.setText("");//R.string.cal_unreach
            signView.setTextColor(mContext.getResources().getColor(R.color.cal_unreach_text));
        }else {
            if(signed) {
                if(result == 0) {
                    setBackgroundResource(R.drawable.cal_today_bg);
                    dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_today_day));
                    signView.setText(R.string.cal_signed);
                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_today_text));
                }else if(result == -1) {
                    setBackgroundResource(R.drawable.cal_signed_bg);
                    dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_day)); // R.color.cal_signed_day
                    signView.setText(R.string.cal_signed);
                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_signed_text));
                }
            }else {
                if(result == 0) {
                    setBackgroundResource(R.drawable.cal_today_bg);
                    dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_today_day));
                    signView.setText(R.string.cal_unsign); //cal_unsigned_day
                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_today_text));
                }else if(result == -1) {
                    dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_day));
                    signView.setText(R.string.cal_unsign);
                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_text));
                }
            }
        }
        mDayView = dayView;
        mSignView = signView;
        addView(mDayView);
        addView(mSignView);
    }
}
