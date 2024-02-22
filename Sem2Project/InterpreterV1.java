import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class InterpreterV1 {
    public static String getBytecodeFromTextFile(String fileName){
        String bytecode = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            try{
                while ((line = br.readLine()) != null){
                    bytecode += line +"\n";
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return bytecode;
    }
    public static void main(String[] args) {
    // This section gets code out of a text file
        String bytecode;
        bytecode = getBytecodeFromTextFile("C:\\Users\\C00273530\\Desktop\\Sem2Project\\Output.txt");
        
        Stack<String> stack = new Stack<>();
    }
}
