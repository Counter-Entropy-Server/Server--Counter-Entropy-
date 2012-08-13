/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.wimpi.modbus.cmd.CEHouse;

/**
 *
 * @author nur
 */
public class ScoketsCommProtocol {
    
    private static ScoketsCommProtocol instance = null;
    private String notificationString = ""; 
    private HashMap variablesFromClients = new HashMap();
    private CEHouse house;
    
   protected ScoketsCommProtocol() {
      // Exists only to defeat instantiation.
   }
   public static ScoketsCommProtocol getInstance() {
      if(instance == null) {
         instance = new ScoketsCommProtocol();
      }
      return instance;
   }
   
   public void setHouseReferance (CEHouse house){
       this.house = house;
   }
   
   
    public String processInput(String theInput) { //process requests from clients
        
        String theOutput = null; //server reply to request
        String variablesDelims;
        String[] variables;
        
        
        if (theInput  == null){
            theOutput = "confirmed sockets connection";
            return theOutput ;
        }
        
        if (theInput.equalsIgnoreCase("readall")){
            
           theOutput = createNotoficationStringOfAllVariables(); 
           return theOutput;
        }
        
        variablesDelims = "[ ]";
        variables = theInput.split(variablesDelims);
        if (variables[0].equalsIgnoreCase("read")){
 
            String request;

            variablesDelims = "[,]";
            request = variables[1];
            variables = request.split(variablesDelims);

            ArrayList<String> names = new ArrayList<String>();
            for (int i = 0; i < variables.length; i++){
                if ("" != variables[i]) 
                    names.add(variables[i]);
            }
            HashMap values = house.getValuesForNames(names);
            theOutput = createNotificationString(values);

            return theOutput;
        }
        
        variablesDelims = "[ ]";
        variables = theInput.split(variablesDelims);
        if (variables[0].equalsIgnoreCase("write")){
            
            String request, key, value;

            variablesDelims = "[,]";
            request = variables[1];
            variables = request.split(variablesDelims);

            variablesDelims = "[:]";
            for (int i = 0; i < variables.length; i++){
                if (variables[i].split(variablesDelims).length != 2) //wrong format
                    continue;
                key = variables[i].split(variablesDelims)[0];
                value = variables[i].split(variablesDelims)[1];

                if (key == null || value == null) //empty strings
                    continue;
                variablesFromClients.put(key,value);
            }
            
            theOutput = "data recived";
            return theOutput;
        }

        
        return "Unrecognized command";
    }
    
    public HashMap getNotifactionsFromClients (){
       return variablesFromClients;
    }
     
    public void clearNotifactionsFromClients()
    {
        variablesFromClients.clear();
    }
    
    /*
    public String getNotificationsFromServer(){
        return notificationString;
    }
    

    public void clearNotifactionsFromServer()
    {
        notificationString = "";
    }
    
    //format data to send to client
    public void addNewNotification(HouseVariable v)
    {
        notificationString += v.name + ":"+v.value+",";
    }
    *
    * 
    */
    
    public String createNotificationString(HashMap map){
        
        String name;
        int value;
        String notificationString = "";
        
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            name = (String)pairs.getKey();
            value = (Integer)pairs.getValue();
            
            notificationString += name + ":"+value+",";
        }
        
        return  notificationString;
    }
    
    public String createNotoficationStringOfAllVariables(){
        
        HouseVariable v;
        String notificationString = "";
        
        Iterator it = house.getHouseVariables().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (HouseVariable)pairs.getValue();            
            
            notificationString += v.name + ":"+v.value+",";
        }
        
        return notificationString;
    }
    
    /*
    if (!getNotificationsFromServer().equals("")){
            System.out.println(getNotificationsFromServer());
            theOutput = getNotificationsFromServer(); //send data from server to client
            }
            * 
            */
}