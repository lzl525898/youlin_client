package com.nfs.youlin.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

public class RingStatus {
	private Vibrator vibrator;
	private Context context;
	public MediaPlayer setRingStatus(Context context) throws Exception,IOException{
		this.context=context;
		Uri  uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer player=new MediaPlayer();
		player.setDataSource(this.context,uri);
		AudioManager audioManager=(AudioManager)this.context.getSystemService(Context.AUDIO_SERVICE);
		
		File voiceFile=new File(this.context.getFilesDir().getParent()+File.separator+"shared_prefs/"+"voice_status.xml");
		if(!voiceFile.exists()){
			if(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)!=0){
				player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				player.prepare();
				player.start();
			}
		}else{
			SharedPreferences preferences=this.context.getSharedPreferences(App.VOICE,Context.MODE_PRIVATE);
			boolean voiceBool=preferences.getBoolean("voiceBool", false);
			if(voiceBool){
				if(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)!=0){
					player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
					player.prepare();
					player.start();
				}
			}else{
				player.stop();
			}
		}
		return player;
		
	}
}
