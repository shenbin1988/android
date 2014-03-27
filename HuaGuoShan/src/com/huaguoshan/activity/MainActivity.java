package com.huaguoshan.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mobstat.StatService;
import com.huaguoshan.adapter.FeedListAdapter;
import com.huaguoshan.app_yulebagua.R;
import com.huaguoshan.dao.FeedDao;
import com.huaguoshan.entity.FeedItem;
import com.huaguoshan.fragment.MenuFragment;
import com.huaguoshan.fragment.MenuFragment.SLMenuListOnItemClickListener;
import com.huaguoshan.util.APIUtil;
import com.huaguoshan.util.net.HttpConnect;
import com.huaguoshan.view.MListView;
import com.huaguoshan.view.MListView.OnLoadMoreListener;
import com.huaguoshan.view.MListView.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity  extends SlidingFragmentActivity implements SLMenuListOnItemClickListener, OnClickListener, BDLocationListener{

	private FragmentManager fragmentManager = getSupportFragmentManager();
	private SlidingMenu slidingMenu;
	
	private MListView mListView;
	private FeedListAdapter mAdapter;
	
	private LinkedList<FeedItem> feedList;
	
	private LocationClient mLocationClient;
	private APIUtil mApi;
	
	public static String sid = "";
	private String send_url = APIUtil.GET_RT;
	private float scale;
	
	private FeedDao feedDao;
	
	private TextView noMoreFeed;
	
	private boolean bCanLoadMore = true; //是否含有更多历史
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause (this);
	}
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
//		Display display = getWindowManager().getDefaultDisplay();
//		String string = "手机的屏幕分辨率为：" + display.getWidth() + "x" + display.getHeight();
//		System.out.println(string);
		
		mApi = new APIUtil(this.getApplicationContext());
		scale = getResources().getDisplayMetrics().density;
		feedDao = new FeedDao(this);
		feedList = feedDao.getFeedsCache();
		if(feedList.size() == 0){
			findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
		}
		
		setBehindContentView(R.layout.frame_left_menu);
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowDrawable(R.drawable.drawer_shadow);
		slidingMenu.setShadowWidth(R.dimen.shadow_width);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
		
		FragmentTransaction fTransaction = fragmentManager.beginTransaction();
		fTransaction.replace(R.id.left_menu, new MenuFragment());
		fTransaction.commit();
		
		mListView = (MListView)findViewById(R.id.m_list_view);
		mAdapter = new FeedListAdapter(this, feedList);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO 下拉刷新
				getData(true);
			}
		});
		mListView.setOnLoadListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				// TODO 加载更多
				if(!bCanLoadMore){
					mListView.noMoreLoad();
				}else{
					getData(false);
				}
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View contentView, int position,
					long id) {
				// TODO Auto-generated method stub
				FeedItem feed = feedList.get(position - 1);
				feedDao.addRead(feed.getId());
				feed.setRead(true);
				TextView title = (TextView)contentView.findViewById(R.id.title);
				TextView desc = (TextView)contentView.findViewById(R.id.desc);
				int color_readed = getResources().getColor(R.color.feed_readed);
	    		title.setTextColor(color_readed);
	    		desc.setTextColor(color_readed);
				Intent intent = new Intent(MainActivity.this, DetailActivity.class);
				intent.putExtra("sid", sid);
				intent.putExtra("fid", feed.getId());
				startActivity(intent);
			}
			
		});
		
		noMoreFeed = (TextView)mListView.findViewById(R.id.load_more);
		
		ImageButton setting = (ImageButton)findViewById(R.id.btn_setting);
		setting.setOnClickListener(this);
		ImageButton refresh = (ImageButton)findViewById(R.id.btn_refresh);
		refresh.setOnClickListener(this);
		ImageView bottom_refresh = (ImageView)findViewById(R.id.bottom_refresh);
		bottom_refresh.setOnClickListener(this);
		
		
		if(HttpConnect.IsHaveInternet(this)){
			initLocationClient();
			mListView.clickToRefresh();
			getData(true);
		}else{
			showToast("没有网络连接，请稍后再试");
		}
		
	}
	
	private void initLocationClient(){
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(this);
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("gcj02");
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		
		timerTask();
	}
	
	public Handler mHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            	case 1:  
	            	mLocationClient.start();
	            	break;
            }  
            super.handleMessage(msg);
        }  
    };
    
    public Timer mTimer = new Timer();// 定时器  
    
    public void timerTask() {  
        //创建定时线程执行更新任务  
        mTimer.schedule(new TimerTask() {  
            @Override  
            public void run() {
            	Message message = new Message();      
                message.what = 1;
            	mHandler.sendMessage(message);// 向Handler发送消息 
            }  
        },5000, 1000*60*15);// 定时任务
    }

	@Override
	public void selectItem(int position, String title) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			toggle();
			break;
		case 1:
			Intent collect_itent = new Intent(this, CollectActivity.class);
			startActivity(collect_itent);
			break;
		case 2:
			Intent intent = new Intent(this, FeedbackActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_setting:
			toggle();
			break;
		case R.id.btn_refresh:
			mListView.clickToRefresh();
			mListView.setSelection(0);
			getData(true);
			break;
		case R.id.bottom_refresh:
			mListView.clickToRefresh();
			mListView.setSelection(0);
			getData(true);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 加载数据
	 */
	private void getData(boolean bRefresh) {
		if(HttpConnect.IsHaveInternet(this)){
			getFeedsFromServer asyncTask = new getFeedsFromServer(bRefresh);
			asyncTask.execute(send_url);
		}else{
			showToast("没有网络连接，请稍后再试");
			mListView.onRefreshComplete();
			mListView.onLoadMoreComplete();
		}
	}
	
	/**
	 * 去服务器获取数据
	 * @author Administrator
	 *
	 */
	private class getFeedsFromServer extends AsyncTask<String, Integer, String>{
		private boolean bRefresh;
		public getFeedsFromServer(boolean bRefresh){
			this.bRefresh = bRefresh;
		}

		@Override
		protected String doInBackground(String... params) {
			//向服务器发送请求的数据
			List<NameValuePair> values = mApi.getRt(bRefresh);
			return HttpConnect.getContent(params[0],values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			int number = -1;
			if(result!=null){
				number = parserJsonData(result,bRefresh);
			}
			if(bRefresh){
				mListView.onRefreshComplete();
				mListView.onLoadMoreComplete();
				bCanLoadMore = true;
			}else{
				if(number == 0){
					mListView.noMoreLoad();
//					Toast.makeText(getApplicationContext(), "没有更多历史，您可以点刷新推荐更多", Toast.LENGTH_LONG).show();
//					noMoreFeed.setVisibility(View.VISIBLE);
//					noMoreFeed.setText("没有更多历史，您可以点刷新推荐更多");
					bCanLoadMore = false;
				}else{
					mListView.onLoadMoreComplete();
				}
			}
			toggleLoading(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
	}
	
	private void toggleLoading(boolean bShow){
		if(bShow){
			findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.loading_bar).setVisibility(View.GONE);
		}
	}
	
	/**
	 * 解析服务器返回的数据
	 * 
	 */
	private int parserJsonData(String data,boolean bRefresh){
		int number = 0;
		try{
			JSONObject ret_data = new JSONObject(data);
			if(ret_data.getInt("code") == 1000){
				sid = ret_data.getString("sid");
				JSONArray arrayList= ret_data.getJSONArray("rt");
				number = arrayList.length();
				LinkedList<FeedItem> tempList = new LinkedList<FeedItem>();
				if(bRefresh){
					feedList.clear();
					for(int i = arrayList.length() - 1; i >= 0; i--){
						JSONObject obj = arrayList.getJSONObject(i);
						FeedItem feed = new FeedItem();
						feed.setId(obj.getString("id"));
						feed.setType(obj.has("type") ? obj.getInt("type") : 0);
						feed.setCrawl_url( obj.has("crawl_url") ? obj.getString("crawl_url") : "" );
						feed.setCrawl_source( obj.has("crawl_source") ? obj.getString("crawl_source") : "" );
						feed.setTitle( obj.has("title") ? obj.getString("title") : "" );
						feed.setDesc( obj.has("desc") ? obj.getString("desc") : "" );						
						feed.setDesc_img( obj.has("desc_img") ? obj.getString("desc_img").indexOf("http://") == -1 ? APIUtil.URL + "/" + obj.getString("desc_img") : obj.getString("desc_img") : "" );
						feed.setPub_time( obj.has("pub_time") ? obj.getLong("pub_time") : 0 );
						feed.setRead(feedDao.isReaded(obj.getString("id")));
						feedList.addFirst(feed);
						tempList.addFirst(feed);
					}
					for(int i = feedList.size() - 1; i >= 20; i--){
						feedList.remove(i);
					}
					if(arrayList.length() > 0){
						showToast("为您推荐了" + arrayList.length() + "条信息");
					}else{
						showToast("没有更多推荐，请稍后再试");
					}
					if(tempList.size() > 5){
						feedDao.refreshFeedCache(tempList);
					}
				}else{
					for(int i = 0, length = arrayList.length(); i < length; i++){
						JSONObject obj = arrayList.getJSONObject(i);
						FeedItem feed = new FeedItem();
						feed.setId(obj.getString("id"));
						feed.setType(obj.has("type") ? obj.getInt("type") : 0);
						feed.setCrawl_url( obj.has("crawl_url") ? obj.getString("crawl_url") : "" );
						feed.setCrawl_source( obj.has("crawl_source") ? obj.getString("crawl_source") : "" );
						feed.setTitle( obj.has("title") ? obj.getString("title") : "" );
						feed.setDesc( obj.has("desc") ? obj.getString("desc") : "" );						
						feed.setDesc_img( obj.has("desc_img") ? obj.getString("desc_img").indexOf("http://") == -1 ? APIUtil.URL + "/" + obj.getString("desc_img") : obj.getString("desc_img") : "" );
						feed.setPub_time( obj.has("pub_time") ? obj.getLong("pub_time") : 0 );
						feed.setRead(feedDao.isReaded(obj.getString("id")));
						feedList.add(feed);
					}
//					number = 0; //模拟没有更多加载
				}
			}
		}catch(Exception e){
			Log.e("JSON", "返回的feeds流解析出错");
		}
		mAdapter.notifyDataSetChanged();
		return number;
	}
	
	private void showToast(String msg){
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

	@Override
	public void onReceiveLocation(BDLocation location) {
		int type = location.getLocType();
		if(type == 61 || type == 161){
			String coord = Double.toString( location.getLatitude() ) + "," + Double.toString( location.getLongitude() );
			String coord_acc = Integer.toString( location.getLocType() );
			String region = location.getProvince() + "," + location.getCity() + "," + location.getDistrict() + "," + location.getStreet() + "," + location.getStreetNumber();
			mApi.setCoord(coord);
			mApi.setCoordAcc(coord_acc);
			mApi.setRegion(region);
		}else{
			mApi.setCoord("");
			mApi.setCoordAcc("");
			mApi.setRegion("");
		}
		new Thread(){
			public void run(){
				try{
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(APIUtil.SEND_CANDY);
					post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					HttpParams params = client.getParams();
					HttpConnectionParams.setConnectionTimeout(params, 5000);
					HttpConnectionParams.setSoTimeout(params, 5000);
					UrlEncodedFormEntity sendDate = new UrlEncodedFormEntity(mApi.getSendCandy(), HTTP.UTF_8);
					post.setEntity(sendDate);
					Log.v("Send", mApi.getSendCandy().toString());
					Log.v("Send", "开始发送地址信息");
					HttpResponse response = client.execute(post);					
				}catch(Exception e){
					Log.e("Send", "发送地址信息出错");
				}
		    }
		}.start();
		mLocationClient.stop();
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		
	}
		
	private long exitTime = 0;  
	  
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){  
	                  
	    if((System.currentTimeMillis()-exitTime) > 2000){  
	        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
	        exitTime = System.currentTimeMillis();  
	    }  
	    else{  
	        finish();  
	        System.exit(0);  
	    }  
	    	return true;
	    }  
	    return super.onKeyDown(keyCode, event);  
	}  
	
}
