/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javaapplication2;

/**
 *
 * @author C00273530
 */
public interface TutorialFilesConstants {
    String AddIntegersText = """
class AddIntegers {

    public static void main(String[] args) {

      int first = 10;
      int second = 20;

      // add two numbers
      int sum = first + second;
    }
}
                         
                             """;
    String BinToDecText = """
class BinToDec {
    
    public static void main(String[] args) {
  
      // binary number
      long num = 110110111;
  
      // call method by passing the binary number
      int decimal = convertBinaryToDecimal(num);
    }
  
    public static int convertBinaryToDecimal(long num) {
      int decimalNumber = 0, i = 0;
      long remainder;
      
      while (num != 0) {
        remainder = num % 10;
        num /= 10;
        decimalNumber += remainder * Math.pow(2, i);
        ++i;
       }
      
      return decimalNumber;
    }
}
                          """;
    String CheckPrimeText = """
public class CheckPrime{
    public static void main(String[] args) {
  
      int num = 29;
      boolean flag = false;
      for (int i = 2; i <= num / 2; ++i) {
        // condition for nonprime number
        if (num % i == 0) {
          flag = true;
          break;
        }
      }
    }
}
                            """;
    String FibonacciText = """
class Fibonacci {
    public static void main(String[] args) {
  
      int n = 10, firstTerm = 0, secondTerm = 1;
  
      for (int i = 1; i <= n; ++i) {
  
        // compute the next term
        int nextTerm = firstTerm + secondTerm;
        firstTerm = secondTerm;
        secondTerm = nextTerm;
      }
    }
}
                           """;
    String ReverseNumberText = """
class ReverseNumber {
    public static void main(String[] args) {
  
      int num = 1234, reversed = 0;
      
      System.out.println("Original Number: " + num);
  
      // run loop until num becomes 0
      while(num != 0) {
      
        // get last digit from num
        int digit = num % 10;
        reversed = reversed * 10 + digit;
  
        // remove the last digit from num
        num /= 10;
      }
    }
  }
                               """;
    
}
