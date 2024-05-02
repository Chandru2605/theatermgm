package com.zoho.theatermgm.user;

import com.zoho.theatermgm.analytics.RevenueReport;
import com.zoho.theatermgm.exceptions.InvalidException;
import com.zoho.theatermgm.screen.ScreenAPI;
import com.zoho.theatermgm.show.ShowAPI;
import com.zoho.theatermgm.theater.TheaterAPI;

import java.util.Scanner;

public class UserOptions {
    public static void options(int orgID,int authID) throws Exception, InvalidException {
        System.out.println("Welcome ");
        boolean loop = true;
        Scanner sc = new Scanner(System.in);
        while (loop) {
            System.out.println("1.Add Theaters\n2.Add Screens\n3.Add Shows\n4.View Reports\n5.Your Theaters\n6.Exit");
            int choice = sc.nextInt();
            switch (choice) {
                case 1: {
                    System.out.println("Add Theater Section");
                    try {
                        TheaterAPI.addTheaters(orgID);
                    }
                    catch (InvalidException e){
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 2: {
                    System.out.println("Add Screen Section");
                    try{
                        ScreenAPI.add_Screen(orgID);
                    }
                    catch (InvalidException e){
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 3: {
                    System.out.println("Add Shows Section");
                   try{
                       ShowAPI.add_Shows(orgID);
                   }
                   catch (InvalidException e){
                       System.out.println(e.getMessage());
                   }
                }
                break;
                case 4: {
                    System.out.println("View Report Section");
                    try{
                        RevenueReport.revenueReport(orgID);
                    }
                    catch (InvalidException e){
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 5: {
                   try {
                       TheaterAPI.getTheaterDetails(orgID);
                   }
                   catch (InvalidException e){
                       System.out.println(e.getMessage());
                   }
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
