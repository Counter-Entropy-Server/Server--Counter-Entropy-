/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author nur
 */
public class CEScoketsProtocol {
    
    private static CEScoketsProtocol instance = null;
    private String notificationString = ""; 
    private HashMap variablesFromClients = new HashMap();
    private CEHouse house;
    private HashMap<String,Integer> commands;

    
   protected CEScoketsProtocol() {
      // Exists only to defeat instantiation.
   }
   
   public static CEScoketsProtocol getInstance() {
      if(instance == null) {
        instance = new CEScoketsProtocol();
        instance.initCommands();
      }
      return instance;
   }
   
   public void setHouseReferance (CEHouse house){
       this.house = house;
   }
   
   private void initCommands(){
       
        commands = new HashMap<String,Integer>(); 
        commands.put("toggle",0);
        commands.put("read",1);
        commands.put("readall",2);
        commands.put("write",3);
   }
   
    public String processInput(String theInput) { //process requests from clients
        
        String theOutput = null; //server reply to request
        String variablesDelims;
        String[] variables;
        String request;
        int commandNo;

        if (theInput  == null){
            theOutput = "confirmed sockets connection";
            return theOutput ;
        }
        
        variablesDelims = "[ ]";
        variables = theInput.split(variablesDelims);

        if (commands.containsKey(variables[0]))
            commandNo = (Integer) commands.get(variables[0]);
        else
            commandNo = -100;
        
        switch(commandNo){
            case 0: //toggel
                variablesDelims = "[,]";
                request = variables[1];
                variables = request.split(variablesDelims);

                for (int i = 0; i < variables.length; i++){
                    if ("" != variables[i]){
                        CEHouseVariable v = house.findByName(variables[i]);
                        if (v != null && "IX".equals(v.type))
                            variablesFromClients.put(variables[i],v.value==1?"0":"1");
                    } 
                }

                theOutput = "received";
                break;
                
            case 1: //read
                variablesDelims = "[,]";
                
                if (variables.length > 1 && variables[1] != null &&  !variables[1].equals(""))
                {
                    request = variables[1];
                    variables = request.split(variablesDelims);

                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < variables.length; i++){
                        if ("" != variables[i]) 
                            names.add(variables[i]);
                    }
                    HashMap values = house.getValuesForNames(names);
                    String output = createNotificationString(values);

                    if (("").equals(output))
                        theOutput = "rejected";
                    theOutput = "reply "+ output;
                }
                else
                    theOutput = "rejected"; 
                break;
                
            case 2: //readall
                theOutput = "reply "+ createNotoficationStringOfAllVariables();
                break;
                
            case 3: //write
                String key, value;
                variablesDelims = "[,]";
                if (variables.length > 1 && variables[1] != null){
                    request = variables[1];
                    variables = request.split(variablesDelims);

                    variablesDelims = "[:]";
                    for (int i = 0; i < variables.length; i++){
                        if (variables[i].split(variablesDelims).length != 2) //wrong format
                            continue;
                        key = variables[i].split(variablesDelims)[0]; //name
                        value = variables[i].split(variablesDelims)[1]; //value

                        if (key == null || value == null) //empty strings
                            continue;
                        variablesFromClients.put(key,value);
                    }

                    theOutput = "received";  
                }
                else
                    theOutput = "rejected";
                break;
                
            default: //null
                theOutput = "unrecognized";
    }
        
        return theOutput;
    }
    
    public HashMap getNotifactionsFromClients (){
       return variablesFromClients;
    }
     
    public void clearNotifactionsFromClients()
    {
        variablesFromClients.clear();
    }
    
    
    public String getNotificationsFromServer(){
        return notificationString;
    }
    

    public void clearNotifactionsFromServer()
    {
        notificationString = "";
    }
    
    //format data to send to client
    public void addNewNotification(CEHouseVariable v)
    {
        notificationString += v.name + ":"+v.value+",";
    }
    
    
    public String createNotificationString(HashMap map){
        
        String name;
        int value;
        String notificationString = "";
        
        if (map == null)
            return "unrecognized";
        
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
        
        CEHouseVariable v;
        String notificationString = "";
        
        Iterator it = house.getHouseVariables().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (CEHouseVariable)pairs.getValue();            
            
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