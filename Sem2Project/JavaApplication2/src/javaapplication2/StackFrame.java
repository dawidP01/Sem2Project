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
    Map<String, String> constantPool;
    boolean finished; // If true, execution has ended
    ArrayList<Integer> keysOfInstructions; // Array that stores the offset/keys in an array
    
    public StackFrame(String classString, String constantPoolString){
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
        setConstantPool(constantPoolString);
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
    public void setParameters(Object[] parameters, String instruction){
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
    public void setConstantPool(String constantPoolString){
        int index = constantPoolString.indexOf("\n");
        constantPoolString = constantPoolString.substring(index+1);
        String[] lines = constantPoolString.split("\\n");
        constantPool = new HashMap<>();
        for(String line : lines){
            String key="";
            String value="";
            boolean keyBoolean = true;
            boolean valueBoolean = false;
            line = line.trim();
            for(int i=0;i<line.length();i++){
                if (keyBoolean){
                    if(line.charAt(i)=='='){
                        keyBoolean=false;
                        i++;
                    } else {
                        key += line.charAt(i);
                    }
                }
                else if(!valueBoolean){
                    
                    if(line.charAt(i)== ' '){
                        while(line.charAt(i)!= ' '){
                            i++;
                        }
                        valueBoolean = true;
                        
                        i++;
                    }
                } else if (valueBoolean){
                    //System.out.print(line.charAt(i));
                    if(i+1<line.length()){
                        if(line.charAt(i) == '/' && line.charAt(i+1) == '/'){
                            break;
                        }
                    }
                    value += line.charAt(i);
                }
            }
           // System.out.println();
            constantPool.put(key, value.trim());
        }
      //  System.out.println();
      System.out.println(constantPool);
    }
    public Map getConstantPool(){
        return constantPool;
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
                Object[] parameters = new Object[10];
                if(currentInstruction != null){
                    setParameters(parameters,currentInstruction);
                    // Below removes the parameters off of the instruction string
                    if(currentInstruction.indexOf(" ")!=-1){
                        int index = currentInstruction.indexOf(" ");
                        currentInstruction = currentInstruction.substring(0, index);
                    }
                    if(currentInstruction.compareTo("nop")==0){
                        nop();
                    }
                    else if(currentInstruction.compareTo("aconst_null")==0){
                        aconst_null();
                    }
                    else if(currentInstruction.compareTo("iconst_m1")==0){
                        iconst_m1();
                    }
                    else if(currentInstruction.compareTo("iconst_0")==0){
                        iconst_0();
                    }
                    else if(currentInstruction.compareTo("iconst_1")==0){
                        iconst_1();
                    }
                    else if(currentInstruction.compareTo("iconst_2")==0){
                        iconst_2();
                    }
                    else if(currentInstruction.compareTo("iconst_3")==0){
                        iconst_3();
                    }
                    else if(currentInstruction.compareTo("iconst_4")==0){
                        iconst_4();
                    }
                    else if(currentInstruction.compareTo("iconst_5")==0){
                        iconst_5();
                    }
                    else if(currentInstruction.compareTo("lconst_0")==0){
                        lconst_0();
                    }
                    else if(currentInstruction.compareTo("lconst_1")==0){
                        lconst_1();
                    }
                    else if(currentInstruction.compareTo("fconst_0")==0){
                        fconst_0();
                    }
                    else if(currentInstruction.compareTo("fconst_1")==0){
                        fconst_1();
                    }
                    else if(currentInstruction.compareTo("fconst_2")==0){
                        fconst_2();
                    }
                    else if(currentInstruction.compareTo("dconst_0")==0){
                        dconst_0();
                    }
                    else if(currentInstruction.compareTo("dconst_0")==0){
                        dconst_0();
                    }
                    else if(currentInstruction.compareTo("dconst_1")==0){
                        dconst_1();
                    }
                    // Fix below
                    else if(currentInstruction.compareTo("bipush")==0){
                        bipush((int) parameters[0]);
                    }
                    // Fix Below
                    else if(currentInstruction.compareTo("sipush")==0){
                        sipush((int) parameters[0]);
                    }
                    else if(currentInstruction.compareTo("ldc")==0){
                      //  ldc();
                    }
                    else if(currentInstruction.compareTo("ldc_w")==0){
                        ldc_w();
                    }
                    else if(currentInstruction.compareTo("ldc2_w")==0){
                        ldc2_w();
                    }
                    else if(currentInstruction.compareTo("iload")==0){
                        iload(Integer.parseInt((String) parameters[0]));
                    }
                    // Fix Below
                    else if(currentInstruction.compareTo("lload")==0){
                        // lload();
                    }
                    // Fix Below
                    else if(currentInstruction.compareTo("fload")==0){
                        // fload();
                    }
                    // Fix Below
                    else if(currentInstruction.compareTo("dload")==0){
                        // dload();
                    }
                    // Fix Below
                    else if(currentInstruction.compareTo("aload")==0){
                        // aload();
                    }
                    else if(currentInstruction.compareTo("iload_0")==0){
                        iload_0();
                    }
                    else if(currentInstruction.compareTo("iload_1")==0){
                        iload_1();
                    }
                    else if(currentInstruction.compareTo("iload_2")==0){
                        iload_2();
                    }
                    else if(currentInstruction.compareTo("iload_3")==0){
                        iload_3();
                    }
                    else if(currentInstruction.compareTo("lload_0")==0){
                        lload_0();
                    }
                    else if(currentInstruction.compareTo("lload_1")==0){
                        lload_1();
                    }
                    else if(currentInstruction.compareTo("lload_2")==0){
                        lload_2();
                    }
                    else if(currentInstruction.compareTo("lload_3")==0){
                        lload_3();
                    }
                    else if(currentInstruction.compareTo("fload_0")==0){
                        fload_0();
                    }
                    else if(currentInstruction.compareTo("fload_1")==0){
                        fload_1();
                    }
                    else if(currentInstruction.compareTo("fload_2")==0){
                        fload_2();
                    }
                    else if(currentInstruction.compareTo("fload_3")==0){
                        fload_3();
                    }
                    else if(currentInstruction.compareTo("dload_0")==0){
                        dload_0();
                    }
                    else if(currentInstruction.compareTo("dload_1")==0){
                        dload_1();
                    }
                    else if(currentInstruction.compareTo("dload_2")==0){
                        dload_2();
                    }
                    else if(currentInstruction.compareTo("dload_3")==0){
                        dload_3();
                    }
                    else if(currentInstruction.compareTo("aload_0")==0){
                        aload_0();
                    }
                    else if(currentInstruction.compareTo("aload_1")==0){
                        aload_1();
                    }
                    else if(currentInstruction.compareTo("aload_2")==0){
                        aload_2();
                    }
                    else if(currentInstruction.compareTo("aload_3")==0){
                        aload_3();
                    }
                    else if(currentInstruction.compareTo("iaload")==0){
                        iaload();
                    }
                    else if(currentInstruction.compareTo("laload")==0){
                        laload();
                    }
                    else if(currentInstruction.compareTo("faload")==0){
                        faload();
                    }
                    else if(currentInstruction.compareTo("daload")==0){
                        daload();
                    }
                    else if(currentInstruction.compareTo("aaload")==0){
                        aaload();
                    }
                    else if(currentInstruction.compareTo("baload")==0){
                        baload();
                    }
                    else if(currentInstruction.compareTo("caload")==0){
                        caload();
                    }
                    else if(currentInstruction.compareTo("saload")==0){
                        saload();
                    }
                    // Fix Below!! 
                    else if(currentInstruction.compareTo("istore")==0){
                        // istore();
                    }
                    // Fix Below!! 
                    else if(currentInstruction.compareTo("lstore")==0){
                        // lstore();
                    }
                    // Fix Below!! 
                    else if(currentInstruction.compareTo("fstore")==0){
                        // fstore();
                    }
                    // Fix Below!! 
                    else if(currentInstruction.compareTo("dstore")==0){
                        // dstore();
                    }
                    // Fix Below!! 
                    else if(currentInstruction.compareTo("astore")==0){
                        // astore();
                    }
                    else if(currentInstruction.compareTo("istore_0")==0){
                        istore_0();
                    }
                    else if(currentInstruction.compareTo("istore_1")==0){
                        istore_1();
                    }
                    else if(currentInstruction.compareTo("istore_2")==0){
                        istore_2();
                    }
                    else if(currentInstruction.compareTo("istore_3")==0){
                        istore_3();
                    }
                    else if(currentInstruction.compareTo("lstore_0")==0){
                        lstore_0();
                    }
                    else if(currentInstruction.compareTo("lstore_1")==0){
                        lstore_1();
                    }
                    else if(currentInstruction.compareTo("lstore_2")==0){
                        lstore_2();
                    }
                    else if(currentInstruction.compareTo("lstore_3")==0){
                        lstore_3();
                    }
                    else if(currentInstruction.compareTo("fstore_0")==0){
                        fstore_0();
                    }
                    else if(currentInstruction.compareTo("fstore_1")==0){
                        fstore_1();
                    }
                    else if(currentInstruction.compareTo("fstore_2")==0){
                        fstore_2();
                    }
                    else if(currentInstruction.compareTo("fstore_3")==0){
                        fstore_3();
                    }
                    else if(currentInstruction.compareTo("dstore_0")==0){
                        dstore_0();
                    }
                    else if(currentInstruction.compareTo("dstore_1")==0){
                        dstore_1();
                    }
                    else if(currentInstruction.compareTo("dstore_2")==0){
                        dstore_2();
                    }
                    else if(currentInstruction.compareTo("dstore_3")==0){
                        dstore_3();
                    }
                    else if(currentInstruction.compareTo("astore_0")==0){
                        astore_0();
                    }
                    else if(currentInstruction.compareTo("astore_1")==0){
                        astore_1();
                    }
                    else if(currentInstruction.compareTo("astore_2")==0){
                        astore_2();
                    }
                    else if(currentInstruction.compareTo("astore_3")==0){
                        astore_3();
                    }
                    else if(currentInstruction.compareTo("iastore")==0){
                        iastore();
                    }
                    else if(currentInstruction.compareTo("lastore")==0){
                        lastore();
                    }
                    else if(currentInstruction.compareTo("fastore")==0){
                        fastore();
                    }
                    else if(currentInstruction.compareTo("dastore")==0){
                        dastore();
                    }
                    else if(currentInstruction.compareTo("aastore")==0){
                        aastore();
                    }
                    else if(currentInstruction.compareTo("bastore")==0){
                        bastore();
                    }
                    else if(currentInstruction.compareTo("castore")==0){
                        castore();
                    }
                    else if(currentInstruction.compareTo("sastore")==0){
                        sastore();
                    }
                    else if(currentInstruction.compareTo("pop")==0){
                        pop();
                    }
                    else if(currentInstruction.compareTo("pop2")==0){
                        pop2();
                    }
                    else if(currentInstruction.compareTo("dup")==0){
                        dup();
                    }
                    else if(currentInstruction.compareTo("dup_x1")==0){
                        dup_x1();
                    }
                    else if(currentInstruction.compareTo("dup_x2")==0){
                        dup_x2();
                    }
                    else if(currentInstruction.compareTo("dup2")==0){
                        dup2();
                    }
                    else if(currentInstruction.compareTo("dup2")==0){
                        dup2_x1();
                    }
                    else if(currentInstruction.compareTo("dup2")==0){
                        dup2_x2();
                    }
                    else if(currentInstruction.compareTo("swap")==0){
                        swap();
                    }
                    else if(currentInstruction.compareTo("iadd")==0){
                        iadd();
                    }
                    else if(currentInstruction.compareTo("ladd")==0){
                        ladd();
                    }
                    else if(currentInstruction.compareTo("fadd")==0){
                        fadd();
                    }
                    else if(currentInstruction.compareTo("dadd")==0){
                        dadd();
                    }
                    else if(currentInstruction.compareTo("isub")==0){
                        isub();
                    }
                    else if(currentInstruction.compareTo("lsub")==0){
                        lsub();
                    }
                    else if(currentInstruction.compareTo("fsub")==0){
                        fsub();
                    }
                    else if(currentInstruction.compareTo("dsub")==0){
                        dsub();
                    }
                    else if(currentInstruction.compareTo("imul")==0){
                        imul();
                    }
                    else if(currentInstruction.compareTo("lmul")==0){
                        lmul();
                    }
                    else if(currentInstruction.compareTo("fmul")==0){
                        fmul();
                    }
                    else if(currentInstruction.compareTo("dmul")==0){
                        dmul();
                    }
                    else if(currentInstruction.compareTo("idiv")==0){
                        idiv();
                    }
                    else if(currentInstruction.compareTo("ldiv")==0){
                        ldiv();
                    }
                    else if(currentInstruction.compareTo("fdiv")==0){
                        fdiv();
                    }
                    else if(currentInstruction.compareTo("ddiv")==0){
                        ddiv();
                    }
                    else if(currentInstruction.compareTo("irem")==0){
                        irem();
                    }
                    else if(currentInstruction.compareTo("irem")==0){
                        lrem();
                    }
                    else if(currentInstruction.compareTo("frem")==0){
                        frem();
                    }
                    else if(currentInstruction.compareTo("drem")==0){
                        drem();
                    }
                    else if(currentInstruction.compareTo("ineg")==0){
                        ineg();
                    }
                    else if(currentInstruction.compareTo("lneg")==0){
                        lneg();
                    }
                    else if(currentInstruction.compareTo("fneg")==0){
                        fneg();
                    }
                    else if(currentInstruction.compareTo("dneg")==0){
                        dneg();
                    }
                    else if(currentInstruction.compareTo("ishl")==0){
                        ishl();
                    }
                    else if(currentInstruction.compareTo("lshl")==0){
                        lshl();
                    }
                    else if(currentInstruction.compareTo("ishr")==0){
                        ishr();
                    }
                    else if(currentInstruction.compareTo("lshr")==0){
                        lshr();
                    }
                    else if(currentInstruction.compareTo("iushr")==0){
                        iushr();
                    }
                    else if(currentInstruction.compareTo("lushr")==0){
                        lushr();
                    }
                    else if(currentInstruction.compareTo("iand")==0){
                        iand();
                    }
                    else if(currentInstruction.compareTo("land")==0){
                        land();
                    }
                    else if(currentInstruction.compareTo("ior")==0){
                        ior();
                    }
                    else if(currentInstruction.compareTo("lor")==0){
                        lor();
                    }
                    else if(currentInstruction.compareTo("ixor")==0){
                        ixor();
                    }
                    else if(currentInstruction.compareTo("lxor")==0){
                        lxor();
                    }
                    else if(currentInstruction.compareTo("iinc")==0){
                        iinc(Integer.parseInt((String)parameters[0]),Integer.parseInt((String)parameters[0]));
                    }
                    else if(currentInstruction.compareTo("i2l")==0){
                        i2l();
                    }
                    else if(currentInstruction.compareTo("i2f")==0){
                        i2f();
                    }
                    else if(currentInstruction.compareTo("i2d")==0){
                        i2d();
                    }
                    else if(currentInstruction.compareTo("l2i")==0){
                        l2i();
                    }
                    else if(currentInstruction.compareTo("l2f")==0){
                        l2f();
                    }
                    else if(currentInstruction.compareTo("l2d")==0){
                        l2d();
                    }
                    else if(currentInstruction.compareTo("f2i")==0){
                        f2i();
                    }
                    else if(currentInstruction.compareTo("f2l")==0){
                        f2l();
                    }
                    else if(currentInstruction.compareTo("f2d")==0){
                        f2d();
                    }
                    else if(currentInstruction.compareTo("d2i")==0){
                        d2i();
                    }
                    else if(currentInstruction.compareTo("d2l")==0){
                        d2l();
                    }
                    else if(currentInstruction.compareTo("d2f")==0){
                        d2f();
                    }
                    else if(currentInstruction.compareTo("i2b")==0){
                        i2b();
                    }
                    else if(currentInstruction.compareTo("i2c")==0){
                        i2c();
                    }
                    else if(currentInstruction.compareTo("i2s")==0){
                        i2s();
                    }
                    else if(currentInstruction.compareTo("lcmp")==0){
                        lcmp();
                    }
                    else if(currentInstruction.compareTo("fcmpl")==0){
                        fcmpl();
                    }
                    else if(currentInstruction.compareTo("fcmpg")==0){
                        fcmpg();
                    }
                    else if(currentInstruction.compareTo("dcmpl")==0){
                        dcmpl();
                    }
                    else if(currentInstruction.compareTo("dcmpg")==0){
                        dcmpg();
                    }
                    else if(currentInstruction.compareTo("ifeq")==0){
                        ifeq();
                    }
                    else if(currentInstruction.compareTo("ifne")==0){
                        ifne();
                    }
                    else if(currentInstruction.compareTo("iflt")==0){
                        iflt();
                    }
                    else if(currentInstruction.compareTo("ifge")==0){
                        ifge();
                    }
                    else if(currentInstruction.compareTo("ifgt")==0){
                        ifgt();
                    }
                    else if(currentInstruction.compareTo("ifle")==0){
                        ifle();
                    }
                    else if(currentInstruction.compareTo("if_icmpeq")==0){
                        if_icmpeq();
                    }
                    else if(currentInstruction.compareTo("if_icmpne")==0){
                        if_icmpne();
                    }
                    else if(currentInstruction.compareTo("if_icmplt")==0){
                        if_icmplt();
                    }
                    // Below needs to be changed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    else if(currentInstruction.compareTo("if_icmpge")==0){
                        int value1 = (int)stack.pop();
                        int value2 = (int)stack.pop();
                        if(value2>=value1){
                            int index=0;
                            currentLine = Integer.parseInt((String)parameters[0]);
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
                    else if(currentInstruction.compareTo("if_icmpgt")==0){
                        if_icmpgt();
                    }
                    else if(currentInstruction.compareTo("if_icmple")==0){
                        if_icmple();
                    }
                    else if(currentInstruction.compareTo("if_acmpeq")==0){
                        if_acmpeq();
                    }
                    else if(currentInstruction.compareTo("if_acmpne")==0){
                        if_acmpne();
                    }
                    // Below needs to be changed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    else if(currentInstruction.compareTo("goto")==0){
                        goto1(Integer.parseInt((String) parameters[0]));
                    }
                    else if(currentInstruction.compareTo("jsr")==0){
                        jsr();
                    }
                    else if(currentInstruction.compareTo("ret")==0){
                        ret();
                    }
                    else if(currentInstruction.compareTo("tableswitch")==0){
                        tableswitch();
                    }
                    else if(currentInstruction.compareTo("lookupswitch")==0){
                        lookupswitch();
                    }
                    else if(currentInstruction.compareTo("ireturn")==0){
                        ireturn();
                    }
                    else if(currentInstruction.compareTo("lreturn")==0){
                        lreturn();
                    }
                    else if(currentInstruction.compareTo("freturn")==0){
                        freturn();
                    }
                    else if(currentInstruction.compareTo("dreturn")==0){
                        dreturn();
                    }
                    else if(currentInstruction.compareTo("areturn")==0){
                        areturn();
                    }
                    else if(currentInstruction.compareTo("return")==0){
                        finished = true;
                    }
                    else if(currentInstruction.compareTo("getstatic")==0){
                        getstatic();
                    }
                    else if(currentInstruction.compareTo("putstatic")==0){
                        putstatic();
                    }
                    else if(currentInstruction.compareTo("getfield")==0){
                        getfield();
                    }
                    else if(currentInstruction.compareTo("putfield")==0){
                        putfield();
                    }
                    else if(currentInstruction.compareTo("invokevirtual")==0){
                        invokevirtual();
                    }
                    else if(currentInstruction.compareTo("invokespecial")==0){
                        invokespecial();
                    }
                    else if(currentInstruction.compareTo("invokestatic")==0){
                        invokestatic();
                    }
                    else if(currentInstruction.compareTo("invokeinterface")==0){
                        invokeinterface();
                    }
                    else if(currentInstruction.compareTo("invokedynamic")==0){
                        invokedynamic();
                    }
                    else if(currentInstruction.compareTo("new")==0){
                        new1();
                    }
                    else if(currentInstruction.compareTo("newarray")==0){
                        newarray();
                    }
                    else if(currentInstruction.compareTo("anewarray")==0){
                        anewarray();
                    }
                    else if(currentInstruction.compareTo("arraylength")==0){
                        arraylength();
                    }
                    else if(currentInstruction.compareTo("athrow")==0){
                        athrow();
                    }
                    else if(currentInstruction.compareTo("checkcast")==0){
                        checkcast();
                    }
                    else if(currentInstruction.compareTo("instanceof")==0){
                        instanceof1();
                    }
                    else if(currentInstruction.compareTo("monitorenter")==0){
                        monitorenter();
                    }
                    else if(currentInstruction.compareTo("monitorexit")==0){
                        monitorexit();
                    }
                    else if(currentInstruction.compareTo("wide")==0){
                        wide();
                    }
                    else if(currentInstruction.compareTo("multianewarray")==0){
                        multianewarray();
                    }
                    else if(currentInstruction.compareTo("ifnull")==0){
                        ifnull();
                    }
                    else if(currentInstruction.compareTo("ifnonnull")==0){
                        ifnonnull();
                    }
                    else if(currentInstruction.compareTo("goto_w")==0){
                        goto_w();
                    }
                    else if(currentInstruction.compareTo("jsr_w")==0){
                        jsr_w();
                    }
                    else if(currentInstruction.compareTo("breakpoint")==0){
                        breakpoint();
                    }
                    else if(currentInstruction.compareTo("impdep1")==0){
                        impdep1();
                    }
                    else if(currentInstruction.compareTo("impdep2")==0){
                        impdep2();
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
    public void bipush(int b){
        stack.push(b);
    }
    // 11
    public void sipush(int s){
        stack.push(s);
    }
    // 12 -- NEEDS WORK!!!!!!!!!!!!!!!!
    public void ldc(Object value){
        stack.push(value);
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
    public void dup_x1(){
        
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
    //9a
    public void ifne(){
        
    }
    //9b 
    public void iflt(){
        
    }
    // 9c
    public void ifge(){
        
    }
    // 9d
    public void ifgt(){
        
    }
    // 9e
    public void ifle(){
        
    }
    // 9f 
    public void if_icmpeq(){
        
    }
    // a0
    public void if_icmpne(){
        
    }
    // a1
    public void if_icmplt(){
        
    }
    // a2
    public void if_icmpge(){
        int a = (int)stack.pop();
        int b = (int)stack.pop();
        if(a>=b){
            // Skip to this position
        }
    }
    // a3
    public void if_icmpgt(){
        
    }
    // a4 
    public void if_icmple(){
        
    }
    // a5
    public void if_acmpeq(){
        
    }
    // a6 
    public void if_acmpne(){
        
    }
    // a7
    public void goto1(int newLine){
        this.currentLineIndex =  newLine-1;
    }
    // a8
    public void jsr(){
        
    }
    // a9
    public void ret(){
        
    }
    // aa
    public void tableswitch(){
        
    }
    //ab
    public void lookupswitch(){
        
    }
    // ac
    public void ireturn(){
        
    }
    // ad 
    public void lreturn(){
        
    }
    // ae
    public void freturn(){
        
    }
    // af
    public void dreturn(){
        
    }
    // b0
    public void areturn(){
        
    }
    // b1
    public void return1(){
        
    }
    // b2
    public void getstatic(){
        
    }
    // b3
    public void putstatic(){
        
    }
    // b4 
    public void getfield(){
        
    }
    // b5
    public void putfield(){
        
    }
    // b6
    public void invokevirtual(){
        
    }
    // b7
    public void invokespecial(){
        
    }
    // b8
    public void invokestatic(){
        
    }
    // b9
    public void invokeinterface(){
        
    }
    // ba
    public void invokedynamic(){
        
    }
    // bb
    public void new1(){
        
    }
    // bc
    public void newarray(){
        
    }
    // bd
    public void anewarray(){
        
    }
    // be
    public void arraylength(){
        
    }
    // bf
    public void athrow(){
        
    }
    // c0
    public void checkcast(){
        
    }
    // c1
    public void instanceof1(){
        
    }
    // c2
    public void monitorenter(){
        
    }
    // c3
    public void monitorexit(){
        
    }
    // c4
    public void wide(){
        
    }
    // c5
    public void multianewarray(){
        
    }
    // c6
    public void ifnull(){
        
    }
    // c7
    public void ifnonnull(){
        
    }
    // c8
    public void goto_w(){
        
    }
    // c9
    public void jsr_w(){
        
    }
    // ca
    public void breakpoint(){
        
    }
    // fe
    public void impdep1(){
        
    }
    // ff
    public void impdep2(){
        
    }
} 