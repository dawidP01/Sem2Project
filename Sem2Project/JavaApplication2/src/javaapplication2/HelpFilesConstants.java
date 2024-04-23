/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javaapplication2;

/**
 *
 * @author C00273530
 */
public interface HelpFilesConstants {
    String nop = """
Opcode in hex: 00
Opcode in binary: 0000 0000
Paramters: None
Effect on Stack: None
Description: Performs no operation
                 """;
    String aconst_null = """
Opcode in hex: 01
Opcode in binary: 0000 0001
Paramters: None
Effect on Stack: -> null
Description: push a null reference onto the stack
                         """;
    String iconst_m1 = """
Opcode in hex: 02
Opcode in binary: 0000 0010
Paramters: None
Effect on Stack: -> -1
Description: load the int value −1 onto the stack
                         """;
    String iconst_0 = """
Opcode in hex: 03
Opcode in binary: 0000 0011
Paramters: None
Effect on Stack: -> 0
Description: load the int value 0 onto the stack
                         """;
    String iconst_1 = """
Opcode in hex: 04
Opcode in binary: 0000 0100
Paramters: None
Effect on Stack: -> 1
Description: load the int value 1 onto the stack
                         """;
    String iconst_2 = """
Opcode in hex: 05
Opcode in binary: 0000 0101
Paramters: None
Effect on Stack: -> 2
Description: load the int value 2 onto the stack
                         """;
    String iconst_3 = """
Opcode in hex: 06
Opcode in binary: 0000 0110
Paramters: None
Effect on Stack: -> 3
Description: load the int value 3 onto the stack
                         """;
    String iconst_4 = """
Opcode in hex: 07
Opcode in binary: 0000 0111
Paramters: None
Effect on Stack: -> 4
Description: load the int value 4 onto the stack
                         """;
    String iconst_5 = """
Opcode in hex: 08
Opcode in binary: 0000 1000
Paramters: None
Effect on Stack: -> 5
Description: load the int value 5 onto the stack
                         """;
    String lconst_0 = """
Opcode in hex: 09
Opcode in binary: 0000 1001
Paramters: None
Effect on Stack: -> 0L
Description: push 0L (the number zero with type long) onto the stack
                         """;
    String lconst_1 = """
Opcode in hex: 0a
Opcode in binary: 0000 1010
Paramters: None
Effect on Stack: -> 1L
Description: push 1L (the number one with type long) onto the stack
                         """;
    String fconst_0 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> 0.0f
Description: push 0.0f on the stack
                         """;
    String fconst_1 = """
Opcode in hex: 0c
Opcode in binary: 0000 1100
Paramters: None
Effect on Stack: -> 1.0f
Description: push 1.0f on the stack
                         """;
    String fconst_2 = """
Opcode in hex: 0d
Opcode in binary: 0000 1101
Paramters: None
Effect on Stack: -> 2.0f
Description: push 2.0f on the stack
                         """;
    String dconst_0 = """
Opcode in hex: 0e
Opcode in binary: 0000 1110
Paramters: None
Effect on Stack: -> 0.0
Description: push the constant 0.0 (a double) onto the stack
                         """;
    String dconst_1 = """
Opcode in hex: 0e
Opcode in binary: 0000 1110
Paramters: None
Effect on Stack: -> 1.0
Description: push the constant 0.0 (a double) onto the stack
                         """;
    String bipush = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String sipush = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String ldc = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String ldc_w = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String ldc2_w = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String iload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String lload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String fload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String dload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String aload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> objectref
Description: push 1L (the number one with type long) onto the stack
                         """;
    String iload_0 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String iload_1 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String iload_2 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String iload_3 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String lload_0 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String lload_1 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String lload_2 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String lload_3 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String fload_0 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String fload_1 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String fload_2 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String fload_3 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String dload_0 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String dload_1 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String dload_2 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String dload_3 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String aload_0 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> objectref
Description: push 1L (the number one with type long) onto the stack
                         """;
    String aload_1 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> objectref
Description: push 1L (the number one with type long) onto the stack
                         """;
    String aload_2 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> objectref
Description: push 1L (the number one with type long) onto the stack
                         """;
    String aload_3 = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> objectref
Description: push 1L (the number one with type long) onto the stack
                         """;
    String iaload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: arrayref, index -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String laload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: arrayref, index -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String faload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: arrayref, index -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String daload = """
Opcode in hex: 0b
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: arrayref, index -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String aaload = """
Opcode in hex: 32
Opcode in binary: 0011 0010
Paramters: None
Effect on Stack: arrayref, index -> value
Description: push 1L (the number one with type long) onto the stack
                         """;
    String baload = """
Opcode in hex: 33
Opcode in binary: 0011 0011
Paramters: None
Effect on Stack: arrayref, index -> value
Description: load a byte or Boolean value from an array
                         """;
    String caload = """
Opcode in hex: 34
Opcode in binary: 0011 0100
Paramters: None
Effect on Stack: arrayref, index -> value
Description: load a char from an array
                         """;
    String saload = """
Opcode in hex: 35
Opcode in binary: 0011 0101
Paramters: None
Effect on Stack: arrayref, index -> value
Description: load short from array
                         """;
    String istore = """
Opcode in hex: 36
Opcode in binary: 0011 0110
Paramters: index
Effect on Stack: value ->
Description: store int value into variable #index
                         """;
    String lstore = """
Opcode in hex: 37
Opcode in binary: 0011 0111
Paramters: index
Effect on Stack: value ->
Description: store a long value in a local variable #index
                         """;
    String fstore = """
Opcode in hex: 38
Opcode in binary: 0011 1000
Paramters: index
Effect on Stack: value ->
Description: variable #index
                         """;
    String dstore = """
Opcode in hex: 39
Opcode in binary: 0011 1001
Paramters: index
Effect on Stack: value ->
Description: store a double value into a local variable #index
                         """;
    String astore = """
Opcode in hex: 3a
Opcode in binary: 0011 1010
Paramters: index
Effect on Stack: objectref ->
Description: store a reference into a local variable #index
                         """;
    String istore_0 = """
Opcode in hex: 3b
Opcode in binary: 0011 1011
Paramters: None
Effect on Stack: value ->
Description: store int value into variable 0
                         """;
    String istore_1 = """
Opcode in hex: 3c
Opcode in binary: 0011 1100
Paramters: None
Effect on Stack: value ->
Description: store int value into variable 1
                         """;
    String istore_2 = """
Opcode in hex: 3d
Opcode in binary: 0011 1101
Paramters: None
Effect on Stack: value ->
Description: store int value into variable 2
                         """;
    String istore_3 = """
Opcode in hex: 3e
Opcode in binary: 0011 1110
Paramters: None
Effect on Stack: value ->
Description: store int value into variable 3
                         """;
    String lstore_0 = """
Opcode in hex: 3f
Opcode in binary: 0011 1111
Paramters: None
Effect on Stack: value ->
Description: store a long value in a local variable 0
                         """;
    String lstore_1 = """
Opcode in hex: 40
Opcode in binary: 0100 0000
Paramters: None
Effect on Stack: value ->
Description: store a long value in a local variable 1
                         """;
    String lstore_2 = """
Opcode in hex: 41
Opcode in binary: 0100 0001
Paramters: None
Effect on Stack: value ->
Description: store a long value in a local variable 2
                         """;
    String lstore_3 = """
Opcode in hex: 42
Opcode in binary: 0100 0010
Paramters: None
Effect on Stack: value ->
Description: store a long value in a local variable 3
                         """;
    String fstore_0 = """
Opcode in hex: 43
Opcode in binary: 0100 0011
Paramters: None
Effect on Stack: value ->
Description: store a float value into local variable 0
                         """;
    String fstore_1 = """
Opcode in hex: 44
Opcode in binary: 0100 0100
Paramters: None
Effect on Stack: value ->
Description: store a float value into local variable 1
                         """;
    String fstore_2 = """
Opcode in hex: 45
Opcode in binary: 0100 0101
Paramters: None
Effect on Stack: value ->
Description: store a float value into local variable 2
                         """;
    String fstore_3 = """
Opcode in hex: 46
Opcode in binary: 0100 0110
Paramters: None
Effect on Stack: value ->
Description: store a float value into local variable 3
                         """;
    String dstore_0 = """
Opcode in hex: 47
Opcode in binary: 0100 0111
Paramters: None
Effect on Stack: value ->
Description: store a double into local variable 0
                         """;
    String dstore_1 = """
Opcode in hex: 48
Opcode in binary: 0100 1000
Paramters: None
Effect on Stack: value ->
Description: store a double into local variable 1
                         """;
    String dstore_2 = """
Opcode in hex: 49
Opcode in binary: 0100 1001
Paramters: None
Effect on Stack: value ->
Description: store a double into local variable 2
                         """;
    String dstore_3 = """
Opcode in hex: 4a
Opcode in binary: 0100 1010
Paramters: None
Effect on Stack: value ->
Description: store a double into local variable 3
                         """;
    String astore_0 = """
Opcode in hex: 4b
Opcode in binary: 0100 1011
Paramters: None
Effect on Stack: objectref ->
Description: store a reference into local variable 0
                         """;
    String astore_1 = """
Opcode in hex: 4c
Opcode in binary: 0100 1100
Paramters: None
Effect on Stack: objectref ->
Description: store a reference into local variable 1
                         """;
    String astore_2 = """
Opcode in hex: 4d
Opcode in binary: 0100 1101
Paramters: None
Effect on Stack: objectref ->
Description: store a reference into local variable 2
                         """;
    String astore_3 = """
Opcode in hex: 4e
Opcode in binary: 0100 1110
Paramters: None
Effect on Stack: objectref ->
Description: store a reference into local variable 3
                         """;
    String iastore = """
Opcode in hex: 4f
Opcode in binary: 0100 1111
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store an int into an array
                         """;
    String lastore = """
Opcode in hex: 50
Opcode in binary: 0101 0000
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store a long to an array
                         """;
    String fastore = """
Opcode in hex: 51
Opcode in binary: 0101 0001
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store a float in an array
                         """;
    String dastore = """
Opcode in hex: 52
Opcode in binary: 0101 0010
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store a double into an array
                         """;
    String aastore = """
Opcode in hex: 53
Opcode in binary: 0101 0011
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store a reference in an array
                         """;
    String bastore = """
Opcode in hex: 54
Opcode in binary: 0101 0100
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store a byte or Boolean value into an array
                         """;
    String castore = """
Opcode in hex: 55
Opcode in binary: 0101 0101
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store a char into an array
                         """;
    String sastore = """
Opcode in hex: 56
Opcode in binary: 0101 0110
Paramters: None
Effect on Stack: arrayref, index, value ->
Description: store short to array
                         """;
    String pop = """
Opcode in hex: 57
Opcode in binary: 0101 0111
Paramters: None
Effect on Stack: value ->
Description: discard the top value on the stack
                         """;
    String pop2 = """
Opcode in hex: 58
Opcode in binary: 0101 1000
Paramters: None
Effect on Stack: {value2, value1} ->
Description: discard the top two values on the stack 
(or one value, if it is a double or long)
                         """;
    String dup = """
Opcode in hex: 59
Opcode in binary: 0101 1001
Paramters: None
Effect on Stack: value -> value, value
Description: duplicate the value on top of the stack
                         """;
    String dup_x1 = """
Opcode in hex: 5a
Opcode in binary: 0101 1010
Paramters: None
Effect on Stack: value2, value1 -> value1, value2, value1
Description: insert a copy of the top value into the stack two values from 
the top. value1 and value2 must not be of the type double or long.
                         """;
    String dup_x2 = """
Opcode in hex: 5b
Opcode in binary: 0101 1011
Paramters: None
Effect on Stack: value3, value2, value1 -> value1, value3, value2, value1
Description: insert a copy of the top value into the stack two 
(if value2 is double or long it takes up the entry of value3, too) or three 
values (if value2 is neither double nor long) from the top
                         """;
    String dup2 = """
Opcode in hex: 5c
Opcode in binary: 0101 1100
Paramters: None
Effect on Stack: {value2, value1} -> {value2, value1}, {value2, value1}
Description: duplicate top two stack words (two values, if value1 is not double 
nor long; a single value, if value1 is double or long)
                         """;
    String dup2_x1 = """
Opcode in hex: 5d
Opcode in binary: 0101 1101
Paramters: None
Effect on Stack: value3, {value2, value1} -> 
{value2, value1}, value3, {value2, value1}
Description: duplicate two words and insert beneath third word
                         """;
    String dup2_x2 = """
Opcode in hex: 5e
Opcode in binary: 0101 1110
Paramters: None
Effect on Stack: {value4, value3}, {value2, value1} -> {value2, value1}, 
{value4, value3}, {value2, value1}
Description: duplicate two words and insert beneath fourth word
                         """;
    String swap = """
Opcode in hex: 5f
Opcode in binary: 0101 1111
Paramters: None
Effect on Stack: value2, value1 -> value1, value2
Description: swaps two top words on the stack (note that value1 and value2 
must not be double or long)
                         """;
    String iadd = """
Opcode in hex: 60
Opcode in binary: 0110 0000
Paramters: None
Effect on Stack: value1, value2 -> result
Description: add two ints
                         """;
    String ladd = """
Opcode in hex: 61
Opcode in binary: 0110 0001
Paramters: None
Effect on Stack: value1, value2 -> result
Description: add two longs
                         """;
    String fadd = """
Opcode in hex: 62
Opcode in binary: 0110 0010
Paramters: None
Effect on Stack: value1, value2 -> result
Description: add two floats
                         """;
    String dadd = """
Opcode in hex: 63
Opcode in binary: 0110 0011
Paramters: None
Effect on Stack: value1, value2 -> result
Description: add two doubles
                         """;
    String isub = """
Opcode in hex: 64
Opcode in binary: 0110 0100
Paramters: None
Effect on Stack: -> 0.0f
Description: push 1L (the number one with type long) onto the stack
                         """;
    String lsub = """
Opcode in hex: 65
Opcode in binary: 0110 0101	
Paramters: None
Effect on Stack: value1, value2 -> result
Description: subtract two longs
                         """;
    String fsub = """
Opcode in hex: 66
Opcode in binary: 0110 0110
Paramters: None
Effect on Stack: value1, value2 -> result
Description: subtract two floats
                         """;
    String dsub = """
Opcode in hex: 67
Opcode in binary: 0110 0111
Paramters: None
Effect on Stack: -> 0.0f
Description: push 1L (the number one with type long) onto the stack
                         """;
    String imul = """
Opcode in hex: 68
Opcode in binary: 0110 1000
Paramters: None
Effect on Stack: value1, value2 -> result
Description: multiply two integers
                         """;
    String lmul = """
Opcode in hex: 69
Opcode in binary: 0110 1001
Paramters: None
Effect on Stack: value1, value2 -> result
Description: multiply two longs
                         """;
    String fmul = """
Opcode in hex: 6a
Opcode in binary: 0110 1010
Paramters: None
Effect on Stack: value1, value2 -> result
Description: multiply two floats
                 """;
    String dmul = """
Opcode in hex: 6b
Opcode in binary: 0110 1011
Paramters: None
Effect on Stack: value1, value2 -> result
Description: multiply two doubles
                         """;
    String idiv = """
Opcode in hex: 6c
Opcode in binary: 0110 1100
Paramters: None
Effect on Stack: value1, value2 -> result
Description: divide two integers
                         """;
    String ldiv = """
Opcode in hex: 6d
Opcode in binary: 0110 1101
Paramters: None
Effect on Stack: value1, value2 -> result
Description: divide two longs
                         """;
    String fdiv = """
Opcode in hex: 6e
Opcode in binary: 0110 1110
Paramters: None
Effect on Stack: value1, value2 -> result
Description: divide two floats
                         """;
    String ddiv = """
Opcode in hex: 6f
Opcode in binary: 0110 1111
Paramters: None
Effect on Stack: value1, value2 -> result
Description: divide two doubles
                         """;
    String irem = """
Opcode in hex: 70
Opcode in binary: 0111 0000
Paramters: None
Effect on Stack: value1, value2 -> result
Description: logical int remainder
                         """;
    String lrem = """
Opcode in hex: 71
Opcode in binary: 0111 0001
Paramters: None
Effect on Stack: value1, value2 -> result
Description: remainder of division of two longs
                         """;
    String frem = """
Opcode in hex: 72
Opcode in binary: 0111 0010
Paramters: None
Effect on Stack: value1, value2 -> result
Description: get the remainder from a division between two floats
                         """;
    String drem = """
Opcode in hex: 73
Opcode in binary: 0111 0011
Paramters: None
Effect on Stack: value1, value2 -> result
Description: get the remainder from a division between two doubles
                         """;
    String ineg = """
Opcode in hex: 74
Opcode in binary: 0111 0100
Paramters: None
Effect on Stack: value -> result
Description: negate int
                         """;
    String lneg = """
Opcode in hex: 75
Opcode in binary: 0111 0101
Paramters: None
Effect on Stack: value -> result
Description: negate a long
                         """;
    String fneg = """
Opcode in hex: 76
Opcode in binary: 0111 0110
Paramters: None
Effect on Stack: value -> result
Description: negate a float
                         """;
    String dneg = """
Opcode in hex: 77
Opcode in binary: 0111 0111
Paramters: None
Effect on Stack: value -> result
Description: negate a double
                         """;
    String ishl = """
Opcode in hex: 78
Opcode in binary: 0111 1000	
Paramters: None
Effect on Stack: value1, value2 -> result
Description: int shift left
                         """;
    String lshl = """
Opcode in hex: 79
Opcode in binary: 0111 1001
Paramters: None
Effect on Stack: value1, value2 -> result
Description: bitwise shift left of a long value1 by int value2 positions
                         """;
    String ishr = """
Opcode in hex: 7a
Opcode in binary: 0111 1010
Paramters: None
Effect on Stack: value1, value2 -> result
Description: int arithmetic shift right
                         """;
    String lshr = """
Opcode in hex: 7b
Opcode in binary: 0111 1011
Paramters: None
Effect on Stack: value1, value2 -> result
Description: bitwise shift right of a long value1 by int value2 positions
                         """;
    String iushr = """
Opcode in hex: 7c
Opcode in binary: 0111 1100
Paramters: None
Effect on Stack: value1, value2 -> result
Description: int logical shift right
                         """;
    String lushr = """
Opcode in hex: 7d
Opcode in binary: 0111 1101
Paramters: None
Effect on Stack: value1, value2 -> result
Description: bitwise shift right of a long value1 
by int value2 positions, unsigned
                         """;
    String iand = """
Opcode in hex: 7e
Opcode in binary: 0111 1110
Paramters: None
Effect on Stack: value1, value2 -> result
Description: perform a bitwise AND on two integers
                         """;
    String land = """
Opcode in hex: 7f
Opcode in binary: 0111 1111
Paramters: None
Effect on Stack: value1, value2 -> result
Description: bitwise AND of two longs
                         """;
    String ior = """
Opcode in hex: 80
Opcode in binary: 1000 0000
Paramters: None
Effect on Stack: value1, value2 -> result
Description: bitwise int OR
                         """;
    String lor = """
Opcode in hex: 81
Opcode in binary: 1000 0001
Paramters: None
Effect on Stack: value1, value2 -> result
Description: bitwise OR of two longs
                         """;
    String ixor = """
Opcode in hex: 82
Opcode in binary: 1000 0010
Paramters: None
Effect on Stack: value1, value2 -> result
Description: int xor
                         """;
    String lxor = """
Opcode in hex: 83
Opcode in binary: 1000 0011
Paramters: None
Effect on Stack: value1, value2 -> result
Description: bitwise XOR of two longs
                         """;
    String iinc = """
Opcode in hex: 84
Opcode in binary: 1000 0100
Paramters: index, const
Effect on Stack: None
Description: increment local variable #index by signed byte const
                         """;
    String i2l = """
Opcode in hex: 85
Opcode in binary: 1000 0101
Paramters: None
Effect on Stack: value -> result
Description: convert an int into a long
                         """;
    String i2f = """
Opcode in hex: 86
Opcode in binary: 1000 0110
Paramters: None
Effect on Stack: value -> result
Description: push 1L (the number one with type long) onto the stack
                         """;
    String i2d = """
Opcode in hex: 87
Opcode in binary: 1000 0111
Paramters: None
Effect on Stack: value -> result
Description: convert an int into a double
                         """;
    String l2i = """
Opcode in hex: 88
Opcode in binary: 1000 1000
Paramters: None
Effect on Stack: value -> result
Description: convert a long to a int
                         """;
    String l2f = """
Opcode in hex: 89
Opcode in binary: 1000 1001
Paramters: None
Effect on Stack: value -> result	
Description: convert a long to a float
                         """;
    String l2d = """
Opcode in hex: 8a
Opcode in binary: value -> result
Paramters: None
Effect on Stack: -> 0.0f
Description: convert a long to a double
                         """;
    String f2i = """
Opcode in hex: 8b
Opcode in binary: 1000 1011
Paramters: None
Effect on Stack: value -> result
Description: convert a float to an int
                         """;
    String f2l = """
Opcode in hex: 8c
Opcode in binary: 1000 1100
Paramters: None
Effect on Stack: value -> result
Description: convert a float to a long
                         """;
    String f2d = """
Opcode in hex: 8d
Opcode in binary: 1000 1101
Paramters: None
Effect on Stack: value -> result
Description: convert a float to a double
                         """;
    String d2i = """
Opcode in hex: 8e
Opcode in binary: 1000 1110
Paramters: None
Effect on Stack: value -> result
Description: convert a double to an int
                         """;
    String d2l = """
Opcode in hex: 8f
Opcode in binary: 1000 1111
Paramters: None
Effect on Stack: value -> result
Description: convert a double to a long
                         """;
    String d2f = """
Opcode in hex: 90
Opcode in binary: 1001 0000
Paramters: None
Effect on Stack: value -> result
Description: convert a double to a float
                         """;
    String i2b = """
Opcode in hex: 91
Opcode in binary: 1001 0001
Paramters: None
Effect on Stack: value -> result
Description: convert an int into a byte
                         """;
    String i2c = """
Opcode in hex: 92
Opcode in binary: 1001 0010
Paramters: None
Effect on Stack: value -> result
Description: convert an int into a character
                         """;
    String i2s = """
Opcode in hex: 93
Opcode in binary: 1001 0011
Paramters: None
Effect on Stack: value -> result
Description: convert an int into a short
                         """;
    String lcmp = """
Opcode in hex: 94
Opcode in binary: 1001 0100
Paramters: None
Effect on Stack: value1, value2 -> result
Description: push 0 if the two longs are the same, 1 if value1 is greater 
than value2, -1 otherwise
                         """;
    String fcmpl = """
Opcode in hex: 95
Opcode in binary: 1001 0101
Paramters: None
Effect on Stack: value1, value2 -> result
Description: compare two floats, -1 on NaN
                         """;
    String fcmpg = """
Opcode in hex: 96
Opcode in binary: 1001 0110
Paramters: None
Effect on Stack: value1, value2 -> result
Description: compare two floats, 1 on NaN
                         """;
    String dcmpl = """
Opcode in hex: 97
Opcode in binary: 1001 0111
Paramters: None
Effect on Stack: value1, value2 -> result
Description: compare two doubles, -1 on NaN
                         """;
    String dcmpg = """
Opcode in hex: 98
Opcode in binary: 1001 1000
Paramters: None
Effect on Stack: value1, value2 -> result
Description: compare two doubles, 1 on NaN
                         """;
    String ifeq = """
Opcode in hex: 99
Opcode in binary: 1001 1001
Paramters: branchbyte1, branchbyte2
Effect on Stack: value ->
Description: if value is 0, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String ifne = """
Opcode in hex: 9a
Opcode in binary: 1001 1010
Paramters: branchbyte1, branchbyte2
Effect on Stack: value ->
Description: if value is not 0, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String iflt = """
Opcode in hex: 9b
Opcode in binary: 1001 1011
Paramters: branchbyte1, branchbyte2
Effect on Stack: value -> 
Description: if value is less than 0, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String ifge = """
Opcode in hex: 9c
Opcode in binary: 1001 1100
Paramters: branchbyte1, branchbyte2
Effect on Stack: value ->
Description: if value is greater than or equal to 0, branch to instruction at 
branchoffset (signed short constructed from unsigned 
bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String ifgt = """
Opcode in hex: 9d
Opcode in binary: 1001 1101
Paramters: branchbyte1, branchbyte2
Effect on Stack: value ->
Description: if value is greater than 0, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String ifle = """
Opcode in hex: 9e
Opcode in binary: 1001 1110
Paramters: branchbyte1, branchbyte2
Effect on Stack: value ->
Description: if value is less than or equal to 0, branch to instruction at 
branchoffset (signed short constructed from unsigned 
bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String if_icmpeq = """
Opcode in hex: 9f
Opcode in binary: 1001 1111
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description: if ints are equal, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String if_icmpne = """
Opcode in hex: a0
Opcode in binary: 1010 0000
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description:if ints are not equal, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String if_icmplt = """
Opcode in hex: a1
Opcode in binary: 1010 0001
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description: if value1 is less than value2, branch to instruction at 
branchoffset (signed short constructed from 
unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String if_icmpge = """
Opcode in hex: a2
Opcode in binary: 1010 0010
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description:if value1 is greater than or equal to value2, branch to 
instruction at branchoffset (signed short constructed from unsigned bytes 
branchbyte1 << 8 | branchbyte2)
                         """;
    String if_icmpgt = """
Opcode in hex: a3
Opcode in binary: 1010 0011
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description: if value1 is greater than value2, branch to instruction at 
branchoffset (signed short constructed from unsigned 
bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String if_icmple = """
Opcode in hex: a4
Opcode in binary: 1010 0100
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description: if value1 is less than or equal to value2, branch to instruction 
at branchoffset (signed short constructed from unsigned 
bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String if_acmpeq = """
Opcode in hex: a5
Opcode in binary: 1010 0101
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description: if references are equal, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String if_acmpne = """
Opcode in hex: a6
Opcode in binary: 1010 0110
Paramters: branchbyte1, branchbyte2
Effect on Stack: value1, value2 ->
Description: if references are not equal, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String gotoText = """
Opcode in hex: a7
Opcode in binary: 1010 0111
Paramters: branchbyte1, branchbyte2
Effect on Stack: None
Description: goes to another instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String jsr = """
Opcode in hex: a8
Opcode in binary: 1010 1000
Paramters: branchbyte1, branchbyte2
Effect on Stack: -> address
Description: jump to subroutine at branchoffset (signed short constructed from 
unsigned bytes branchbyte1 << 8 | branchbyte2) and place the return 
address on the stack
                         """;
    String ret = """
Opcode in hex: a9
Opcode in binary: 1010 1001
Paramters: index
Effect on Stack: None
Description: continue execution from address taken from a local variable #index 
(the asymmetry with jsr is intentional)
                         """;
    String tableswitch = """
Opcode in hex: aa
Opcode in binary: 0000 1011
Paramters: [0–3 bytes padding], defaultbyte1, defaultbyte2, defaultbyte3, 
defaultbyte4, lowbyte1, lowbyte2, lowbyte3, lowbyte4, highbyte1, highbyte2, 
highbyte3, highbyte4, jump offsets...
Effect on Stack: index ->
Description: continue execution from an address in the table at offset index
                         """;
    String lookupswitch = """
Opcode in hex: ab
Opcode in binary: 1010 1011
Paramters: <0–3 bytes padding>, defaultbyte1, defaultbyte2, defaultbyte3, 
defaultbyte4, npairs1, npairs2, npairs3, npairs4, match-offset pairs...
Effect on Stack: key ->
Description: a target address is looked up from a table using a key and 
execution continues from the instruction at that address
                         """;
    String ireturn = """
Opcode in hex: ac
Opcode in binary: 1010 1100
Paramters: None
Effect on Stack: value -> [empty]
Description: return an integer from a method
                         """;
    String lreturn = """
Opcode in hex: ad
Opcode in binary: 1010 1101
Paramters: None
Effect on Stack: value -> [empty]
Description: return a long value
                         """;
    String freturn = """
Opcode in hex: ae
Opcode in binary: 1010 1110
Paramters: None
Effect on Stack: value -> [empty]
Description: return a float
                         """;
    String dreturn = """
Opcode in hex: af
Opcode in binary: 1010 1111
Paramters: None
Effect on Stack: value -> [empty]
Description: return a double from a method
                         """;
    String areturn = """
Opcode in hex: b0
Opcode in binary: 1011 0000
Paramters: None
Effect on Stack: objectref -> [empty]
Description: return a reference from a method
                         """;
    String returnText = """
Opcode in hex: b1
Opcode in binary: 1011 0001
Paramters: None
Effect on Stack: -> [empty]
Description: return void from method
                         """;
    String getstatic = """
Opcode in hex: b2
Opcode in binary: 1011 0010
Paramters: indexbyte1, indexbyte2
Effect on Stack: -> value
Description: get a static field value of a class, where the field is identified 
by field reference in the constant pool index (indexbyte1 << 8 | indexbyte2)
                         """;
    String putstatic = """
Opcode in hex: b3
Opcode in binary: 1011 0011
Paramters: indexbyte1, indexbyte2
Effect on Stack: value ->
Description: set static field to value in a class, where the field is 
identified by a field reference index in constant pool 
(indexbyte1 << 8 | indexbyte2)
                         """;
    String getfield = """
Opcode in hex: b4
Opcode in binary: 1011 0100
Paramters: indexbyte1, indexbyte2
Effect on Stack: objectref -> value
Description: get a field value of an object objectref, where the field is 
identified by field reference in the constant pool index 
(indexbyte1 << 8 | indexbyte2)
                         """;
    String putfield = """
Opcode in hex: b5
Opcode in binary: 1011 0101
Paramters: indexbyte1, indexbyte2
Effect on Stack: objectref, value ->
Description: set field to value in an object objectref, where the field is 
identified by a field reference index in constant pool
(indexbyte1 << 8 | indexbyte2)
                         """;
    String invokevirtual = """
Opcode in hex: b6
Opcode in binary: 1011 0110
Paramters: indexbyte1, indexbyte2
Effect on Stack: objectref, [arg1, arg2, ...] -> result
Description: invoke virtual method on object objectref and puts the result on 
the stack (might be void); the method is identified by method reference index 
in constant pool (indexbyte1 << 8 | indexbyte2)
                         """;
    String invokespecial = """
Opcode in hex: b7
Opcode in binary: 1011 0111
Paramters: indexbyte1, indexbyte2
Effect on Stack: objectref, [arg1, arg2, ...] -> result
Description: invoke instance method on object objectref and puts the result on 
the stack (might be void); the method is identified by method reference index 
in constant pool (indexbyte1 << 8 | indexbyte2)
                         """;
    String invokestatic = """
Opcode in hex: b8
Opcode in binary: 1011 1000
Paramters: indexbyte1, indexbyte2
Effect on Stack: [arg1, arg2, ...] -> result
Description: invoke a static method and puts the result on the stack 
(might be void); the method is identified by method reference index in constant 
pool (indexbyte1 << 8 | indexbyte2)
                         """;
    String invokeinterface = """
Opcode in hex: b9
Opcode in binary: 1011 1001
Paramters: indexbyte1, indexbyte2, count, 0
Effect on Stack: objectref, [arg1, arg2, ...] -> result
Description: invokes an interface method on object objectref and puts the 
result on the stack (might be void); the interface method is identified by 
method reference index in constant pool (indexbyte1 << 8 | indexbyte2)
                         """;
    String invokedynamic = """
Opcode in hex: ba
Opcode in binary: 1011 1010
Paramters: indexbyte1, indexbyte2, 0, 0
Effect on Stack: [arg1, arg2, ...] -> result	
Description: invokes a dynamic method and puts the result on the stack(might be void); the method is identified by method reference index in constant
pool (indexbyte1 << 8 | indexbyte2)
                         """;
    String newText = """
Opcode in hex: bb
Opcode in binary: 1011 1011
Paramters: indexbyte1, indexbyte2
Effect on Stack: -> objectref
Description: create new object of type identified by class reference in constant 
pool index (indexbyte1 << 8 | indexbyte2)
                         """;
    String newarray = """
Opcode in hex: bc
Opcode in binary: 1011 1100
Paramters: atype
Effect on Stack: count -> arrayref
Description: create new array with count elements of primitive type 
identified by atype
                         """;
    String anewarray = """
Opcode in hex: bd
Opcode in binary: 1011 1101
Paramters: indexbyte1, indexbyte2
Effect on Stack: count -> arrayref
Description: create a new array of references of length count and component 
type identified by the class reference index (indexbyte1 << 8 | indexbyte2)
in the constant pool
                         """;
    String arraylength = """
Opcode in hex: be
Opcode in binary: 1011 1110
Paramters: None
Effect on Stack: arrayref -> length
Description: get the length of an array
                         """;
    String athrow = """
Opcode in hex: bf
Opcode in binary: 1011 1111
Paramters: None
Effect on Stack: objectref -> [empty], objectref
Description: throws an error or exception (notice that the rest of the stack is 
cleared, leaving only a reference to the Throwable)
                         """;
    String checkcast = """
Opcode in hex: c0
Opcode in binary: 1100 0000
Paramters: indexbyte1, indexbyte2
Effect on Stack: objectref -> objectref
Description: checks whether an objectref is of a certain type, the class 
reference of which is in the constant pool at index
(indexbyte1 << 8 | indexbyte2)
                         """;
    String instanceofText = """
Opcode in hex: c1
Opcode in binary: 1100 0001
Paramters: indexbyte1, indexbyte2
Effect on Stack: objectref -> result
Description: determines if an object objectref is of a given type, identified
by class reference index in constant pool (indexbyte1 << 8 | indexbyte2)
                         """;
    String monitorenter = """
Opcode in hex: c2
Opcode in binary: 1100 0010
Paramters: None
Effect on Stack: objectref ->
Description: enter monitor for object ("grab the lock" – 
start of synchronized() section)
                         """;
    String monitorexit = """
Opcode in hex: c3
Opcode in binary: 0000 1011
Paramters: None
Effect on Stack: -> 0.0f
Description: push 1L (the number one with type long) onto the stack
                         """;
    String wide = """
Opcode in hex: c4
Opcode in binary: 1100 0100
Paramters: 3/5: opcode, indexbyte1, indexbyte2
or
iinc, indexbyte1, indexbyte2, countbyte1, countbyte2
Effect on Stack: [same as for corresponding instructions]
Description: execute opcode, where opcode is either iload, fload, aload, lload, 
dload, istore, fstore, astore, lstore, dstore, or ret, but assume the index is 
16 bit; or execute iinc, where the index is 16 bits and the constant to 
increment by is a signed 16 bit short
                         """;
    String multianewarray = """
Opcode in hex: c5
Opcode in binary: 1100 0101
Paramters: indexbyte1, indexbyte2, dimensions
Effect on Stack: count1, [count2,...] -> arrayref
Description: create a new array of dimensions dimensions of type identified by 
class reference in constant pool index (indexbyte1 << 8 | indexbyte2); the sizes 
of each dimension is identified by count1, [count2, etc.]
                         """;
    String ifnull = """
Opcode in hex: c6
Opcode in binary: 1100 0110
Paramters: branchbyte1, branchbyte2
Effect on Stack: value ->
Description: if value is null, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String ifnonnull = """
Opcode in hex: c7
Opcode in binary: 1100 0111
Paramters: branchbyte1, branchbyte2
Effect on Stack: Value ->
Description: if value is not null, branch to instruction at branchoffset 
(signed short constructed from unsigned bytes branchbyte1 << 8 | branchbyte2)
                         """;
    String goto_w = """
Opcode in hex: c8
Opcode in binary: 1100 1000
Paramters: branchbyte1, branchbyte2, branchbyte3, branchbyte4
Effect on Stack: None
Description: goes to another instruction at branchoffset (signed int constructed
from unsigned bytes branchbyte1 << 24 | branchbyte2 << 16 | branchbyte3 << 8 |
branchbyte4)
                         """;
    String jsr_w = """
Opcode in hex: c9
Opcode in binary: 1100 1001
Paramters: branchbyte1, branchbyte2, branchbyte3, branchbyte4
Effect on Stack: -> address
Description: jump to subroutine at branchoffset (signed int constructed from 
unsigned bytes branchbyte1 << 24 | branchbyte2 << 16 | branchbyte3 << 8 | 
branchbyte4) and place the return address on the stack
                         """;
    String breakpoint = """
Opcode in hex: ca
Opcode in binary: 1100 1010
Paramters: None
Effect on Stack: None
Description: reserved for breakpoints in Java debuggers;
should not appear in any class file
                         """;
    String impdep1 = """
Opcode in hex: fe
Opcode in binary: 1111 1110
Paramters: None
Effect on Stack: None
Description: reserved for implementation-dependent operations within debuggers; 
should not appear in any class file
                         """;
    String impdep2 = """
Opcode in hex: ff
Opcode in binary: 1111 1111
Paramters: None
Effect on Stack: None
Description: reserved for implementation-dependent operations within debuggers; 
should not appear in any class file
                         """;
}
