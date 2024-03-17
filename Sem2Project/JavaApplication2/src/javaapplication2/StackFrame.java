package javaapplication2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author C00273530
 */
public class StackFrame {
    String methodName;
    Stack<Object> stack; // Operand Stack
    ArrayList<Object> LVA; // Locla Variable Array
    String classString; // The string of all the code in this frame
    ArrayList<String> instructions; // Array of all the instructions
    int currentLineIndex; // Current line of execution
    String currentInstruction; // Current Instruction
    Map<Integer, String> instructionsMap; // A key value pair -> line : instruction
    boolean finished; // If true, execution has ended
    ArrayList<Integer> keysOfInstructions; // Array that stores the offset/keys in an array
    
    public StackFrame(String classString){
        setMethodName("");
        setStack();
        setLVA();
        setClassString(classString);
        setCurrentLineIndex();
        setInstructions();
        setCurrentInstruction();
        setInstructionsMap();
        setFinished();
        setKeysOfInstructions();
    }
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }
    public String getMethodName(){
        return methodName;
    }
    public void setCurrentLineIndex(){
        this.currentLineIndex = 0;
    }
    public int getCurrentLineIndex(){
        return currentLineIndex;
    }
    public void setStack(){
        stack = new Stack();
    }
    public Stack getStack(){
        return stack;
    }
    
    public void setLVA(){
        LVA = new ArrayList<>();
        for(int i=0;i<100;i++){
            LVA.add(null);
        }
    }
    public ArrayList getLVA(){
        return LVA;
    }
    
    public void setClassString(String classString){
        this.classString = classString;
    }
    public String getClassString(){
        return classString;
    }
    
    public void setInstructions(){
        instructions = new ArrayList<>();
        String text = "";
        for(int i=0;i<classString.length();i++){
            if(classString.charAt(i)!= ' '){
                if(classString.charAt(i) == '\n'){
                    instructions.add(text);
                    text = "";
                }
                else {
                    text += classString.charAt(i);
                }
            }
        }
    }
    public ArrayList getInstructions(){
        return instructions;
    }
    public void setCurrentInstruction(){
        
    }
    public String getCurrentInstruction(){
        return currentInstruction;
    }
    // Converts classString into a map using offset as key and bytecode as value
    public void setInstructionsMap(){
        instructionsMap = new HashMap<>();
        Object[] lines = classString.lines().toArray();
        // lines.length-1 due to an additional empty line at the end for an
        // unknown reason
        for(int i=0;i<lines.length-1;i++){
            lines[i] = lines[i].toString().strip();
            String[] sections = lines[i].toString().split(":");
            sections[1] = sections[1].strip();
            instructionsMap.put(Integer.parseInt(sections[0]), sections[1]);
        }
    }
    public Map getInstructionsMap(){
        return instructionsMap;
    }
    public void setFinished(){
        finished = false;
    }
    public boolean getFinished(){
        return finished;
    }
    public void setKeysOfInstructions(){
        keysOfInstructions = new ArrayList<>();
        for(Map.Entry<Integer, String> entry : instructionsMap.entrySet()){
            int key = entry.getKey();
            keysOfInstructions.add(key);
        }
    }
    public ArrayList getKeysOfInstructions(){
        return keysOfInstructions;
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
        if(!finished){
            // Sets the current line
            int currentLine = keysOfInstructions.get(currentLineIndex);
            // Sets the current instruction
            currentInstruction = instructionsMap.get(currentLine);
            if(currentLineIndex <= keysOfInstructions.size()){
               // System.out.println("test: " + currentLine + ", Inst: " + currentInstruction);
                String[] parameters = new String[10];
                if(currentInstruction != null){
                    setParameters(parameters,currentInstruction);
                    // Below removes the parameters off of the instruction string
                    if(currentInstruction.indexOf(" ")!=-1){
                        int index = currentInstruction.indexOf(" ");
                        currentInstruction = currentInstruction.substring(0, index);
                    }
                    if(currentInstruction.compareTo("iconst_0")==0){
                        iconst_0();
                    }
                    else if(currentInstruction.compareTo("iconst_5")==0){
                        iconst_5();
                    }
                    else if(currentInstruction.compareTo("iload_1")==0){
                        iload_1();
                    }
                    else if(currentInstruction.compareTo("aload_0")==0){
                        aload_0();
                    }
                    else if(currentInstruction.compareTo("istore_1")==0){
                        istore_1();
                    }
                    else if(currentInstruction.compareTo("iconst_5")==0){
                        iconst_5();
                    }
                    else if(currentInstruction.compareTo("iinc")==0){
                        iinc(Integer.parseInt(parameters[0]),Integer.parseInt(parameters[0]));
                    }
                    // Below needs to be changed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    else if(currentInstruction.compareTo("if_icmpge")==0){
                        int value1 = (int)stack.pop();
                        int value2 = (int)stack.pop();
                        System.out.println("Value 1: " + value1);
                        System.out.println("Value 2: " + value2);
                        if(value2>=value1){
                            System.out.println("Loop Value: " + Integer.parseInt(parameters[0]));
                            int index=0;
                            currentLine = Integer.parseInt(parameters[0]);
                            for(int i = 0; i<keysOfInstructions.size();i++){
                                if(keysOfInstructions.get(i) == currentLine){
                                    index = i;
                                    break;
                                }
                            }
                            currentLineIndex = index-1;
                            System.out.println("Loopey Loop: " + currentLineIndex);
                        }

                    }
                    // Below needs to be changed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    else if(currentInstruction.compareTo("goto")==0){
                        goto1(Integer.parseInt(parameters[0]));
                    }
                    else if(currentInstruction.compareTo("return")==0){
                        finished = true;
                    }
                // Sets the index of the position in the 
                currentLineIndex++;
                }
            }
        }
    }
    public void printStack(){
        System.out.print("Stack: ");
        for (Object element : stack) {
            System.out.print(element.toString() + ", ");
        }
    }
    // 00
    public void nop(){
        
    }
    // 01
    public void aconst_null(){
        stack.push(null);
    }
    // 02
    public void iconst_m1(){
        stack.push(-1);
    }
    // 03
    public void iconst_0(){
        stack.push(0);
    }
    // 04
    public void iconst_1(){
        stack.push(1);
    }
    // 05
    public void iconst_2(){
        stack.push(2);
    }
    // 06
    public void iconst_3(){
        stack.push(3);
    }
    // 07
    public void iconst_4(){
        stack.push(4);
    }
    // 08
    public void iconst_5(){
        stack.push(5);
    }
    // 09
    public void lconst_0(){
        stack.push(0L);
    }
    // 0a
    public void lconst_1(){
        stack.push(1L);
    }
    // 0b
    public void fconst_0(){
        stack.push(0.0f);
    }
    // 0c
    public void fconst_1(){
        stack.push(1.0f);
    }
    // 0d
    public void fconst_2(){
        stack.push(2.0f);
    }
    // 0e
    public void dconst_0(){
        stack.push(0.0);
    }
    // 0f
    public void dconst_1(){
        stack.push(1.0);
    }
    // 10
    public void bipush(byte b){
        stack.push((int) b);
    }
    // 11
    public void sipush(short s){
        stack.push((int) s);
    }
    // 12 -- NEEDS WORK!!!!!!!!!!!!!!!!
    public void ldc(){
        
    }
    // 13 -- NEEDS WORK!!!!!!!!!!!!!!!!
    public void ldc_w(){
        
    }
    // 14 -- NEEDS WORK!!!!!!!!!!!!!!!!
    public void ldc2_w(){
        
    }
    // 15
    public void iload(int index){
        stack.push((int) LVA.get(index));
    }
    // 16
    public void lload(int index){
        stack.push((long) LVA.get(index)); 
    }
    // 17
    public void fload(int index){
        stack.push((float) LVA.get(index));
    }
    // 18
    public void dload(int index){
        stack.push((double) LVA.get(index));
    }
    // 19
    public void aload(int index){
        stack.push(LVA.get(index));
    }
    // 1a
    public void iload_0(){
        stack.push((int) LVA.get(0));
    }
    // 1b
    public void iload_1(){
        stack.push((int) LVA.get(1));
    }
    // 1c
    public void iload_2(){
        stack.push((int) LVA.get(2));
    }
    // 1d
    public void iload_3(){
        stack.push((int) LVA.get(3));
    }
    // 1e
    public void lload_0(){
        stack.push((long) LVA.get(0));
    }
    // 1f
    public void lload_1(){
        stack.push((long) LVA.get(1));
    }
    // 20
    public void lload_2(){
        stack.push((long) LVA.get(2));
    }
    // 21
    public void lload_3(){
        stack.push((long) LVA.get(3));
    }
    // 22
    public void fload_0(){
        stack.push((float) LVA.get(0));
    }
    // 23
    public void fload_1(){
        stack.push((float) LVA.get(1));
    }
    // 24
    public void fload_2(){
        stack.push((float) LVA.get(2));
    }
    // 25
    public void fload_3(){
        stack.push((float) LVA.get(3));
    }
    // 26
    public void dload_0(){
        stack.push((double) LVA.get(0));
    }
    // 27
    public void dload_1(){
        stack.push((double) LVA.get(1));
    }
    // 28
    public void dload_2(){
        stack.push((double) LVA.get(2));
    }
    // 29
    public void dload_3(){
        stack.push((double) LVA.get(3));
    }
    // 2a
    public void aload_0(){
        stack.push(LVA.get(0));
    }
    // 2b
    public void aload_1(){
        stack.push(LVA.get(1));
    }
    // 2c
    public void aload_2(){
        stack.push(LVA.get(2));
    }
    // 2d
    public void aload_3(){
        stack.push(LVA.get(3));
    }
    // 2e -- NEEDS WORK!!!!!!!!!!!
    public void iaload(){
        
    }
    // 2f -- NEEDS WORK!!!!!!!!!!!
    public void laload(){
        
    }
    // 30 -- NEEDS WORK!!!!!!!!!!!
    public void faload(){
        
    }
    // 31 -- NEEDS WORK!!!!!!!!!!!
    public void daload(){
        
    }
    // 32 -- NEEDS WORK!!!!!!!!!!!
    public void aaload(){
        
    }
    // 33 -- NEEDS WORK!!!!!!!!!!!
    public void baload(){
        
    }
    // 34 -- NEEDS WORK!!!!!!!!!!!
    public void caload(){
        
    }
    // 35 -- NEEDS WORK!!!!!!!!!!!
    public void saload(){
        
    }
    // 36
    public void istore(int index){
        Object entry = stack.pop();
        LVA.set(index, (int) entry);
    }
    // 37
    public void lstore(int index){
        Object entry = stack.pop();
        LVA.set(index, (long) entry);
    }
    // 38
    public void fstore(int index){
        Object entry = stack.pop();
        LVA.set(index,(float) entry);
    }
    // 39
    public void dstore(int index){
        Object entry = stack.pop();
        LVA.set(index, (double) entry);
    }
    // 3a
    public void astore(int index){
        Object entry = stack.pop();
        LVA.set(index, entry);
    }
    // 3b
    public void istore_0(){
        Object entry = stack.pop();
        LVA.set(0, (int) entry);
    }
    // 3c
    public void istore_1(){
        Object entry = stack.pop();
        LVA.set(1, (int) entry);
    }
    // 3d
    public void istore_2(){
        Object entry = stack.pop();
        LVA.set(2, (int) entry);
    }
    // 3e
    public void istore_3(){
        Object entry = stack.pop();
        LVA.set(3, (int) entry);
    }
    // 3f
    public void lstore_0(){
        Object entry = stack.pop();
        LVA.set(0,(long) entry);
    }
    // 40
    public void lstore_1(){
        Object entry = stack.pop();
        LVA.set(1,(long) entry);
    }
    // 41
    public void lstore_2(){
        Object entry = stack.pop();
        LVA.set(2,(long) entry);
    }
    // 42
    public void lstore_3(){
        Object entry = stack.pop();
        LVA.set(3,(long) entry);
    }
    // 43
    public void fstore_0(){
        Object entry = stack.pop();
        LVA.set(0,(float) entry);
    }
    // 44
    public void fstore_1(){
        Object entry = stack.pop();
        LVA.set(1,(float) entry);
    }
    // 45
    public void fstore_2(){
        Object entry = stack.pop();
        LVA.set(2,(float) entry);
    }
    // 46
    public void fstore_3(){
        Object entry = stack.pop();
        LVA.set(3,(float) entry);
    }
    // 47
    public void dstore_0(){
        Object entry = stack.pop();
        LVA.set(0,(double) entry);
    }
    // 48
    public void dstore_1(){
        Object entry = stack.pop();
        LVA.set(1,(double) entry);
    }
    // 49
    public void dstore_2(){
        Object entry = stack.pop();
        LVA.set(2,(double) entry);
    }
    // 4a
    public void dstore_3(){
        Object entry = stack.pop();
        LVA.set(3,(double) entry);
    }
    // 4b
    public void astore_0(){
        Object entry = stack.pop();
        LVA.set(0,entry);
    }
    // 4c
    public void astore_1(){
        Object entry = stack.pop();
        LVA.set(1,entry);
    }
    // 4d
    public void astore_2(){
        Object entry = stack.pop();
        LVA.set(2,entry);
    }
    // 4e
    public void astore_3(){
        Object entry = stack.pop();
        LVA.add(3,entry);
    }
    // 4f -- NEEDS WORK!!!!!!
    public void iastore(){
        
    }
    // 50 -- NEEDS WORK!!!!!!
    public void lastore(){
        
    }
    // 51 -- NEEDS WORK!!!!!!
    public void fastore(){
        
    }
    // 52 -- NEEDS WORK!!!!!!
    public void dastore(){
        
    }
    // 53 -- NEEDS WORK!!!!!!
    public void aastore(){
    
    }
    // 54 -- NEEDS WORK!!!!!!
    public void bastore(){
        
    }
    // 55 -- NEEDS WORK!!!!!!
    public void castore(){
        
    }
    // 56 -- NEEDS WORK!!!!!!
    public void sastore(){
        
    }
    // 57
    public void pop(){
        stack.pop();
    }
    // 58
    public void pop2(){
        Object obj = stack.pop();
        if(!(obj instanceof Long || obj instanceof Double)){
            stack.pop();
        }
    }
    // 59 -- NEEDS WORK!!!!
    // Make sure it clones an object and doesnt push 2 memory references to the same object
    public void dup(){
     /*   Object obj = stack.pop();
        stack.push(obj);
        Object obj2 = new Object(obj);
        stack.push(obj2);
     */
    }
    // 5a -- NEEDS WORK!!!!!!!!
    public void dup_x(){
        
    }
    // 5b -- NEEDS WORK!!!!!!!!
    public void dup_x2(){
        
    } 
    // 5c -- NEEDS WORK!!!!!!!!
    public void dup2(){
        
    }
    // 5d -- NEEDS WORK!!!!!!!!
    public void dup2_x1(){
        
    }
    // 5e -- NEEDS WORK!!!!!!!!
    public void dup2_x2(){
        
    }
    // 5f -- NEEDS WORK!!!!!!!!
    public void swap(){
        
    }
    // 60
    public void iadd(){
        Object value1 = stack.pop();
        Object value2 = stack.pop();
        int result = (int) value1 + (int) value2;
        stack.push(result);
    }
    // 61
    public void ladd(){
        Object value1 = stack.pop();
        Object value2 = stack.pop();
        long result = (long) value1 + (long) value2;
        stack.push(result);
    }
    // 62
    public void fadd(){
        Object value1 = stack.pop();
        Object value2 = stack.pop();
        float result = (float) value1 + (float) value2;
        stack.push(result);
    }
    // 63
    public void dadd(){
        Object value1 = stack.pop();
        Object value2 = stack.pop();
        double result = (double) value1 + (double) value2;
        stack.push(result);
    }
    // 64
    public void isub(){
        int value1 = (int) stack.pop();
        int value2 = (int) stack.pop();
        int result = value2 - value1;
        stack.push(result);
    }
    // 65
    public void lsub(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        long result = value2 - value1;
        stack.push(result);
    }
    // 66
    public void fsub(){
        float value1 = (float) stack.pop();
        float value2 = (float) stack.pop();
        float result = value2 - value1;
        stack.push(result);
    }
    // 67
    public void dsub(){
        double value1 = (double) stack.pop();
        double value2 = (double) stack.pop();
        double result = value2 - value1;
        stack.push(result);
    }
    // 68
    public void imul(){
        int value1 = (int) stack.pop();
        int value2 = (int) stack.pop();
        int result = value1*value2;
        stack.push(result);
    }
    // 69
    public void lmul(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        long result = value1*value2;
        stack.push(result);
    }
    // 6a
    public void fmul(){
        float value1 = (float) stack.pop();
        float value2 = (float) stack.pop();
        float result = value1*value2;
        stack.push(result);
    }
    // 6b
    public void dmul(){
        double value1 = (double) stack.pop();
        double value2 = (double) stack.pop();
        double result = value1*value2;
        stack.push(result);
    }
    // 6c
    public void idiv(){
        int value1 = (int) stack.pop();
        int value2 = (int) stack.pop();
        int result = value2/value1;
        stack.push(result);
    }
    // 6d
    public void ldiv(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        long result = value2/value1;
        stack.push(result);
    }
    // 6e
    public void fdiv(){
        float value1 = (float) stack.pop();
        float value2 = (float) stack.pop();
        float result = value2/value1;
        stack.push(result);
    }
    // 6f
    public void ddiv(){
        double value1 = (double) stack.pop();
        double value2 = (double) stack.pop();
        double result = value2/value1;
        stack.push(result);
    }
    // 70 
    public void irem(){
        int value1 = (int) stack.pop();
        int value2 = (int) stack.pop();
        int result = value2%value1;
        stack.push(result);
    }        
    // 71 
    public void lrem(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        long result = value2%value1;
        stack.push(result);
    }
    // 72
    public void frem(){
        float value1 = (float) stack.pop();
        float value2 = (float) stack.pop();
        float result = value2%value1;
        stack.push(result);
    }
    // 73
    public void drem(){
        double value1 = (double) stack.pop();
        double value2 = (double) stack.pop();
        double result = value2%value1;
        stack.push(result);
    }
    // 74
    public void ineg(){
        int value = (int) stack.pop();
        value *= -1;
        stack.push(value);
    }
    // 75
    public void lneg(){
        long value = (long) stack.pop();
        value *= -1;
        stack.push(value);
    }
    // 76
    public void fneg(){
        float value = (float) stack.pop();
        value *= -1;
        stack.push(value);
    }
    // 77
    public void dneg(){
        double value = (double) stack.pop();
        value *= -1;
        stack.push(value);
    }
    // 78
    public void ishl(){
        int numToBeShifted = (int) stack.pop();
        int numOfBitsToShift = (int) stack.pop();
        int result = numToBeShifted << numOfBitsToShift;
        stack.push(result);
    }
    // 79
    public void lshl(){
        long numToBeShifted = (long) stack.pop();
        int numOfBitsToShift = (int) stack.pop();
        long result = numToBeShifted << numOfBitsToShift;
        stack.push(result);
    }
    // 7a
    public void ishr(){
        int numToBeShifted = (int) stack.pop();
        int numOfBitsToShift = (int) stack.pop();
        int result = numToBeShifted >> numOfBitsToShift;
        stack.push(result);
    }
    // 7b
    public void lshr(){
        long numToBeShifted = (long) stack.pop();
        int numOfBitsToShift = (int) stack.pop();
        long result = numToBeShifted >> numOfBitsToShift;
        stack.push(result);
    }
    // 7c
    public void iushr(){
        int numToBeShifted = (int) stack.pop();
        int numOfBitsToShift = (int) stack.pop();
        int result = numToBeShifted >>> numOfBitsToShift;
        stack.push(result);
    }
    // 7d
    public void lushr(){
        long numToBeShifted = (long) stack.pop();
        int numOfBitsToShift = (int) stack.pop();
        long result = numToBeShifted >> numOfBitsToShift;
        stack.push(result);
    }
    // 7e
    public void iand(){
        int value1 = (int) stack.pop();
        int value2 = (int) stack.pop();
        int result = value1 & value2;
        stack.push(result);
    }
    // 7f
    public void land(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        long result = value1 & value2;
        stack.push(result);
    }
    // 80
    public void ior(){
        int value1 = (int) stack.pop();
        int value2 = (int) stack.pop();
        int result = value1 | value2;
        stack.push(result);
    }
    // 81
    public void lor(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        long result = value1 | value2;
        stack.push(result);
    }
    // 82
    public void ixor(){
        int value1 = (int) stack.pop();
        int value2 = (int) stack.pop();
        int result = value1 ^ value2;
        stack.push(result);
    }
    // 83
    public void lxor(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        long result = value1 ^ value2;
        stack.push(result);
    }
    // 84
    public void iinc(int index, int incValue){
        LVA.set(index, ((int) LVA.get(index)) + incValue);
    }
    // 85
    public void i2l(){
        long value = (int) stack.pop();
        stack.push(value);
    }
    // 86
    public void i2f(){
        float value = (int) stack.pop();
        stack.push(value);
    }
    // 87
    public void i2d(){
        double value = (double) stack.pop();
        stack.push(value);
    }
    // 88
    public void l2i(){
        long value = (long) stack.pop();
        int result = (int) value;
        stack.push(result);
    }
    // 89
    public void l2f(){
        long value = (long) stack.pop();
        float result = (float) value;
        stack.push(result);
    }
    // 8a
    public void l2d(){
        double value = (long) stack.pop();
        double result = (double) value;
        stack.push(result);
    }
    // 8b
    public void f2i(){
        float value = (float) stack.pop();
        int result = (int) value;
        stack.push(result);
    }
    // 8c
    public void f2l(){
        float value = (float) stack.pop();
        long result = (long) value;
        stack.push(result);
    }
    // 8d
    public void f2d(){
        float value = (float) stack.pop();
        double result = (double) value;
        stack.push(result);
    }
    // 8e
    public void d2i(){
        double value = (double) stack.pop();
        int result = (int) value;
        stack.push(result);
    }
    // 8f
    public void d2l(){
        double value = (double) stack.pop();
        long result = (long) value;
        stack.push(result);
    }
    // 90
    public void d2f(){
        double value = (double) stack.pop();
        float result = (float) value;
        stack.push(result);
    }
    // 91
    public void i2b(){
        int value = (int) stack.pop();
        byte result = (byte) value;
        stack.push(result);
    }
    // 92
    public void i2c(){
        int value = (int) stack.pop();
        char result = (char) value;
        stack.push(result);
    }
    // 93
    public void i2s(){
        int value = (int) stack.pop();
        short result = (short) value;
        stack.push(result);
    }
    // 94
    public void lcmp(){
        long value1 = (long) stack.pop();
        long value2 = (long) stack.pop();
        if(value1 == value2){
            stack.push(0);
        } else if(value1<value2){
            stack.push(-1);
        } else if(value1>value2){
            stack.push(1);
        }
    }
    // 95
    public void fcmpl(){
        float value1 = (float) stack.pop();
        float value2 = (float) stack.pop();
        if (Float.isNaN(value1) || Float.isNaN(value2)) {
            stack.push(1);
        }
        else if(value1 == value2){
            stack.push(-1);
        } else if(value1<value2){
            stack.push(-1);
        } else if(value1>value2){
            stack.push(1);
        }
    }
    // 96
    public void fcmpg(){
        float value1 = (float) stack.pop();
        float value2 = (float) stack.pop();
        if (Float.isNaN(value1) || Float.isNaN(value2)) {
            stack.push(1);
        }
        else if(value1 == value2){
            stack.push(0);
        } else if(value1<value2){
            stack.push(-1);
        } else if(value1>value2){
            stack.push(1);
        }
    }
    // 97
    public void dcmpl(){
        double value1 = (double) stack.pop();
        double value2 = (double) stack.pop();
        if (Double.isNaN(value1) || Double.isNaN(value2)) {
            stack.push(-1);
        }
        else if(value1 == value2){
            stack.push(0);
        } else if(value1<value2){
            stack.push(-1);
        } else if(value1>value2){
            stack.push(1);
        }
    }
    // 98
    public void dcmpg(){
        double value1 = (double) stack.pop();
        double value2 = (double) stack.pop();
        if (Double.isNaN(value1) || Double.isNaN(value2)) {
            stack.push(1);
        }
        else if(value1 == value2){
            stack.push(0);
        } else if(value1<value2){
            stack.push(-1);
        } else if(value1>value2){
            stack.push(1);
        }
    }
    // 99
    public void ifeq(){
        
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
    public void goto1(int newLine){
        this.currentLineIndex =  newLine-1;
    }
    // b7
    public void invokespecial(){
        
    }
    
} 