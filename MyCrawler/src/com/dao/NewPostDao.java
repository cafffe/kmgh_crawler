package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.entity.NewPostEntity;

public class NewPostDao {
	private Connection con = null; 
    private PreparedStatement sta = null; 
    private Date new_func_time;
    private String DBname="kmghdb";
    private String user="root";
    private String passwd="Mawenneng123";
    private String hostip="localhost:3307";
    SimpleDateFormat time;
	public NewPostDao(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//con = DriverManager.getConnection("jdbc:mysql://localhost:3307/kmghdb","root","Mawenneng123"); 
			con=DriverManager.getConnection("jdbc:mysql://"+hostip +"/"+DBname+"?user="+user+"&password="+passwd+"&useUnicode=true&characterEncoding=utf-8");
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("sql connect error!");
		}
		time=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	}
	public int insert(NewPostEntity newPostEntity){
        int rows = 0;
        try {
            /*String sql = "insert into Newpost(new_cop,new_date,new_gznx,new_jobid,"
            		+ "new_place,new_post,new_sal,new_xlyq,new_cop_url,new_func_time) "
            		+ "value(?,?,?,?,?,?,?,?,?,?)"; */
        	String sql = "insert into Newpost(new_cop) "
            		+ "value(?)"; 
            sta = con.prepareStatement(sql); 
            PreparedStatement statement=con.prepareStatement("");
            statement.executeUpdate("set names utf8;");
            System.out.println("<<<<<<<<"+newPostEntity.get_new_cop());
            String j=new String(newPostEntity.get_new_cop().getBytes("iso8859_1"));
            System.out.println(">>>>>>>>"+j);
            //????--???10 
            new_func_time=new Date();
            //sta.setString(1, new String(newPostEntity.get_new_cop().getBytes(),"utf-8")); 
            //sta.setString(1, j); 
            sta.setString(1, "xiaoming小明"); 
            /*sta.setString(2, new String(newPostEntity.get_new_date().getBytes(),"gb2312")); 
            sta.setString(3, new String(newPostEntity.get_new_gznx().getBytes(),"gb2312")); 
            sta.setString(4, new String(newPostEntity.get_new_jobid().getBytes(),"gb2312")); 
            sta.setString(5, new String(newPostEntity.get_new_place().getBytes(),"gb2312")); 
            sta.setString(6, new String(newPostEntity.get_new_post().getBytes(),"gb2312")); 
            sta.setString(7, new String(newPostEntity.get_new_sal().getBytes(),"gb2312")); 
            sta.setString(8, new String(newPostEntity.get_new_xlyq().getBytes(),"gb2312")); 
            sta.setString(9, new String(newPostEntity.get_new_cop_url().getBytes(),"gb2312")); 
            sta.setString(10, time.format(new_func_time).toString()); */
            rows = sta.executeUpdate(); 
            if(rows > 0) 
                System.out.println("sql update successfully!!"); 
            
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("sql function error!");
			e.printStackTrace();
		}finally {
			/*if(sta!=null){
				try {
					sta.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		}
		return 0;
	}
}
