package com.huaguoshan.activity;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaguoshan.app_yulebagua.R;
import com.huaguoshan.dao.FeedDao;
import com.huaguoshan.entity.FeedItem;
import com.huaguoshan.util.APIUtil;
import com.huaguoshan.util.Utils;
import com.huaguoshan.util.img.ImageLoader;
import com.huaguoshan.util.net.HttpConnect;

public class DetailActivity extends BaseActivity implements OnClickListener{
	
	private String sid;
	private String fid;
	private APIUtil mApi;
	private FeedItem feed = new FeedItem();
	private Resources res;
	private ImageLoader imageLoader;
	private float fontScale; //sp/px
	private float scale;     //dp/px
	private int space;
	private FeedDao feedDao = new FeedDao(this);
	private boolean bInCollect = false;
	private int action_type = -1;
	
	private TextView ding_text;
	private TextView cai_text;
	private TextView collect_text;
	private JSONObject b2c_item;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mApi = new APIUtil(this.getApplicationContext());
		imageLoader = new ImageLoader(this,true);
		res = getResources();
		scale = res.getDisplayMetrics().density;
		fontScale = res.getDisplayMetrics().scaledDensity;
		space = (int)res.getDimension(R.dimen.text_space);
		
		setContentView(R.layout.activity_detail);
		Bundle extras = getIntent().getExtras();
		sid = extras.getString("sid");
		fid = extras.getString("fid");
		
		bInCollect = feedDao.isInCollect(fid);
		action_type = feedDao.getFeedActionType(fid);
		
		if(HttpConnect.IsHaveInternet(this)){
			getFeedFromServer asyncTask = new getFeedFromServer();
			asyncTask.execute(APIUtil.GET_FEED);
		}else{
			showNetToast("没有网络连接，请稍后再试");
		}
		
		ImageView back = (ImageView)findViewById(R.id.btn_back);
		back.setOnClickListener(this);
		View ding = (View)findViewById(R.id.ding);
		ding.setOnClickListener(this);
		View cai = (View)findViewById(R.id.cai);
		cai.setOnClickListener(this);
		View collect = (View)findViewById(R.id.collect);
		collect.setOnClickListener(this);
		
		ding_text = (TextView)findViewById(R.id.ding_text);
		cai_text = (TextView)findViewById(R.id.cai_text);
		collect_text = (TextView)findViewById(R.id.collect_text);
		
		if(bInCollect){
			changeCollectState(bInCollect);
		}
		changeActionState(action_type);
	}
	
	private class getFeedFromServer extends AsyncTask<String, Integer, String>{
		public getFeedFromServer(){}

		@Override
		protected String doInBackground(String... params) {
			//向服务器发送请求的数据
			List<NameValuePair> values = mApi.getFeed(sid, fid);
			return HttpConnect.getContent(params[0],values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result!=null){
				parserJsonData(result);
			}
			findViewById(R.id.detail_loading).setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
	}
	
	/**
	 * 解析服务器返回的数据
	 * 
	 */
	private void parserJsonData(String data){
//		System.out.println(data);
		try{
			JSONObject ret_data = new JSONObject(data);
			if(ret_data.getInt("code") == 1000){
				sid = ret_data.getString("sid");
				JSONObject content = ret_data.getJSONObject("rt");
				feed.setId(content.has("id") ? content.getString("id") : "");
				feed.setTitle(content.has("title") ? content.getString("title") : "");
				feed.setDesc(content.has("desc") ? content.getString("desc") : "");
				feed.setDesc_img(content.has("desc_img") ? content.getString("desc_img") : "");
				feed.setCrawl_source(content.has("crawl_source") ? content.getString("crawl_source") : "");
				feed.setCrawl_url(content.has("crawl_source") ? content.getString("crawl_url") : "");
				feed.setPub_time(content.has("pub_time") ? content.getLong("pub_time") : 0);
				feed.setText(content.has("text") ? content.getJSONArray("text") : null);
				feed.setType(content.has("type") ? content.getInt("type") : 0);
				feed.setGo_link(content.has("go_link") ? content.getString("go_link") : "");
				feed.setYunying(content.has("yunying") ? content.getJSONObject("yunying") : null);
				generateContentView();
			}
		}catch(Exception e){
			Log.e("JSON", "返回的feeds流解析出错");
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void generateContentView(){
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.feed_content);
		
		LayoutParams llp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, 0, 0, space);
		
		if(!feed.getTitle().equals("")){
			TextView title = new TextView(this);
			title.setLayoutParams(llp);
			title.setText(feed.getTitle());
			title.setTextColor(res.getColor(R.color.content_title));
			title.setTextSize(px2sp(res.getDimension(R.dimen.content_title)));
			title.setBottom(space);
			layout.addView(title);
		}
		
		TextView time = new TextView(this);
		time.setLayoutParams(llp);
		time.setText(feed.getCrawl_source() + " " + Utils.getTimeDifference(feed.getPub_time()));
		time.setTextColor(res.getColor(R.color.content_time));
		time.setTextSize(px2sp(res.getDimension(R.dimen.content_time)));
		time.setBottom(space);
		layout.addView(time);
		JSONArray text = feed.getText();
		if( text!=null ){
			try{
				String type;
				String content;
				for(int i = 0, length = text.length(); i < length; i++){
					JSONArray temp = text.getJSONArray(i);
					type = temp.getString(0);
					content = temp.getString(1);
					if(type.equals("0")){
						ImageView image = new ImageView(this);
//						LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//						params.setMargins(0, 0, 0, space);
//						image.setLayoutParams(params);
//						image.setScaleType(ScaleType.CENTER_CROP);
						layout.addView(image);
						if(content.indexOf("http://") == -1){
							content = APIUtil.URL + "/" + content;
						}
						imageLoader.DisplayImage(content, image);
					}else if(type.equals("1")){
						TextView p = new TextView(this);
						p.setLayoutParams(llp);
						p.setText(content);
						p.setTextColor(res.getColor(R.color.content_desc));
						p.setTextSize(px2sp(res.getDimension(R.dimen.content_desc)));
						p.setBottom(space);
						layout.addView(p);
					}
				}			
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		if(feed.getType() == 1){
			TextView go_link = new TextView(this);
			LayoutParams go_llp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			go_llp.setMargins(0, (int)(20*scale), 0, 0);
			go_llp.gravity = Gravity.CENTER;
			go_link.setBackgroundResource(R.drawable.go_link_back);
			go_link.setGravity(Gravity.CENTER);
			go_link.setText(res.getString(R.string.go_link));
			go_link.setTextColor(res.getColor(R.color.go_link));
			go_link.setTextSize(16);
			go_link.setLayoutParams(go_llp);
			go_link.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(DetailActivity.this, BrowserActivity.class);
					if(feed.getGo_link().equals("")){
						intent.putExtra("url", feed.getCrawl_url());
					}else{
						intent.putExtra("url", feed.getGo_link());
					}
					startActivity(intent);
				}
			});
			layout.addView(go_link);
		}else{
			TextView go_origin = new TextView(this);
			LayoutParams go_llp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			go_llp.setMargins(0, space*2, 0, space);
			go_llp.gravity = Gravity.CENTER;
			go_origin.setBackgroundResource(R.drawable.go_origin_back);
			go_origin.setText(res.getString(R.string.go_origin));
			go_origin.setTextSize(14);
			go_origin.setLayoutParams(go_llp);
			go_origin.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SendFeelingToServer send_feeling = new SendFeelingToServer();
					send_feeling.setType("5");
					send_feeling.execute(APIUtil.SEND_FEELING);
					Intent intent = new Intent(DetailActivity.this, BrowserActivity.class);
					intent.putExtra("url", feed.getCrawl_url());
					startActivity(intent);
				}
			});
			layout.addView(go_origin);
		}
		try{
			JSONObject yunying = feed.getYunying();
			if(yunying != null){
				int padding = (int)(10*scale);
				
				LayoutParams wrap = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout.LayoutParams left = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				left.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
				RelativeLayout.LayoutParams right = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				
				if(yunying.has("price_graph") && !yunying.getString("price_graph").equals("")){
					RelativeLayout header = new RelativeLayout(this);
					LayoutParams header_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					header_lp.setMargins(0, (int)(20*scale), 0, 0);
					header.setLayoutParams(header_lp);
					header.setPadding(padding, padding, padding, padding);
					header.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_action_back));
					
					LinearLayout leftLayout = new LinearLayout(this);
					leftLayout.setLayoutParams(left);
					header.addView(leftLayout);
					
					TextView left_lable = new TextView(this);
					left_lable.setLayoutParams(wrap);
					left_lable.setText(getResources().getString(R.string.high_price));
					left_lable.setTextColor(getResources().getColor(R.color.goods_extra_label));
					left_lable.setTextSize(15);
					leftLayout.addView(left_lable);
					
					if(!yunying.getString("highest_price").equals("")){
						TextView high_price = new TextView(this);
						high_price.setLayoutParams(wrap);
						high_price.setText(yunying.getString("highest_price"));
						high_price.setTextColor(getResources().getColor(R.color.goods_high_price));
						high_price.setTextSize(15);
						high_price.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.up), null);
						leftLayout.addView(high_price);
					}
					
					LinearLayout rightLayout = new LinearLayout(this);
					rightLayout.setLayoutParams(right);
					header.addView(rightLayout);
					
					TextView right_lable = new TextView(this);
					right_lable.setLayoutParams(wrap);
					right_lable.setText(getResources().getString(R.string.low_price));
					right_lable.setTextColor(getResources().getColor(R.color.goods_extra_label));
					right_lable.setTextSize(15);
					rightLayout.addView(right_lable);
					
					if(!yunying.getString("lowest_price").equals("")){
						TextView low_price = new TextView(this);
						low_price.setLayoutParams(wrap);
						low_price.setText(yunying.getString("lowest_price"));
						low_price.setTextColor(getResources().getColor(R.color.goods_low_price));
						low_price.setTextSize(15);
						low_price.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down), null);
						rightLayout.addView(low_price);
					}
					
					layout.addView(header);
					
					LayoutParams image_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					ImageView praph = new ImageView(this);
					praph.setLayoutParams(image_lp);
					praph.setScaleType(ScaleType.CENTER_CROP);
					int img_padding = (int)(1*scale);
					praph.setPadding(img_padding, 0, img_padding, img_padding);
					praph.setBackgroundDrawable(getResources().getDrawable(R.drawable.b2c_item_back));
					layout.addView(praph);
					String price_praph = yunying.getString("price_graph");
					if(price_praph.indexOf("http://") == -1){
						price_praph = APIUtil.URL + "/" + price_praph;
					}
					imageLoader.DisplayImage(price_praph, praph);
					
				}
				if(yunying.has("b2c_info") && !yunying.getJSONArray("b2c_info").getJSONObject(0).getString("name").equals("")){
					TextView b2c_header = new TextView(this);
					LayoutParams  b2c_lp= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					b2c_lp.setMargins(0, (int)(20*scale), 0, 0);
					b2c_header.setLayoutParams(b2c_lp);
					b2c_header.setText(getResources().getString(R.string.other_b2c));
					b2c_header.setTextSize(15);
					b2c_header.setTextColor(getResources().getColor(R.color.goods_other_label));
					b2c_header.setPadding(padding, padding, padding, padding);
					b2c_header.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_action_back));
					layout.addView(b2c_header);
					
					LayoutParams item_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					JSONArray b2c_info = yunying.getJSONArray("b2c_info");
					for(int i = 0; i < b2c_info.length(); i++){
						b2c_item = b2c_info.getJSONObject(i);
						if(!b2c_item.getString("name").equals("") && !b2c_item.getString("price").equals("")){
							RelativeLayout item_layout = new RelativeLayout(this);
							item_layout.setLayoutParams(item_lp);
							item_layout.setPadding(padding, padding, padding, padding);
							item_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.b2c_item_back));
							layout.addView(item_layout);
							
							TextView b2c_name = new TextView(this);
							b2c_name.setLayoutParams(wrap);
							b2c_name.setText(b2c_item.getString("name"));
							b2c_name.setTextColor(getResources().getColor(R.color.goods_other_label));
							b2c_name.setTextSize(14);
							item_layout.addView(b2c_name);
							
							TextView b2c_price = new TextView(this);
							b2c_price.setLayoutParams(right);
							b2c_price.setText("￥"+b2c_item.getString("price"));
							b2c_price.setTextColor(getResources().getColor(R.color.goods_other_price));
							b2c_price.setTextSize(14);
							b2c_price.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.go_link), null);
							b2c_price.setCompoundDrawablePadding(padding);
							item_layout.addView(b2c_price);
							
							item_layout.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									SendFeelingToServer send_feeling = new SendFeelingToServer();
									send_feeling.setType("5");
									send_feeling.execute(APIUtil.SEND_FEELING);
									Intent intent = new Intent(DetailActivity.this, BrowserActivity.class);
									try {
										intent.putExtra("url", b2c_item.getString("link"));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									startActivity(intent);
								}
							});
							layout.addView(item_layout);
						}
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	private int dp2px(int dp){
		return (int)(dp*scale+0.5f);
	}
	
	private float px2sp(float px){
		return px/fontScale+0.5f;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SendFeelingToServer send_feeling = new SendFeelingToServer();
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.ding:
			if(action_type == -1){
				action_type = 3;
				feedDao.addFeedAction(fid, action_type);
				send_feeling.setType("3");
				send_feeling.execute(APIUtil.SEND_FEELING);
			}else{
				showToast();
			}
			break;
		case R.id.cai:
			if(action_type == -1){
				action_type = 4;
				feedDao.addFeedAction(fid, action_type);
				send_feeling.setType("4");
				send_feeling.execute(APIUtil.SEND_FEELING);
			}else{
				showToast();
			}
			break;
		case R.id.collect:
			if(bInCollect){
				feedDao.deleteCollect(fid);
				send_feeling.setType("2");
				send_feeling.execute(APIUtil.SEND_FEELING);				
			}else{
				feedDao.addCollect(feed);
				send_feeling.setType("1");
				send_feeling.execute(APIUtil.SEND_FEELING);	
			}
			bInCollect = !bInCollect;
			break;
		default:
			break;
		}
	}
	
	private void showToast(){
		if(action_type == 3){
			Toast.makeText(getApplicationContext(), "您已经顶过", Toast.LENGTH_SHORT).show();
		}else if(action_type == 4){
			Toast.makeText(getApplicationContext(), "您已经踩过", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showNetToast(String msg){
		View layout = getLayoutInflater().inflate(R.layout.toast, null);
		TextView toast_content = (TextView)layout.findViewById(R.id.toast_content);
		toast_content.setText(msg);
		toast_content.setWidth(getWindowManager().getDefaultDisplay().getWidth());
		Toast toast = new Toast(this);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, (int)(50*scale+0.5f));
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	
	private void changeActionState(int type){
		if(type == 3){
			ding_text.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ding1_1), null, null, null);
			ding_text.setTextColor(getResources().getColor(R.color.detail_action_font_on));
		}else if(type == 4){
			cai_text.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.cai1_1), null, null, null);
			cai_text.setTextColor(getResources().getColor(R.color.detail_action_font_on));
		}
	}
	
	private void changeCollectState(boolean bInCollect){
		if(bInCollect){
			collect_text.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.collect1_1), null, null, null);
			collect_text.setTextColor(getResources().getColor(R.color.detail_action_font_on));
		}else{
			collect_text.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.collect1), null, null, null);
			collect_text.setTextColor(getResources().getColor(R.color.detail_action_font));
		}
	}
	
	private class SendFeelingToServer extends AsyncTask<String, Integer, String>{
		private String type;
		public SendFeelingToServer(){}
		
		public void setType(String type){
			this.type = type;
		}

		@Override
		protected String doInBackground(String... params) {
			//向服务器发送请求的数据
			List<NameValuePair> values = mApi.getSendFeeling(sid, fid, type, "");
			return HttpConnect.getContent(params[0],values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result!=null){
				try{
					JSONObject data = new JSONObject(result);
					int code = data.getInt("code");
					if(code == 1000){
						if(type == "1" || type == "2"){
							changeCollectState(bInCollect);
						}else if(type == "3" || type == "4"){
							changeActionState(action_type);
						}
					}
				}catch(Exception e){
					Log.e("SEND_FEELING","后台处理失败");
					if(type == "1" || type == "2"){
						bInCollect = !bInCollect;
					}else if(type == "3" || type == "4"){
						action_type = -1;
					}
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
	}
}
