package opdb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;

/**
 *
 * @author Joao
 */
public class DBHandler {
    
    private InputStream credStream;
    private Properties properties;

    //JDBC Driver & DB URL
    static String JDBC_DRIVER;
    static String DB_URL;
    
    //Credentials
    static String USER;
    static String PASS;
    
    
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    
    //Class Constructor 
    public DBHandler(){
        
        try {
            credStream = this.getClass().getResourceAsStream("credentials.properties"); //get inputStream from credentials file 
            
            if(credStream != null){                 
            
              //create properties variable and feed with input stream
              properties = new Properties();
              properties.load(credStream);
              
              //get credentials from fil
              JDBC_DRIVER = properties.getProperty("jdbc_driver");
              DB_URL =  properties.getProperty("db_url");
              USER = properties.getProperty("user");
              PASS = properties.getProperty("pass");
            
              //Register JDBC driver
              Class.forName(JDBC_DRIVER);
            
              //Connect to database
              conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            }
           
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //method needed for first time users
        //when program loads for the first time, it needs to create DISTBELT table
        if(!distBeltExists()){
            
            try {
                
                //create DISTBELT table
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE DISTBELT(MAT VARCHAR(12),DATE VARCHAR(12));");
                stmt.close();

            } catch (SQLException ex) {
                Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    //utility methods(last to implement)
    

    //convert normal plate to DBForm 
    public static String convertToDBForm(String plate){
        
        String newstring = new String();
        
        for(int i=0;i<plate.length();i++){
           if(plate.charAt(i) != '-')
               newstring += plate.charAt(i);
        }
        
        newstring = "MAT" + newstring;
        return newstring.toUpperCase();
    }
    
    //check if DISTBELT TABLE exists
    private boolean distBeltExists(){        
        String table = "DISTBELT";
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'");
            
            while(rs.next()){
                if(table.equalsIgnoreCase(rs.getString("TABLE_NAME"))){
                    return true;
                }
            }
            
            stmt.close();
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       return false;    
    }
    
    //check if that table exists
    private boolean checkTableExists(String plate){
       //there is no need to close or start connection
       //function is only used in the context of an already created connection
        
        try {
            rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'");
            
            while(rs.next()){
                if(plate.equalsIgnoreCase(rs.getString("TABLE_NAME"))){
                    return true;
                }
            }
            
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       return false;
    }
    
    
    
    
    
    
    //Read from DB methods
    
    
    //returns all the service dates of a car, giving its plate
   public Set<String> getDatesFromPlate(String plate){
       Set<String> lista = new HashSet<>();
       
       try {
            stmt = conn.createStatement();
            
            if(checkTableExists(plate)){
                rs = stmt.executeQuery("SELECT DATA FROM " + plate);
            
                while(rs.next()){
                 lista.add(rs.getString("DATA"));
                }
                
                rs.close();
                stmt.close();
            }
    
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
      

       
        return lista;
   }
   
   
   
   //returns a List of services made, giving the date
   public List<Service> getServiceFromDateNPlate(String date,String plate){
       
       List<Service> lista = new ArrayList<>(); 
       
       try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT QNT,DESC,PRICE FROM " + plate + " WHERE DATA = '" + date + "';"  );
            
            while(rs.next()){
              lista.add(new Service( rs.getDouble("QNT"), rs.getString("DESC"), rs.getDouble("PRICE") ) );     
            }
            
             stmt.close();
             rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
      
       return lista;
   }
   
   // get dates of when a car changed belt
   
   public String getBeltDates(String plate){
       String beltDates = new String();
       try {
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT DATE FROM DISTBELT WHERE MAT = '" + plate + "';");
            
            while(rs.next()){
                beltDates += rs.getString("DATE");
                beltDates += ";\n";
            }
            
            stmt.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       return beltDates;
   }
   
   
   
   
   
   //get km´s from a specific plate and date
   public String getKmfromDateNPlate(String date,String plate){
       
       double km = 0;
       
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT KM FROM " + plate + " WHERE DATA = '" + date + "';");
            
            while(rs.next()){
                km = rs.getDouble("KM");
                break;
            }
            
            
            stmt.close();
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Double.toString(km);
   }
    
    
    
    
    
    
    
    //Write into DB methods
   public void addtoDB(String plate,String date,double km,ObservableList<Service> serv){
       
       plate = convertToDBForm(plate);
       
        try {
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       if(!checkTableExists(plate)){ 
        try {
            stmt.execute("CREATE TABLE " + plate + "(DATA VARCHAR(12),QNT DOUBLE,DESC VARCHAR(50),PRICE DOUBLE,KM DOUBLE);");
        }catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
          
       }
       
       for(int i=0;i<serv.size();i++){
          
        double qnt = serv.get(i).getQnt();
        String desc = serv.get(i).getDesc();
        double price = serv.get(i).getPrice();
        
        try {
         stmt.executeUpdate("INSERT INTO " + plate + " VALUES('" + date + "'," + Double.toString(qnt) + ",'" + desc + "'," + Double.toString(price) + "," + Double.toString(km) + ");");
        }catch (SQLException ex) {
         Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
  
       }
       
        try {
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       

   }
   
   //add to distbelt table
   public void addDistBelt(String plate,String date){
       
       plate = convertToDBForm(plate);
       
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO DISTBELT VALUES('" + plate + "','" + date + "');");
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
   
   }
   
   //delete date from table
   
   public void deleteFromTable(String plate, String date){
       
       //plate = convertToDBForm(plate);
       try {
            //delete all rows with that date in table     
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM " + plate + " WHERE DATA = '" + date + "'");
            
            //delete all rows with that date in distbelt
            stmt.executeUpdate("DELETE FROM DISTBELT WHERE DATE = '" + date + "'");
            
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
   }
   
}
