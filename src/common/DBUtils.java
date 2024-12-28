package common;

import java.sql.*;

public class DBUtils {
    private static String url = "jdbc:mysql://127.0.0.1:3306/project_ssd";
    private static String DBUsername = "userCheck777";
    private static String DBPassword = "12ADMIN345!!";

    static Connection setUser(String role){
        String uname="";
        String pword="";
        if (role.equals("receptionist")){
            uname = "rec5666";
            pword = "12rec34!!";
        }else if(role.equals("manager")){
            uname = "man4555";
            pword = "12man34!!";
        }else if(role.equals("trainer")){
            uname = "tra2333";
            pword = "12tra34!!";
        }
        return establishConnection(uname,pword);
    }

    public static Connection establishConnection(String username,String password){
        Connection con = null;
        try{            
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection Successful");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return con;        
    }

    public static Connection establishConnection(){
        Connection con = null;
        try{            
            con = DriverManager.getConnection(url, DBUsername, DBPassword);
            System.out.println("Connection Successful");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return con;
    }
    public static void closeConnection(Connection con,Statement stmt){
        try{
            if (stmt!=null){
                stmt.close();
            }
            con.close();
            System.out.println("Connection is closed");        
        }catch(SQLException e){
            e.getMessage();
        }
    }
}