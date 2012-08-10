/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author nur
 */
public class ScoketsCommProtocol {
    
    private static ScoketsCommProtocol instance = null;
    private String clientNotificationString = null; 
    private HashMap variablesFromClients = new HashMap();
    
   protected ScoketsCommProtocol() {
      // Exists only to defeat instantiation.
   }
   public static ScoketsCommProtocol getInstance() {
      if(instance == null) {
         instance = new ScoketsCommProtocol();
      }
      return instance;
   }
   
   public HashMap getvariablesFromClients (){
       return variablesFromClients;
   }

    public String processInput(String theInput) { //process requests from clients
        String theOutput = null; //server reply to request
        
        if (theInput  == null){
            return "confirmed sockets connection";
        }
        
        String variablesDelims = "[,]";
        String[] variables = theInput.split(variablesDelims);
        
        String valuesDelims = "[:]";
        for (int i = 0; i < variables.length; i++){
            variablesFromClients.put(variables[i].split(valuesDelims)[0],variables[i].split(valuesDelims)[1]);
        }
            
        theOutput = "data recieved";
        
        return theOutput;
    }
    
    public String ServerNotifications(){
        return clientNotificationString;
    }
    
    
    public boolean notifyClients(HouseVariable [] variablesWithNewValues, int variablesToNotifyClientsCount){ //format data to send to client
        
        HouseVariable v;
        
        for (int i = 0 ; i <variablesToNotifyClientsCount; i++){
            
            v = variablesWithNewValues[i];
            clientNotificationString = v.name + ":"+v.value+",";
        }
        return true;
    }
    
    
}