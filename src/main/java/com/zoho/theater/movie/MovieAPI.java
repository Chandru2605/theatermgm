package com.zoho.theater.movie;

import com.zoho.theater.exceptions.InvalidException;
import com.zoho.theater.connection.ConnectionUtil;

import java.sql.ResultSet;
import java.util.Scanner;
public class MovieAPI {
   static Scanner sc = new Scanner(System.in);
   public static void showMovieDetails() throws Exception {
       String q = "Select MovieID,Title from Movie";;
       ResultSet r = ConnectionUtil.selectQuery(q);
       System.out.println("ID    MovieName");
       System.out.println("---------------");
       while (r.next()){
           System.out.print(r.getInt(1)+"       ");
           System.out.println(r.getString(2));
       }

   }
   private static int getMovieIdFromMovieName(String movieName) throws Exception {
       String query = "Select MovieID from Movie where title = '"+movieName+"'";
       ResultSet rs = ConnectionUtil.selectQuery(query);
       if(rs.next()){
           return (rs.getInt(1));
       }
       else{
           return -1;
       }
   }
   private static void addMovieEntry(String movieName) throws Exception {
       String query = "insert into Movie(title) values('"+movieName+"')";
       ConnectionUtil.insertQuery(query);
   }
    public static void addMovies() throws Exception, InvalidException {
        System.out.println("Movie Name: ");
        String movieName = sc.next();
        int movieId = getMovieIdFromMovieName(movieName);
        if(movieId!=-1){
            throw new InvalidException("Movie Name Already Exists");
        }
        addMovieEntry(movieName);
        System.out.println("Movie "+movieName+" added successfully");
    }

    public static void main(String[] args) throws InvalidException, Exception {
        addMovies();
    }
}
