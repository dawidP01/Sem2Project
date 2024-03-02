/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication2;

/**
 *
 * @author C00273530
 */
public class stackEntry {
    private String string;
    private int i;
    private float f;
    private char c;
    private double d;
    private boolean b;
    private Object o;

    
    public stackEntry(String string){
        setString(string);
    }
    public stackEntry(int i){
        setI(i);
    }
    public stackEntry(float f){
        setF(f);
    }
    public stackEntry(char c){
        setC(c);
    }
    public stackEntry(double d){
        setD(d);
    }
    public stackEntry(boolean b){
        setB(b);
    }
    public stackEntry(Object o){
        setO(o);
    }
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }
    
    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }

}
