/**
 * 
 */
package com.newpost;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.dao.NewPostDao;
import com.entity.NewPostEntity;
import com.kmgh.QcwyHttpUtil;
import com.kmgh.utils.URLStatic;

/**
 * @author mawenneng
 * 前程无忧新上传工作（24h之内）
 */
public class NewPost implements Runnable{
	private String SearchEngine;
	private CloseableHttpClient httpClient;
	private static int threadnum=50;
	private String city;
	private NewPostDao newPostDao;
	public NewPost(String SearchEngine, String city){
		this.SearchEngine=SearchEngine;
		this.city=city;
		this.newPostDao=new NewPostDao();
		new ArrayList<>();
		try {
			httpClient=QcwyHttpUtil.getHttpclient();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//独立线程运行方法
	public void run(){
		System.out.println("...Thread NewPost start");
		GetCorpInfo(city,SearchEngine);
	}
	public Map<String, String> GetCorpInfo(String city,String SearchEngine){
		System.out.println("getcorpinfo");
		Map<String, String> map=new HashMap<>();
		String connUrl=null;
		if(SearchEngine.equals("qcwy")){
			connUrl=URLStatic.qcwy_work_url;
		}else{
			connUrl=URLStatic.zlzp_work_url;
		}
		try {
			int page = 0;
			connUrl=connUrl+"?keywordtype=2&stype=2&funtype=0000&keyword="+URLEncoder.encode(city, "gb2312")+"&curr_page=1";
			HttpGet httpPost=new HttpGet(connUrl);
			//System.out.println("connUrl:"+connUrl);
			HttpResponse res=httpClient.execute(httpPost);
			HttpEntity en=res.getEntity();
			String getResult =  EntityUtils.toString(en, "gb2312");
			page=findPage(getResult);
			//System.out.println(getResult);
			//跑多个线程
			int[] startPages=new int[threadnum];
			Thread[] threads=new Thread[threadnum];
			for(int i=0;i<threadnum;i++){
				startPages[i]=i*(page/threadnum);
				threads[i]=new Thread(new MyRunnable(city, startPages[i],i,page,threadnum));
				threads[i].start();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Http Error!");
			e.printStackTrace();
		}
		return map;
	}
	//设置新线程
	class MyRunnable implements Runnable{
		int startpage;
		String city;
		int threadid;
		int page;
		int threadnum;
		public MyRunnable(String city,int startpage,int threadid,int page,int threadnum) {
			// TODO Auto-generated constructor stub
			this.startpage=startpage;
			this.city=city;
			this.threadid=threadid;
			this.page=page;
			this.threadnum=threadnum;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("...thread "+(threadid+1)+" start!");
			String connUrl;
			for(int i=startpage;i<startpage+page/threadnum;i++){
				if(i%100==0)
					System.out.println("...reading page "+i);
				try {
					connUrl = URLStatic.qcwy_work_url+"?keywordtype=2&stype=2&funtype=0000&keyword="+URLEncoder.encode(city, "gb2312")+"&curr_page="+Integer.toString(i);
					HttpGet httpPost=new HttpGet(connUrl);
					//System.out.println("connUrl:"+connUrl);
					HttpResponse res;
					res = httpClient.execute(httpPost);
					HttpEntity en=res.getEntity();
					String getResult =  EntityUtils.toString(en,"gb2312");
					List<NewPostEntity> list=getNewPostEntity(getResult);
					for(int j=0;j<list.size();j++){
						newPostDao.insert(list.get(j));
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	//获取每个页面中的工作信息并返回工作实体
	private List<NewPostEntity> getNewPostEntity(String result) throws UnsupportedEncodingException{
		List<NewPostEntity> list=new ArrayList<>();
		int start = 0,end=0;
		while(result.indexOf("fbrq",end)!=-1){
			NewPostEntity tempEntity=new NewPostEntity();
			
			//job id
			start=result.indexOf("Jobid",end);
			end=result.indexOf("\" value",start);
			tempEntity.set_new_jobid(result.substring(start+5,end));
			//System.out.println("jobid-------->"+result.substring(start+5,end));
			
			//job url
			start=result.indexOf("href=\"",end);
			end=result.indexOf("\"",start+6);
			tempEntity.set_new_post_url(result.substring(start+6,end));
			//System.out.println("joburl-------->"+result.substring(start+6,end));
			
			//post name
			start=result.indexOf("blank",end);
			start=result.indexOf(">",start+5);
			end=result.indexOf("</a>",start+1);
			tempEntity.set_new_post(result.substring(start+1,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
			//System.out.println("postname-------->"+result.substring(start+1,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
			
			//cop url
			start=result.indexOf("href=\"",end);
			end=result.indexOf("\" class",start+6);
			String uu=new String(result.substring(start+6,end).getBytes(),"utf-8");
			tempEntity.set_new_cop_url(uu);
			//System.out.println("uu========"+uu);
			//System.out.println("copurl-------->"+result.substring(start+6,end));
			
			//cop name
			start=result.indexOf("blank\" >",end);
			end=result.indexOf("</a>",start+6);
			//String tt=new String(result.substring(start+"blank\" >".length(),end).replaceAll("[^\u4E00-\u9FA5]{3,40}", "").getBytes(), "gb2312");
			tempEntity.set_new_cop(result.substring(start+"blank\" >".length(),end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
			//System.out.println("tt============"+result.substring(start+"blank\" >".length(),end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
			//System.out.println("copname-------->"+result.substring(start+"blank\" >".length(),end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
			
			//work place
			start=result.indexOf("jobarea",end);
			start=result.indexOf(">",start+7);
			end=result.indexOf("<",start);
			tempEntity.set_new_place((result.substring(start+1,end)));
			//System.out.println("workplace-------->"+result.substring(start+1,end));
			
			//date
			start=result.indexOf("fbrq",end);
			start=result.indexOf(">",start+4);
			end=result.indexOf("<",start);
			tempEntity.set_new_date(result.substring(start+1,end));
			if(!tempEntity.get_new_post_url().equals("")){
				try {
					CloseableHttpClient postclient=QcwyHttpUtil.getHttpclient();
					HttpGet get=new HttpGet(tempEntity.get_new_post_url());
					//System.out.println(tempEntity.get_new_post_url());
					HttpResponse response=postclient.execute(get);
					HttpEntity entity=response.getEntity();
					String postresult=EntityUtils.toString(entity,"gb2312");
					//System.out.println(postresult);
					findPostInfo(postresult,tempEntity);
					//System.out.println(postresult);
				} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			list.add(tempEntity);
		}
		return list;
	}
	//获取每个result中的工作页数
	private int findPage(String result){
		System.out.println("...FindPage!");
		int page=0;
		int start=0,end=0;
		start=result.indexOf("navBold",end);
		start=result.indexOf("<td>",start+8);
		start=result.indexOf("/",start+4);
		end=result.indexOf("<",start+1);
		page=Integer.parseInt(result.substring(start+1,end).trim());
		return page;
	}
	//获取工作详细之信息
	private void findPostInfo(String result,NewPostEntity newPostEntity){
		int start=0,end=0;
		if(result.indexOf("发布日期")!=-1){
			start=result.indexOf("发布日期");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_func_time(result.substring(start+2,end));
		}
		
		if(result.indexOf("工作地点")!=-1){
			start=result.indexOf("工作地点");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_place(result.substring(start+2,end));
		}
		
		if(result.indexOf("工作年限")!=-1){
			start=result.indexOf("工作年限");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_gznx(result.substring(start+2,end));
		}
		if(result.indexOf("语言要求")!=-1){
			start=result.indexOf("语言要求");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start);
			//System.out.println(result.substring(start+2,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
			newPostEntity.set_yyyq(result.substring(start+2,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
		}
		if(result.indexOf(";历")!=-1){
			start=result.indexOf(";历");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_xlyq(result.substring(start+2,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
		}
		if(result.indexOf("薪资范围")!=-1){
			start=result.indexOf("薪资范围");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_sal(result.substring(start+2,end));
		}
	}
}
