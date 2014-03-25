package com.huaguoshan.entity;

import org.json.JSONArray;
import org.json.JSONObject;


public class FeedItem {
	private String id;
	private int type;
	private String crawl_url;
	private String go_link;
	private String crawl_source;
	private String title;
	private String desc;
	private String desc_img;
	private long pub_time;
	private JSONArray text;
	private JSONObject yunying;
	private boolean isRead = false;
	

	public FeedItem() {
		super();
	}
	
	public FeedItem(String id, int type, String crawl_url, String crawl_source, String title, 
			String desc, String desc_img, long pub_time) {
		super();
		this.id = id;
		this.type = type;
		this.crawl_url = crawl_url;
		this.crawl_source = crawl_source;
		this.title = title;
		this.desc = desc;
		this.desc_img = desc_img;
		this.pub_time = pub_time;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCrawl_url() {
		return crawl_url;
	}

	public void setCrawl_url(String crawl_url) {
		this.crawl_url = crawl_url;
	}

	public String getCrawl_source() {
		return crawl_source;
	}

	public void setCrawl_source(String crawl_source) {
		this.crawl_source = crawl_source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc_img() {
		return desc_img;
	}

	public void setDesc_img(String desc_img) {
		this.desc_img = desc_img;
	}

	public long getPub_time() {
		return pub_time;
	}

	public void setPub_time(long pub_time) {
		this.pub_time = pub_time;
	}

	public boolean save(){
		System.out.println("保存");
		return true;
	}
	
	public boolean delete(){
		return true;
	}

	public JSONArray getText() {
		return text;
	}
	
	public void setText(JSONArray text) {
		this.text = text;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getGo_link() {
		return go_link;
	}

	public void setGo_link(String go_link) {
		this.go_link = go_link;
	}

	public JSONObject getYunying() {
		return yunying;
	}

	public void setYunying(JSONObject yunying) {
		this.yunying = yunying;
	}
	
}
