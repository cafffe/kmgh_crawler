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
    private String DBname="kmdb";
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
            String sql = "insert into Newpost(new_cop,new_date,new_gznx,new_jobid,"
            		+ "new_place,new_post,new_sal,new_xlyq,new_cop_url,new_func_time,new_post_url,new_yyyq) "
            		+ "value(?,?,?,?,?,?,?,?,?,?,?,?)"; 
        	/*String sql = "insert into Newpost(new_cop) "
            		+ "value(?)";*/ 
            sta = con.prepareStatement(sql); 
            //PreparedStatement statement=con.prepareStatement("");
            //statement.executeUpdate("set names utf8;");
            new_func_time=new Date();
            sta.setString(1, newPostEntity.get_new_cop()); 
            sta.setString(2, newPostEntity.get_new_date()); 
            sta.setString(3, newPostEntity.get_new_gznx()); 
            sta.setString(4, newPostEntity.get_new_jobid()); 
            sta.setString(5, newPostEntity.get_new_place()); 
            sta.setString(6, newPostEntity.get_new_post()); 
            sta.setString(7, newPostEntity.get_new_sal()); 
            sta.setString(8, newPostEntity.get_new_xlyq()); 
            sta.setString(9, newPostEntity.get_new_cop_url()); 
            sta.setString(10, time.format(new_func_time).toString());
            sta.setString(11, newPostEntity.get_new_post_url());
            sta.setString(12, newPostEntity.get_new_yyyq());
            rows = sta.executeUpdate(); 
            /*if(rows>0) 
                System.out.println("sql updated"); */
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
