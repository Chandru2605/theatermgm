package com.zoho.theatermgm.show;

import com.zoho.theatermgm.exceptions.InvalidException;
import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.movie.MovieAPI;
import com.zoho.theatermgm.screen.ScreenAPI;
import com.zoho.theatermgm.theater.TheaterAPI;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ShowAPI {
    static Scanner sc = new Scanner(System.in);
    private static int addShows(int scr_id, long date, int movie, int showTime, int spec) throws Exception{
        String query1 = "insert into theater.Show(ScreenID,MovieID,ShowTime,Date,Special) values("+scr_id+","+movie+","+showTime+","+date+","+spec+")";
        ConnectionUtil.insertQuery(query1);
        String getMrngShowID = "Select ShowID from theater.Show where ScreenID ="+scr_id+" and ShowTime = "+showTime+" and date = "+date+" ";
        ResultSet rs1 = ConnectionUtil.selectQuery(getMrngShowID);
        rs1.next();
        int showID = rs1.getInt(1);
        return showID;
    }
    public static void add_Shows(int orgId) throws Exception, InvalidException {
        TheaterAPI.getTheaterDetails(orgId);
        System.out.println("Enter Theater ID:  ");
        int theaterID = sc.nextInt();
        TheaterAPI.checkTheaterID(theaterID, orgId);
        String q = "Select ScreenNumber from Screen where TheaterID = "+theaterID+" ";;
        ResultSet r = ConnectionUtil.selectQuery(q);
        ArrayList<String> screenNames = new ArrayList<>();
        while (r.next()){
            screenNames.add(r.getString(1));
        }
        if(screenNames.size()==0){
            throw new InvalidException("No Screens Available");
        }
            System.out.println("Select Screen Number : " + screenNames);
            int screenNumber = sc.nextInt();
            String query = "Select ScreenID from Screen where ScreenNumber = '" + screenNumber + "' and TheaterID = "+theaterID+" ";
            ResultSet rs = ConnectionUtil.selectQuery(query);
            rs.next();
            int scrId = rs.getInt(1);
            System.out.println("Enter Date:[yyyy-mm-dd]");
            String date = sc.next();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date da =  formatter.parse(date);
            long fDate = da.getTime();
            if (ScreenAPI.getScreenBookedStatus(scrId, fDate)) {
                throw new InvalidException("Already Updated");
            }
            showCheck(scrId,fDate);
            System.out.println("Shows updated successfully");

    }

    private static void showCheck(int scrId, long fDate) throws Exception {
        System.out.println("Do you want morning movie will be a Special Movie?[1-Yes/2-No]");
        int Mchoice  =sc.nextInt();
        if(Mchoice==1){
            MovieAPI.showMovieDetails();
            System.out.println("Choose Morning Movie ID: ");
            int mrngSpecMov = sc.nextInt();
            int mShwID = addShows(scrId,fDate,mrngSpecMov,1,1);
            addShowSeat(scrId,mShwID);
            addSpecialShow(scrId,mShwID);
        }
        else{
            MovieAPI.showMovieDetails();
            System.out.println("Choose Morning Movie ID: ");
            int mrngMov = sc.nextInt();
            int shwID = addShows(scrId,fDate,mrngMov,1,0);
            addShowSeat(scrId,shwID);
        }
        System.out.println("Do you want afternoon movie will be a Special Movie?[1-Yes/2-No]");
        int Achoice  =sc.nextInt();
        if(Achoice==1){
            MovieAPI.showMovieDetails();
            System.out.println("Choose Morning Movie ID: ");
            int aftrSpecMov = sc.nextInt();
            int aShwID = addShows(scrId,fDate,aftrSpecMov,2,1);
            addShowSeat(scrId,aShwID);
            addSpecialShow(scrId,aShwID);
        }
        else{
            MovieAPI.showMovieDetails();
            System.out.println("Choose Morning Movie ID: ");
            int mrngSpecMov = sc.nextInt();
            int shwID= addShows(scrId,fDate,mrngSpecMov,2,0);
            addShowSeat(scrId,shwID);
        }
        System.out.println("Do you want Evening movie will be a Special Movie?[1-Yes/2-No]");
        int Echoice  =sc.nextInt();
        if(Echoice==1){
            MovieAPI.showMovieDetails();
            System.out.println("Choose Morning Movie ID: ");
            int aftrSpecMov = sc.nextInt();
            int nShwID = addShows(scrId,fDate,aftrSpecMov,3,1);
            addSpecialShow(scrId,nShwID);
            addShowSeat(scrId,nShwID);
        }
        else{
            MovieAPI.showMovieDetails();
            System.out.println("Choose Morning Movie ID: ");
            int mrngSpecMov = sc.nextInt();
            int shwID = addShows(scrId,fDate,mrngSpecMov,3,0);
            addShowSeat(scrId,shwID);
        }

    }

    private static void addSpecialShow(int scrId,int shwID) throws Exception {
        String query = "SELECT C.ClassID, C.ClassName, C.Rate FROM Screen S INNER JOIN Seat SE ON S.ScreenID = SE.ScreenID  INNER JOIN `class` C ON C.ClassID = SE.ClassID  WHERE S.ScreenID = "+scrId+" GROUP BY  C.ClassID;";
        ResultSet rs = ConnectionUtil.selectQuery(query);
        while (rs.next()){
            System.out.println("Enter "+rs.getString(2)+" Class Special Amount: ");
            int specAmount = sc.nextInt();
            int clasID = rs.getInt(1);
            String query1 = "insert into theater.SpecialShow(ShowID,ClassID,Amount) values("+shwID+","+clasID+","+specAmount+")";
            ConnectionUtil.insertQuery(query1);
        }
    }

    private static void addShowSeat(int scr_id,int shw_id) throws Exception{
        String getSeatID = "Select SeatID from Seat where ScreenID="+scr_id+" ";
        ResultSet r = ConnectionUtil.selectQuery(getSeatID);
        while (r.next()){
            int seatId = r.getInt(1);
            String q3 = "insert into theater.ShowSeat(ShowID,SeatID) values("+shw_id+","+seatId+")";
            ConnectionUtil.insertQuery(q3);
        }
    }

}