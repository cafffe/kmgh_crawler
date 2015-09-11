package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.entity.NewPostEntity;

public class NewPostDao {
	private Connection con = null; 
    private PreparedStatement sta = null; 
	public NewPostDao(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307/kmghdb","root","Mawenneng123"); 
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("sql connect error!");
		}
	}
	public int insert(NewPostEntity newPostEntity){
        int rows = 0;
        try {
            String sql = "insert into Newpost(new_cop,new_date,new_gznx,new_jobid,"
            		+ "new_place,new_post,new_sal,new_xlyq,new_cop_url) "
            		+ "value(?,?,?,?,?,?,?,?,?)"; 
            sta = con.prepareStatement(sql); 
            sta.setString(1, newPostEntity.get_new_cop()); 
            sta.setString(2, newPostEntity.get_new_date()); 
            sta.setString(3, newPostEntity.get_new_gznx()); 
            sta.setString(4, newPostEntity.get_new_jobid()); 
            sta.setString(5, newPostEntity.get_new_place()); 
            sta.setString(6, newPostEntity.get_new_post()); 
            sta.setString(7, newPostEntity.get_new_sal()); 
            sta.setString(8, newPostEntity.get_new_xlyq()); 
            sta.setString(9, newPostEntity.get_new_cop_url()); 
            rows = sta.executeUpdate(); 
            if(rows > 0) 
                System.out.println("sql install successfully!!"); 
            
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("sql function error!");
		}finally {
			if(sta!=null){
				try {
					sta.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
}
