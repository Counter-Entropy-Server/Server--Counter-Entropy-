/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.cmd;

import java.util.*;
import net.wimpi.modbus.util.HouseVariable;
import net.wimpi.modbus.util.MBAddressSequance;
import net.wimpi.modbus.util.ReadExcel;

/**
 *
 * @author nur
 */
public class CEHouse {
    
    private ArrayList<MBAddressSequance> memorySequances = null;
    private HashMap<String,HouseVariable> houseVariables = null;
    
    public void CEHouse(){
        
    }
    
    //read xls file into HashMap
    private HashMap processExcelFile(String filePath)
    {
        try
        {
            ReadExcel reader = new ReadExcel();
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
        
        List<HouseVariable> houseVariablesList = new ArrayList<HouseVariable>();
        List<HouseVariable> coils = new ArrayList<HouseVariable>();
        List<HouseVariable> registers = new ArrayList<HouseVariable>();

        HouseVariable v;

        try{
            //Separate HouseVariable objects by type (IX vs. IW) to 2 lists to sort them
            Iterator it = houseVariables.entrySet().iterator();
            while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (HouseVariable)pairs.getValue();
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
            for(HouseVariable hv : houseVariablesList){
                System.out.println(hv);
            }
            */

            memorySequances = new ArrayList<MBAddressSequance>();
            int ref1 = -1, ref2 = -1;
            String type = null;
            HouseVariable var;
            MBAddressSequance sequance = null;

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
                    sequance = new MBAddressSequance(ref1,ref2,var.readFunctionCall,var.type); //create new address sequance
                    memorySequances.add(sequance);
                }      
            }

            /*
            for(MBAddressSequance s : sequences){
                System.out.println(s);
            }
            */

            return memorySequances;

        } catch (Exception ex){
            ex.printStackTrace();
        }

    return null;
        
    }
    
    public HouseVariable findByAddressAndType(int addr, String type) {
        
        HouseVariable v;
        
        Iterator it = houseVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (HouseVariable)pairs.getValue();
            if (v.modbusAddr == addr && v.type == type)
                return v;
        }
        return null;
    }
    
    public HouseVariable findByName(String name){
       
        HouseVariable v;
        
        Iterator it = houseVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (HouseVariable)pairs.getValue();
            if (name.equalsIgnoreCase(v.name))
                return v;
        }
        return null; 
    }
    
    public HashMap getValuesForNames (ArrayList names){
        
        HouseVariable v;
        HashMap values = new HashMap(); 
        
        for (int i = 0 ; i < names.size(); i++){
            v = findByName((String)names.get(i));
            values.put(v.name,Integer.valueOf((v.value)));
        }
        
        return values;
    }
    
}


//Sort HouseVariable objects according to thier modbus address in ascending order
class HouseVariablesComparator implements Comparator<HouseVariable>{
 
    @Override
    public int compare(HouseVariable v1, HouseVariable v2) {
 
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