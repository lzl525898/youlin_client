package com.easemob.chatuidemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class UserUtils {
    static ImageLoader imageLoader;
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        try {
            User user = DemoApplication.getInstance().getContactList().get(username);
            if(user == null){
                user = new User(username);
            }
                
            if(user != null){
                //demo没有这些数据，临时填充
                user.setNick(username);
//            user.setAvatar("http://downloads.easemob.com/downloads/57.png");
            }
            return user;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
        User user = getUserInfo(username);
                imageLoader = ImageLoader.getInstance();
        if(user != null){
            Loger.d("hyytest","user != null");
            imageLoader.displayImage(user.getAvatar(),imageView, App.options_default_avatar);
//            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Loger.d("hyytest","else user = null");
            imageLoader.displayImage("null",imageView, App.options_default_avatar);
//            Picasso.with(context).load(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).into(imageView);
        }
    }
    
}
