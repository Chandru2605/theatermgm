package com.zoho.theater.customer;

import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.exceptions.InvalidException;
import com.zoho.theater.user.booking.Booking;
import com.zoho.theater.user.cancellation.Cancellation;

import java.sql.ResultSet;
import java.util.Scanner;

public class CustomerOptions {
    public static void options(int uId) throws Exception, InvalidException {
        System.out.println("Welcome " + getName(uId));
        boolean loop = true;
        Scanner sc = new Scanner(System.in);
        while (loop){
            System.out.println("1.Book Tickets\n2.View History\n3.Cancel Ticket\n4.Exit");
            int choice = sc.nextInt();
            switch (choice){
                case 1:
                {
                    System.out.println("Book Ticket Section");
                   try{
                       Booking.bookTickets(uId);
                   }
                   catch (InvalidException e){
                       System.out.println(e.getMessage());
                   }
                }
                break;
                case 2:
                {
                    System.out.println("View History Section");
                    try{
                        viewReport(uId);
                    }
                    catch (InvalidException e){
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 3:
                {
                    System.out.println("Cancel Ticket Section");
                    try{
                        Cancellation.cancelSeats(uId);
                    }
                    catch (InvalidException e){
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 4:
                {
                    System.out.println("Exiting..");
                    loop = false;
                }
                break;
            }
        }
    }

    private static String getName(int uId) throws Exception {
        String q = "select name from customer where id = "+uId+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        return r.getString(1);
    }

    private static void viewReport(int uId) throws Exception, InvalidException {
        String q = "SELECT B.BookingID,M.title,T.Theatername,T.location,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') AS BookingDate,SH.showtime,SUM(CASE WHEN B.`Option` = 'Book' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsBooked,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsCancelled,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) AS Paid,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) AS Refund FROM Booking B INNER JOIN Customer C on C.ID = B.cusID INNER JOIN `Show` SH ON B.showId = SH.showId INNER JOIN Screen SC ON SH.screenId = SC.screenId INNER JOIN Movie M on SH.MovieID = M.MovieID INNER JOIN Theater T ON SC.theaterId = T.theaterId WHERE C.ID = "+uId+" GROUP BY T.TheaterID,B.`BookingID`;";
        ResultSet rs = ConnectionUtil.selectQuery(q);
        if(rs.next()){
            ResultSet r = ConnectionUtil.selectQuery(q);
            System.out.println("ID    Movie   Theater   Location     Date    ShowTime       Booked   Cancelled    Paid    Refund");
            System.out.println("------------------------------------------------------------------------------------------------");
            while (r.next()){
                System.out.print(r.getInt(1)+"    ");
                System.out.print(r.getString(2)+"      ");
                System.out.print(r.getString(3)+"     ");
                System.out.print(r.getString(4)+"     ");
                System.out.print(r.getString(5)+"     ");
                System.out.print(r.getString(6)+"     ");
                System.out.print(r.getInt(7)+"         ");
                System.out.print(r.getInt(8)+"          ");
                System.out.print(r.getInt(9)+"       ");
                System.out.println(r.getInt(10)+"       ");

            }
            System.out.println("-----------------------------------------------------------------------------------------------");
        }
        else{
            throw new InvalidException("You have no booking yet");
        }
    }
}
