package com.huaguoshan.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huaguoshan.app_yulebagua.R;

public class BrowserActivity extends BaseActivity implements OnTouchListener{
	
	//手指向右滑动时的最小速度  
    private static final int XSPEED_MIN = 200;  
      
    //手指向右滑动时的最小距离  
    private static final int XDISTANCE_MIN = 150;  
      
    //记录手指按下时的横坐标。  
    private float xDown;  
      
    //记录手指移动时的横坐标。  
    private float xMove;  
      
    //用于计算手指滑动的速度。  
    private VelocityTracker mVelocityTracker;  
    
	private WebView webView;
	private ImageView btn_backward;
	private ImageView btn_forward;
	private ImageView btn_refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		
		Bundle extras = getIntent().getExtras();
		String url = extras.getString("url");
		
		webView = (WebView)findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(url);
		
		ImageView back = (ImageView)findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btn_backward = (ImageView)findViewById(R.id.btn_backward);
		btn_forward = (ImageView)findViewById(R.id.btn_forward);
		btn_refresh = (ImageView)findViewById(R.id.btn_refresh);
		
		
		btn_backward.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	if(webView.canGoBack()){
	        		webView.goBack();
	        		btn_forward.setImageResource(R.drawable.forward);
	        		if(!webView.canGoBack()){
	        			btn_backward.setImageResource(R.drawable.backward_2);
	        		}
	        	}
	        }
	    });
		
		btn_forward.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	if(webView.canGoForward()){
		        	webView.goForward();
		        	btn_backward.setImageResource(R.drawable.backward);
		        	if(!webView.canGoForward()){
		        		btn_forward.setImageResource(R.drawable.forward_2);
		        	}
	        	}
	        }
	    });
		
		btn_refresh.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	webView.reload();
	        }
	    });
		
		
		LinearLayout body = (LinearLayout)findViewById(R.id.body);
		body.setOnTouchListener(this);
	}
	
	private class MyWebViewClient extends WebViewClient {
		private boolean bfirst = true;
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	btn_backward.setImageResource(R.drawable.backward);
//	    	if ( url.indexOf("taobao") > -1 || url.indexOf("tmall") > -1) {
//    			if(checkApkExist(BrowserActivity.this.getApplicationContext(),"com.taobao.taobao")){
//    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("http://", "taobao://")) );
//    				startActivity(intent);
//    			}else{
//    				view.loadUrl(url);
//    			}
//	        }else if(url.indexOf("jd.com") > -1){
//	        	if(checkApkExist(BrowserActivity.this.getApplicationContext(),"com.jingdong.app.mall")){
//    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("http://", "openapp.jdmoble://")) );
//    				startActivity(intent);
//    			}else{
//    				view.loadUrl(url);
//    			}
//		    }else if(url.indexOf("amazon") > -1){
//		    	if(checkApkExist(BrowserActivity.this.getApplicationContext(),"cn.amazon.mShop.android")){
//		    		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url) );
//		    		startActivity(intent);
//		    	}else{
//		    		view.loadUrl(url);
//		    	}
//		    }else if(url.indexOf("yhd.com") > -1){
//		    	if(checkApkExist(BrowserActivity.this.getApplicationContext(),"com.thestore.main")){
//		    		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("http://", "wccbyihaodian://")) );
//		    		startActivity(intent);
//		    	}else{
//		    		view.loadUrl(url);
//		    	}
//		    }
	        return false;
	    }
	    
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	
	    }
	    
	    @Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// Store main URL
	    	if(bfirst){
	    		bfirst = false;
	    		if ( url.indexOf("taobao") > -1 || url.indexOf("tmall") > -1) {
	    			if(checkApkExist(BrowserActivity.this.getApplicationContext(),"com.taobao.taobao")){
	    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("http://", "taobao://")) );
	    				startActivity(intent);
	    			}
	    		}
	    	}
		}
	    
	}
	
	boolean checkApkExist(Context context, String packageName)
	{
		if (packageName == null || "".equals(packageName))
		{
			return false;
		}
		try
		{
			context.getPackageManager().getApplicationInfo(
			packageName,PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		}
			catch (NameNotFoundException e)
		{
			return false;
		}
	}
	
	@Override  
    public boolean onTouch(View v, MotionEvent event) {  
        createVelocityTracker(event);  
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            xDown = event.getRawX();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            xMove = event.getRawX();  
            //活动的距离  
            int distanceX = (int) (xMove - xDown);  
            //获取顺时速度  
            int xSpeed = getScrollVelocity();  
            //当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity  
            if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {  
                finish();  
            }  
            break;  
        case MotionEvent.ACTION_UP:  
            recycleVelocityTracker();  
            break;  
        default:  
            break;  
        }  
        return true;  
    }  
      
    /** 
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。 
     *  
     * @param event 
     *         
     */  
    private void createVelocityTracker(MotionEvent event) {  
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        mVelocityTracker.addMovement(event);  
    }  
      
    /** 
     * 回收VelocityTracker对象。 
     */  
    private void recycleVelocityTracker() {  
        mVelocityTracker.recycle();  
        mVelocityTracker = null;  
    }  
      
    /** 
     * 获取手指在content界面滑动的速度。 
     *  
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。 
     */  
    private int getScrollVelocity() {  
        mVelocityTracker.computeCurrentVelocity(1000);  
        int velocity = (int) mVelocityTracker.getXVelocity();  
        return Math.abs(velocity);  
    }  
	
}
