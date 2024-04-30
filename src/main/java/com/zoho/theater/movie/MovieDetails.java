package com.zoho.theater.movie;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieDetails {
    public int ShowID;
    public String MovieName;
    public String Date;
    public int ScreenNumber;
    public String ShowTime;
    public String TheaterName;
    public String Location;
    public MovieDetails(int shwID, String mName, long date, int scrNumber, String showTime, String tName, String location){
        this.ShowID = shwID;
        this.MovieName = mName;
        java.util.Date dd = new Date(date);
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        this.Date = f.format(dd);
        this.ScreenNumber = scrNumber;
        this.ShowTime =showTime ;
        this.TheaterName = tName;
        this.Location =location;
    }
}
