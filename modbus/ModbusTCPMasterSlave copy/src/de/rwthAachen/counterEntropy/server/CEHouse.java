/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import java.util.*;
import de.rwthAachen.counterEntropy.server.CEHouseVariable;
import de.rwthAachen.counterEntropy.server.CEAddressSequance;
import de.rwthAachen.counterEntropy.server.CEExcelReader;

/**
 *
 * @author nur
 */
public class CEHouse {
    
    private ArrayList<CEAddressSequance> memorySequances = null;
    private HashMap<String,CEHouseVariable> houseVariables = null;
    
    public void CEHouse(){
        
    }
    
    //read xls file into HashMap
    private HashMap processExcelFile(String filePath)
    {
        try
        {
            CEExcelReader reader = new CEExcelReader();
            reader.setInputFile(filePath);

            return reader.read();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public HashMap getHouseVariablesFromFile (String filePath)
    {
        if (houseVariables != null)
            return houseVariables;
        
        houseVariables = processExcelFile(filePath);
        System.out.println("House contains "+houseVariables.size()+" variables");
        
       
        return houseVariables;
    }
    
     public HashMap getHouseVariables ()
    {
        return houseVariables;
    }
     
    public ArrayList getMemorySequances()
    {
        if(memorySequances != null)
            return memorySequances;
        
        List<CEHouseVariable> houseVariablesList = new ArrayList<CEHouseVariable>();
        List<CEHouseVariable> coils = new ArrayList<CEHouseVariable>();
        List<CEHouseVariable> registers = new ArrayList<CEHouseVariable>();

        CEHouseVariable v;

        try{
            //Separate CEHouseVariable objects by type (IX vs. IW) to 2 lists to sort them
            Iterator it = houseVariables.entrySet().iterator();
            while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (CEHouseVariable)pairs.getValue();
            if ("IX".equals(v.type))
                coils.add(v);  
            if ("IW".equals(v.type))
                registers.add(v);  
            }

            //Initialize a custom comparator
            HouseVariablesComparator comparator = new HouseVariablesComparator();

            //Sort lists
            Collections.sort(registers, comparator);
            Collections.sort(coils, comparator);

            //Combine sorted lists
            houseVariablesList.addAll(coils);
            houseVariablesList.addAll(registers);

            /*
            for(CEHouseVariable hv : houseVariablesList){
                System.out.println(hv);
            }
            */

            memorySequances = new ArrayList<CEAddressSequance>();
            int ref1 = -1, ref2 = -1;
            String type = null;
            CEHouseVariable var;
            CEAddressSequance sequance = null;

            //Get consecutive modbus addresses
            for (int i=0 ; i < houseVariablesList.size(); i++)
            {
                var = houseVariablesList.get(i);
                if (var.modbusAddr == ref2 && type.equals(var.type)){ //add address to sequance
                    sequance.ref2++;
                    ref2++;
                }else{ 
                    ref1 = var.modbusAddr;
                    ref2 = ref1+1;
                    type = var.type;
                    sequance = new CEAddressSequance(ref1,ref2,var.readFunctionCall,var.type); //create new address sequance
                    memorySequances.add(sequance);
                }      
            }

            /*
            for(CEAddressSequance s : sequences){
                System.out.println(s);
            }
            */

            return memorySequances;

        } catch (Exception ex){
            ex.printStackTrace();
        }

    return null;
        
    }
    
    public CEHouseVariable findByAddressAndType(int addr, String type) {
        
        CEHouseVariable v;
        
        Iterator it = houseVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (CEHouseVariable)pairs.getValue();
            if (v.modbusAddr == addr && v.type == type)
                return v;
        }
        return null;
    }
    
    public CEHouseVariable findByName(String name){
       
        CEHouseVariable v;
        
        Iterator it = houseVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (CEHouseVariable)pairs.getValue();
            if (name.equalsIgnoreCase(v.name))
                return v;
        }
        return null; 
    }
    
    public HashMap getValuesForNames (ArrayList names){
        
        CEHouseVariable v;
        HashMap values = new HashMap(); 
        
        for (int i = 0 ; i < names.size(); i++){
            v = findByName((String)names.get(i));
            values.put(v.name,Integer.valueOf((v.value)));
        }
        
        return values;
    }
    
}


//Sort CEHouseVariable objects according to thier modbus address in ascending order
class HouseVariablesComparator implements Comparator<CEHouseVariable>{
 
    @Override
    public int compare(CEHouseVariable v1, CEHouseVariable v2) {
 
        int rank1 = v1.modbusAddr;
        int rank2 = v2.modbusAddr;
 
        if (rank1 > rank2){
            return +1;
        }else if (rank1 < rank2){
            return -1;
        }else{
            return 0;
        }
    }
    
}