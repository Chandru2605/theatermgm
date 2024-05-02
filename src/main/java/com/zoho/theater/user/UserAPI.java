package com.zoho.theater.user;

import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.exceptions.InvalidException;

import java.sql.ResultSet;
import java.util.Scanner;

public class UserAPI {
    static Scanner sc = new Scanner(System.in);
    private static int authID = 0;
    public static int orgnID = 0;
    public static void main(String[] args) throws Exception, InvalidException {
        boolean loop = true;
        while (loop) {
            System.out.println("Welcome to Movie Ticket Booking System --- Admin Section");
            System.out.println("1.New user\n2.Old user\n3.Exit");
            int userChoice = sc.nextInt();
            switch (userChoice) {
                case 1: {
                    try {
                        registerUser();
                        createOrg();
                        try {
                            orgnID = getOrgID(authID);
                            UserOptions.options(orgnID, authID);
                        }
                        catch (InvalidException e){
                            System.out.println(e.getMessage());
                        }
                    } catch (InvalidException e) {
                        System.out.println(e.getMessage());
                    }

                }
                break;
                case 2: {
                    System.out.println("Enter Email: ");
                    String email = sc.next();
                   try {
                       checkEmailExists(email);
                       System.out.println("Password: ");
                       String password = sc.next();
                       try{
                           validateUser(email, password);
                           authID = getAuthID(email,password);
                           orgnID = getOrgID(authID);
                           UserOptions.options(orgnID,authID);
                       }
                       catch (InvalidException e){
                           System.out.println(e.getMessage());
                       }
                   }
                   catch (InvalidException e){
                       System.out.println(e.getMessage());
                   }

                }
                break;
                case 3:
                {
                    loop = false;
                }
                break;
                default: {
                    System.out.println("Wrong Choice");
                }
                break;
            }
        }
    }

    private static void createOrg() throws Exception, InvalidException {
        System.out.println("Org Creation");
        System.out.println("Enter you organization name: ");
        String orgName = sc.next();
        String q = "insert into org(name,authID) values('"+orgName+"',"+authID+")";
        ConnectionUtil.insertQuery(q);
        int orgID = getOrgID(authID);
        System.out.println("Enter your name: ");
        String name = sc.next();
        String q1 = "insert into user(name,orgID,authID,role) values('"+name+"',"+orgID+","+authID+",1)";
        ConnectionUtil.insertQuery(q1);
    }

    private static int getOrgID(int authID) throws Exception, InvalidException {
        String q = "select id from org where authID = "+authID+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
       if(r.next()){
           return r.getInt(1);
       }
       else{
           throw new InvalidException("Not a part of any organisation");
       }
    }


    public static int getAuthID(String email, String password) throws Exception {
        String q = "select id from auth where userid = '"+email+"' and password = '"+password+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        return r.getInt(1);
    }

    public static boolean validateUser(String email, String password) throws Exception, InvalidException {
        String q = "select * from auth where userid = '"+email+"' and password = '"+password+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            throw new InvalidException("Invalid Username or Password");
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
        System.out.println("Set Password: ");
        String pass = sc.next();
        System.out.println("Confirm Password: ");
        String c_pass = sc.next();
        if(pass.equals(c_pass)){
            String q = "insert into auth(userID,password) values('"+email+"','"+pass+"')";
            ConnectionUtil.insertQuery(q);
            authID = getAuthID(email,pass);
        }
        else{
            throw new InvalidException("Password Mismatch");
        }
    }

    public static boolean checkEmailExists(String email) throws Exception, InvalidException {
        String q = "select * from auth where userid = '"+email+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
        }
    }
}
