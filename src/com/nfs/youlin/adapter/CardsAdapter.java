package com.nfs.youlin.adapter;

import java.util.List;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.WeatherInforActivity;
import com.nfs.youlin.entity.CardsBean;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ChooseWeaherTB;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * ClassName:CardsAdapter Function: 
 * FUNCTION : 
 * TODO ADD Reason
 *
 * @author hujiahuan
 * @version
 * @since Ver 1.1
 * @Date 
 * 
 * @see
 */
public class CardsAdapter extends BaseAdapter {

	private List<CardsBean> cardlist;
	Context context;
	private LayoutInflater myinflate;
	ImageLoader imageLoader;
	public CardsAdapter(Context context, List<CardsBean> mylist) {
		imageLoader = ImageLoader.getInstance();
		this.context = context;
		myinflate = LayoutInflater.from(context);
		cardlist = mylist;
		
	}
	public void addList(List<CardsBean> list){
		this.cardlist.addAll(list);
	}

	public void replaceList(List<CardsBean> list){
		this.cardlist = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cardlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return cardlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		class viewHolder {
			LinearLayout bg;
			TextView time;
			TextView position;
			TextView now_wendu;
			TextView now_weather;
			TextView low_high;
			TextView time1;
			TextView time1_wendu;
			TextView time2;
			TextView time2_wendu;
			ImageView now_tb;
			ImageView img_info;
			TextView infor;
		}
		viewHolder holder = null;
		if (convertView == null) {
			holder = new viewHolder();
			convertView = myinflate.inflate(R.layout.item_weather, null);
			holder.bg=(LinearLayout)convertView.findViewById(R.id.linear_cardbg);
			holder.time=(TextView)convertView.findViewById(R.id.tv_time);
			holder.position=(TextView)convertView.findViewById(R.id.tv_position);
			holder.now_wendu=(TextView)convertView.findViewById(R.id.tv_now_wendu);
			holder.now_weather=(TextView)convertView.findViewById(R.id.tv_now_weather);
			holder.low_high=(TextView)convertView.findViewById(R.id.tv_low_high);
			holder.time1=(TextView)convertView.findViewById(R.id.tv_time1);
			holder.time1_wendu=(TextView)convertView.findViewById(R.id.tv_time1_wendu);
			holder.time2=(TextView)convertView.findViewById(R.id.tv_time2);
			holder.time2_wendu=(TextView)convertView.findViewById(R.id.tv_time2_wendu);
			holder.infor=(TextView)convertView.findViewById(R.id.tv_info);
			holder.now_tb=(ImageView)convertView.findViewById(R.id.img_now_tb);
			holder.img_info=(ImageView)convertView.findViewById(R.id.img_info);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}
		
		holder.time.setText(cardlist.get(position).getTime());
		holder.position.setText(cardlist.get(position).getPosition());
		holder.now_wendu.setText(""+cardlist.get(position).getNowwendu());
		holder.time1.setText(cardlist.get(position).getTime1());
		holder.time1_wendu.setText(""+cardlist.get(position).getTime1_wendu());
		holder.now_weather.setText(cardlist.get(position).getNowweather());
		holder.low_high.setText(cardlist.get(position).getLow()+"°"+"/"+cardlist.get(position).getHigh()+"°");
		holder.time.setText(cardlist.get(position).getTime());
		holder.time2.setText(cardlist.get(position).getTime2());
		holder.time2_wendu.setText(""+cardlist.get(position).getTime2_wendu());
		holder.now_tb.setImageResource(new ChooseWeaherTB().choosetb(cardlist.get(position).getNowweather()));
		holder.infor.setText(cardlist.get(position).getInfo());
		//holder.img_info.setImageResource(new ChooseWeaherTB().choosexz(cardlist.get(position).getImag()));
		imageLoader.displayImage(cardlist.get(position).getImagUrl(), holder.img_info, App.options_weather);
		return convertView;

	}

}
