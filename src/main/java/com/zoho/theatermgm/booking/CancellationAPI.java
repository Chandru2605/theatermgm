package com.zoho.theatermgm.booking;

import com.zoho.theatermgm.connection.ConnectionUtil;
import com.zoho.theatermgm.exceptions.InvalidException;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class CancellationAPI {
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
    public static void cancelSeats(int uId) throws Exception, InvalidException {
        showBookings(uId);
        System.out.println("Enter Booking ID: ");
        int bookingID  = sc.nextInt();
        showBookingDetails(bookingID);
        boolean bookingOccur = checkBooking(bookingID);
        if(!bookingOccur){
            throw new InvalidException("All seats are cancelled");
        }
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

    private static boolean checkBooking(int bookingID) throws Exception, InvalidException {
        String q = "SELECT BookingSeat.Status FROM BookingSeat  INNER JOIN Booking ON Booking.BookingID = BookingSeat.BookingID INNER JOIN ShowSeat ON BookingSeat.ShowSeatID = ShowSeat.ShowSeatID INNER JOIN Seat ON ShowSeat.SeatID = Seat.SeatID INNER JOIN `class` C on C.classID = Seat.classID where Booking.BookingID ="+bookingID+" ";
        ResultSet r = ConnectionUtil.selectQuery(q);
        boolean bookingOccur = false;
        while (r.next()){
            if(r.getString(1).equals("Booked")){
                bookingOccur = true;
                break;
            }
        }
        return bookingOccur;
    }

    private static void showBookings(int uID) throws Exception, InvalidException {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());;
        String q = "SELECT B.BookingID,M.title,T.Theatername,T.location,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') AS BookingDate,FROM_UNIXTIME(SH.Date / 1000, '%Y-%m-%d') AS ShowDate,SH.showtime,SUM(CASE WHEN B.`Option` = 'Book' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsBooked,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) AS Paid FROM Booking B INNER JOIN Customer C on C.ID = B.cusID INNER JOIN `Show` SH ON B.showId = SH.showId INNER JOIN Screen SC ON SH.screenId = SC.screenId INNER JOIN Movie M on SH.MovieID = M.MovieID INNER JOIN Theater T ON SC.theaterId = T.theaterId WHERE C.ID = "+uID+" and FROM_UNIXTIME(SH.Date / 1000, '%Y-%m-%d')>='"+currentTime+"' and B.option = 'Book' GROUP BY T.TheaterID,B.`BookingID`;";
        ResultSet rs = ConnectionUtil.selectQuery(q);
        if(rs.next()){
            ResultSet r = ConnectionUtil.selectQuery(q);
            System.out.println("ID    Movie   Theater   Location     BookingDate    ShowDate    ShowTime       Booked    Paid");
            System.out.println("---------------------------------------------------------------------------------------------");
            while (r.next()){
                System.out.print(r.getInt(1)+"    ");
                System.out.print(r.getString(2)+"      ");
                System.out.print(r.getString(3)+"     ");
                System.out.print(r.getString(4)+"     ");
                System.out.print(r.getString(5)+"     ");
                System.out.print(r.getString(6)+"     ");
                System.out.print(r.getString(7)+"     ");
                System.out.print(r.getInt(8)+"         ");
                System.out.println(r.getInt(9)+"          ");
            }
        }
        else{
            throw new InvalidException("You have no booking yet");
        }
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
