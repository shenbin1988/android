package com.huaguoshan.activity;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.huaguoshan.adapter.FeedListAdapter;
import com.huaguoshan.app_yulebagua.R;
import com.huaguoshan.dao.FeedDao;
import com.huaguoshan.entity.FeedItem;
import com.huaguoshan.util.APIUtil;
import com.huaguoshan.util.net.HttpConnect;
import com.huaguoshan.view.MListView;
import com.huaguoshan.view.MListView.OnRefreshListener;

public class CollectActivity extends BaseActivity {

	private MListView mListView;
	private FeedListAdapter mAdapter;
	private LinkedList<FeedItem> feedList = new LinkedList<FeedItem>();
	private String sid = "";
	private String send_url = APIUtil.GET_COLLECT;
	private APIUtil mApi;
	private FeedDao feedDao;
	private float scale;     //dp/px
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mApi = new APIUtil(this.getApplicationContext());
		feedDao = new FeedDao(this);
		feedList = feedDao.getCollectFeeds();
		
		scale = getResources().getDisplayMetrics().density;
		
		setContentView(R.layout.activity_collect);
		
		if(feedList.size() > 0){
			findViewById(R.id.loading_bar).setVisibility(View.GONE);
		}
		
		mListView = (MListView)findViewById(R.id.m_list_view);
		mAdapter = new FeedListAdapter(this, feedList);
		mListView.setAdapter(mAdapter);
		mListView.setCanLoadMore(false);
		mListView.setReleaseRefresh(R.string.sync_release_refresh);
		mListView.setPullToRefresh(R.string.sync_pull_to_refresh);
		mListView.setDoingHeadRefresh(R.string.sync_doing_head_refresh);
		mListView.setRefreshLasttime(R.string.sync_refresh_lasttime);
		mListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO 下拉刷新
				getData();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View contentView, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
				intent.putExtra("sid", sid);
				intent.putExtra("fid", feedList.get(position - 1).getId());
				startActivity(intent);
			}
		});
		View back = (View)findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
	private void getData(){
		if(HttpConnect.IsHaveInternet(this)){
			GetCollections asyncTask = new GetCollections();
			asyncTask.execute(send_url);
		}else{
			showToast("没有网络连接，请稍后再试");
			mListView.onRefreshComplete();
		}
	}
	
	/**
	 * 去服务器获取数据
	 * @author Administrator
	 *
	 */
	private class GetCollections extends AsyncTask<String, Integer, String>{
		public GetCollections(){}

		@Override
		protected String doInBackground(String... params) {
			//向服务器发送请求的数据
			List<NameValuePair> values = mApi.getCollect();
			return HttpConnect.getContent(params[0],values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result!=null){
				parserJsonData(result);
			}
			mListView.onRefreshComplete();
			toggleLoading(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
	}
	
	private void toggleLoading(boolean bShow){
//		if(bShow){
//			findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
//		}else{
//			findViewById(R.id.loading_bar).setVisibility(View.GONE);
//		}
	}
	
	/**
	 * 解析服务器返回的数据
	 * 
	 */
	private void parserJsonData(String data){
		try{
			JSONObject ret_data = new JSONObject(data);
			if(ret_data.getInt("code") == 1000){
				sid = ret_data.getString("sid");
				JSONArray arrayList= ret_data.getJSONArray("rt");
				feedList.clear();
				for(int i = arrayList.length() - 1; i>=0; i--){
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
					feedList.addFirst(feed);
				}
				feedDao.refreshFeedCollect(feedList);
			}
		}catch(Exception e){
			Log.e("JSON", "返回的feeds流解析出错");
		}
		mAdapter.notifyDataSetChanged();
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
	
}
