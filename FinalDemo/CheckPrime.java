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