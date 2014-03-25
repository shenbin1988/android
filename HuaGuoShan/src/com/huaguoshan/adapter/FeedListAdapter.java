package com.huaguoshan.adapter;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huaguoshan.activity.MainActivity;
import com.huaguoshan.app_yulebagua.R;
import com.huaguoshan.dao.FeedDao;
import com.huaguoshan.entity.FeedItem;
import com.huaguoshan.util.APIUtil;
import com.huaguoshan.util.Utils;
import com.huaguoshan.util.img.ImageLoader;
import com.huaguoshan.util.net.HttpConnect;

public class FeedListAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;
    private Context context;
    private ImageLoader imageLoader;
    private LinkedList<FeedItem> feedsList;
    private float scale;
    private PopupWindow mPopupWindow;
    private FeedDao feedDao;
    private APIUtil mApi;
//    private SendFeelingToServer send_feeling;
    private String sid;
    
    public FeedListAdapter() {
    	super();
    }
    
    public FeedListAdapter(Activity activity, LinkedList<FeedItem> feeds) {
    	feedsList = feeds;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = activity.getApplicationContext();
        feedDao = new FeedDao(context);
        mApi = new APIUtil(context);
//        send_feeling = new SendFeelingToServer();
        scale = activity.getResources().getDisplayMetrics().density;
        imageLoader = new ImageLoader(activity.getApplicationContext(), true);
    }

    public int getCount() {
        return feedsList.size();
    }

    public Object getItem(int position) {
        return feedsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, final ViewGroup parent) {
    	final FeedItem feed = feedsList.get(position);
        View vi = convertView;
    	vi = inflater.inflate(R.layout.feed_item, null);
    	TextView title = (TextView)vi.findViewById(R.id.title);
    	TextView desc = (TextView)vi.findViewById(R.id.desc);
    	ImageView desc_img = (ImageView)vi.findViewById(R.id.desc_img);
    	TextView extro = (TextView)vi.findViewById(R.id.extro);
    	ImageView comment = (ImageView)vi.findViewById(R.id.comment);
    	
    	title.setText(feed.getTitle());
    	if(feed.getTitle().equals("")){
    		title.setVisibility(View.GONE);
    	}
    	desc.setText(feed.getDesc().length() > 80 ? feed.getDesc().substring(0, 80) + "..." : feed.getDesc());
    	if(feed.getDesc().equals("")){
    		desc.setVisibility(View.GONE);
    	}
    	extro.setText(feed.getCrawl_source() + "/" + Utils.getTimeDifference(feed.getPub_time()));
    	if(feed.getDesc_img().equals("")){
    		desc_img.setVisibility(View.GONE);
    	}else{
    		imageLoader.DisplayImage(feed.getDesc_img(), desc_img);
    	}
    	
    	if(feed.isRead()){
    		int color_readed = context.getResources().getColor(R.color.feed_readed);
    		title.setTextColor(color_readed);
    		desc.setTextColor(color_readed);
    	}
    	
    	comment.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			View layout = LayoutInflater.from(v.getContext()).inflate(R.layout.feed_action, null);
    			View ding = (View)layout.findViewById(R.id.ding);
    			if(feedDao.getFeedActionType(feed.getId()) == 3){
    				TextView t = (TextView)layout.findViewById(R.id.ding_text);
    				t.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ding2_1), null, null, null);
    				t.setTextColor(context.getResources().getColor(R.color.detail_action_font2_on));
    			}
    			ding.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(feedDao.getFeedActionType(feed.getId()) == -1){
							feedDao.addFeedAction(feed.getId(), 3);
							SendFeelingToServer send_feeling = new SendFeelingToServer(MainActivity.sid, feed.getId(), "3");
							send_feeling.execute(APIUtil.SEND_FEELING);
						}
						mPopupWindow.dismiss();
					}
				});
    			View cai = (View)layout.findViewById(R.id.cai);
    			if(feedDao.getFeedActionType(feed.getId()) == 4){
    				TextView t = (TextView)layout.findViewById(R.id.cai_text);
    				t.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.cai2_1), null, null, null);
    				t.setTextColor(context.getResources().getColor(R.color.detail_action_font2_on));
    			}
				cai.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					if(feedDao.getFeedActionType(feed.getId()) == -1){
    						feedDao.addFeedAction(feed.getId(), 4);
							SendFeelingToServer send_feeling = new SendFeelingToServer(MainActivity.sid, feed.getId(), "4");
							send_feeling.execute(APIUtil.SEND_FEELING);
						}
						mPopupWindow.dismiss();
    				}
    			});
    			View collect = (View)layout.findViewById(R.id.collect);
    			if(feedDao.isInCollect(feed.getId())){
    				TextView t = (TextView)layout.findViewById(R.id.collect_text);
    				t.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.collect2_1), null, null, null);
    				t.setTextColor(context.getResources().getColor(R.color.detail_action_font2_on));
    			}
    			collect.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					if(feedDao.isInCollect(feed.getId())){
    						feedDao.deleteCollect(feed.getId());
    						SendFeelingToServer send_feeling = new SendFeelingToServer(MainActivity.sid, feed.getId(), "2");
							send_feeling.execute(APIUtil.SEND_FEELING);
    					}else{
    						feedDao.addCollect(feed);
    						SendFeelingToServer send_feeling = new SendFeelingToServer(MainActivity.sid, feed.getId(), "1");
							send_feeling.execute(APIUtil.SEND_FEELING);
    					}
    					mPopupWindow.dismiss();
    				}
    			});
    			mPopupWindow = new PopupWindow(layout, (int)(260*scale+0.5f), (int)(40*scale+0.5f), true);
    			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());  
    			mPopupWindow.setOutsideTouchable(true); 
    			mPopupWindow.showAsDropDown(v, -(int)(260*scale+8.5f), -(int)(35*scale+0.5f));
    		}
    	});
    		
        return vi;
    }
    
    private class SendFeelingToServer extends AsyncTask<String, Integer, String>{
		private String sid;
		private String fid;
		private String type;
		public SendFeelingToServer(String sid, String fid, String type){
			this.sid = sid;
			this.fid = fid;
			this.type = type;
		}
		
		public void setType(String type){
			this.type = type;
		}
		public void setSid(String sid){
			this.sid = sid;
		}
		public void setFid(String fid){
			this.fid = fid;
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
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
	}

}
