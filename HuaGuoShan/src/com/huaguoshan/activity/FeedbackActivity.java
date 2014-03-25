package com.huaguoshan.activity;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huaguoshan.app_yulebagua.R;
import com.huaguoshan.util.APIUtil;
import com.huaguoshan.util.net.HttpConnect;

public class FeedbackActivity extends BaseActivity implements OnClickListener{
	
	private APIUtil mApi;
	private TextView feed_content;
	private PopupWindow popupWindow;
	private boolean bInFeedback = false; //判断是否正在发送反馈信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		mApi = new APIUtil(this.getApplicationContext());
		
		feed_content = (TextView)findViewById(R.id.feed_content);
//		feed_content.setOnKeyListener(new OnKeyListener() {
//		    public boolean onKey(View v, int keyCode, KeyEvent event) {
//		        if(keyCode == 66) {
//		        	doFeedback();
//		        	return true;
//		        }
//		        return false;
//		    }
//		});
		
		ImageButton back = (ImageButton)findViewById(R.id.btn_back);
		back.setOnClickListener(this);
		
		Button submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.submit:
			if(!bInFeedback){
				doFeedback();
			}
			break;
		default:
			break;
		}
	}
	
	private void doFeedback(){
		String content = feed_content.getText().toString();
		SendFeelingToServer send_feeling = new SendFeelingToServer(content);
		send_feeling.execute(APIUtil.SEND_FEELING);
		hideKeyBoard();
	}
	
	private void hideKeyBoard(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(feed_content.getWindowToken(), 0);
	}
	
	private class SendFeelingToServer extends AsyncTask<String, Integer, String>{
		private String content;
		public SendFeelingToServer(String content){
			this.content = content;
		}
		
		@Override
		protected String doInBackground(String... params) {
			//向服务器发送请求的数据
			List<NameValuePair> values = mApi.getSendFeeling("", "", "7", content);
			return HttpConnect.getContent(params[0],values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			bInFeedback = false;
			if(result!=null){
				handleResult(result);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bInFeedback = true;
		}
		
	}
	
	private void handleResult(String result){
		try{
			JSONObject data = new JSONObject(result);
			int code = data.getInt("code");
			if(code == 1000){
				feed_content.setText("");
				View layout = LayoutInflater.from(this).inflate(R.layout.feedback_success, null);
				popupWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());  
				popupWindow.setOutsideTouchable(true); 
				popupWindow.showAtLocation(findViewById(R.id.body), Gravity.LEFT|Gravity.TOP, 0, 0);
				Button close = (Button)layout.findViewById(R.id.close);
				close.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
					}
				});
			}
		}catch(Exception e){
			
		}
	}
}
