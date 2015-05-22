package com.bmob.im.demo.view.dialog;

import com.bmob.im.demo.R;
import com.bmob.im.demo.view.CircularProgressView;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
	
	final Handler handler0 = new Handler();
	
	Thread updateThread = new Thread();

	public CustomProgressDialog(Context context, String strMessage) {  
        this(context, R.style.CustomProgressDialog, strMessage);  
    }  
  
    public CustomProgressDialog(Context context, int theme, String strMessage) {  
        super(context, theme);  
        this.setContentView(R.layout.customprogressdialog);  
        this.getWindow().getAttributes().gravity = Gravity.CENTER;  
        TextView tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);  
        if (tvMsg != null) {  
            tvMsg.setText(strMessage);  
        }  
        
        final ProgressBar progressView = (ProgressBar) this.findViewById(R.id.cpv_progress_view);
        
//        handler0.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Start animation after a delay so there's no missed frames while the app loads up
//                progressView.setProgress(0f);
//                progressView.startAnimation(); // Alias for resetAnimation, it's all the same
//                // Run thread to update progress every half second until full
//                updateThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (progressView.getProgress() < progressView.getMaxProgress() && !Thread.interrupted()) {
//                            // Must set progress in UI thread
//                            handler0.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    progressView.setProgress(progressView.getProgress() + 10);
//                                }
//                            });
//                            SystemClock.sleep(250);
//                        }
//                    }
//                });
//                updateThread.start();
//            }
//        }, 0);
    }  
    
    @Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
  
        if (!hasFocus) {  
            dismiss();  
        }  
    }  
	
}


