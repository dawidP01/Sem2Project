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
    String classString;
    String[] instructions = new String[100];
    int currentLine;
    
    public Opicodes(String classString){
        setStack();
        setLVA();
        setClassString(classString);
        setCurrentLine(8);
        setInstructions();
    }
    public void setCurrentLine(int currentLine){
        this.currentLine = currentLine;
    }
    public int getCurrentLine(){
        return currentLine;
    }
    public void setStack(){
        
    }
    public Stack getStack(){
        return stack;
    }
    
    public void setLVA(){
    
    }
    public Object[] getLVA(){
        return LVA;
    }
    
    public void setClassString(String classString){
        this.classString = classString;
    }
    public String getClassString(){
        return classString;
    }
    
    public void setInstructions(){
        String instruction = "";
        int instructionCount = 0;
        int lineCount = 0;
        boolean add = false;
        for(int i=0;i<classString.length();i++){
            // checks if linebreak
            if(classString.charAt(i)=='\n'){
                lineCount++;
            }
            // Checks to look for start of code section
            if(classString.charAt(i)=='C' )
                if(classString.charAt(i+1)=='o')
                    if(classString.charAt(i+2)=='d')
                        if(classString.charAt(i+3)=='e'){
                            add=true;
                        }
            // Checks if end of code section
            if(add){
                if(classString.charAt(i)=='L'){
                    if(classString.charAt(i+1)=='i')
                        if(classString.charAt(i+2)=='n')
                            if(classString.charAt(i+3)=='e')
                                if(classString.charAt(i+4)=='N'){
                                    add = false;
                                    instruction = "";
                                }
                }
                
                else{
                    // Checks if add Instruction
                    if(classString.charAt(i)=='\n'){
                        instructions[instructionCount] = instruction.strip();
                        // Removes line number
                        for(int j=0;j<instructions[instructionCount].length();j++){
                            if(instructions[instructionCount].charAt(j)==':'){
                                instructions[instructionCount] = instructions[instructionCount].substring(j+1);
                                instructions[instructionCount] = instructions[instructionCount].strip();
                            }
                        }
                        instructionCount++;
                        instruction="";
                    // Checks if add letter to instruction entry
                    } else {
                        instruction+=classString.charAt(i);
                    }
                }
            }
        }
    }
    public String[] getInstructions(){
        return instructions;
    }
    public String stripInstruction(String instruction){
        for(int i=0;i<instruction.length();i++){
            if(instruction.charAt(i)==' '){
                return instruction.substring(0, i);
            }
        }
        return instruction;
    }
    public void setParameters(String[] parameters, String instruction){
        String parameter = "";
        String arg = "";
        int paramCount = 0;
        char currentChar;
        // Extracts the parameters from the command string
        for(int i=0;i<instruction.length();i++){
            if(instruction.charAt(i) == ' '){
                parameter = instruction.substring(i).strip();
                break;
            }
        }
        // Adds the parameter to the parameter array
        for(int i=0;i<parameter.length();i++){
            currentChar = parameter.charAt(i);
            if(currentChar>='0' && currentChar<='9'){
                arg += currentChar;
            }
            if(currentChar==','){
                parameters[paramCount] = arg;
                arg = "";
                paramCount++;
            }
        }
        // Adds the last parameter
        if(arg.compareTo("")!=0){
            parameters[paramCount] = arg;
        }
    }
    // For demo start has to be 8 minimum, and end has to be 16 max
    public void runInstructions(){
        String instruction = stripInstruction(instructions[currentLine]);
        String[] parameters = new String[10];
        setParameters(parameters,instructions[currentLine].strip());
        if(instructions[currentLine].compareTo("iconst_0")==0){
            iconst_0();
        }
        else if(instructions[currentLine].compareTo("iconst_5")==0){
            iconst_5();
        }
        else if(instructions[currentLine].compareTo("iload_1")==0){
            iload_1();
        }
        else if(instructions[currentLine].compareTo("aload_0")==0){
            aload_0();
        }
        else if(instructions[currentLine].compareTo("istore_1")==0){
            istore_1();
        }
        else if(instructions[currentLine].compareTo("iconst_5")==0){
            iconst_5();
        }
        else if(instruction.compareTo("iinc")==0){
            iinc(Integer.parseInt(parameters[0]),Integer.parseInt(parameters[0]));
        }
        // Below needs to be changed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        else if(instruction.compareTo("if_icmpge")==0){
            int value1 = (int)stack.pop();
            int value2 = (int)stack.pop();
            if(value2>=value1){
                currentLine = 14;
            }
        }
        // Below needs to be changed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        else if(instruction.compareTo("goto")==0){
            currentLine = 9;
        }
        else if(instruction.compareTo("return")==0){

        }
        System.out.print("Line: "+ currentLine);
        System.out.print(", Instruction: " +instruction + ", ");
        printStack();
        System.out.println();
    }
    public void printStack(){
        System.out.print("Stack: ");
        for (Object element : stack) {
            System.out.print(element.toString() + ", ");
        }
    }
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
    // a2
    public void if_icmpge(){
        int a = (int)stack.pop();
        int b = (int)stack.pop();
        if(a>=b){
            // Skip to this position
        }
    }
    // a7
    public void goto1(){
    
    }
    // b7
    public void invokespecial(){
        
    }
    
}