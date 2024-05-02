package com.zoho.theatermgm.analytics;

import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.exceptions.InvalidException;
import com.zoho.theatermgm.theater.TheaterAPI;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class RevenueReport {
    static Scanner sc=  new Scanner(System.in);
    public static void revenueReportByDate(int theaterID,long from,long to) throws Exception, InvalidException {
        String q = "SELECT T.Theatername,T.location,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') AS BookingDate,SUM(CASE WHEN B.`Option` = 'Book' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsBooked,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) AS Revenue,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsCancelled,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) AS Refund,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) - SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) as Profit FROM Booking B INNER JOIN `Show` SH ON B.showId = SH.showId INNER JOIN Screen SC ON SH.screenId = SC.screenId INNER JOIN Theater T ON SC.theaterId = T.theaterId WHERE T.theaterId = "+theaterID+" AND B.Date BETWEEN "+from+" AND "+to+" GROUP BY FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') order by FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d');";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            System.out.println("Theater  Location     Date       Booked   Cancelled    Revenue    Refund    Profit");
            System.out.println("----------------------------------------------------------------------------------");
            do{
                System.out.print(r.getString(1)+"      ");
                System.out.print(r.getString(2)+"  ");
                System.out.print(r.getString(3)+"     ");
                System.out.print(r.getInt(4)+"         ");
                System.out.print(r.getInt(6)+"          ");
                System.out.print(r.getInt(5)+"           ");
                System.out.print(r.getInt(7)+"       ");
                System.out.println(r.getInt(8));
            }while (r.next());
            System.out.println("----------------------------------------------------------------------------------");
        }
        else{
            throw new InvalidException("Nothing to show here");
        }
    }
    private static void revenueReportByShowTime(int theaterID,long from,long to) throws Exception, InvalidException {
        String q = "SELECT T.Theatername,T.location,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') AS BookingDate,SH.`ShowTime`,SUM(CASE WHEN B.`Option` = 'Book' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsBooked,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) AS Revenue,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsCancelled,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) AS Refund,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) - SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) as Profit FROM Booking B INNER JOIN `Show` SH ON B.showId = SH.showId INNER JOIN Screen SC ON SH.screenId = SC.screenId INNER JOIN Theater T ON SC.theaterId = T.theaterId WHERE T.theaterId = "+theaterID+" AND B.Date BETWEEN "+from+" AND "+to+" GROUP BY FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d'),SH.ShowTime ORDER BY SH.SHowTime;;";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            System.out.println("Theater    Location    Date    ShowTime    Booked    Cancelled    Revenue    Refund    Profit");
            System.out.println("---------------------------------------------------------------------------------------------");
            do{
                System.out.print(r.getString(1)+"       ");
                System.out.print(r.getString(2)+"    ");
                System.out.print(r.getString(3)+"  ");
                System.out.print(r.getString(4)+"      ");
                System.out.print(r.getInt(5)+"        ");
                System.out.print(r.getInt(7)+"         ");
                System.out.print(r.getInt(6)+"        ");
                System.out.print(r.getInt(8)+"      ");
                System.out.println(r.getInt(9));
            }while (r.next());
            System.out.println("---------------------------------------------------------------------------------------------");
        }
        else{
            throw new InvalidException("Nothing to show");
        }
    }
    private static void revenueReportByScreen(int theaterID, long from,long to) throws Exception, InvalidException {
        String q= "SELECT T.Theatername,T.location,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') AS BookingDate,SC.ScreenNumber,SUM(CASE WHEN B.`Option` = 'Book' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsBooked,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsCancelled,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) AS Revenue,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) AS Refund,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) - SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) as Profit FROM Booking B INNER JOIN `Show` SH ON B.showId = SH.showId INNER JOIN Screen SC ON SH.screenId = SC.screenId INNER JOIN Theater T ON SC.theaterId = T.theaterId WHERE T.theaterId = "+theaterID+" AND B.Date BETWEEN "+from+" AND "+to+" GROUP BY FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d'),SC.ScreenNumber ORDER BY SC.ScreenNumber ASC;";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            System.out.println("Theater    Location    Date    Screen    Booked    Cancelled    Revenue    Refund    Profit");
            System.out.println("-------------------------------------------------------------------------------------------");
            do{
                System.out.print(r.getString(1)+"       ");
                System.out.print(r.getString(2)+"    ");
                System.out.print(r.getString(3)+"  ");
                System.out.print(r.getInt(4)+"        ");
                System.out.print(r.getInt(5)+"          ");
                System.out.print(r.getInt(6)+"           ");
                System.out.print(r.getInt(7)+"       ");
                System.out.print(r.getInt(8)+"        ");
                System.out.println(r.getInt(9));
            }while (r.next());
            System.out.println("------------------------------------------------------------------------------------------");
        }
        else{
            throw new InvalidException("Nothing to show");
        }
    }
    private static void revenueReportByMovie(int theaterID,long from,long to) throws Exception, InvalidException {
        String q = "SELECT M.title,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') AS BookingDate,T.Theatername,T.location,SUM(CASE WHEN B.`Option` = 'Book' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsBooked,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsCancelled,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) AS Revenue,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) AS Refund,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) - SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) as Profit FROM Booking B INNER JOIN `Show` SH ON B.showId = SH.showId INNER JOIN Movie M on M.MovieID = SH.MovieID INNER JOIN Screen SC ON SH.screenId = SC.screenId INNER JOIN Theater T ON SC.theaterId = T.theaterId WHERE T.theaterId = "+theaterID+" AND B.Date BETWEEN "+from+" AND "+to+"  GROUP BY M.MovieID,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d');";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            System.out.println("Movie    Date        Theater    Location    Booked    Cancelled    Revenue    Refund    Profit");
            System.out.println("---------------------------------------------------------------------------------------------");
            do{
                System.out.print(r.getString(1)+"    ");
                System.out.print(r.getString(2)+"    ");
                System.out.print(r.getString(3)+"  ");
                System.out.print(r.getString(4)+"          ");
                System.out.print(r.getInt(5)+"         ");
                System.out.print(r.getInt(6)+"         ");
                System.out.print(r.getInt(7)+"          ");
                System.out.print(r.getInt(8)+"       ");
                System.out.println(r.getInt(9));
            }while (r.next());
            System.out.println("---------------------------------------------------------------------------------------------");
        }
        else{
            throw new InvalidException("Nothing to show");
        }
    }

    private static long getMillis(String myDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(myDate);
        long millis = date.getTime();
        return millis;
    }
    public static void revenueReport(int orgID) throws Exception, InvalidException {
        TheaterAPI.getTheaterDetails(orgID);
        System.out.println("Theater ID:");
        int theaterID = sc.nextInt();
        TheaterAPI.checkTheaterID(theaterID,orgID);
        System.out.println("From Date: [yyyy-MM-dd]");
        String from = sc.next();
        long fromMillis = getMillis(from+" 00:00:00");
        System.out.println("To Date: [yyyy-MM-dd]");
        String to = sc.next();
        long toMillis = getMillis(to+" 23:59:59");
        boolean loop = true;
        while (loop){
            System.out.println("1.Report by Date\n2.Report by ShowTime\n3.Report by Screen\n4.Report by Movie\n5.Exit");
            int c = sc.nextInt();
            switch (c){
                case 1:
                {
                    System.out.println("REVENUE REPORT BY DATE FROM "+from+" TO "+to);
                    System.out.println("-----------------------------------------------------");
                    revenueReportByDate(theaterID,fromMillis,toMillis);
                }
                break;
                case 2:
                {
                    System.out.println("REVENUE REPORT BY SHOWTIME FROM "+from+" TO "+to);
                    System.out.println("--------------------------------------------------------");
                    revenueReportByShowTime(theaterID,fromMillis,toMillis);
                }
                break;
                case 3:
                {
                    System.out.println("REVENUE REPORT BY SCREEN FROM "+from+" TO "+to);
                    System.out.println("--------------------------------------------------------");
                    revenueReportByScreen(theaterID,fromMillis,toMillis);
                }
                break;
                case 4:
                {
                    System.out.println("REVENUE REPORT BY MOVIE FROM "+from+" TO "+to);
                    System.out.println("--------------------------------------------------------");
                    revenueReportByMovie(theaterID,fromMillis,toMillis);
                }
                break;
                case 5:
                {
                    loop = false;
                }
                break;
                default:
                {
                    System.out.println("Wrong Choice");
                }
                break;
            }
        }
    }

}
