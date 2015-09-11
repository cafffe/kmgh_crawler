package com.kmgh.utils;
/*
 * 用来查找每个企业的信息
 */

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.kmgh.QcwyHttpUtil;

public class CorpInfo {
	//use 51job or zlzp
	private String SearchEngine;
	//Corp name
	private String Corp;
	private CloseableHttpClient httpClient;
	private static final String qcwyUrl="http://search.51job.com/jobsearch/search.html";
	private static final String zlzpUrl="http://search.51job.com/jobsearch/search.html";
	public CorpInfo(String SearchEngine,String Corp) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		this.SearchEngine=SearchEngine;//qcwy+zlzp
		this.Corp=Corp;
		this.httpClient=QcwyHttpUtil.getHttpclient();
	}
	public Map<String, String> GetCorpInfo(){
		System.out.println("getcorpinfo");
		Map<String, String> map=new HashMap<>();
		String connUrl=null;
		String redirectUrl=null;
		if(SearchEngine.equals("qcwy")){
			connUrl=qcwyUrl;
		}else{
			connUrl=zlzpUrl;
		}
		try {
			List<NameValuePair> searchParames = new ArrayList<>();
			searchParames.add(new BasicNameValuePair("keywordtype", "0"));
			searchParames.add(new BasicNameValuePair("keyword", new String(Corp.getBytes(), "gb2312")));
			System.out.println(Corp);
			HttpEntity he = new UrlEncodedFormEntity(searchParames,"gb2312");

			HttpPost httpPost=new HttpPost(connUrl);
			httpPost.setEntity(he);
			System.out.println("connUrl:"+connUrl);
			HttpResponse res=httpClient.execute(httpPost);
			int resCode=res.getStatusLine().getStatusCode();
			if(resCode==302){
				System.out.println("redirect found(302)!");
				System.out.println(res.toString());
				redirectUrl=res.toString().substring(res.toString().indexOf("Location: ")+10,
						res.toString().indexOf("lang=c")+6);
				System.out.println(redirectUrl);
			}
			
			HttpGet httpGet=new HttpGet(redirectUrl);
			HttpResponse newresponse=httpClient.execute(httpGet);
			HttpEntity en=newresponse.getEntity();
			String getResult =  EntityUtils.toString(en, "gb2312");
			System.out.println(getResult);
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Http Error!");
		}
		return map;
	}
}
