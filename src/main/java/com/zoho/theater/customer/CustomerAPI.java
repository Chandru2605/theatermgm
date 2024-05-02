package com.zoho.theater.customer;

import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.exceptions.InvalidException;
import com.zoho.theater.user.UserAPI;

import java.sql.ResultSet;
import java.util.Scanner;

public class CustomerAPI {
    private static int uId = 0;
    private static int authID = 0;
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws Exception{
        boolean loop = true;
        while (loop) {
            System.out.println("Welcome to Movie Ticket Booking System ---Customer Page");
            System.out.println("1.New user\n2.Old use\n3.Exitr");
            int userChoice = sc.nextInt();
            switch (userChoice) {
                case 1: {
                    try {
                        registerUser();
                    } catch (InvalidException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 2: {
                    try {
                        oldUserLogin();
                    } catch (InvalidException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 3: {
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

    private static void oldUserLogin() throws Exception, InvalidException {
        System.out.println("Enter Email: ");
        String email = sc.next();
        if(!UserAPI.checkEmailExists(email)){
            throw new InvalidException("Invalid email");
        }
        System.out.println("Password: ");
        String password = sc.next();
        if(!UserAPI.validateUser(email,password)){
            throw new InvalidException("Invalid username or password");
        }
        authID = UserAPI.getAuthID(email,password);
        try{
            uId = getUID(authID);
            CustomerOptions.options(uId);
        }
        catch (InvalidException e){
            System.out.println(e.getMessage());
        }
    }

    private static void registerUser() throws Exception, InvalidException {
        System.out.println("Registration: ");
        System.out.println("Enter Email: ");
        String email = sc.next();
        boolean emailExists = UserAPI.checkEmailExists(email);
        if(emailExists){
            throw new InvalidException("User already exists");
        }
        System.out.println("Set Password: ");
        String pass = sc.next();
        System.out.println("Confirm Password: ");
        String c_pass = sc.next();
        if(pass.equals(c_pass)){
            System.out.println("Enter your name: ");
            String name = sc.next();
            String q = "insert into auth(userID,password) values('"+email+"','"+pass+"')";
            ConnectionUtil.insertQuery(q);
            authID = UserAPI.getAuthID(email,pass);
            System.out.println("Customer created successfully");
            String q1 = "insert into customer(name,authID) values('"+name+"',"+authID+")";
            ConnectionUtil.insertQuery(q1);
            try{
                uId = getUID(authID);
                CustomerOptions.options(uId);
            }
            catch (InvalidException e){
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Password Mismatch");
        }
    }

    private static int getUID(int authID) throws Exception, InvalidException {
            String q = "select id from customer where authID = "+authID+";";
            ResultSet r = ConnectionUtil.selectQuery(q);
            if(r.next()){
                return r.getInt(1);
            }
            else{
                throw new InvalidException("Not a user");
            }
    }

}
