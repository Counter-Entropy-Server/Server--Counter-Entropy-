/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

/**
 *
 * @author nur
 */
public class CEAddressSequance {
    public int ref1;
    public int ref2;
    public int functionCall;
    public String type;
    
    public CEAddressSequance(int ref1, int ref2, int function, String type){
        this.ref2 = ref2;
        this.ref1 = ref1;
        this.functionCall = function;
        this.type = type;
    }
    
    public String toString(){
        return "ref1: "+ref1+" ref2: "+ref2+" function call: "+functionCall+ " type: " + type;
    }
}
