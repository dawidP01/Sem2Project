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
import javax.swing.tree.TreeSelectionModel;
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
    StackFrame currentStackFrame;
    int currentLine;
    int currentStackFrameIndex; // Holds the index of the current stack frame in
                                // the stackFrames ArrayList
    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        setTerminal();
        setTextTable();
    }
    public void setcurrentStackFrameIndex(){
        currentStackFrameIndex = 1;
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
        setClassString();
        redirectSystemOutput();
        setClassName();
        getMethodNamesString();
        setBottomSection();
        runInterpreter();
        setLeftSide();
        displayCode();
        setOutputText();
        setcurrentStackFrameIndex();
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
    // Sets the class string by running the javap command
    public void setClassString(){
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
        Stack<Object> stack = currentStackFrame.getStack();
        for(int i=0;i<stack.size();i++){
            stackTable.setValueAt(stack.get(i), stack.size()-1-i, 0);
        }
    }
    public void updateLVA(){
        ArrayList<Object> LVA = currentStackFrame.getLVA();
        for(int i=0;i<stackTable.getRowCount();i++){
            stackTable.setValueAt(LVA.get(i), i, 1);
        }
    }
    public void updateStackTable(){
        currentStackFrame = stackFrames.get(1);
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
            textTableRemoveAllRows();
            DefaultTableModel model = (DefaultTableModel) textTable.getModel();
            Object[] lines = stackFrames.get(0).getClassString().lines().toArray();
            // Set to lines-1 because last line is empty
            for(int i=0;i<lines.length-1;i++){
                int index = lines[i].toString().indexOf(":");
                String lineOffset = lines[i].toString().substring(0, index).strip();
                String code = lines[i].toString().substring(index+1).strip();
                model.addRow(new Object[]{lineOffset, code});
            }
        }
    }
    // Sets the text table rows depending on the method selected in the Method tree
    public void setTextTableRows(int selectedIndex){
        if (classString != null){
            textTableRemoveAllRows();
            DefaultTableModel model = (DefaultTableModel) textTable.getModel();
            Object[] lines = stackFrames.get(selectedIndex).getClassString().lines().toArray();
            for(int i=0;i<lines.length-1;i++){
                int index = lines[i].toString().indexOf(":");
                String lineOffset = lines[i].toString().substring(0, index).strip();
                String code = lines[i].toString().substring(index+1).strip();
                model.addRow(new Object[]{lineOffset, code});
            }
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
        methodTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        javax.swing.tree.DefaultMutableTreeNode classNode = new javax.swing.tree.DefaultMutableTreeNode(className);
        for(int i=0;i<methodNames.size();i++){
            javax.swing.tree.DefaultMutableTreeNode node = new javax.swing.tree.DefaultMutableTreeNode(methodNames.get(i));
            classNode.add(node);
        }
        methodTree.setModel(new javax.swing.tree.DefaultTreeModel(classNode));
    }
    // Highlights whichever line is currently executing
    public void highlightCurrentLine(){
        // Deselects all selected rows
        textTable.clearSelection();
        // Selects the current line
        int currentLineIndex = stackFrames.get(1).getCurrentLineIndex();
        if(currentLineIndex < textTable.getRowCount()){
            textTable.addRowSelectionInterval(currentLineIndex, currentLineIndex);   
        }
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
        helpPanel = new javax.swing.JPanel();
        helpFrameTitle = new javax.swing.JTextField();
        jScrollPane15 = new javax.swing.JScrollPane();
        tutorialFrameTextArea2 = new javax.swing.JTextArea();
        jScrollPane16 = new javax.swing.JScrollPane();
        helpFrameTree = new javax.swing.JTree();
        jSeparator3 = new javax.swing.JSeparator();
        settingsFrame = new javax.swing.JFrame();
        settingsPanel = new javax.swing.JPanel();
        tutorialFrameTitle1 = new javax.swing.JTextField();
        jScrollPane13 = new javax.swing.JScrollPane();
        tutorialFrameTextArea1 = new javax.swing.JTextArea();
        jScrollPane14 = new javax.swing.JScrollPane();
        tutorialTree1 = new javax.swing.JTree();
        jButton3 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        lineNumberTableFrame = new javax.swing.JFrame();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane10 = new javax.swing.JScrollPane();
        lineNumberTable = new javax.swing.JTable();
        tutorialFrame = new javax.swing.JFrame();
        tutorialPanel = new javax.swing.JPanel();
        tutorialFrameTitle = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        tutorialFrameTextArea = new javax.swing.JTextArea();
        jScrollPane11 = new javax.swing.JScrollPane();
        tutorialTree = new javax.swing.JTree();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        FilePanel = new javax.swing.JPanel();
        fileBtn = new javax.swing.JButton();
        settingsBtn = new javax.swing.JButton();
        tutorialBtn = new javax.swing.JButton();
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
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
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

        helpFrameTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        helpFrameTitle.setText("000. nop");
        helpFrameTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpFrameTitleActionPerformed(evt);
            }
        });

        tutorialFrameTextArea2.setColumns(20);
        tutorialFrameTextArea2.setRows(5);
        tutorialFrameTextArea2.setText("Opcode in hex: 00\nOpcode in binary: 0000 0000\nParamters: None\nEffect on Stack: None\nDescription: Performs no operation\n");
        jScrollPane15.setViewportView(tutorialFrameTextArea2);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Topics:");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Opcodes");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("000. nop");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("001. aconst_null");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("002. icontst_m1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("003. iconst_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("004. iconst_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("005. iconst_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("006. iconts_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("007. iconst_4");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("008. iconst_5");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("009. lconst_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("010. lconst_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("011. fconst_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("012. fconst_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("013. fconst_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("014. dconst_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("015. dconst_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("016. bipush");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("017. sipush");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("018. ldc");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("019. ldc_w");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("020. ldc2_w");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("021. iload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("022. lload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("023. fload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("024. dload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("025. aload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("026. iload_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("027. iload_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("028. iload_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("029. iload_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("030. lload_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("031. lload_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("032. lload_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("033. lload_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("034. fload_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("035. fload_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("036. fload_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("037. fload_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("038. dload_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("039. dload_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("040. dload_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("041. dload_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("042. aload_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("043. aload_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("044. aload_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("045. aload_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("046. iaload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("047. laload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("048. faload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("049. daload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("050. aaload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("051. baload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("052. caload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("053. saload");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("054. isotre");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("055. lstore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("056. fstore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("057. dstore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("058.  astore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("059. istore_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("060. istore_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("061. istore_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("062. istore_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("063. lstore_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("064. lstore_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("065. lstore_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("066. lstore_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("067. fstore_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("068. fstore_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("069. fstore_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("070. fstore_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("071. dstore_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("072. dstore_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("073. dstore_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("074. dstore_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("075. astore_0");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("076. astore_1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("077. astore_2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("078. astore_3");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("079. iastore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("080. lastore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("081. fastore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("082. dastore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("083. aastore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("084. bastore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("085. castore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("086. sastore");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("087. pop");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("088. pop2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("089. dup");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("090. dup_x1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("091. dup_x2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("092. dup2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("093. dup2_x1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("094. dup2_x2");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("095. swap");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("096. iadd");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("097. ladd");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("098. fadd");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("099. dadd");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("100. isub");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("101. lsub");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("102. fsub");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("103. dsub");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("104. imul");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("105. lmul");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("106. fmul");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("107. dmul");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("108. idiv");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("109. ldiv");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("110. fdiv");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("111. ddiv");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("112. irem");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("113. lrem");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("114. frem");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("115. drem");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("116. ineg");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("117. lneg");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("118. fneg");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("119. dneg");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("120. ishl");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("121. lshl");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("122. ishr");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("123. lshr");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("124. iushr");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("125. lushr");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("126. iand");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("127. land");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("128. ior");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("129. lor");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("130. ixor");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("131. lxor");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("132. iinc");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("133. i2l");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("134. i2f");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("135. i2d");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("136. l2i");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("137. l2f");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("138. l2d");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("139. f2i");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("140. f2l");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("141. f2d");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("142. d2i");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("143. d2l");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("144. d2f");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("145. i2b");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("146. i2c");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("147. i2s");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("148. lcmp");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("149. fcmpl");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("150. fcmpg");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("151. dcmpl");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("152. dcmpg");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("153. ifeq");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("154. ifne");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("155. iflt");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("156. ifge");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("157. ifgt");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("158. ifle");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("159. if_icmpeq");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("160. if_icmpne");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("161. if_icmplt");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("162. if_icmpge");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("163. if_icmpgt");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("164. if_icmple");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("165. if_acmpeq");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("166. if_acmpne");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("167. goto");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("168. jsr");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("169. ret");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("170. tableswitch");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("171. lookupswitch");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("172. ireturn");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("173. lreturn");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("174. freturn");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("175. dreturn");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("176. areturn");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("177. return");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("178. getstatic");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("179. putstatic");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("180. getfield");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("181. putfield");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("182. invokevirtual");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("183. invokespecial");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("184. invokestatic");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("185. invokeinterface");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("186. invokedynamic");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("187. new");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("188. newarray");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("189. anewarray");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("190. arraylength");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("191. athrow");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("192. checkcast");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("193. instanceof");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("194. monitorenter");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("195. monitorexit");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("196. wide");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("197. multianewarray");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("198. ifnull");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("199. ifnonnull");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("200. goto_w");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("201. jsr_w");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("202. breakpoint");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("203-253.  (no name)");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("254. impdep1");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("255. impdep2");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Memory");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Heap");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Stack");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        helpFrameTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane16.setViewportView(helpFrameTree);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(helpPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(helpPanelLayout.createSequentialGroup()
                        .addComponent(helpFrameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addGroup(helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(helpPanelLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(helpFrameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane15))
                    .addGroup(helpPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane16)
                        .addGap(0, 0, 0)))
                .addContainerGap())
            .addComponent(jSeparator3)
        );

        javax.swing.GroupLayout helpFrameLayout = new javax.swing.GroupLayout(helpFrame.getContentPane());
        helpFrame.getContentPane().setLayout(helpFrameLayout);
        helpFrameLayout.setHorizontalGroup(
            helpFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        helpFrameLayout.setVerticalGroup(
            helpFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        settingsFrame.setTitle("Options");

        tutorialFrameTitle1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        tutorialFrameTitle1.setText("AddIntegers.java");
        tutorialFrameTitle1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialFrameTitle1ActionPerformed(evt);
            }
        });

        tutorialFrameTextArea1.setColumns(20);
        tutorialFrameTextArea1.setRows(5);
        tutorialFrameTextArea1.setText("class Main {\n\n  public static void main(String[] args) {\n    \n    int first = 10;\n    int second = 20;\n\n    // add two numbers\n    int sum = first + second;\n    System.out.println(first + \" + \" + second + \" = \"  + sum);\n  }\n}\n\nEnter two numbers\n10 20\nThe sum is: 30\n\nIn this program, two integers 10 and 20 are stored in integer variables first and second respectively.\n\nThen, first and second are added using the + operator, and its result is stored in another variable sum.\n\nFinally, sum is printed on the screen using println() function.");
        jScrollPane13.setViewportView(tutorialFrameTextArea1);

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Tutorials");
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("AddIntegers");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("BinToDecimal");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("CheckPrime");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Fibonacci");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("ReverseNumber");
        treeNode1.add(treeNode2);
        tutorialTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane14.setViewportView(tutorialTree1);

        jButton3.setText("Confirm");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane13)
                        .addGap(6, 6, 6))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, 0)
                        .addComponent(tutorialFrameTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(tutorialFrameTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane13))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap())
            .addComponent(jSeparator2)
        );

        javax.swing.GroupLayout settingsFrameLayout = new javax.swing.GroupLayout(settingsFrame.getContentPane());
        settingsFrame.getContentPane().setLayout(settingsFrameLayout);
        settingsFrameLayout.setHorizontalGroup(
            settingsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        settingsFrameLayout.setVerticalGroup(
            settingsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        tutorialFrameTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        tutorialFrameTitle.setText("AddIntegers.java");
        tutorialFrameTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialFrameTitleActionPerformed(evt);
            }
        });

        tutorialFrameTextArea.setColumns(20);
        tutorialFrameTextArea.setRows(5);
        tutorialFrameTextArea.setText("class Main {\n\n  public static void main(String[] args) {\n    \n    int first = 10;\n    int second = 20;\n\n    // add two numbers\n    int sum = first + second;\n    System.out.println(first + \" + \" + second + \" = \"  + sum);\n  }\n}\n\nEnter two numbers\n10 20\nThe sum is: 30\n\nIn this program, two integers 10 and 20 are stored in integer variables first and second respectively.\n\nThen, first and second are added using the + operator, and its result is stored in another variable sum.\n\nFinally, sum is printed on the screen using println() function.");
        jScrollPane12.setViewportView(tutorialFrameTextArea);

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Tutorials");
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("AddIntegers");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("BinToDecimal");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("CheckPrime");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Fibonacci");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("ReverseNumber");
        treeNode1.add(treeNode2);
        tutorialTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane11.setViewportView(tutorialTree);

        jButton1.setText("Confirm");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout tutorialPanelLayout = new javax.swing.GroupLayout(tutorialPanel);
        tutorialPanel.setLayout(tutorialPanelLayout);
        tutorialPanelLayout.setHorizontalGroup(
            tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tutorialPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tutorialPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(tutorialPanelLayout.createSequentialGroup()
                        .addGap(0, 0, 0)
                        .addComponent(tutorialFrameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        tutorialPanelLayout.setVerticalGroup(
            tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tutorialPanelLayout.createSequentialGroup()
                .addGroup(tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(tutorialPanelLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(tutorialFrameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
                    .addGroup(tutorialPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
            .addComponent(jSeparator1)
        );

        javax.swing.GroupLayout tutorialFrameLayout = new javax.swing.GroupLayout(tutorialFrame.getContentPane());
        tutorialFrame.getContentPane().setLayout(tutorialFrameLayout);
        tutorialFrameLayout.setHorizontalGroup(
            tutorialFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tutorialFrameLayout.createSequentialGroup()
                .addComponent(tutorialPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        tutorialFrameLayout.setVerticalGroup(
            tutorialFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tutorialPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        FilePanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        fileBtn.setBackground(new java.awt.Color(242, 242, 242));
        fileBtn.setText("File");
        fileBtn.setBorder(null);
        fileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileButtonActionPerformed(evt);
            }
        });

        settingsBtn.setText("Settings");
        settingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsBtnActionPerformed(evt);
            }
        });

        tutorialBtn.setText("Tutorial");
        tutorialBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialBtnActionPerformed(evt);
            }
        });

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
                .addComponent(fileBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingsBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tutorialBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(helpButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        FilePanelLayout.setVerticalGroup(
            FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileBtn)
                    .addComponent(settingsBtn)
                    .addComponent(tutorialBtn)
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
        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Method1");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Additional Info Here");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Method2");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Additional Info Here");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        methodTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        methodTree.setAutoscrolls(true);
        methodTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                methodTreeValueChanged(evt);
            }
        });
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

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        textJavaTable.setGridColor(new java.awt.Color(242, 242, 242));
        jScrollPane3.setViewportView(textJavaTable);

        jTabbedPane1.addTab("Java", jScrollPane3);

        textTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        textTable.setGridColor(new java.awt.Color(242, 242, 242));
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
// Action listener for the file button - This button is used to select a java
// file to inspect
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

    private void RunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunButtonActionPerformed
        if (stackFrames == null){
            errorPopUp("Please Select a Java File First");
        } else {
            try {
                // Build the command to execute
                String className = this.className;
                int index = className.indexOf("class");
                className = className.substring(index+6);

                // Runs the java file
                String command = "java " + className;
                Process process = Runtime.getRuntime().exec(command);

                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Execution successful");
                } else {
                    System.err.println("Execution failed with exit code " + exitCode);
                }

            } catch (IOException | InterruptedException e) {
                System.out.println(className);
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_RunButtonActionPerformed

    private void StackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StackButtonActionPerformed
        stackFrame.setVisible(true);
        stackFrame.setSize(280,200);
    }//GEN-LAST:event_StackButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        helpFrame.setVisible(true);
        helpFrame.setSize(200,200);
    }//GEN-LAST:event_helpButtonActionPerformed

    private void settingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsBtnActionPerformed
        settingsFrame.setVisible(true);
        settingsFrame.setSize(200,200);
        settingsFrame.setTitle("Settings");
    }//GEN-LAST:event_settingsBtnActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if(stackFrames == null){
            errorPopUp("Please Select a Java File First");
        } else{   
            highlightCurrentLine();
            if(stackFrames.get(1).getFinished()){
                errorPopUp("The end of the program has been reached");
            } else {
                stackFrames.get(1).runInstructions(); 
            }
            updateStackTable();
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void lineNumberTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineNumberTableBtnActionPerformed
        lineNumberTableFrame.setVisible(true);
        lineNumberTableFrame.setSize(300,300);
    }//GEN-LAST:event_lineNumberTableBtnActionPerformed
// Below is a tree selection event listener, it sets the bytecode text table
// when a tree node is selected
    private void methodTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_methodTreeValueChanged
        javax.swing.tree.DefaultMutableTreeNode selectedNode = (javax.swing.tree.DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            int index = selectedNode.getParent().getIndex(selectedNode);
            setTextTableRows(index);
        }
    }//GEN-LAST:event_methodTreeValueChanged

    private void tutorialBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialBtnActionPerformed
        tutorialFrame.setVisible(true);
        tutorialFrame.setSize(200, 200);
        tutorialFrame.setTitle("Tutorial");
    }//GEN-LAST:event_tutorialBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tutorialFrameTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialFrameTitleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tutorialFrameTitleActionPerformed

    private void tutorialFrameTitle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialFrameTitle1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tutorialFrameTitle1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void helpFrameTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpFrameTitleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpFrameTitleActionPerformed

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
    private javax.swing.JButton RunButton;
    private javax.swing.JPanel RunPanel;
    private javax.swing.JButton StackButton;
    private javax.swing.JTextArea constPoolTextArea;
    private javax.swing.JButton fileBtn;
    private javax.swing.JButton helpButton;
    private javax.swing.JFrame helpFrame;
    private javax.swing.JTextField helpFrameTitle;
    private javax.swing.JTree helpFrameTree;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTable lineNumberTable;
    private javax.swing.JButton lineNumberTableBtn;
    private javax.swing.JFrame lineNumberTableFrame;
    private javax.swing.JTree methodTree;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton settingsBtn;
    private javax.swing.JFrame settingsFrame;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JFrame stackFrame;
    private javax.swing.JTextArea stackMapTableTextArea;
    private javax.swing.JTable stackTable;
    private javax.swing.JTabbedPane terminalPane;
    private javax.swing.JTextArea terminalTextArea;
    private javax.swing.JTable textJavaTable;
    private javax.swing.JTable textTable;
    private javax.swing.JButton tutorialBtn;
    private javax.swing.JFrame tutorialFrame;
    private javax.swing.JTextArea tutorialFrameTextArea;
    private javax.swing.JTextArea tutorialFrameTextArea1;
    private javax.swing.JTextArea tutorialFrameTextArea2;
    private javax.swing.JTextField tutorialFrameTitle;
    private javax.swing.JTextField tutorialFrameTitle1;
    private javax.swing.JPanel tutorialPanel;
    private javax.swing.JTree tutorialTree;
    private javax.swing.JTree tutorialTree1;
    // End of variables declaration//GEN-END:variables
}
