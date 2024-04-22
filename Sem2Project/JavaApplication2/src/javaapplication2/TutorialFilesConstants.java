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
class Main {

public static void main(String[] args) {

  int first = 10;
  int second = 20;

  // add two numbers
  int sum = first + second;
  System.out.println(first + " + " + second + " = "  + sum);
}
}

Enter two numbers
10 20
The sum is: 30

In this program, two integers 10 and 20 are stored in integer variables first and second respectively.
Then, first and second are added using the + operator, and its result is stored in another variable sum.
Finally, sum is printed on the screen using println() function.                             
                             """;
    String BinToDecText = """
class BinToDec {
    
    public static void main(String[] args) {
  
      // binary number
      long num = 110110111;
  
      // call method by passing the binary number
      int decimal = convertBinaryToDecimal(num);
  
      System.out.println("Binary to Decimal");
      System.out.println(num + " = " + decimal);
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
  
      if (!flag)
        System.out.println(num + " is a prime number.");
      else
        System.out.println(num + " is not a prime number.");
    }
}
                            """;
    String FibonacciText = """
class Fibonacci {
    public static void main(String[] args) {
  
      int n = 10, firstTerm = 0, secondTerm = 1;
      System.out.println("Fibonacci Series till " + n + " terms:");
  
      for (int i = 1; i <= n; ++i) {
        System.out.print(firstTerm + ", ");
  
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
  
      System.out.println("Reversed Number: " + reversed);
    }
  }
                               """;
    
}
