package com.zoho.theater.theater;

import com.zoho.theater.exceptions.InvalidException;
import com.zoho.theater.connection.ConnectionUtil;

import java.sql.ResultSet;
import java.util.Scanner;
public class TheaterAPI {
    static Scanner sc = new Scanner(System.in);
    public static boolean checkTheater(int theaterID, int orgId) throws Exception {
        String query = "Select TheaterID from Theater where TheaterID = "+theaterID+" and orgID = "+orgId+"";
        ResultSet rs = ConnectionUtil.selectQuery(query);
        if(rs.next()){
            return true;
        }
        else{
            return false;
        }
    }
    public static int getTheaterID(String theaterName, String location) throws Exception{
        String query = "Select TheaterID from Theater where TheaterName = '"+theaterName+"' and location = '"+location+"'";
        ResultSet rs = ConnectionUtil.selectQuery(query);
        if(rs.next()){
            return (rs.getInt(1));
        }
        else{
            return -1;
        }
    }
    public static void getTheaterDetails(int orgID) throws Exception, InvalidException {
        String q = "Select TheaterID,TheaterName,location from Theater  where orgID = "+orgID+";";;
        ResultSet r1 = ConnectionUtil.selectQuery(q);
        if(r1.next()){
            ResultSet r = ConnectionUtil.selectQuery(q);
            System.out.println("Your Theaters: ");
            System.out.println("ID    TheaterName      Location");
            System.out.println("-------------------------------");
            while (r.next()){
                System.out.print(r.getInt(1)+"       ");
                System.out.print(r.getString(2)+"           ");
                System.out.println(r.getString(3));
            }
        }
        else{
            throw new InvalidException("You don't have any theaters right now");
        }
    }
    private static void addTheaterEntry(String t_name, String location, int orgId) throws Exception{
        String query = "insert into Theater(TheaterName,Location,orgID) values('"+t_name+"','"+location+"',"+orgId+")";
        ConnectionUtil.insertQuery(query);
    }
    public static void addTheaters(int orgId) throws Exception, InvalidException {
        System.out.println("Theater Name: ");
        String theaterName = sc.next();
        System.out.println("Theater Location ");
        String location = sc.next();
        int thr_id = getTheaterID(theaterName,location);
        if(thr_id!=-1){
            throw new InvalidException("Theater already exists with this location");
        }
        addTheaterEntry(theaterName,location,orgId);
        System.out.println("Theater "+theaterName+" added successfully");
    }
    public static void showScreensInTheater(int theaterID) throws Exception {
        String q = "Select ScreenNumber from Screen where TheaterID = "+theaterID+";";;
        ResultSet r1 = ConnectionUtil.selectQuery(q);
        System.out.print("Available Screens: ");
        if(r1.next()){
            ResultSet r = ConnectionUtil.selectQuery(q);
            while (r.next()) {
                System.out.print(r.getInt(1) + "  ");
            }
        }
        else{
            System.out.print("No available screens");
        }
        System.out.println();

    }
}
