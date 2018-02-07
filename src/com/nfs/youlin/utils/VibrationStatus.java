package com.nfs.youlin.utils;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

public class VibrationStatus{
	private Vibrator vibrator;
	private Context context;
	public void setVibraStatus(Context context){
		this.context=context;
		vibrator=(Vibrator)this.context.getSystemService(Context.VIBRATOR_SERVICE);
		long [] pattern = {100,400,100,400}; 
		File vibrationFile=new File(this.context.getFilesDir().getParent()+File.separator+"shared_prefs/"+"vibration_status.xml");
		if(!vibrationFile.exists()){
			vibrator.vibrate(pattern,-1);
		}else{
			SharedPreferences preferences=this.context.getSharedPreferences(App.VIBRATION,Context.MODE_PRIVATE);
			boolean vibraBool=preferences.getBoolean("vibraBool", false);
			if(vibraBool){
				vibrator.vibrate(pattern,-1);
			}else{
				vibrator.cancel();
			}
		}
		
	}
	
}
