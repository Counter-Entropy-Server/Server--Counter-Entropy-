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

public class ReadExcel {

	private String inputFile;

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public HashMap read() throws IOException  {
		File inputWorkbook = new File(inputFile);
		Workbook w;
                HashMap houseVariables = new HashMap();
                HouseVariable v = null;
                
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			
                        // Get the first sheet
			Sheet sheet = w.getSheet(0);
			
                        Cell cell = null;
                        int row, col;
                        // Loop over all columns and rows
			 for (row = 1; row < sheet.getRows(); row++) {
                             v = new HouseVariable();
                             
                            for (col = 1; col < sheet.getColumns(); col++) {
                                    cell = sheet.getCell(col, row);
                                    if ("".equals((String)cell.getContents())) //if col is empty stop reading this row
                                        break;
                                    
                                    // System.out.println("row "+row+" col "+col+" = " + cell.getContents());

                                    switch (col){
                                        case 1: //modbus address
                                            v.modbusAddr = Integer.parseInt(cell.getContents());
                                            break;
                                        case 4: //function call
                                            v.functionCall = Integer.parseInt(cell.getContents());
                                            break;
                                        case 5: //name
                                            v.name = cell.getContents();
                                            break;
                                        case 6: //read/write
                                            if ("r".equals((String)cell.getContents())){
                                                v.readable = true;
                                            }
                                            break;          
                                    }
                            }

                            if (col == 1 && "".equals((String)cell.getContents())) //if 1st col in row is empty stop reading file
                                break;
                            houseVariables.put(v.name,v);
			}
                         
		} catch (BiffException ex) {
			ex.printStackTrace();
		}
                return houseVariables;
        }
} 



