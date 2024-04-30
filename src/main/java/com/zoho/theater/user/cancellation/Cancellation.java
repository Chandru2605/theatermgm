package com.zoho.theater.user.cancellation;

import com.zoho.theater.connection.ConnectionUtil;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Cancellation {
    static Scanner sc = new Scanner(System.in);

    private static void updateBookingSeat(ArrayList<Integer> bookingSeatIDs) throws Exception{
        for(Integer i:bookingSeatIDs){
            String q = "UPDATE BookingSeat SET status = 2 WHERE bookingseatID = "+i+";";
            ConnectionUtil.insertQuery(q);
        }
    }
    private static void updateShowSeat(ArrayList<Integer> bookingSeatID) throws Exception{
        for(Integer i:bookingSeatID){
            String q = "UPDATE ShowSeat AS SS JOIN BookingSeat AS BS ON SS.showseatID = BS.showseatID SET SS.status = 0 WHERE BS.bookingseatID = "+i+";";
            ConnectionUtil.insertQuery(q);
        }
    }
    private static void addBooking(int noOfSeats, int amount, int showID, int uId) throws Exception{
        Timestamp datetime = new Timestamp(new Date().getTime());
        long fDate = datetime.getTime();
        String query = "insert into Booking(Date,NoOfSeats,Amount,ShowID,`Option`,cusID) values("+fDate+","+noOfSeats+","+amount+","+showID+",2,"+uId+")";
        ConnectionUtil.insertQuery(query);
    }
    public static void cancelSeats(int uId) throws Exception {
        System.out.println("Enter Booking ID: ");
        int bookingID  = sc.nextInt();
        showBookingDetails(bookingID);
        System.out.println("No of Seats to Cancel: ");
        int no_of_seats = sc.nextInt();
        System.out.println("Enter IDs: ");
        ArrayList<Integer> bookingSeatIdToCancel = new ArrayList<>();
        for(int i=0;i<no_of_seats;i++){
            bookingSeatIdToCancel.add(sc.nextInt());
        }
        int refundedAmount = getRefundAmount(bookingSeatIdToCancel);
        int showID = getShowID(bookingID);
        addBooking(no_of_seats,refundedAmount,showID,uId);
        updateBookingSeat(bookingSeatIdToCancel);
        updateShowSeat(bookingSeatIdToCancel);
        printCancelReport(bookingSeatIdToCancel,bookingID,refundedAmount);
    }

    private static void printCancelReport(ArrayList<Integer> bookingSeatIdToCancel, int bookingID,int refund) throws Exception {
        System.out.println("Tickets Cancelled Successfully");
        System.out.println("------------------------------");
        System.out.println("Seat Numbers: "+getSeatNumbers(bookingSeatIdToCancel));
        System.out.println("Refunded amount: "+refund);
    }

    private static ArrayList<Integer> getSeatNumbers(ArrayList<Integer> bookingSeatIdToCancel) throws Exception {
        ArrayList<Integer> seatNos = new ArrayList<>();
        for(Integer i:bookingSeatIdToCancel){
            String q = "SELECT Seat.SeatNumber FROM BookingSeat  INNER JOIN Booking ON Booking.BookingID = BookingSeat.BookingID INNER JOIN ShowSeat ON BookingSeat.ShowSeatID = ShowSeat.ShowSeatID INNER JOIN Seat ON ShowSeat.SeatID = Seat.SeatID where BookingSeat.BookingSeatID = "+i+";";
            ResultSet r = ConnectionUtil.selectQuery(q);
            r.next();
            seatNos.add(r.getInt(1));
        }
        return seatNos;
    }

    private static int getShowID(int bookingID) throws Exception {
        String q = "Select showID from Booking where BookingID = "+bookingID+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        return r.getInt(1);
    }

    private static int getRefundAmount(ArrayList<Integer> bookingSeatIdToCancel) throws Exception {
        int amount = 0;
        for(Integer i:bookingSeatIdToCancel){
            String q = "SELECT C.rate FROM BookingSeat  INNER JOIN Booking ON Booking.BookingID = BookingSeat.BookingID INNER JOIN ShowSeat ON BookingSeat.ShowSeatID = ShowSeat.ShowSeatID INNER JOIN Seat ON ShowSeat.SeatID = Seat.SeatID INNER JOIN `Class` C on C.classID = Seat.classID where BookingSeat.BookingSeatID ="+i+" ";
            ResultSet r = ConnectionUtil.selectQuery(q);
            r.next();
            amount += r.getInt(1);
        }
        int refAmount = (amount)-((amount)*10/100);
        return refAmount;
    }

    private static void showBookingDetails(int bookingID) throws Exception {
        String q = "SELECT BookingSeat.BookingSeatID,Seat.SeatNumber,C.ClassName,BookingSeat.Status FROM BookingSeat  INNER JOIN Booking ON Booking.BookingID = BookingSeat.BookingID INNER JOIN ShowSeat ON BookingSeat.ShowSeatID = ShowSeat.ShowSeatID INNER JOIN Seat ON ShowSeat.SeatID = Seat.SeatID INNER JOIN `class` C on C.classID = Seat.classID where Booking.BookingID ="+bookingID+" ";
        ResultSet r = ConnectionUtil.selectQuery(q);
        System.out.println("ID    SeatNo     Class     Status");
        System.out.println("---------------------------------");
        while (r.next()){
            System.out.println(r.getInt(1)+"      "+r.getInt(2)+"        "+r.getString(3)+"      "+r.getString(4));
        }
    }

}
