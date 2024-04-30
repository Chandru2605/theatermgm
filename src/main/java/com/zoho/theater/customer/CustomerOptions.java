package com.zoho.theater.customer;

import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.exceptions.InvalidException;
import com.zoho.theater.user.booking.Booking;
import com.zoho.theater.user.cancellation.Cancellation;

import java.sql.ResultSet;
import java.util.Scanner;

public class CustomerOptions {
    public static void options(String uName, String name, int uId) throws Exception, InvalidException {
        System.out.println("Welcome "+name);
        boolean loop = true;
        Scanner sc = new Scanner(System.in);
        while (loop){
            System.out.println("1.Book Tickets\n2.View History\n3.Cancel Ticket\n4.Exit");
            int choice = sc.nextInt();
            switch (choice){
                case 1:
                {
                    System.out.println("Book Ticket Section");
                    Booking.bookTickets(uId);
                }
                break;
                case 2:
                {
                    System.out.println("View History Section");
                    viewReport(uId);
                }
                break;
                case 3:
                {
                    System.out.println("Cancel Ticket Section");
                    Cancellation.cancelSeats(uId);
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

    private static void viewReport(int uId) throws Exception {
        String q = "SELECT T.Theatername,T.location,FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d') AS BookingDate,SUM(CASE WHEN B.`Option` = 'Book' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsBooked,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) AS Paid,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.noOfSeats ELSE 0 END) AS NoOfSeatsCancelled,SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) AS Refund,SUM(CASE WHEN B.`Option` = 'Book' THEN B.amount ELSE 0 END) - SUM(CASE WHEN B.`Option` = 'Cancel' THEN B.amount ELSE 0 END) as Spent FROM Booking B INNER JOIN Customer C on C.ID = B.cusID INNER JOIN `Show` SH ON B.showId = SH.showId INNER JOIN Screen SC ON SH.screenId = SC.screenId INNER JOIN Theater T ON SC.theaterId = T.theaterId WHERE C.ID = "+uId+" GROUP BY FROM_UNIXTIME(B.Date / 1000, '%Y-%m-%d'),T.TheaterID;";
        ResultSet r = ConnectionUtil.selectQuery(q);
        System.out.println("Theater  Location     Date       Booked   Cancelled    Paid    Refund    Spent");
        System.out.println("----------------------------------------------------------------------------------");
        while (r.next()){
            System.out.print(r.getString(1)+"      ");
            System.out.print(r.getString(2)+"  ");
            System.out.print(r.getString(3)+"     ");
            System.out.print(r.getInt(4)+"         ");
            System.out.print(r.getInt(6)+"          ");
            System.out.print(r.getInt(5)+"           ");
            System.out.print(r.getInt(7)+"       ");
            System.out.println(r.getInt(8));
        }
        System.out.println("----------------------------------------------------------------------------------");
    }
}
