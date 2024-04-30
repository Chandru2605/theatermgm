package com.zoho.theater.user;

import com.zoho.theater.exceptions.InvalidException;
import com.zoho.theater.screen.ScreenAPI;
import com.zoho.theater.show.ShowAPI;
import com.zoho.theater.theater.TheaterAPI;

import java.util.Scanner;

public class UserOptions {
    public static void options(String uName, String name, int uId) throws Exception, InvalidException {
        System.out.println("Welcome " + name);
        boolean loop = true;
        Scanner sc = new Scanner(System.in);
        while (loop) {
            System.out.println("1.Add Theaters\n2.Add Screens\n3.Add Shows\n4.View Reports\n5.Your Theaters\n6.Exit");
            int choice = sc.nextInt();
            switch (choice) {
                case 1: {
                    System.out.println("Add Theater Section");
                    TheaterAPI.addTheaters(uId);
                }
                break;
                case 2: {
                    System.out.println("Add Screen Section");
                    ScreenAPI.add_Screen(uId);
                }
                break;
                case 3: {
                    System.out.println("Add Shows Section");
                    ShowAPI.add_Shows(uId);
                }
                break;
                case 4: {
                    System.out.println("View Report Section");
                    RevenueReport.revenueReport(uId);
                }
                break;
                case 5: {
                    TheaterAPI.getTheaterDetails(uId);
                }
                break;
                case 6: {
                    System.out.println("Exiting..");
                    loop = false;
                }
                break;
            }
        }
    }
}
