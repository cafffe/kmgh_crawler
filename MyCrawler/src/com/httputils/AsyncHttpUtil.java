package com.httputils;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

public class AsyncHttpUtil {
	public static AsyncHttpClient getAsyncHttpClient(){
		AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
		builder.setMaximumConnectionsTotal(10000);
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient(builder.build()); 
		return asyncHttpClient;
	}
}
