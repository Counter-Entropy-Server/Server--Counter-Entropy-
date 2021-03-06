//License
/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
package net.wimpi.modbus.cmd;

import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import net.wimpi.modbus.procimg.SimpleRegister;


/**
 * Class implementing a simple Modbus/TCP slave.
 * A simple process image is available to test
 * functionality and behaviour of the implementation.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class TCPSlaveTest {


  public static void main(String[] args) {

    ModbusTCPListener listener = null;
    SimpleProcessImage spi = null;
    int port = Modbus.DEFAULT_PORT;
  
    try {
      if(args != null && args.length ==1) {
        port = Integer.parseInt(args[0]);
      }
      System.out.println("jModbus Modbus Slave (Server)");

      //1. prepare a process image
      spi = new SimpleProcessImage();

      // Coils (the modbus address of the first bit is writter in the LSB)
      /*
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      * 
      */
      
      spi.addDigitalOut(new SimpleDigitalOut(false));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(false));




      // Holding Registers
      spi.addRegister(new SimpleRegister(0)); // in decimal
      spi.addRegister(new SimpleRegister(100)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(5)); // in decimal
      spi.addRegister(new SimpleRegister(6)); // in decimal
      spi.addRegister(new SimpleRegister(7)); // in decimal
      spi.addRegister(new SimpleRegister(8)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(251)); // in decimal
      spi.addRegister(new SimpleRegister(200)); // in decimal
      spi.addRegister(new SimpleRegister(26)); // in decimal
      spi.addRegister(new SimpleRegister(27)); // in decimal
      spi.addRegister(new SimpleRegister(28)); // in decimal
      //spi.addRegister(new SimpleRegister(200)); // in decimal
      
      // Input Registers
      //spi.addInputRegister(new SimpleInputRegister(45));

      //2. create the coupler holding the image
      ModbusCoupler.getReference().setProcessImage(spi);
      ModbusCoupler.getReference().setMaster(false);
      ModbusCoupler.getReference().setUnitID(15);

      //3. create a listener with 3 threads in pool
      if (Modbus.debug) System.out.println("Listening...");
      listener = new ModbusTCPListener(3);
      listener.setPort(port);
      listener.start();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }//main


}//class TCPSlaveTest
