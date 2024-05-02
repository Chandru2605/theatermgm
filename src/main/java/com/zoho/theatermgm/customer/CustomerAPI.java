package com.zoho.theatermgm.customer;

import com.zoho.theatermgm.CommonAPI;
import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.exceptions.InvalidException;
import com.zoho.theatermgm.user.UserAPI;

import java.sql.ResultSet;
import java.util.Scanner;

public class CustomerAPI {

    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws Exception, InvalidException {
        int authID = CommonAPI.commonCases();
       if(checkAuthIdExsist(authID)){
           try{
               int uId = getUID(authID);
               CustomerOptions.options(uId);
           }
           catch (InvalidException e){
               System.out.println(e.getMessage());
           }
       }
       else{
           try {
               checkAuthIdExsistInOrg(authID);
               System.out.println("Customer Creation: ");
               System.out.println("Enter your name: ");
               String name = sc.next();
               System.out.println("Customer created successfully");
               String q1 = "insert into customer(name,authID) values('" + name + "'," + authID + ")";
               ConnectionUtil.insertQuery(q1);
               int uId = getUID(authID);
               CustomerOptions.options(uId);
           } catch (InvalidException e) {
               System.out.println(e.getMessage());
           }
       }
    }

    private static boolean checkAuthIdExsist(int authID) throws Exception, InvalidException {
        String q = "select id from customer where authID = "+authID+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
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
    public static void checkAuthIdExsistInOrg(int authID) throws Exception, InvalidException {
        String q = "select id from org where authID = "+authID+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            throw new InvalidException("Invalid user");
        }
    }

}
