package com.zoho.theatermgm.screen;

import com.zoho.theatermgm.exceptions.InvalidException;
import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.theater.TheaterAPI;

import java.sql.ResultSet;
import java.util.Scanner;

public class ScreenAPI {
    static Scanner sc = new Scanner(System.in);
    private static int getScreenID(int screenNumber,int ticketId) throws Exception{
        String query = "Select ScreenID from Screen where ScreenNumber = "+screenNumber+" and TheaterID = "+ticketId+" ";
        ResultSet rs = ConnectionUtil.selectQuery(query);
        if(rs.next()){
            return (rs.getInt(1));
        }
        else{
            return -1;
        }
    }
    public static void addSeats(int screenId) throws Exception, InvalidException {
        int start = 1;
        System.out.println("No of Seats to add: ");
        int noOfSeats = sc.nextInt();
        showClassDetails();
        while (noOfSeats>0){
            System.out.println("Enter class ID: ");
            int clsID = sc.nextInt();
            try {
                checkClassID(clsID);
                System.out.println("Enter no of seat for the class: ");
                int seatCount = sc.nextInt();
                if (seatCount > noOfSeats) {
                    throw new InvalidException("Seat Limit Exceeded");
                }
                int a = start + seatCount;
                for (int i = start; i < a; i++) {
                    String query = "insert into Seat(ScreenID,SeatNumber,ClassID) values(" + screenId + "," + i + "," + clsID + ")";
                    ConnectionUtil.insertQuery(query);
                    noOfSeats--;
                }
                System.out.println("Remaining seats to add: " + noOfSeats);
                start += seatCount;
            }
            catch (InvalidException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static void checkClassID(int clsID) throws Exception, InvalidException {
        String q = "select * from theater.class where classID = "+clsID+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(!r.next()){
            throw new InvalidException("Invalid Class ID Chosen");
        }
    }

    private static void showClassDetails() throws Exception {
        String q = "Select * from theater.class";;
        ResultSet r = ConnectionUtil.selectQuery(q);
        System.out.println("ID    Class    Amount");
        System.out.println("---------------------");
        while (r.next()){
            System.out.println(r.getInt(1)+"     "+r.getString(2)+"      "+r.getInt(3));
        }
    }

    public static boolean getScreenBookedStatus(int scrId, long date) throws Exception{
        String q = "Select * from theater.Show where ScreenID = "+scrId+" and Date = "+date+" ";;
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
        }
    }
    private static int addScreen(int screenNumber,int theaterID) throws Exception{
        String query = "insert into Screen(ScreenNumber,TheaterID) values('"+screenNumber+"',"+theaterID+")";
        ConnectionUtil.insertQuery(query);
        return getScreenID(screenNumber,theaterID);
    }
    public static void add_Screen(int orgId) throws Exception,InvalidException {
        TheaterAPI.getTheaterDetails(orgId);
        System.out.println("Enter Theater ID: ");
        int theaterID = sc.nextInt();
        TheaterAPI.checkTheaterID(theaterID,orgId);
            TheaterAPI.showScreensInTheater(theaterID);
            System.out.println("Screen number to add: ");
            int screenNumber = sc.nextInt();
            String query = "Select ScreenID from Screen where ScreenNumber = '" + screenNumber + "' and TheaterID = " + theaterID + " ";
            ResultSet rs = ConnectionUtil.selectQuery(query);
            if (rs.next()) {
                throw new InvalidException("Theater already have this screen");
            }
               
              int scr_id = addScreen(screenNumber, theaterID);
               try {
                   addSeats(scr_id);
                   System.out.println("Screen " + screenNumber + " added successfully for Theater ");
               }
               catch (InvalidException e){
                   System.out.println(e.getMessage());
               }
    }

}
