package com.zoho.theater.user;

import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.customer.CustomerOptions;
import com.zoho.theater.exceptions.InvalidException;

import java.sql.ResultSet;
import java.util.Scanner;

public class UserAPI {
    static Scanner sc = new Scanner(System.in);
    private static String uName = "";
    private static String Name = "";
    private static int uId = 0;
    public static void main(String[] args) throws Exception, InvalidException {
        System.out.println("Welcome to Movie Ticket Booking System");
        System.out.println("1.New user\n2.Old user");
        int userChoice = sc.nextInt();
        switch (userChoice){
            case 1:
            {
                registerUser();
                UserOptions.options(uName,Name, uId);
            }
            break;
            case 2:
            {
                System.out.println("Enter Email: ");
                String email = sc.next();
                if(!checkEmailExists(email)){
                    throw new InvalidException("Invalid email");
                }
                uName = email;
                System.out.println("Password: ");
                String password = sc.next();
                if(!validateUser(email,password)){
                    throw new InvalidException("Invalid username or password");
                }
                Name = getName(email,password);
                uId = getId(email,password);
                UserOptions.options(uName,Name,uId);
            }
            break;
            default:
            {
                System.out.println("Wrong Choice");
            }
            break;
        }
    }

    private static int getId(String email, String password) throws Exception {
        String q = "select id from user where userid = '"+email+"' and password = '"+password+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        return r.getInt(1);
    }

    private static String getName(String email, String password) throws Exception {
        String q = "select name from user where userid = '"+email+"' and password = '"+password+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        return r.getString(1);
    }

    private static boolean validateUser(String email, String password) throws Exception {
        String q = "select * from user where userid = '"+email+"' and password = '"+password+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
        }
    }

    private static void registerUser() throws Exception, InvalidException {
        System.out.println("Registration: ");
        System.out.println("Enter Email: ");
        String email = sc.next();
        boolean emailExists = checkEmailExists(email);
        if(emailExists){
            throw new InvalidException("User already exists");
        }
        uName = email;
        System.out.println("Set Password: ");
        String pass = sc.next();
        System.out.println("Confirm Password: ");
        String c_pass = sc.next();
        if(pass.equals(c_pass)){
            System.out.println("Enter Name: ");
            String name = sc.next();
            String q = "insert into user(userID,password,name) values('"+email+"','"+pass+"','"+name+"')";
            int r = ConnectionUtil.insertQuery(q);
            System.out.println("User created successfully");
            Name = getName(email,pass);
        }
        else{
            System.out.println("Password Mismatch");
        }
    }

    private static boolean checkEmailExists(String email) throws Exception {
        String q = "select * from user where userid = '"+email+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
        }
    }
}
