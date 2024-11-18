package com.major.potholerectifier.utils;

import android.util.Log;

import com.major.potholerectifier.model.UserDetails;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private String CONNECTION_URL;
    private String user;
    private String pass;
    private java.sql.Statement stmt;
    private java.sql.Connection conn;

    private final String TAG="DbHelper";

    public DbHelper(String conn_url, String user, String pass) {
        this.CONNECTION_URL = conn_url;
        this.user = user;
        this.pass = pass;
    }

    public void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException {

        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        conn = DriverManager.getConnection(CONNECTION_URL+"?user="+user+"&password="+pass);
        //, user, pass
        stmt = conn.createStatement();

    }

    public void testData(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    List<UserDetails> users= new ArrayList<>();
                    Log.e(TAG,"Getting Test data");
                    try {
                        ResultSet rs = stmt.executeQuery("SELECT * FROM pothole_rectifier.pr_user");
                        while (rs.next()){
                            UserDetails usd = new UserDetails();
                            usd.setUsername(rs.getString(2));
                            usd.setPassword(rs.getString(3));
                            usd.setPhone(rs.getString(4));
                            usd.setUserId(rs.getInt(1));
                            users.add(usd);
                            Log.e(TAG,"Details: "+users);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public List<UserDetails> getUsers() {
        List<UserDetails> users= new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM pothole_rectifier.pr_user");
            while (rs.next()){
                UserDetails usd = new UserDetails();
                usd.setUsername(rs.getString(2));
                usd.setPassword(rs.getString(3));
                usd.setPhone(rs.getString(4));
                usd.setUserId(rs.getInt(1));
                users.add(usd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;


    }
}
