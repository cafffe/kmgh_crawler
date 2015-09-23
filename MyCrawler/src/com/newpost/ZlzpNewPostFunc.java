/**
 * 
 */
package com.newpost;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
public class ZlzpNewPostFunc extends Thread{
	private String SearchEngine;
	private CloseableHttpClient httpClient;
	private static int threadnum=1;
	private String city;
	private NewPostDao newPostDao;
	private CloseableHttpClient[] httpClients;
	private final int pages=90;
	public ZlzpNewPostFunc(String SearchEngine, String city) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		this.SearchEngine=SearchEngine;
		this.city=city;
		this.newPostDao=new NewPostDao();
		new ArrayList<>();
		httpClients=new CloseableHttpClient[threadnum];
		try {
			httpClient=QcwyHttpUtil.getHttpclient(1);
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
		//System.out.println("getcorpinfo");
		Map<String, String> map=new HashMap<>();
		try {
			//System.out.println(getResult);
			//跑多个线程
			int[] startPages=new int[threadnum];
			//Thread[] threads=null;
			for(int i=0;i<threadnum;i++){
				try {
					httpClients[i]=QcwyHttpUtil.getHttpclient(i);
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
			for(int i=0;i<threadnum;i++){
				startPages[i]=i*(pages/threadnum);
				new Thread(new MyThread(city, startPages[i],i,threadnum,httpClients[i])).start();;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Http Error!");
			e.printStackTrace();
		}
		return map;
	}
	//设置新线程
	class MyThread extends Thread{
		int startpage;
		String city;
		int threadid;
		int threadnum;
		CloseableHttpClient httpClient;
		public MyThread(String city,int startpage,int threadid,int threadnum,CloseableHttpClient httpClient) {
			// TODO Auto-generated constructor stub
			this.startpage=startpage;
			this.city=city;
			this.threadid=threadid;
			this.threadnum=threadnum;
			this.httpClient=httpClient;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("...thread "+city+" start!");
			String connUrl="";
			for(int i=startpage;i<startpage+pages/threadnum;i++){
				if(i%10==0)
					System.out.println("......reading page "+i);
				try {
					connUrl = URLStatic.zlzp_work_url+Integer.toString(i);
					HttpGet httpGet=new HttpGet(connUrl);
					//System.out.println("connUrl:"+connUrl);
					HttpResponse res;
					res = httpClient.execute(httpGet);
					//System.out.println("...reading page "+i);
					HttpEntity en=res.getEntity();
					String getResult =  EntityUtils.toString(en,"gb2312");
					List<NewPostEntity> list;
					try {
						list = getNewPostEntity(i,getResult,httpClient);
					for(int j=0;j<list.size();j++){
						/*
						 * 打印出实体里信息
						 */
						//list.get(i).printElements();
						newPostDao.insert(list.get(j));
					}
					} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
			System.out.println("...thread "+(threadid+1)+" has ended!");
		}
		
	}
	//获取每个页面中的工作信息并返回工作实体
	private List<NewPostEntity> getNewPostEntity(int page,String result,CloseableHttpClient postclient) throws UnsupportedEncodingException, KeyManagementException,SocketTimeoutException, NoSuchAlgorithmException, KeyStoreException{
		//System.out.println("...page=========>>"+page);
		List<NewPostEntity> list=new ArrayList<>();
		int start = 0,end=0;
		while(result.indexOf("vacancyid\" value=\"CC",end)!=-1){
			NewPostEntity tempEntity=new NewPostEntity();
			tempEntity.set_new_post_src(URLStatic.zlzp);
			//job id
			start=result.indexOf("vacancyid\" value=\"CC",end);
			end=result.indexOf("J",start);
			tempEntity.set_new_jobid(result.substring(start+"vacancyid\" value=\"CC".length(),end));
			//System.out.println("jobid-------->"+tempEntity.get_new_jobid());
			
			//job url
			start=result.indexOf("href=\"",end);
			end=result.indexOf("\"",start+6);
			tempEntity.set_new_post_url(result.substring(start+6,end));
			//System.out.println("joburl-------->"+tempEntity.get_new_post_url());

			//post name
			start=result.indexOf(">",end);
			end=result.indexOf("</a>",start+1);
			tempEntity.set_new_post(result.substring(start+1,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));//去掉非中文
			//System.out.println("postname-------->"+tempEntity.get_new_post());
			
			//cop url
			start=result.indexOf("href=\"",end);
			end=result.indexOf("\"",start+6);
			tempEntity.set_new_cop_url(result.substring(start+6,end));
			//System.out.println("copurl-------->"+tempEntity.get_new_cop_url());
			
			//cop name
			start=result.indexOf("blank\">",end);
			end=result.indexOf("</a>",start+7);
			tempEntity.set_new_cop(result.substring(start+7,end));
			//System.out.println("copname-------->"+tempEntity.get_new_cop());
			
			start=result.indexOf("zwyx",end);
			end=result.indexOf("<",start);
			tempEntity.set_new_sal(result.substring(start+6,end));
			//System.out.println("zwyx-------->"+tempEntity.get_new_sal());
			
			//work place
			start=result.indexOf("gzdd",end);
			end=result.indexOf("<",start);
			tempEntity.set_new_place((result.substring(start+6,end)));
			//System.out.println("workplace-------->"+tempEntity.get_new_place());
			
			//date
			start=result.indexOf("gxsj",end);
			start=result.indexOf("<span>",start);
			end=result.indexOf("<",start+6);
			tempEntity.set_new_date(result.substring(start+6,end));
			if(!tempEntity.get_new_post_url().equals("")){
				try {
					//CloseableHttpClient postclient=QcwyHttpUtil.getHttpclient();
					HttpGet get=new HttpGet(tempEntity.get_new_post_url());
					//System.out.println(tempEntity.get_new_post_url());
					HttpResponse response=postclient.execute(get);
					HttpEntity entity=response.getEntity();
					String postresult=EntityUtils.toString(entity,"gb2312");
					//System.out.println(postresult);
					//System.out.println(tempEntity.get_new_post_url());
					findPostInfo(postresult,tempEntity);
					//System.out.println("......find post success");
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
	//获取工作详细之信息
	private void findPostInfo(String result,NewPostEntity newPostEntity){
		int start=0,end=0;
		if(result.indexOf("发布日期")!=-1){
			start=result.indexOf("发布日期");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start+8);
			newPostEntity.set_new_func_time(result.substring(start+2,end));
			//System.out.println(newPostEntity.get_new_func_time());
		}
		/*if(result.indexOf("工作地点")!=-1){
			start=result.indexOf("工作地点");
			start=result.indexOf("<strong>",start);
			end=result.indexOf("<",start+8);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_place(result.substring(start+8,end));
		}*/
		if(result.indexOf("工作经验")!=-1){
			start=result.indexOf("工作经验");
			start=result.indexOf("<strong>",start);
			end=result.indexOf("<",start+8);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_gznx(result.substring(start+8,end));
		}
		/*if(result.indexOf("语言要求")!=-1){
			start=result.indexOf("语言要求");
			start=result.indexOf("\">",start);
			end=result.indexOf("<",start);
			//System.out.println(result.substring(start+2,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
			newPostEntity.set_yyyq(result.substring(start+2,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
		}*/
		if(result.indexOf("最低学历")!=-1){
			start=result.indexOf("最低学历");
			start=result.indexOf("<strong>",start);
			end=result.indexOf("<",start+8);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_xlyq(result.substring(start+8,end).replaceAll("[^\u4E00-\u9FA5]{3,40}", ""));
		}
		if(result.indexOf("招聘人数")!=-1){
			start=result.indexOf("招聘人数");
			start=result.indexOf("<strong>",start);
			end=result.indexOf("<",start+8);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_renum(result.substring(start+8,end));
		}
		if(result.indexOf("公司行业")!=-1){
			start=result.indexOf("公司行业");
			start=result.indexOf("href",start);
			start=result.indexOf(">",start);
			end=result.indexOf("<",start+1);
			//System.out.println(result.substring(start+1,end));
			newPostEntity.set_new_hangye(result.substring(start+1,end));
		}
		if(result.indexOf("公司性质")!=-1){
			start=result.indexOf("公司性质");
			start=result.indexOf("<strong>",start);
			end=result.indexOf("<",start+8);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_cop_attr(result.substring(start+8,end).replace("&nbsp;", ""));
		}
		if(result.indexOf("公司规模")!=-1){
			start=result.indexOf("公司规模");
			start=result.indexOf("<strong>",start);
			end=result.indexOf("<",start+8);
			//System.out.println(result.substring(start+2,end));
			newPostEntity.set_new_cop_workers(result.substring(start+8,end));
		}
		if(result.indexOf("工作性质")!=-1){//<strong>set_new_post_attr
			start=result.indexOf("工作性质");
			start=result.indexOf("<strong>",start);
			end=result.indexOf("<",start+8);
			newPostEntity.set_new_post_attr(result.substring(start+8,end));
		}
	}
}
