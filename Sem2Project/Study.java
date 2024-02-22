import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;

public class Study{
    public static void main(String[] args) throws NotFoundException, IOException, CannotCompileException {
        String classFilePath = "Hello.class";
        try {
            FileInputStream fileInputStream = new FileInputStream(classFilePath);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            ClassFile classFile = new ClassFile(dataInputStream);

            ConstPool table = classFile.getConstPool();
            System.out.println(table);
        
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
}