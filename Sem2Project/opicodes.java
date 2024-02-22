public class opicodes {
    /* 12 - push a constant #index from a constant pool (String,
     int, float, Class, java.lang.invoke.MethodType, java.lang.invoke.MethodHandle, 
     or a dynamically-computed constant) onto the stack*/
    public void ldc(){

    }
    // 2a - load a reference onto the stack from local variable 0
    public void aload_0(){

    }
    // b1 - return void from method
    public void return1(){

    }
    /* b2 - get a static field value of a class, where the field is 
    identified by field reference in the constant pool index 
    (indexbyte1 << 8 | indexbyte2) */ 
    public void getstatic(){

    }
    /* b6 - invoke virtual method on object objectref and puts the result on 
    the stack (might be void); the method is identified by method reference 
    index in constant pool (indexbyte1 << 8 | indexbyte2) */
    public void invokeVirtual(){

    }
    /* b7 - invoke instance method on object objectref and puts the 
    result on the stack (might be void); the method is identified by 
    method reference index in constant pool (indexbyte1 << 8 | indexbyte2) */
    public void invokespecial(){

    }
}
