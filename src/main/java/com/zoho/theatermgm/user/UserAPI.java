package com.zoho.theatermgm.user;

import com.zoho.theatermgm.accounts.AccountsAPI;
import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.exceptions.InvalidException;

import java.sql.ResultSet;
import java.util.Scanner;

public class UserAPI {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws Exception, InvalidException {
        int authID = AccountsAPI.commonCases();
        if(checkAuthIdExsist(authID)){
            try{
                int orgnID = getOrgID(authID);
                UserOptions.options(orgnID,authID);
            }
            catch (InvalidException e){
                System.out.println(e.getMessage());
            }
        }
        else{

            try{
                createOrg(authID);
                int orgnID = getOrgID(authID);
                UserOptions.options(orgnID,authID);
            }
            catch (InvalidException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static void createOrg(int authID) throws Exception, InvalidException {
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
    public static boolean checkAuthIdExsist(int authID) throws Exception {
        String q = "select id from org where authID = "+authID+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
        }
    }

}
