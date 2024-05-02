package com.zoho.theatermgm.accounts;

import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.exceptions.InvalidException;

import java.sql.ResultSet;
import java.util.Scanner;

public class AccountsAPI {
    static Scanner sc = new Scanner(System.in);
    public static int commonCases(){
        boolean loop = true;
        while (loop) {
            System.out.println("Welcome to Movie Ticket Booking System");
            System.out.println("1.New user\n2.Old user\n3.Exit");
            int userChoice = sc.nextInt();
            switch (userChoice) {
                case 1: {
                    try {
                        int authID = registerUser();
                        return authID;

                    } catch (InvalidException | Exception e) {
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
                            int authID = getAuthID(email,password);
                            return authID;
                        }
                        catch (InvalidException e){
                            System.out.println(e.getMessage());
                        }
                    }
                    catch (InvalidException | Exception e){
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
        return 0;
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
    private static int registerUser() throws Exception, InvalidException {
        System.out.println("Registration: ");
        System.out.println("Enter Email: ");
        String email = sc.next();
        boolean emailExists = AccountsAPI.checkEmailExists(email);
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
            int authID = getAuthID(email,pass);
            return authID;
        }
        else{
            throw new InvalidException("Password Mismatch");
        }
    }
}
