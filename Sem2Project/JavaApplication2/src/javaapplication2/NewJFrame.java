/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaapplication2;

import java.io.File;
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
import java.util.ArrayList;
import java.util.Stack;
import java.util.spi.ToolProvider;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
/**
 *
 * @author C00273530
 */
public class NewJFrame extends javax.swing.JFrame {

    // Global Variables
    File f;
    String sourceFilePath;
    String classString; // Contains the contents of the javap command 
    String javaFilePath;
    String className; // String that contains the name of the class
    ArrayList<String> methodNames; // Contains all of the methods in the file
    String code;
    StackFrame op; 
    ArrayList<StackFrame> stackFrames;
    int currentLine;
    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        setTerminal();
        setTextTable();
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
        displayCode();
        setOutputText(); 
        redirectSystemOutput();
        setClassName();
        getMethodNamesString();
        runInterpreter();
        setBottomSection();
        setLeftSide();
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
        classString = out.toString();
    }
    // Displays code in the centre of the app
    public void displayCode(){
        displayBytecode();
        setTextJavaTable();
        setTextTable();
    }
    public void setOutputText(){
        String classText = classString;
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
    public void setStackFrames(){
        stackFrames = new ArrayList<>();
        String classString = this.classString;
        while(classString.indexOf("Code:")!= -1){
            int i = classString.indexOf("Code:");
            i = classString.indexOf("\n", i) + 1;
            i = classString.indexOf("\n", i) + 1;
            int end = classString.indexOf("LineNumberTable:", i);
            String result = "";
            for(;i<end;i++){
                result += classString.charAt(i);
                // Removes comments
                if(classString.charAt(i+1) == '/' && classString.charAt(i+2)=='/'){
                    i = classString.indexOf("\n", i);
                    result += "\n";
                }
            }
            classString = classString.substring(i);
            stackFrames.add(new StackFrame(result));
            result = "";
        }
    }
    public void runInterpreter(){
        setStackFrames();
        ArrayList<String> s = stackFrames.get(0).getInstructions();
        System.out.println(s.get(0));

      //  op.setParameters(a, a[13]);
      //  System.out.println(a[13]);
      //  System.out.println(a[8]);
    }
    public void clearStackTable(){
        int rowCount = stackTable.getRowCount();
        for (int i=0;i<rowCount;i++){
            stackTable.setValueAt(null,i,0);
            stackTable.setValueAt(null,i,1);
        }
    }
    public void updateOperandStack(){
        Stack<Object> stack = op.getStack();
        for(int i=0;i<stack.size();i++){
            stackTable.setValueAt(stack.get(i), stack.size()-1-i, 0);
        }
    }
    public void updateLVA(){
        Object[] LVA = op.getLVA();
        for(int i=0;i<LVA.length;i++){
            stackTable.setValueAt(LVA[i], i, 1);
        }
    }
    public void updateStackTable(){
        clearStackTable();
        updateOperandStack();
        updateLVA();
    }
    // Removes all rows in the text table
    public void textTableRemoveAllRows(){
        DefaultTableModel model = (DefaultTableModel) textTable.getModel();
        model.getDataVector().removeAllElements();
    }
    // Assigns each row a corresponding line from the bytecode
    public void setTextTableRows(){
        if (classString != null){
            DefaultTableModel model = (DefaultTableModel) textTable.getModel();
            Object[] lines = classString.lines().toArray();
            for(int i=0;i<lines.length;i++){
                model.addRow(new Object[]{i, lines[i]});
            }
        }
    }
    public void setTextTableRows(String text){
        textTableRemoveAllRows();
        DefaultTableModel model = (DefaultTableModel) textTable.getModel();
        Object[] lines = text.lines().toArray();
        for(int i=0;i<lines.length;i++){
            model.addRow(new Object[]{i, lines[i]});
        }
    }
    public void setTextTable(){
        // Makes the title of columns invisible
        textTable.getTableHeader().setVisible(false);
        // Turns off resize mode for the table
        textTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // Makes the column containing numbers as small as possible
        textTable.getColumnModel().getColumn(0).setMinWidth(0);
        textTable.getColumnModel().getColumn(0).setMaxWidth(30);
        textTableRemoveAllRows();
        setTextTableRows();
    }
    public void textJavaTableRemoveAllRows(){
        DefaultTableModel model = (DefaultTableModel) textJavaTable.getModel();
        model.getDataVector().removeAllElements();
    }
    public void setTextJavaTableRows(){
        if (javaFilePath != null){
            DefaultTableModel model = (DefaultTableModel) textJavaTable.getModel();
            try{
                String content = Files.readString(Paths.get(javaFilePath), StandardCharsets.UTF_8);
                Object[] lines = content.lines().toArray();
                for(int i=0;i<lines.length;i++){
                    model.addRow(new Object[]{i, lines[i]});
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public void setTextJavaTable(){
        // Makes the title of columns invisible
        textJavaTable.getTableHeader().setVisible(false);
        // Turns off resize mode for the table
        textJavaTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // Makes the column containing numbers as small as possible
        textJavaTable.getColumnModel().getColumn(0).setMinWidth(0);
        textJavaTable.getColumnModel().getColumn(0).setMaxWidth(30);
        textJavaTableRemoveAllRows();
        setTextJavaTableRows();
    }
    public void setBottomSection(){
        setFileInfoTextArea();
    }
    // Sets the details of the file info tab
    public void setFileInfoTextArea(){
        String classString = this.classString;
        String text = "";
        jTextArea2.setText(text);
        for(int i=0;i<classString.length();i++){
            if(classString.substring(i, i+13).compareTo("Constant pool")==0){
                // Removes the class info from the central text table
                int lastLineIndex = classString.lastIndexOf("}");
                text = jTextArea2.getText();
                // Adds the source file to the class info tab
                text += classString.substring(lastLineIndex+3);
                jTextArea2.setText(text);
                setConstantsPoolTab(classString.substring(i,lastLineIndex+1));
                
                break;
            }else{
                text = jTextArea2.getText();
                jTextArea2.setText(text+classString.charAt(i));
            }
        }
    }
    public void setConstantsPoolTab(String text){
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)=='{'){
                constPoolTextArea.setText(text.substring(0,i));
                setStackMapTableTextArea(text.substring(i));
                setTextTableRows(text.substring(i));
                break;
            }
        }
    }
    public void setStackMapTableTextArea(String text){
        stackMapTableTextArea.setText(text);
    }
    // Gets all the methods present in the file and places them in a string
    public void getMethodNamesString(){
        ToolProvider javap = ToolProvider.findFirst("javap").orElseThrow();
        StringWriter out = new StringWriter();
        PrintWriter stdout = new PrintWriter(out);
        StringWriter err = new StringWriter();
        PrintWriter stderr = new PrintWriter(err);
        int exitCode = javap.run(
            stdout,
            stderr,
            sourceFilePath);
        String methodNamesString = out.toString();
        setMethodNames(methodNamesString);
    }
    // Sets the methodNames array and fills it with entries
    public void setMethodNames(String methodNamesString){
        methodNames = new ArrayList<String>();
        int startIndex = methodNamesString.indexOf("{");
        startIndex = methodNamesString.indexOf("\n ", startIndex);
        String name = "";
        for(int i=startIndex;i<methodNamesString.indexOf("}");i++){
            if(methodNamesString.charAt(i) != '\n')
                name += methodNamesString.charAt(i);
            if(methodNamesString.charAt(i)=='\n')
                if(name.compareTo("")!=0){
                methodNames.add(name.strip());
                name="";
            }
        }
    }
    public void setClassName(){
        int start = classString.indexOf(" class ");
        start = classString.indexOf(" ", start);
        int end = classString.indexOf("\n", start);
        className = classString.substring(start, end);
    }
    // Sets the left panel of the screen
    public void setLeftSide(){
        setMethodTree();
    }
    // Set the method tree on the left side of the screen with the methodNames
    public void setMethodTree(){
        javax.swing.tree.DefaultMutableTreeNode classNode = new javax.swing.tree.DefaultMutableTreeNode(className);
        for(int i=0;i<methodNames.size();i++){
            javax.swing.tree.DefaultMutableTreeNode node = new javax.swing.tree.DefaultMutableTreeNode(methodNames.get(i));
            classNode.add(node);
        }
        methodTree.setModel(new javax.swing.tree.DefaultTreeModel(classNode));
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
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        stackTable = new javax.swing.JTable();
        helpFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        optionsFrame = new javax.swing.JFrame();
        jPanel3 = new javax.swing.JPanel();
        lineNumberTableFrame = new javax.swing.JFrame();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane10 = new javax.swing.JScrollPane();
        lineNumberTable = new javax.swing.JTable();
        FilePanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        OptionsButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        RunPanel = new javax.swing.JPanel();
        RunButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        StackButton = new javax.swing.JButton();
        lineNumberTableBtn = new javax.swing.JButton();
        LeftPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        methodTree = new javax.swing.JTree();
        CentrePanel = new javax.swing.JPanel();
        terminalPane = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        terminalTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        constPoolTextArea = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        stackMapTableTextArea = new javax.swing.JTextArea();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        textJavaTable = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        textTable = new javax.swing.JTable();

        stackFrame.setTitle("Stack");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Method A", "Method B" }));

        jLabel2.setText("Choose Stack Frame:");

        stackTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Operand Stack", "LVA", "Frame Data"
            }
        ));
        jScrollPane5.setViewportView(stackTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout stackFrameLayout = new javax.swing.GroupLayout(stackFrame.getContentPane());
        stackFrame.getContentPane().setLayout(stackFrameLayout);
        stackFrameLayout.setHorizontalGroup(
            stackFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stackFrameLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

        jLabel3.setText("Choose Method:");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lineNumberTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Java Line", "Bytecode Line"
            }
        ));
        jScrollPane10.setViewportView(lineNumberTable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout lineNumberTableFrameLayout = new javax.swing.GroupLayout(lineNumberTableFrame.getContentPane());
        lineNumberTableFrame.getContentPane().setLayout(lineNumberTableFrameLayout);
        lineNumberTableFrameLayout.setHorizontalGroup(
            lineNumberTableFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        lineNumberTableFrameLayout.setVerticalGroup(
            lineNumberTableFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        nextButton.setText("Next Instruction");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        StackButton.setText("Stack Frame");
        StackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StackButtonActionPerformed(evt);
            }
        });

        lineNumberTableBtn.setText("Line Number Tables");
        lineNumberTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineNumberTableBtnActionPerformed(evt);
                lineNumberTableBtnActionPerformed1(evt);
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
                .addComponent(nextButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StackButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineNumberTableBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RunPanelLayout.setVerticalGroup(
            RunPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RunPanelLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(RunPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RunButton)
                    .addComponent(nextButton)
                    .addComponent(StackButton)
                    .addComponent(lineNumberTableBtn))
                .addContainerGap())
        );

        LeftPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 1, new java.awt.Color(0, 0, 0)));

        methodTree.setBackground(new java.awt.Color(242, 242, 242));
        methodTree.setBorder(null);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Method1");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Additional Info Here");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Method2");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Additional Info Here");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        methodTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        methodTree.setAutoscrolls(true);
        jScrollPane2.setViewportView(methodTree);

        javax.swing.GroupLayout LeftPanelLayout = new javax.swing.GroupLayout(LeftPanel);
        LeftPanel.setLayout(LeftPanelLayout);
        LeftPanelLayout.setHorizontalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        LeftPanelLayout.setVerticalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
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

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane6.setViewportView(jTextArea1);

        terminalPane.addTab("Call Stack", jScrollPane6);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane8.setViewportView(jTextArea2);

        terminalPane.addTab("File Info", jScrollPane8);

        stackMapTableTextArea.setColumns(20);
        stackMapTableTextArea.setRows(5);
        jScrollPane9.setViewportView(stackMapTableTextArea);

        terminalPane.addTab("StackMapTable", jScrollPane9);

        textJavaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jScrollPane3.setViewportView(textJavaTable);

        jTabbedPane1.addTab("Java", jScrollPane3);

        textTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jScrollPane7.setViewportView(textTable);

        jTabbedPane1.addTab("Bytecode", jScrollPane7);

        javax.swing.GroupLayout CentrePanelLayout = new javax.swing.GroupLayout(CentrePanel);
        CentrePanel.setLayout(CentrePanelLayout);
        CentrePanelLayout.setHorizontalGroup(
            CentrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(terminalPane, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        CentrePanelLayout.setVerticalGroup(
            CentrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CentrePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(terminalPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(FilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(RunPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(LeftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(LeftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CentrePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))))
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
            String shortName = fileName.substring(0, dotIndex);
            String extension = fileName.substring(dotIndex + 1);
            // Checks if the selected file is a class file
            if(extension.compareTo("class") == 0){
                /*
                this.f = f;
                this.sourceFilePath = filePath;
                initClassComponents();
                */
                errorPopUp("Please select a java File");
                // Below compiles the file
            } else if(extension.compareTo("java") == 0){
                // Path to the directory where you want to place the compiled .class file
                String outputDirectoryPath = System.getProperty("user.dir");
                // Create a ProcessBuilder instance for executing the javac command
                ProcessBuilder processBuilder = new ProcessBuilder();

                // Set the command and arguments
                java.util.List<String> command = new java.util.ArrayList<>();
                command.add("javac");
                command.add("-d");
                command.add(outputDirectoryPath);
                command.add(filePath);
                processBuilder.command(command);

                // Set the working directory (optional)
                File workingDirectory = new File(System.getProperty("user.dir"));
                processBuilder.directory(workingDirectory);

                try {
                    // Start the process
                    Process process = processBuilder.start();

                    // Wait for the process to finish
                    int exitCode = process.waitFor();

                    // Check the exit code
                    if (exitCode == 0) {
                        this.javaFilePath = filePath;
                        this.f = new File(shortName+".class");
                        this.sourceFilePath = shortName+".class";
                        initClassComponents();
                    } else {
                        errorPopUp("Compilation Failed!");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                errorPopUp("Please Select a java File");
            }
        } else {
            errorPopUp("File has no extension");
        }
    }//GEN-LAST:event_FileButtonActionPerformed

    private void OptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OptionsButtonActionPerformed
        errorPopUp("Testing");
    }//GEN-LAST:event_OptionsButtonActionPerformed

    private void RunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunButtonActionPerformed
       /*
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
*/
    }//GEN-LAST:event_RunButtonActionPerformed

    private void StackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StackButtonActionPerformed
        stackFrame.setVisible(true);
        stackFrame.setSize(280,200);
    }//GEN-LAST:event_StackButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        helpFrame.setVisible(true);
        helpFrame.setSize(200,200);
    }//GEN-LAST:event_helpButtonActionPerformed

    private void OptionsButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OptionsButtonActionPerformed1
        optionsFrame.setVisible(true);
        optionsFrame.setSize(200,200);
    }//GEN-LAST:event_OptionsButtonActionPerformed1

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if(op != null){
            op.runInstructions();
            op.setCurrentLine(op.getCurrentLine()+1);
            updateStackTable();
        } else {
            errorPopUp("Please select a class file first");
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void lineNumberTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineNumberTableBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lineNumberTableBtnActionPerformed

    private void lineNumberTableBtnActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineNumberTableBtnActionPerformed1
        lineNumberTableFrame.setVisible(true);
        lineNumberTableFrame.setSize(300,300);
    }//GEN-LAST:event_lineNumberTableBtnActionPerformed1

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
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTable lineNumberTable;
    private javax.swing.JButton lineNumberTableBtn;
    private javax.swing.JFrame lineNumberTableFrame;
    private javax.swing.JTree methodTree;
    private javax.swing.JButton nextButton;
    private javax.swing.JFrame optionsFrame;
    private javax.swing.JFrame stackFrame;
    private javax.swing.JTextArea stackMapTableTextArea;
    private javax.swing.JTable stackTable;
    private javax.swing.JTabbedPane terminalPane;
    private javax.swing.JTextArea terminalTextArea;
    private javax.swing.JTable textJavaTable;
    private javax.swing.JTable textTable;
    // End of variables declaration//GEN-END:variables
}
