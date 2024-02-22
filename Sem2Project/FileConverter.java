import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileConverter {
     public static void main(String[] args) {
  try {
   List<String> cmdList = new ArrayList<String>();
   cmdList.add("C:\\Program Files\\Java\\jdk-21\\bin\\javap.exe");
   cmdList.add("-v");
   cmdList.add("C:\\Users\\C00273530\\Desktop\\Sem2Project\\Hello.class");
   
   // Constructing ProcessBuilder with List as argument
   ProcessBuilder pb = new ProcessBuilder(cmdList);
   
   Process p = pb.start();
   p.waitFor();
   InputStream fis = p.getInputStream();
   
   DisplayClassStructure(fis);
  } catch (InterruptedException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  } catch (IOException e1) {
   // TODO Auto-generated catch block
   e1.printStackTrace();
  }
 }
 
 // Method used for displaying the disassembled class
 private static void DisplayClassStructure(InputStream is){
  
  InputStream stream;
  
  try {
   
   BufferedReader reader = new BufferedReader(new InputStreamReader(is));
   String line;   
   while ((line = reader.readLine()) != null) {   
        System.out.println(line);   
   }
   // Better put it in finally
   reader.close();
  } catch (FileNotFoundException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
}
