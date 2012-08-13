/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;


import java.io.File;
import java.io.IOException;
import java.util.*;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.wimpi.modbus.util.HouseVariable;

/**
 *
 * @author nur
 */

/**
 *
 * Takes excel file path as input and returns a HashMap of keys:variable-name, values:HouseVariable-objects.
 * NOTE: While reading the file the arrangment of cloumns in the sheet is essential
 */
public class ReadExcel {

    private String inputFile;

    public void setInputFile(String inputFile) {
            this.inputFile = inputFile;
    }

    public HashMap read() throws IOException  {

            File inputWorkbook = new File(inputFile);
            Workbook w;
            HashMap houseVariables = new HashMap(); //to store read variables
            HouseVariable v = null;

            try {
                w = Workbook.getWorkbook(inputWorkbook);
                Sheet sheet = w.getSheet(0); //get the first sheet

                Cell cell = null;
                int row, col;

                System.out.println("Reading house variables from excel file");

                // Loop over all columns and rows
                for (row = 1; row < sheet.getRows(); row++) {
                    v = new HouseVariable();

                for (col = 1; col < sheet.getColumns(); col++) {
                        cell = sheet.getCell(col, row);
                        if ("".equals((String)cell.getContents())) //if col is empty stop reading this row
                            break;

                        switch (col){
                            case 1: //modbus address
                                v.modbusAddr = Integer.parseInt(cell.getContents());
                                break;
                            case 2: //variable type (IX/IW)
                            v.type = cell.getContents();
                                break;
                            case 4: //function call
                                v.readFunctionCall = Integer.parseInt(cell.getContents());
                                break;
                            case 5: //function call
                                v.writeFunctionCall = Integer.parseInt(cell.getContents());
                                break;
                            case 6: //name
                                v.name = cell.getContents();
                                break;
                            case 9: //minimum expected value
                                v.minValue = Integer.parseInt(cell.getContents());
                                break;
                            case 10: //maximum expected value
                                v.maxValue = Integer.parseInt(cell.getContents());
                                break;
                        }
                }

                if (col == 1 && "".equals((String)cell.getContents())) //if 1st col in row is empty stop reading file
                    break;
                houseVariables.put(v.name,v);
                }
                
            } catch (BiffException ex) {
                    ex.printStackTrace();
                    System.out.println("ERR: Cannot read house variables from excel file");
            }
            return houseVariables;
        }
} 



