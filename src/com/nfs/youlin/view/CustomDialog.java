package com.nfs.youlin.view;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog {  
	static boolean mCancelable = true;
    public CustomDialog(Context context) {  
        super(context);  
    }  
  
    public CustomDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	 if (mCancelable){
             cancel();
             return true;
         }
         return false;
    }
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	if (mCancelable) {
    		cancel();
		}
    }
    
    public static class Builder {  
        private Context context;  
        private String title;  
        private String message;  
        private String positiveButtonText;  
        private String negativeButtonText;  
        private View contentView;  
        private DialogInterface.OnClickListener positiveButtonClickListener;  
        private DialogInterface.OnClickListener negativeButtonClickListener;  
  
        public Builder(Context context) {  
            this.context = context;
            mCancelable=true;
        }  
  
        public Builder setMessage(String message) {  
            this.message = message;  
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message = (String) context.getText(message);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
        }  
  
        public void setCancelable(boolean flag) {
        	// TODO Auto-generated method stub
        	mCancelable = flag;
        }
        
        /** 
         * Set the positive button resource and it's listener 
         *  
         * @param positiveButtonText 
         * @return 
         */  
        public Builder setPositiveButton(int positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = (String) context  
                    .getText(positiveButtonText);  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setPositiveButton(String positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = positiveButtonText;  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(int negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = (String) context  
                    .getText(negativeButtonText);  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(String negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public CustomDialog createWithPwd() {
        	LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        	final CustomDialog dialog = new CustomDialog(context,R.style.Dialog);  
        	View layout = inflater.inflate(R.layout.dialog_passwd_layout, null);  
        	dialog.addContentView(layout, new LayoutParams(  
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        	if(title!=null){
            	((TextView) layout.findViewById(R.id.title)).setVisibility(View.VISIBLE);
            	((View) layout.findViewById(R.id.view1)).setVisibility(View.VISIBLE);
            	((TextView) layout.findViewById(R.id.title)).setText(title);
            }
        	if (positiveButtonText != null) {  
                ((Button) layout.findViewById(R.id.positiveButton))  
                        .setText(positiveButtonText);  
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.positiveButton))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.positiveButton).setVisibility(  
                        View.GONE);  
            }  
        	if (message != null) {  
        		Loger.d("test5", "message != null");
                ((TextView) layout.findViewById(R.id.message)).setText(message);  
            } else if (contentView != null) {  
                // if no message set  
                // add the contentView to the dialog body  
//            	Loger.d("test5", "contentView != null");
//            	((LinearLayout) layout.findViewById(R.id.content)).setVisibility(View.GONE);
                ((LinearLayout) layout.findViewById(R.id.content))  
                        .removeAllViews();  
                ((LinearLayout) layout.findViewById(R.id.content))  
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));  
            } 
//            else{
//            	Loger.d("test5", "****************");
//            	((LinearLayout) layout.findViewById(R.id.content)).setVisibility(View.GONE);
//            }
            dialog.setContentView(layout);  
            return dialog;  
        }
        
        public CustomDialog create() {  
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final CustomDialog dialog = new CustomDialog(context,R.style.Dialog);  
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);  
            dialog.addContentView(layout, new LayoutParams(  
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));  
            // set the dialog title
            if(title!=null){
            	((TextView) layout.findViewById(R.id.title)).setVisibility(View.VISIBLE);
            	((View) layout.findViewById(R.id.view1)).setVisibility(View.VISIBLE);
            	((TextView) layout.findViewById(R.id.title)).setText(title);
            }
            // set the confirm button  
            if (positiveButtonText != null) {  
                ((Button) layout.findViewById(R.id.positiveButton))  
                        .setText(positiveButtonText);  
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.positiveButton))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.positiveButton).setVisibility(  
                        View.GONE);  
            }  
            // set the cancel button  
            if (negativeButtonText != null) {  
                ((Button) layout.findViewById(R.id.negativeButton))  
                        .setText(negativeButtonText);  
                if (negativeButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.negativeButton))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.negativeButton).setVisibility(  
                        View.GONE);  
            }  
            // set the content message  
            if (message != null) {  
                ((TextView) layout.findViewById(R.id.message)).setText(message);  
            } else if (contentView != null) {  
                // if no message set  
                // add the contentView to the dialog body  
//            	((LinearLayout) layout.findViewById(R.id.content)).setVisibility(View.GONE);
                ((LinearLayout) layout.findViewById(R.id.content))  
                        .removeAllViews();  
                ((LinearLayout) layout.findViewById(R.id.content))  
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));  
            }else{
            	((LinearLayout) layout.findViewById(R.id.content)).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);  
            return dialog;  
        }
        
    }
    
}
