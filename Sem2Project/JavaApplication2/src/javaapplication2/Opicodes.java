package javaapplication2;

import java.util.Stack;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author C00273530
 */
public class Opicodes {
    Stack<Object> stack = new Stack();
    Object[] LVA = new Object[100];
    // 03
    public void iconst_0(){
        stack.push(0);
    }
    // 08
    public void iconst_5(){
        stack.push(5);
    }
    // 1b
    public void iload_1(){
        stack.push(LVA[1]);
    }
    // 2a
    public void aload_0(){
        stack.push(LVA[0]);
    }
    // 3c
    public void istore_1(){
        Object entry = stack.pop();
        LVA[1] = entry;
    }
    // 84
    public void iinc(int index, int incValue){
        LVA[index] = ((int)LVA[index]) + incValue;
    }
    // a7
    public void goto1(){
    
    }
    // b7
    public void invokespecial(){
        
    }
    
}
