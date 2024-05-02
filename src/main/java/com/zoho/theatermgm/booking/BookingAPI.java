package com.zoho.theatermgm.booking;

import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.exceptions.InvalidException;
import com.zoho.theatermgm.movie.MovieDetails;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class BookingAPI {
    static Scanner sc = new Scanner(System.in);
    public static void showAvailableSeatsToBook(int showID) throws Exception {
        String query = "SELECT DISTINCT SS.ShowSeatID,S.SeatNumber, C.ClassName, SS.Status, CASE WHEN SP.Amount IS NOT NULL THEN SP.Amount ELSE C.Rate END AS Amount FROM ShowSeat SS INNER JOIN Seat S ON SS.SeatID = S.SeatID INNER JOIN `Class` C ON S.ClassID = C.ClassID LEFT JOIN SpecialShow SP ON SS.ShowID = SP.ShowID AND S.ClassID = SP.ClassID WHERE SS.ShowID = "+showID+" order by SS.ShowSeatID;";
        ResultSet rs = ConnectionUtil.selectQuery(query);
        System.out.println("ID   SeatNumber   Class    Status    Amount");
        System.out.println("-----------------------------------------");
        while (rs.next()){
            System.out.print(rs.getInt(1)+"       ");
            System.out.print(rs.getInt(2)+"      ");
            System.out.print(rs.getString(3)+"    ");
            if(rs.getInt(4)==0){
                System.out.print("Availabele"+"  ");
            }
            else{
                System.out.print("Booked"+"    ");
            }
            System.out.println(rs.getInt(5));
        }
    }
    private static ArrayList<MovieDetails> getMovies() throws Exception, InvalidException {
        String q = "SELECT  S.SHowID,M.title AS movie_title,S.Date,SC.ScreenNumber,S.ShowTime,T.TheaterName,T.Location FROM Movie M INNER JOIN `Show` S ON M.MovieID = S.movieID INNER JOIN Screen SC ON S.screenID = SC.screenID INNER JOIN Theater T ON SC.theaterID = T.theaterID order by M.title;";
        ArrayList<MovieDetails> movies = new ArrayList<>();
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            ResultSet rs = ConnectionUtil.selectQuery(q);
            while (rs.next()){
                int shwID = rs.getInt(1);
                String name = rs.getString(2);
                long date = rs.getLong(3);
                int scrNum = rs.getInt(4);
                String shwTime = rs.getString(5);
                String tname = rs.getString(6);
                String loc = rs.getString(7);
                MovieDetails md = new MovieDetails(shwID,name,date,scrNum,shwTime,tname,loc);
                movies.add(md);
            }
            return movies;
        }
        else {
            throw new InvalidException("No Movies were Available");
        }
    }

    private static void setBooking(ArrayList<Integer> showSeatIDs, int showID, int uId) throws Exception {
        int amount = getAmount(showSeatIDs);
        Timestamp date = new Timestamp(new Date().getTime());
        long fDate = date.getTime();
        String query1 = "insert into Booking(NoOfSeats,Date,Amount,ShowID,`Option`,cusID) values("+showSeatIDs.size()+","+fDate+","+amount+","+showID+","+1+","+uId+")";
        ConnectionUtil.insertQuery(query1);
        int bookingID = getLatestBookingID();
        for(Integer i:showSeatIDs){
            addBookingSeatEntry(bookingID,i);
        }
    }

    private static int getAmount(ArrayList<Integer> showSeatIDs) throws Exception {
        int amount = 0;
        for(Integer i:showSeatIDs){
            String q = "SELECT DISTINCT CASE WHEN SP.Amount IS NOT NULL THEN SP.Amount ELSE C.Rate END AS Amount FROM ShowSeat SS INNER JOIN Seat S ON SS.SeatID = S.SeatID INNER JOIN `Class` C ON S.ClassID = C.ClassID LEFT JOIN SpecialShow SP ON SS.ShowID = SP.ShowID AND S.ClassID = SP.ClassID WHERE SS.ShowSeatID = "+i+";";
            ResultSet r = ConnectionUtil.selectQuery(q);
            r.next();
            amount += r.getInt(1);
        }
        return amount;
    }

    private static void addBookingSeatEntry(int bookingID, Integer i) throws Exception {
        String query = "insert into BookingSeat(BookingID,ShowSeatID,Status) values("+bookingID+","+i+",1)";
        ConnectionUtil.insertQuery(query);
        setShowSeat(i);
    }
    private static void setShowSeat(int shw_seat_id)  throws Exception {
        String q2 = "Update ShowSeat set Status = 1 where ShowSeatID = "+shw_seat_id+" ";
        ConnectionUtil.insertQuery(q2);
    }
    private static int getLatestBookingID() throws Exception {
        String q = "Select * from Booking ORDER BY BookingID DESC LIMIT 1";
        ResultSet rs = ConnectionUtil.selectQuery(q);
        rs.next();
        int bookingID = rs.getInt(1);
        return bookingID;
    }
    private static void showMovies(ArrayList<MovieDetails> m) {
        System.out.println("ID   Title       Date      Screen     ShowTime   Theater    Location");
        System.out.println("---------------------------------------------------------------");
        for (int i=0;i<m.size();i++){
            System.out.println(m.get(i).ShowID +"   "+m.get(i).MovieName +"     "+m.get(i).Date +"     "+m.get(i).ScreenNumber +"     "+m.get(i).ShowTime +"     "+m.get(i).TheaterName +"     "+m.get(i).Location +" ");
        }
    }

    private static void printDetails(ArrayList<Integer> showSeatIDs) throws Exception {
        System.out.println("Ticket Booked Successfully");
        System.out.println("--------------------------");
        String q = "Select M.title,T.Theatername,T.location,S.SHowTime,SR.ScreenNumber FROM ShowSeat SS JOIN `Show` S on S.showid = SS.showID JOIN Movie M on S.movieID = M.movieID JOIN Screen SR on SR.screenid = S.screenID JOIN theater T on T.TheaterID = SR.theaterID where SS.Showseatid = "+showSeatIDs.get(0)+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        System.out.println("Booking ID: "+getLatestBookingID());
        System.out.println("Movie Name: "+r.getString(1));
        System.out.println("Theater: "+r.getString(2));
        System.out.println("Location: "+r.getString(3));
        System.out.println("ShowTime: "+r.getString(4));
        System.out.println("Screen: "+r.getInt(5));
        System.out.println("Seat Numbers: "+getSeatNumbers(showSeatIDs));
        System.out.println("Amount Paid: "+getAmount(showSeatIDs));
        System.out.println("---------THANK YOU---------");
    }

    private static ArrayList<Integer> getSeatNumbers(ArrayList<Integer> showSeatIDs) throws Exception {
        ArrayList<Integer> seatNos = new ArrayList<>();
        for(Integer i:showSeatIDs){
            String q = "Select S.SeatNumber FROM ShowSeat SS JOIN Seat S ON SS.seatID = s.seatID where SS.Showseatid = "+i+";";
            ResultSet r = ConnectionUtil.selectQuery(q);
            r.next();
            seatNos.add(r.getInt(1));
        }
        return seatNos;
    }

    private static void checkShowSeatID(int showSeatID, int showID) throws Exception, InvalidException {
        String q = "Select * from theater.showSeat where `showSeat`.showID = "+showID+" and showSeatID = "+showSeatID+" and status=0";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(!r.next()){
            throw new InvalidException("ID invalid");
        }
    }

    private static void checkShowID(int showID) throws Exception, InvalidException {
        String q = "Select * from theater.show where `show`.showID = "+showID+"";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(!r.next()){
            throw new InvalidException("Show invalid");
        }
    }

    public static void bookTickets(int uId) throws Exception, InvalidException {
        ArrayList<MovieDetails> movies = getMovies();
        showMovies(movies);
        System.out.println("Show ID: ");
        int showID = sc.nextInt();
        checkShowID(showID);
        showAvailableSeatsToBook(showID);
        System.out.println("Enter number of tickets of book: ");
        int n = sc.nextInt();
        System.out.println("Enter IDs: ");
        ArrayList<Integer> showSeatIDs = new ArrayList<>();
        for(int i=0;i<n;i++){
            int showSeatID = sc.nextInt();
            checkShowSeatID(showSeatID,showID);
            showSeatIDs.add(showSeatID);
        }
        setBooking(showSeatIDs,showID,uId);
        printDetails(showSeatIDs);
    }

}
