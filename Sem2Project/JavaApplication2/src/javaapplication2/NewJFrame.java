/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaapplication2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Stack;
import java.util.spi.ToolProvider;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javax.swing.JOptionPane;
/**
 *
 * @author C00273530
 */
public class NewJFrame extends javax.swing.JFrame {

    // Global Variables
    File f;
    String sourceFilePath;
    ClassPool pool = ClassPool.getDefault();
    CtClass ctClass;
    ClassFile classFile;
    ConstPool constantsPool;
    java.util.List<MethodInfo> methodNames;
    CtMethod[] methods;
    
    String classString;
    String code;
    Stack<stackEntry> stack = new Stack();
    stackEntry[] localVariableArray = new stackEntry[100];
    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        setTerminal();
    }
    // Sets the terminal window at the bottom of the screen
    public void setTerminal(){
        //System.setOut(new TextAreaPrintStream(terminal, System.out));
        
    }
    // An Error Message
    public void errorPopUp(String message){
        JOptionPane.showMessageDialog(null, message,
               "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Initialises certain components after a class file is selected
    public void initClassComponents(){
        copyToWorkingDirectory();
        setClassFileAndConstantPool();
        setMethodNames();
        setConstantsPoolTab();
        displayBytecode();
        setOutputText();
        setClassString();
        setCtClass();  
        setMethods();  
        redirectSystemOutput();
        test();
        
        runInterpreter();
    }
    public void copyToWorkingDirectory() {
        String destinationDirectory = System.getProperty("user.dir");
        try{
            Path sourcePath = Paths.get(sourceFilePath);
            Path destinationPath = Paths.get(destinationDirectory, sourcePath.getFileName().toString());
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    // The below needs to be split into two different methods!!!
    // Sets the class file and constants Pool variables
    public void setClassFileAndConstantPool(){
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFilePath);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            ClassFile classFile = new ClassFile(dataInputStream);
            ConstPool table = classFile.getConstPool();
            
            this.classFile = classFile;
            this.constantsPool = table;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Sets the list of the method names in the class file
    public void setMethodNames(){  
        methodNames = classFile.getMethods();
       // testLabel.setText(methodNames.get(0).toString());
    }
    /* Sets the 3rd tab at the bottom of the screen to display the
       constants table */
    public void setConstantsPoolTab(){
        try{
            ConstPool constPool = classFile.getConstPool();
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            constPool.print(printWriter);
            printWriter.flush();
            String constantPoolString = stringWriter.toString();
            printWriter.close();
            stringWriter.close();
            this.constPoolTextArea.setText(constantPoolString);
        } catch (IOException e){
            e.printStackTrace();
            errorPopUp("Error with setting the Constants Pool Table");
        }
    }
    public void displayBytecode(){
        ToolProvider javap = ToolProvider.findFirst("javap").orElseThrow();
        StringWriter out = new StringWriter();
        PrintWriter stdout = new PrintWriter(out);
        StringWriter err = new StringWriter();
        PrintWriter stderr = new PrintWriter(err);
        int exitCode = javap.run(
            stdout,
            stderr,
            "-v", "-p", sourceFilePath);
        testArea.setText(out.toString());
    }
    public void setOutputText(){
        String classText = testArea.getText();
        Path workingDir = Paths.get("Output.txt");
        workingDir = workingDir.toAbsolutePath();
        try {
            Files.writeString(workingDir, classText,
                              StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            System.out.print("Invalid Path");
        }
    }
    public void setClassString(){
        this.classString = testArea.getText();
    }
    public void setCtClass(){
        ctClass = pool.makeClass(classFile); 
    }
    public void setMethods(){
        methods = ctClass.getDeclaredMethods();
    }
    public void addPauseStatements(){
        for (CtMethod method : methods){
            CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
            CodeIterator iterator = codeAttribute.iterator();
            while (iterator.hasNext()){
                try{
                    int index = iterator.next();
                    method.insertAt(index, "Thread.sleep(1000)");
                } catch(Exception e){
                    e.printStackTrace();
                }    
            }
        }
    }
    // Redirects output to the terminal text area
    // FIX: THE OUTPUT DOESNT FULLY REDIRECT
    private void redirectSystemOutput() {
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                terminalTextArea.append(String.valueOf((char) b));
                terminalTextArea.setCaretPosition(terminalTextArea.getDocument().getLength());
            }
        };
        System.setOut(new PrintStream(outputStream));
    }
        
    public void test(){
        CtMethod method = methods[0];
        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr != null) {
            for (int i = 0; i < attr.tableLength(); i++) {
                System.out.println("Local variable #" + i);
                System.out.println("Name: " + attr.variableName(i));
                System.out.println("Index: " + attr.index(i));
                System.out.println("Descriptor: " + attr.descriptor(i));
                System.out.println();
            }
        } else {
            System.out.println("null");
        }
        System.out.println("hi");
    }
    
    public void runInterpreter(){
        setCode();
    }
    public void setCode(){
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stackFrame = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        stackTextArea = new javax.swing.JTextArea();
        helpFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        optionsFrame = new javax.swing.JFrame();
        jPanel3 = new javax.swing.JPanel();
        FilePanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        OptionsButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        RunPanel = new javax.swing.JPanel();
        RunButton = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        StackButton = new javax.swing.JButton();
        LeftPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        CentrePanel = new javax.swing.JPanel();
        terminalPane = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        terminalTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        constPoolTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        testArea = new javax.swing.JTextArea();
        testLabel = new javax.swing.JLabel();

        stackFrame.setTitle("Stack");

        stackTextArea.setColumns(20);
        stackTextArea.setRows(5);
        jScrollPane5.setViewportView(stackTextArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout stackFrameLayout = new javax.swing.GroupLayout(stackFrame.getContentPane());
        stackFrame.getContentPane().setLayout(stackFrameLayout);
        stackFrameLayout.setHorizontalGroup(
            stackFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        stackFrameLayout.setVerticalGroup(
            stackFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        helpFrame.setTitle("Manual");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 282, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout helpFrameLayout = new javax.swing.GroupLayout(helpFrame.getContentPane());
        helpFrame.getContentPane().setLayout(helpFrameLayout);
        helpFrameLayout.setHorizontalGroup(
            helpFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        helpFrameLayout.setVerticalGroup(
            helpFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpFrameLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        optionsFrame.setTitle("Options");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout optionsFrameLayout = new javax.swing.GroupLayout(optionsFrame.getContentPane());
        optionsFrame.getContentPane().setLayout(optionsFrameLayout);
        optionsFrameLayout.setHorizontalGroup(
            optionsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        optionsFrameLayout.setVerticalGroup(
            optionsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        FilePanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        jButton2.setBackground(new java.awt.Color(242, 242, 242));
        jButton2.setText("File");
        jButton2.setBorder(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileButtonActionPerformed(evt);
            }
        });

        OptionsButton.setText("Options");
        OptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OptionsButtonActionPerformed(evt);
                OptionsButtonActionPerformed1(evt);
            }
        });

        jButton4.setText("Tutorial");

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FilePanelLayout = new javax.swing.GroupLayout(FilePanel);
        FilePanel.setLayout(FilePanelLayout);
        FilePanelLayout.setHorizontalGroup(
            FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(OptionsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(helpButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        FilePanelLayout.setVerticalGroup(
            FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(OptionsButton)
                    .addComponent(jButton4)
                    .addComponent(helpButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        RunButton.setText("Run");
        RunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunButtonActionPerformed(evt);
            }
        });

        jButton6.setText("Next Instruction");

        StackButton.setText("Stack");
        StackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StackButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout RunPanelLayout = new javax.swing.GroupLayout(RunPanel);
        RunPanel.setLayout(RunPanelLayout);
        RunPanelLayout.setHorizontalGroup(
            RunPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RunPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RunButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StackButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RunPanelLayout.setVerticalGroup(
            RunPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RunPanelLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(RunPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RunButton)
                    .addComponent(jButton6)
                    .addComponent(StackButton))
                .addContainerGap())
        );

        LeftPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 1, new java.awt.Color(0, 0, 0)));

        jTree1.setBackground(new java.awt.Color(242, 242, 242));
        jTree1.setBorder(null);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Method1");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Additional Info Here");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Method2");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Additional Info Here");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setAutoscrolls(true);
        jScrollPane2.setViewportView(jTree1);

        javax.swing.GroupLayout LeftPanelLayout = new javax.swing.GroupLayout(LeftPanel);
        LeftPanel.setLayout(LeftPanelLayout);
        LeftPanelLayout.setHorizontalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
        );
        LeftPanelLayout.setVerticalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        CentrePanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(0, 0, 0)));

        terminalPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        terminalPane.setDoubleBuffered(true);

        terminalTextArea.setColumns(20);
        terminalTextArea.setRows(5);
        jScrollPane4.setViewportView(terminalTextArea);

        terminalPane.addTab("Terminal", jScrollPane4);
        terminalPane.addTab("Tutorial", jLabel1);

        constPoolTextArea.setColumns(20);
        constPoolTextArea.setRows(5);
        jScrollPane1.setViewportView(constPoolTextArea);

        terminalPane.addTab("Constants Pool", jScrollPane1);

        testArea.setColumns(20);
        testArea.setRows(5);
        jScrollPane3.setViewportView(testArea);

        testLabel.setText("testLabel");

        javax.swing.GroupLayout CentrePanelLayout = new javax.swing.GroupLayout(CentrePanel);
        CentrePanel.setLayout(CentrePanelLayout);
        CentrePanelLayout.setHorizontalGroup(
            CentrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(terminalPane)
            .addGroup(CentrePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CentrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CentrePanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(testLabel))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        CentrePanelLayout.setVerticalGroup(
            CentrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CentrePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(testLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(terminalPane))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(FilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(RunPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(LeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(CentrePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(FilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RunPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CentrePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LeftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileButtonActionPerformed
        javax.swing.JFileChooser ch = new javax.swing.JFileChooser();
        ch.showOpenDialog(null);
        File f = ch.getSelectedFile(); // Sets the class file that will be used
        String filePath = f.getAbsolutePath(); // Sets the path to that file
        
        String fileName = f.getName();      
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            // Extract the extension
            String extension = fileName.substring(dotIndex + 1);
            // Checks if the selected file is a class file
            if(extension.compareTo("class") == 0){
                this.f = f;
                this.sourceFilePath = filePath;
                initClassComponents();
            } else {
                errorPopUp("Please Select a class File");
            }
        } else {
            errorPopUp("File has no extension");
        }
    }//GEN-LAST:event_FileButtonActionPerformed

    private void OptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OptionsButtonActionPerformed
        errorPopUp("Testing");
    }//GEN-LAST:event_OptionsButtonActionPerformed

    private void RunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunButtonActionPerformed
        try {
            // Specify the class name you want to run
            String className = classFile.getName();

            // Build the command to execute
            ProcessBuilder processBuilder = new ProcessBuilder("java", className);

            // Redirect the standard output and error streams to this process's streams
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Execution successful");
            } else {
                System.err.println("Execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_RunButtonActionPerformed

    private void StackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StackButtonActionPerformed
        stackFrame.setVisible(true);
        stackFrame.setSize(200,200);
    }//GEN-LAST:event_StackButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        helpFrame.setVisible(true);
        helpFrame.setSize(200,200);
    }//GEN-LAST:event_helpButtonActionPerformed

    private void OptionsButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OptionsButtonActionPerformed1
        optionsFrame.setVisible(true);
        optionsFrame.setSize(200,200);
    }//GEN-LAST:event_OptionsButtonActionPerformed1

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CentrePanel;
    private javax.swing.JPanel FilePanel;
    private javax.swing.JPanel LeftPanel;
    private javax.swing.JButton OptionsButton;
    private javax.swing.JButton RunButton;
    private javax.swing.JPanel RunPanel;
    private javax.swing.JButton StackButton;
    private javax.swing.JTextArea constPoolTextArea;
    private javax.swing.JButton helpButton;
    private javax.swing.JFrame helpFrame;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTree jTree1;
    private javax.swing.JFrame optionsFrame;
    private javax.swing.JFrame stackFrame;
    private javax.swing.JTextArea stackTextArea;
    private javax.swing.JTabbedPane terminalPane;
    private javax.swing.JTextArea terminalTextArea;
    private javax.swing.JTextArea testArea;
    private javax.swing.JLabel testLabel;
    // End of variables declaration//GEN-END:variables
}
