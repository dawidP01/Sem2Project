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
import javax.swing.tree.TreePath;
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
    String constantPoolString;
    String code;
    StackFrame op; 
    ArrayList<StackFrame> stackFrames;
    StackFrame currentStackFrame;
    int[] breakPoint; 
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
        hideTable();
    }
    public void hideTable(){
        mainPane.setVisible(false);
        terminalPane.setVisible(false);
        methodTree.setVisible(false);
    }
    public void showTable(){
        mainPane.setVisible(true);
        terminalPane.setVisible(true);
        methodTree.setVisible(true);
    }
    public void setcurrentStackFrameIndex(){
        currentStackFrameIndex = 0;
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
        showTable();
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
            stackFrames.add(new StackFrame(result,constantPoolString));
            result = "";
        }
    }
    public void runInterpreter(){
        setStackFrames();
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
        DefaultTableModel model = (DefaultTableModel) bytecodeTextTable.getModel();
        model.getDataVector().removeAllElements();
    }
    // Assigns each row a corresponding line from the bytecode
    public void setTextTableRows(){
        if (classString != null){
            textTableRemoveAllRows();
            DefaultTableModel model = (DefaultTableModel) bytecodeTextTable.getModel();
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
            DefaultTableModel model = (DefaultTableModel) bytecodeTextTable.getModel();
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
        bytecodeTextTable.getTableHeader().setVisible(false);
        // Turns off resize mode for the table
        bytecodeTextTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // Makes the column containing numbers as small as possible
        bytecodeTextTable.getColumnModel().getColumn(0).setMinWidth(0);
        bytecodeTextTable.getColumnModel().getColumn(0).setMaxWidth(30);
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
                constantPoolString = text.substring(0,i);
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
        bytecodeTextTable.clearSelection();
        // Selects the current line
        int currentLineIndex = stackFrames.get(currentStackFrameIndex).getCurrentLineIndex();
        if(currentLineIndex < bytecodeTextTable.getRowCount()){
            setTextTableRows(currentStackFrameIndex);
            bytecodeTextTable.addRowSelectionInterval(currentLineIndex, currentLineIndex);   
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
        helpFrameTextArea = new javax.swing.JTextArea();
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
        tutorialFrame = new javax.swing.JFrame();
        tutorialPanel = new javax.swing.JPanel();
        tutorialFrameTitle = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        tutorialFrameTextArea = new javax.swing.JTextArea();
        jScrollPane11 = new javax.swing.JScrollPane();
        tutorialTree = new javax.swing.JTree();
        tutorialFrameConfirmBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        FilePanel = new javax.swing.JPanel();
        fileBtn = new javax.swing.JButton();
        tutorialBtn = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        RunPanel = new javax.swing.JPanel();
        RunButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        StackButton = new javax.swing.JButton();
        resetBtn = new javax.swing.JButton();
        LeftPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        methodTree = new javax.swing.JTree();
        breakPointPanel = new javax.swing.JPanel();
        selectBPButton = new javax.swing.JButton();
        runToBPButton = new javax.swing.JButton();
        JScrollPanelBP = new javax.swing.JScrollPane();
        breakPointTextArea = new javax.swing.JTextArea();
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
        mainPane = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        textJavaTable = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        bytecodeTextTable = new javax.swing.JTable();

        stackFrame.setTitle("Stack");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Main", "Method B" }));

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
                "Operand Stack", "Local Variable Array", "Frame Data"
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

        helpFrameTextArea.setColumns(20);
        helpFrameTextArea.setRows(5);
        helpFrameTextArea.setText("Opcode in hex: 00\nOpcode in binary: 0000 0000\nParamters: None\nEffect on Stack: None\nDescription: Performs no operation\n");
        jScrollPane15.setViewportView(helpFrameTextArea);

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
        helpFrameTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                helpFrameTreeValueChanged(evt);
            }
        });
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
                        .addComponent(jScrollPane16)))
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
        tutorialTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                tutorialTreeValueChanged(evt);
            }
        });
        jScrollPane11.setViewportView(tutorialTree);

        tutorialFrameConfirmBtn.setText("Confirm Example");
        tutorialFrameConfirmBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialFrameConfirmBtnActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout tutorialPanelLayout = new javax.swing.GroupLayout(tutorialPanel);
        tutorialPanel.setLayout(tutorialPanelLayout);
        tutorialPanelLayout.setHorizontalGroup(
            tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tutorialPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(tutorialFrameConfirmBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tutorialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tutorialPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(tutorialPanelLayout.createSequentialGroup()
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
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
                    .addGroup(tutorialPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tutorialFrameConfirmBtn)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        resetBtn.setText("Reset");
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
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
                .addComponent(resetBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StackButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RunPanelLayout.setVerticalGroup(
            RunPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(RunPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(RunPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RunButton)
                    .addComponent(nextButton)
                    .addComponent(StackButton)
                    .addComponent(resetBtn))
                .addContainerGap())
        );

        LeftPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 1, new java.awt.Color(0, 0, 0)));

        methodTree.setBackground(new java.awt.Color(242, 242, 242));
        methodTree.setBorder(null);
        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("-");
        methodTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        methodTree.setAutoscrolls(true);
        methodTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                methodTreeValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(methodTree);

        breakPointPanel.setBackground(new java.awt.Color(204, 204, 204));

        selectBPButton.setText("Select BreakPoint");
        selectBPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBPButtonActionPerformed(evt);
            }
        });

        runToBPButton.setText("Run To BreakPoint");
        runToBPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runToBPButtonActionPerformed(evt);
            }
        });

        breakPointTextArea.setColumns(20);
        breakPointTextArea.setRows(5);
        JScrollPanelBP.setViewportView(breakPointTextArea);

        javax.swing.GroupLayout breakPointPanelLayout = new javax.swing.GroupLayout(breakPointPanel);
        breakPointPanel.setLayout(breakPointPanelLayout);
        breakPointPanelLayout.setHorizontalGroup(
            breakPointPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(breakPointPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(breakPointPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(JScrollPanelBP, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(selectBPButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(runToBPButton, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        breakPointPanelLayout.setVerticalGroup(
            breakPointPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(breakPointPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectBPButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(runToBPButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JScrollPanelBP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout LeftPanelLayout = new javax.swing.GroupLayout(LeftPanel);
        LeftPanel.setLayout(LeftPanelLayout);
        LeftPanelLayout.setHorizontalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftPanelLayout.createSequentialGroup()
                .addGroup(LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LeftPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(breakPointPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        LeftPanelLayout.setVerticalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(breakPointPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
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

        mainPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainPaneStateChanged(evt);
            }
        });

        textJavaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        textJavaTable.setGridColor(new java.awt.Color(242, 242, 242));
        jScrollPane3.setViewportView(textJavaTable);

        mainPane.addTab("Java", jScrollPane3);

        bytecodeTextTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        bytecodeTextTable.setGridColor(new java.awt.Color(242, 242, 242));
        jScrollPane7.setViewportView(bytecodeTextTable);

        mainPane.addTab("Bytecode", jScrollPane7);

        javax.swing.GroupLayout CentrePanelLayout = new javax.swing.GroupLayout(CentrePanel);
        CentrePanel.setLayout(CentrePanelLayout);
        CentrePanelLayout.setHorizontalGroup(
            CentrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(terminalPane)
            .addComponent(mainPane, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
        );
        CentrePanelLayout.setVerticalGroup(
            CentrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CentrePanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(mainPane, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(terminalPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(FilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(RunPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(LeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addGap(0, 0, Short.MAX_VALUE))))
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
        stackFrame.setSize(330,380);
        stackFrame.setTitle("Stack");
    }//GEN-LAST:event_StackButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        helpFrame.setVisible(true);
        helpFrame.setSize(500,500);
        helpFrame.setTitle("Manual");
    }//GEN-LAST:event_helpButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if(stackFrames == null){
            errorPopUp("Please Select a Java File First");
        } else{   
            highlightCurrentLine();
            if(stackFrames.get(currentStackFrameIndex).getFinished()){
                currentStackFrameIndex++;
                if(currentStackFrameIndex >= stackFrames.size())
                    errorPopUp("The end of the program has been reached");
            } else {
                stackFrames.get(currentStackFrameIndex).runInstructions(); 
            }
            updateStackTable();
        }
    }//GEN-LAST:event_nextButtonActionPerformed
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
        tutorialFrame.setSize(500, 500);
        tutorialFrame.setTitle("Tutorial");
    }//GEN-LAST:event_tutorialBtnActionPerformed

    private void tutorialFrameConfirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialFrameConfirmBtnActionPerformed
        int selectedRow = tutorialTree.getSelectionRows()[0];
        File f = new File("AddIntegers.java");       
        if(selectedRow==1){
            f = new File("AddIntegers.java");
        }
        if(selectedRow==2){
            f = new File("BinToDec.java");              
        }
        if(selectedRow==3){
            f = new File("CheckPrime.java");           
        }
        if(selectedRow==4){
            f = new File("Fibonacci.java");            
        }
        if(selectedRow==5){
            f = new File("ReverseNumber.java");             
        }
        String filePath = f.getAbsolutePath();
        String fileName = f.getName(); 
        int dotIndex = fileName.lastIndexOf(".");
        String shortName = fileName.substring(0, dotIndex);
        String extension = fileName.substring(dotIndex + 1);
        
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
                errorPopUp("Example selected successfully");
            } else {
                errorPopUp("Compilation Failed!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }           

    }//GEN-LAST:event_tutorialFrameConfirmBtnActionPerformed

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

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        initClassComponents();
    }//GEN-LAST:event_resetBtnActionPerformed

    private void tutorialTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_tutorialTreeValueChanged
        TreePath path = tutorialTree.getSelectionPath();
        Object node = path.getLastPathComponent();
        String nodeString = node.toString();
        if(nodeString.compareTo("AddIntegers")==0){
            tutorialFrameTextArea.setText(TutorialFilesConstants.AddIntegersText);
        }
        else if(nodeString.compareTo("BinToDecimal")==0){
            tutorialFrameTextArea.setText(TutorialFilesConstants.BinToDecText);
        }
        else if(nodeString.compareTo("CheckPrime")==0){
            tutorialFrameTextArea.setText(TutorialFilesConstants.CheckPrimeText);
        }
        else if(nodeString.compareTo("Fibonacci")==0){
            tutorialFrameTextArea.setText(TutorialFilesConstants.FibonacciText);
        }
        else if(nodeString.compareTo("ReverseNumber")==0){
            tutorialFrameTextArea.setText(TutorialFilesConstants.ReverseNumberText);
        }
    }//GEN-LAST:event_tutorialTreeValueChanged

    private void helpFrameTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_helpFrameTreeValueChanged
        TreePath path = helpFrameTree.getSelectionPath();
        String value = path.getLastPathComponent().toString();
        int index = value.indexOf(".");
        value = value.substring(index+2);
        System.out.println(value);
        if(value.compareTo("nop") == 0){
            helpFrameTitle.setText("nop");
            helpFrameTextArea.setText(HelpFilesConstants.nop);
        } else if(value.compareTo("aconst_null") == 0){
            helpFrameTitle.setText("aconst_null");
            helpFrameTextArea.setText(HelpFilesConstants.aconst_null);
        } else if(value.compareTo("iconst_m1") == 0){
            helpFrameTitle.setText("iconst_m1");
            helpFrameTextArea.setText(HelpFilesConstants.iconst_m1);
        } else if(value.compareTo("iconst_0") == 0){
            helpFrameTitle.setText("iconst_0");
            helpFrameTextArea.setText(HelpFilesConstants.iconst_0);
        } else if(value.compareTo("iconst_1") == 0){
            helpFrameTitle.setText("iconst_1");
            helpFrameTextArea.setText(HelpFilesConstants.iconst_1);
        } else if(value.compareTo("iconst_2") == 0){
            helpFrameTitle.setText("iconst_2");
            helpFrameTextArea.setText(HelpFilesConstants.iconst_2);
        } else if(value.compareTo("iconst_3") == 0){
            helpFrameTitle.setText("iconst_3");
            helpFrameTextArea.setText(HelpFilesConstants.iconst_3);
        } else if(value.compareTo("iconst_4") == 0){
            helpFrameTitle.setText("iconst_4");
            helpFrameTextArea.setText(HelpFilesConstants.iconst_4);
        } else if(value.compareTo("iconst_5") == 0){
            helpFrameTitle.setText("iconst_5");
            helpFrameTextArea.setText(HelpFilesConstants.iconst_5);
        } else if(value.compareTo("lconst_0") == 0){
            helpFrameTitle.setText("lconst_0");
            helpFrameTextArea.setText(HelpFilesConstants.lconst_0);
        } else if(value.compareTo("lconst_1") == 0){
            helpFrameTitle.setText("lconst_1");
            helpFrameTextArea.setText(HelpFilesConstants.lconst_1);
        } else if(value.compareTo("fconst_0") == 0){
            helpFrameTitle.setText("fconst_0");
            helpFrameTextArea.setText(HelpFilesConstants.fconst_0);
        } else if(value.compareTo("fconst_1") == 0){
            helpFrameTitle.setText("fconst_1");
            helpFrameTextArea.setText(HelpFilesConstants.fconst_1);
        } else if(value.compareTo("fconst_2") == 0){
            helpFrameTitle.setText("fconst_2");
            helpFrameTextArea.setText(HelpFilesConstants.fconst_2);
        } else if(value.compareTo("dconst_0") == 0){
            helpFrameTitle.setText("dconst_0");
            helpFrameTextArea.setText(HelpFilesConstants.dconst_0);
        } else if(value.compareTo("dconst_1") == 0){
            helpFrameTitle.setText("dconst_1");
            helpFrameTextArea.setText(HelpFilesConstants.dconst_1);
        } else if(value.compareTo("bipush") == 0){
            helpFrameTitle.setText("bipush");
            helpFrameTextArea.setText(HelpFilesConstants.bipush);
        } else if(value.compareTo("sipush") == 0){
            helpFrameTitle.setText("sipush");
            helpFrameTextArea.setText(HelpFilesConstants.sipush);
        } else if(value.compareTo("ldc") == 0){
            helpFrameTitle.setText("ldc");
            helpFrameTextArea.setText(HelpFilesConstants.ldc);
        } else if(value.compareTo("ldc_w") == 0){
            helpFrameTitle.setText("ldc_w");
            helpFrameTextArea.setText(HelpFilesConstants.ldc_w);
        } else if(value.compareTo("ldc2_w") == 0){
            helpFrameTitle.setText("ldc2_w");
            helpFrameTextArea.setText(HelpFilesConstants.ldc2_w);
        } else if(value.compareTo("iload") == 0){
            helpFrameTitle.setText("iload");
            helpFrameTextArea.setText(HelpFilesConstants.iload);
        } else if(value.compareTo("lload") == 0){
            helpFrameTitle.setText("lload");
            helpFrameTextArea.setText(HelpFilesConstants.lload);
        } else if(value.compareTo("fload") == 0){
            helpFrameTitle.setText("fload");
            helpFrameTextArea.setText(HelpFilesConstants.fload);
        } else if(value.compareTo("dload") == 0){
            helpFrameTitle.setText("dload");
            helpFrameTextArea.setText(HelpFilesConstants.dload);
        } else if(value.compareTo("aload") == 0){
            helpFrameTitle.setText("aload");
            helpFrameTextArea.setText(HelpFilesConstants.aload);
        } else if(value.compareTo("iload_0") == 0){
            helpFrameTitle.setText("iload_0");
            helpFrameTextArea.setText(HelpFilesConstants.iload_0);
        } else if(value.compareTo("iload_1") == 0){
            helpFrameTitle.setText("iload_1");
            helpFrameTextArea.setText(HelpFilesConstants.iload_1);
        } else if(value.compareTo("iload_2") == 0){
            helpFrameTitle.setText("iload_2");
            helpFrameTextArea.setText(HelpFilesConstants.iload_2);
        } else if(value.compareTo("iload_3") == 0){
            helpFrameTitle.setText("iload_3");
            helpFrameTextArea.setText(HelpFilesConstants.iload_3);
        } else if(value.compareTo("lload_0") == 0){
            helpFrameTitle.setText("lload_0");
            helpFrameTextArea.setText(HelpFilesConstants.lload_0);
        } else if(value.compareTo("lload_1") == 0){
            helpFrameTitle.setText("lload_1");
            helpFrameTextArea.setText(HelpFilesConstants.lload_1);
        } else if(value.compareTo("lload_2") == 0){
            helpFrameTitle.setText("lload_2");
            helpFrameTextArea.setText(HelpFilesConstants.lload_2);
        } else if(value.compareTo("lload_3") == 0){
            helpFrameTitle.setText("lload_3");
            helpFrameTextArea.setText(HelpFilesConstants.lload_3);
        } else if(value.compareTo("fload_0") == 0){
            helpFrameTitle.setText("fload_0");
            helpFrameTextArea.setText(HelpFilesConstants.fload_0);
        } else if(value.compareTo("fload_1") == 0){
            helpFrameTitle.setText("fload_1");
            helpFrameTextArea.setText(HelpFilesConstants.fload_1);
        } else if(value.compareTo("fload_2") == 0){
            helpFrameTitle.setText("fload_2");
            helpFrameTextArea.setText(HelpFilesConstants.fload_2);
        } else if(value.compareTo("fload_3") == 0){
            helpFrameTitle.setText("fload_3");
            helpFrameTextArea.setText(HelpFilesConstants.fload_3);
        } else if(value.compareTo("dload_0") == 0){
            helpFrameTitle.setText("dload_0");
            helpFrameTextArea.setText(HelpFilesConstants.dload_0);
        } else if(value.compareTo("dload_1") == 0){
            helpFrameTitle.setText("dload_1");
            helpFrameTextArea.setText(HelpFilesConstants.dload_1);
        } else if(value.compareTo("dload_2") == 0){
            helpFrameTitle.setText("dload_2");
            helpFrameTextArea.setText(HelpFilesConstants.dload_2);
        } else if(value.compareTo("dload_3") == 0){
            helpFrameTitle.setText("dload_3");
            helpFrameTextArea.setText(HelpFilesConstants.dload_3);
        } else if(value.compareTo("aload_0") == 0){
            helpFrameTitle.setText("aload_0");
            helpFrameTextArea.setText(HelpFilesConstants.aload_0);
        } else if(value.compareTo("aload_1") == 0){
            helpFrameTitle.setText("aload_1");
            helpFrameTextArea.setText(HelpFilesConstants.aload_1);
        } else if(value.compareTo("aload_2") == 0){
            helpFrameTitle.setText("aload_2");
            helpFrameTextArea.setText(HelpFilesConstants.aload_2);
        } else if(value.compareTo("aload_3") == 0){
            helpFrameTitle.setText("aload_3");
            helpFrameTextArea.setText(HelpFilesConstants.aload_3);
        } else if(value.compareTo("iaload") == 0){
            helpFrameTitle.setText("iaload");
            helpFrameTextArea.setText(HelpFilesConstants.iaload);
        } else if(value.compareTo("laload") == 0){
            helpFrameTitle.setText("laload");
            helpFrameTextArea.setText(HelpFilesConstants.laload);
        } else if(value.compareTo("faload") == 0){
            helpFrameTitle.setText("faload");
            helpFrameTextArea.setText(HelpFilesConstants.faload);
        } else if(value.compareTo("daload") == 0){
            helpFrameTitle.setText("daload");
            helpFrameTextArea.setText(HelpFilesConstants.daload);
        } else if(value.compareTo("aaload") == 0){
            helpFrameTitle.setText("aaload");
            helpFrameTextArea.setText(HelpFilesConstants.aaload);
        } else if(value.compareTo("baload") == 0){
            helpFrameTitle.setText("baload");
            helpFrameTextArea.setText(HelpFilesConstants.baload);
        } else if(value.compareTo("caload") == 0){
            helpFrameTitle.setText("caload");
            helpFrameTextArea.setText(HelpFilesConstants.caload);
        } else if(value.compareTo("saload") == 0){
            helpFrameTitle.setText("saload");
            helpFrameTextArea.setText(HelpFilesConstants.saload);
        } else if(value.compareTo("istore") == 0){
            helpFrameTitle.setText("istore");
            helpFrameTextArea.setText(HelpFilesConstants.istore);
        } else if(value.compareTo("lstore") == 0){
            helpFrameTitle.setText("lstore");
            helpFrameTextArea.setText(HelpFilesConstants.lstore);
        } else if(value.compareTo("fstore") == 0){
            helpFrameTitle.setText("fstore");
            helpFrameTextArea.setText(HelpFilesConstants.fstore);
        } else if(value.compareTo("dstore") == 0){
            helpFrameTitle.setText("dstore");
            helpFrameTextArea.setText(HelpFilesConstants.dstore);
        } else if(value.compareTo("astore") == 0){
            helpFrameTitle.setText("astore");
            helpFrameTextArea.setText(HelpFilesConstants.astore);
        } else if(value.compareTo("istore_0") == 0){
            helpFrameTitle.setText("istore_0");
            helpFrameTextArea.setText(HelpFilesConstants.istore_0);
        } else if(value.compareTo("istore_1") == 0){
            helpFrameTitle.setText("istore_1");
            helpFrameTextArea.setText(HelpFilesConstants.istore_1);
        } else if(value.compareTo("istore_2") == 0){
            helpFrameTitle.setText("istore_2");
            helpFrameTextArea.setText(HelpFilesConstants.istore_2);
        } else if(value.compareTo("istore_3") == 0){
            helpFrameTitle.setText("istore_3");
            helpFrameTextArea.setText(HelpFilesConstants.istore_3);
        } else if(value.compareTo("lstore_0") == 0){
            helpFrameTitle.setText("lstore_0");
            helpFrameTextArea.setText(HelpFilesConstants.lstore_0);
        } else if(value.compareTo("lstore_1") == 0){
            helpFrameTitle.setText("lstore_1");
            helpFrameTextArea.setText(HelpFilesConstants.lstore_1);
        } else if(value.compareTo("lstore_2") == 0){
            helpFrameTitle.setText("lstore_2");
            helpFrameTextArea.setText(HelpFilesConstants.lstore_2);
        } else if(value.compareTo("lstore_3") == 0){
            helpFrameTitle.setText("lstore_3");
            helpFrameTextArea.setText(HelpFilesConstants.lstore_3);
        } else if(value.compareTo("fstore_0") == 0){
            helpFrameTitle.setText("fstore_0");
            helpFrameTextArea.setText(HelpFilesConstants.fstore_0);
        } else if(value.compareTo("fstore_1") == 0){
            helpFrameTitle.setText("fstore_1");
            helpFrameTextArea.setText(HelpFilesConstants.fstore_1);
        } else if(value.compareTo("fstore_2") == 0){
            helpFrameTitle.setText("fstore_2");
            helpFrameTextArea.setText(HelpFilesConstants.fstore_2);
        } else if(value.compareTo("fstore_3") == 0){
            helpFrameTitle.setText("fstore_3");
            helpFrameTextArea.setText(HelpFilesConstants.fstore_3);
        } else if(value.compareTo("dstore_0") == 0){
            helpFrameTitle.setText("dstore_0");
            helpFrameTextArea.setText(HelpFilesConstants.dstore_0);
        } else if(value.compareTo("dstore_1") == 0){
            helpFrameTitle.setText("dstore_1");
            helpFrameTextArea.setText(HelpFilesConstants.dstore_1);
        } else if(value.compareTo("dstore_2") == 0){
            helpFrameTitle.setText("dstore_2");
            helpFrameTextArea.setText(HelpFilesConstants.dstore_2);
        } else if(value.compareTo("dstore_3") == 0){
            helpFrameTitle.setText("dstore_3");
            helpFrameTextArea.setText(HelpFilesConstants.dstore_3);
        } else if(value.compareTo("astore_0") == 0){
            helpFrameTitle.setText("astore_0");
            helpFrameTextArea.setText(HelpFilesConstants.astore_0);
        } else if(value.compareTo("astore_1") == 0){
            helpFrameTitle.setText("astore_1");
            helpFrameTextArea.setText(HelpFilesConstants.astore_1);
        } else if(value.compareTo("astore_2") == 0){
            helpFrameTitle.setText("astore_2");
            helpFrameTextArea.setText(HelpFilesConstants.astore_2);
        } else if(value.compareTo("astore_3") == 0){
            helpFrameTitle.setText("astore_3");
            helpFrameTextArea.setText(HelpFilesConstants.astore_3);
        } else if(value.compareTo("iastore") == 0){
            helpFrameTitle.setText("iastore");
            helpFrameTextArea.setText(HelpFilesConstants.iastore);
        } else if(value.compareTo("lastore") == 0){
            helpFrameTitle.setText("lastore");
            helpFrameTextArea.setText(HelpFilesConstants.lastore);
        } else if(value.compareTo("fastore") == 0){
            helpFrameTitle.setText("fastore");
            helpFrameTextArea.setText(HelpFilesConstants.fastore);
        } else if(value.compareTo("dastore") == 0){
            helpFrameTitle.setText("dastore");
            helpFrameTextArea.setText(HelpFilesConstants.dastore);
        } else if(value.compareTo("aastore") == 0){
            helpFrameTitle.setText("aastore");
            helpFrameTextArea.setText(HelpFilesConstants.aastore);
        } else if(value.compareTo("bastore") == 0){
            helpFrameTitle.setText("bastore");
            helpFrameTextArea.setText(HelpFilesConstants.bastore);
        } else if(value.compareTo("castore") == 0){
            helpFrameTitle.setText("castore");
            helpFrameTextArea.setText(HelpFilesConstants.castore);
        } else if(value.compareTo("sastore") == 0){
            helpFrameTitle.setText("sastore");
            helpFrameTextArea.setText(HelpFilesConstants.sastore);
        } else if(value.compareTo("pop") == 0){
            helpFrameTitle.setText("pop");
            helpFrameTextArea.setText(HelpFilesConstants.pop);
        } else if(value.compareTo("pop2") == 0){
            helpFrameTitle.setText("pop2");
            helpFrameTextArea.setText(HelpFilesConstants.pop2);
        } else if(value.compareTo("dup") == 0){
            helpFrameTitle.setText("dup");
            helpFrameTextArea.setText(HelpFilesConstants.dup);
        } else if(value.compareTo("dup_x1") == 0){
            helpFrameTitle.setText("dup_x1");
            helpFrameTextArea.setText(HelpFilesConstants.dup_x1);
        } else if(value.compareTo("dup_x2") == 0){
            helpFrameTitle.setText("dup_x2");
            helpFrameTextArea.setText(HelpFilesConstants.dup_x2);
        } else if(value.compareTo("dup2") == 0){
            helpFrameTitle.setText("dup2");
            helpFrameTextArea.setText(HelpFilesConstants.dup2);
        } else if(value.compareTo("dup2_x1") == 0){
            helpFrameTitle.setText("dup2_x1");
            helpFrameTextArea.setText(HelpFilesConstants.dup2_x1);
        } else if(value.compareTo("dup2_x2") == 0){
            helpFrameTitle.setText("dup2_x2");
            helpFrameTextArea.setText(HelpFilesConstants.dup2_x2);
        } else if(value.compareTo("swap") == 0){
            helpFrameTitle.setText("swap");
            helpFrameTextArea.setText(HelpFilesConstants.swap);
        } else if(value.compareTo("iadd") == 0){
            helpFrameTitle.setText("iadd");
            helpFrameTextArea.setText(HelpFilesConstants.iadd);
        } else if(value.compareTo("ladd") == 0){
            helpFrameTitle.setText("ladd");
            helpFrameTextArea.setText(HelpFilesConstants.ladd);
        } else if(value.compareTo("fadd") == 0){
            helpFrameTitle.setText("fadd");
            helpFrameTextArea.setText(HelpFilesConstants.fadd);
        } else if(value.compareTo("dadd") == 0){
            helpFrameTitle.setText("dadd");
            helpFrameTextArea.setText(HelpFilesConstants.dadd);
        } else if(value.compareTo("isub") == 0){
            helpFrameTitle.setText("isub");
            helpFrameTextArea.setText(HelpFilesConstants.isub);
        } else if(value.compareTo("lsub") == 0){
            helpFrameTitle.setText("lsub");
            helpFrameTextArea.setText(HelpFilesConstants.lsub);
        } else if(value.compareTo("fsub") == 0){
            helpFrameTitle.setText("fsub");
            helpFrameTextArea.setText(HelpFilesConstants.fsub);
        } else if(value.compareTo("dsub") == 0){
            helpFrameTitle.setText("dsub");
            helpFrameTextArea.setText(HelpFilesConstants.dsub);
        } else if(value.compareTo("imul	") == 0){
            helpFrameTitle.setText("imul");
            helpFrameTextArea.setText(HelpFilesConstants.imul);
        } else if(value.compareTo("lmul") == 0){
            helpFrameTitle.setText("lmul");
            helpFrameTextArea.setText(HelpFilesConstants.lmul);
        } else if(value.compareTo("fmul") == 0){
            helpFrameTitle.setText("fmul");
            helpFrameTextArea.setText(HelpFilesConstants.fmul);
        } else if(value.compareTo("dmul") == 0){
            helpFrameTitle.setText("dmul");
            helpFrameTextArea.setText(HelpFilesConstants.dmul);
        } else if(value.compareTo("idiv") == 0){
            helpFrameTitle.setText("idiv");
            helpFrameTextArea.setText(HelpFilesConstants.idiv);
        } else if(value.compareTo("ldiv") == 0){
            helpFrameTitle.setText("ldiv");
            helpFrameTextArea.setText(HelpFilesConstants.ldiv);
        } else if(value.compareTo("fdiv") == 0){
            helpFrameTitle.setText("fdiv");
            helpFrameTextArea.setText(HelpFilesConstants.fdiv);
        } else if(value.compareTo("ddiv") == 0){
            helpFrameTitle.setText("ddiv");
            helpFrameTextArea.setText(HelpFilesConstants.ddiv);
        } else if(value.compareTo("irem") == 0){
            helpFrameTitle.setText("irem");
            helpFrameTextArea.setText(HelpFilesConstants.irem);
        } else if(value.compareTo("lrem") == 0){
            helpFrameTitle.setText("lrem");
            helpFrameTextArea.setText(HelpFilesConstants.lrem);
        } else if(value.compareTo("frem") == 0){
            helpFrameTitle.setText("frem");
            helpFrameTextArea.setText(HelpFilesConstants.frem);
        } else if(value.compareTo("drem") == 0){
            helpFrameTitle.setText("drem");
            helpFrameTextArea.setText(HelpFilesConstants.drem);
        } else if(value.compareTo("ineg") == 0){
            helpFrameTitle.setText("ineg");
            helpFrameTextArea.setText(HelpFilesConstants.ineg);
        } else if(value.compareTo("lneg") == 0){
            helpFrameTitle.setText("lneg");
            helpFrameTextArea.setText(HelpFilesConstants.lneg);
        } else if(value.compareTo("fneg") == 0){
            helpFrameTitle.setText("fneg");
            helpFrameTextArea.setText(HelpFilesConstants.fneg);
        } else if(value.compareTo("dneg") == 0){
            helpFrameTitle.setText("dneg");
            helpFrameTextArea.setText(HelpFilesConstants.dneg);
        } else if(value.compareTo("ishl") == 0){
            helpFrameTitle.setText("ishl");
            helpFrameTextArea.setText(HelpFilesConstants.ishl);
        } else if(value.compareTo("lshl") == 0){
            helpFrameTitle.setText("lshl");
            helpFrameTextArea.setText(HelpFilesConstants.lshl);
        } else if(value.compareTo("ishr") == 0){
            helpFrameTitle.setText("ishr");
            helpFrameTextArea.setText(HelpFilesConstants.ishr);
        } else if(value.compareTo("lshr") == 0){
            helpFrameTitle.setText("lshr");
            helpFrameTextArea.setText(HelpFilesConstants.lshr);
        } else if(value.compareTo("iushr") == 0){
            helpFrameTitle.setText("iushr");
            helpFrameTextArea.setText(HelpFilesConstants.iushr);
        } else if(value.compareTo("lushr") == 0){
            helpFrameTitle.setText("lushr");
            helpFrameTextArea.setText(HelpFilesConstants.lushr);
        } else if(value.compareTo("iand") == 0){
            helpFrameTitle.setText("iand");
            helpFrameTextArea.setText(HelpFilesConstants.iand);
        } else if(value.compareTo("land") == 0){
            helpFrameTitle.setText("land");
            helpFrameTextArea.setText(HelpFilesConstants.land);
        } else if(value.compareTo("ior") == 0){
            helpFrameTitle.setText("ior");
            helpFrameTextArea.setText(HelpFilesConstants.ior);
        } else if(value.compareTo("lor") == 0){
            helpFrameTitle.setText("lor");
            helpFrameTextArea.setText(HelpFilesConstants.lor);
        } else if(value.compareTo("ixor") == 0){
            helpFrameTitle.setText("ixor");
            helpFrameTextArea.setText(HelpFilesConstants.ixor);
        } else if(value.compareTo("lxor") == 0){
            helpFrameTitle.setText("lxor");
            helpFrameTextArea.setText(HelpFilesConstants.lxor);
        } else if(value.compareTo("iinc") == 0){
            helpFrameTitle.setText("iinc");
            helpFrameTextArea.setText(HelpFilesConstants.iinc);
        } else if(value.compareTo("i2l") == 0){
            helpFrameTitle.setText("i2l");
            helpFrameTextArea.setText(HelpFilesConstants.i2l);
        } else if(value.compareTo("i2f") == 0){
            helpFrameTitle.setText("i2f");
            helpFrameTextArea.setText(HelpFilesConstants.i2f);
        } else if(value.compareTo("i2d") == 0){
            helpFrameTitle.setText("i2d");
            helpFrameTextArea.setText(HelpFilesConstants.i2d);
        } else if(value.compareTo("l2i") == 0){
            helpFrameTitle.setText("l2i");
            helpFrameTextArea.setText(HelpFilesConstants.l2i);
        } else if(value.compareTo("l2f") == 0){
            helpFrameTitle.setText("l2f");
            helpFrameTextArea.setText(HelpFilesConstants.l2f);
        } else if(value.compareTo("l2d") == 0){
            helpFrameTitle.setText("l2d");
            helpFrameTextArea.setText(HelpFilesConstants.l2d);
        } else if(value.compareTo("f2i") == 0){
            helpFrameTitle.setText("f2i");
            helpFrameTextArea.setText(HelpFilesConstants.f2i);
        } else if(value.compareTo("f2l") == 0){
            helpFrameTitle.setText("f2l");
            helpFrameTextArea.setText(HelpFilesConstants.f2l);
        } else if(value.compareTo("f2d") == 0){
            helpFrameTitle.setText("f2d");
            helpFrameTextArea.setText(HelpFilesConstants.f2d);
        } else if(value.compareTo("d2i") == 0){
            helpFrameTitle.setText("d2i");
            helpFrameTextArea.setText(HelpFilesConstants.d2i);
        } else if(value.compareTo("d2l") == 0){
            helpFrameTitle.setText("d2l");
            helpFrameTextArea.setText(HelpFilesConstants.d2l);
        } else if(value.compareTo("d2f") == 0){
            helpFrameTitle.setText("d2f");
            helpFrameTextArea.setText(HelpFilesConstants.d2f);
        } else if(value.compareTo("i2b") == 0){
            helpFrameTitle.setText("i2b");
            helpFrameTextArea.setText(HelpFilesConstants.i2b);
        } else if(value.compareTo("i2c") == 0){
            helpFrameTitle.setText("i2c");
            helpFrameTextArea.setText(HelpFilesConstants.i2c);
        } else if(value.compareTo("i2s") == 0){
            helpFrameTitle.setText("i2s");
            helpFrameTextArea.setText(HelpFilesConstants.i2s);
        } else if(value.compareTo("lcmp") == 0){
            helpFrameTitle.setText("lcmp");
            helpFrameTextArea.setText(HelpFilesConstants.lcmp);
        } else if(value.compareTo("fcmpl") == 0){
            helpFrameTitle.setText("fcmpl");
            helpFrameTextArea.setText(HelpFilesConstants.fcmpl);
        } else if(value.compareTo("fcmpg") == 0){
            helpFrameTitle.setText("fcmpg");
            helpFrameTextArea.setText(HelpFilesConstants.fcmpg);
        } else if(value.compareTo("dcmpl") == 0){
            helpFrameTitle.setText("dcmpl");
            helpFrameTextArea.setText(HelpFilesConstants.dcmpl);
        } else if(value.compareTo("dcmpg") == 0){
            helpFrameTitle.setText("dcmpg");
            helpFrameTextArea.setText(HelpFilesConstants.dcmpg);
        } else if(value.compareTo("ifeq") == 0){
            helpFrameTitle.setText("ifeq");
            helpFrameTextArea.setText(HelpFilesConstants.ifeq);
        } else if(value.compareTo("ifne") == 0){
            helpFrameTitle.setText("ifne");
            helpFrameTextArea.setText(HelpFilesConstants.ifne);
        } else if(value.compareTo("iflt") == 0){
            helpFrameTitle.setText("iflt");
            helpFrameTextArea.setText(HelpFilesConstants.iflt);
        } else if(value.compareTo("ifge") == 0){
            helpFrameTitle.setText("ifge");
            helpFrameTextArea.setText(HelpFilesConstants.ifge);
        } else if(value.compareTo("ifgt") == 0){
            helpFrameTitle.setText("ifgt");
            helpFrameTextArea.setText(HelpFilesConstants.ifgt);
        } else if(value.compareTo("ifle") == 0){
            helpFrameTitle.setText("ifle");
            helpFrameTextArea.setText(HelpFilesConstants.ifle);
        } else if(value.compareTo("if_icmpeq") == 0){
            helpFrameTitle.setText("if_icmpeq");
            helpFrameTextArea.setText(HelpFilesConstants.if_icmpeq);
        } else if(value.compareTo("if_icmpne") == 0){
            helpFrameTitle.setText("if_icmpne");
            helpFrameTextArea.setText(HelpFilesConstants.if_icmpne);
        } else if(value.compareTo("if_icmplt") == 0){
            helpFrameTitle.setText("if_icmplt");
            helpFrameTextArea.setText(HelpFilesConstants.if_icmplt);
        } else if(value.compareTo("if_icmpge") == 0){
            helpFrameTitle.setText("if_icmpge");
            helpFrameTextArea.setText(HelpFilesConstants.if_icmpge);
        } else if(value.compareTo("if_icmpgt") == 0){
            helpFrameTitle.setText("if_icmpgt");
            helpFrameTextArea.setText(HelpFilesConstants.if_icmpgt);
        } else if(value.compareTo("if_icmple") == 0){
            helpFrameTitle.setText("if_icmple");
            helpFrameTextArea.setText(HelpFilesConstants.if_icmple);
        } else if(value.compareTo("if_acmpeq") == 0){
            helpFrameTitle.setText("if_acmpeq");
            helpFrameTextArea.setText(HelpFilesConstants.if_acmpeq);
        } else if(value.compareTo("if_acmpne") == 0){
            helpFrameTitle.setText("if_acmpne");
            helpFrameTextArea.setText(HelpFilesConstants.if_acmpne);
        } else if(value.compareTo("goto") == 0){
            helpFrameTitle.setText("goto");
            helpFrameTextArea.setText(HelpFilesConstants.gotoText);
        } else if(value.compareTo("jsr") == 0){
            helpFrameTitle.setText("jsr");
            helpFrameTextArea.setText(HelpFilesConstants.jsr);
        } else if(value.compareTo("ret") == 0){
            helpFrameTitle.setText("ret");
            helpFrameTextArea.setText(HelpFilesConstants.ret);
        } else if(value.compareTo("tableswitch") == 0){
            helpFrameTitle.setText("tableswitch");
            helpFrameTextArea.setText(HelpFilesConstants.tableswitch);
        } else if(value.compareTo("lookupswitch") == 0){
            helpFrameTitle.setText("lookupswitch");
            helpFrameTextArea.setText(HelpFilesConstants.lookupswitch);
        } else if(value.compareTo("ireturn") == 0){
            helpFrameTitle.setText("ireturn");
            helpFrameTextArea.setText(HelpFilesConstants.ireturn);
        } else if(value.compareTo("lreturn") == 0){
            helpFrameTitle.setText("lreturn");
            helpFrameTextArea.setText(HelpFilesConstants.lreturn);
        } else if(value.compareTo("freturn") == 0){
            helpFrameTitle.setText("freturn");
            helpFrameTextArea.setText(HelpFilesConstants.freturn);
        } else if(value.compareTo("dreturn") == 0){
            helpFrameTitle.setText("dreturn");
            helpFrameTextArea.setText(HelpFilesConstants.dreturn);
        } else if(value.compareTo("areturn") == 0){
            helpFrameTitle.setText("areturn");
            helpFrameTextArea.setText(HelpFilesConstants.areturn);
        } else if(value.compareTo("return") == 0){
            helpFrameTitle.setText("return");
            helpFrameTextArea.setText(HelpFilesConstants.returnText);
        } else if(value.compareTo("getstatic") == 0){
            helpFrameTitle.setText("getstatic");
            helpFrameTextArea.setText(HelpFilesConstants.getstatic);
        } else if(value.compareTo("putstatic") == 0){
            helpFrameTitle.setText("putstatic");
            helpFrameTextArea.setText(HelpFilesConstants.putstatic);
        } else if(value.compareTo("getfield") == 0){
            helpFrameTitle.setText("getfield");
            helpFrameTextArea.setText(HelpFilesConstants.getfield);
        } else if(value.compareTo("putfield") == 0){
            helpFrameTitle.setText("putfield");
            helpFrameTextArea.setText(HelpFilesConstants.putfield);
        } else if(value.compareTo("invokevirtual") == 0){
            helpFrameTitle.setText("invokevirtual");
            helpFrameTextArea.setText(HelpFilesConstants.invokevirtual);
        } else if(value.compareTo("invokespecial") == 0){
            helpFrameTitle.setText("invokespecial");
            helpFrameTextArea.setText(HelpFilesConstants.invokespecial);
        } else if(value.compareTo("invokestatic") == 0){
            helpFrameTitle.setText("invokestatic");
            helpFrameTextArea.setText(HelpFilesConstants.invokestatic);
        } else if(value.compareTo("invokeinterface") == 0){
            helpFrameTitle.setText("invokeinterface");
            helpFrameTextArea.setText(HelpFilesConstants.invokeinterface);
        } else if(value.compareTo("invokedynamic") == 0){
            helpFrameTitle.setText("invokedynamic");
            helpFrameTextArea.setText(HelpFilesConstants.invokedynamic);
        } else if(value.compareTo("new") == 0){
            helpFrameTitle.setText("new");
            helpFrameTextArea.setText(HelpFilesConstants.newText);
        } else if(value.compareTo("newarray") == 0){
            helpFrameTitle.setText("newarray");
            helpFrameTextArea.setText(HelpFilesConstants.newarray);
        } else if(value.compareTo("anewarray") == 0){
            helpFrameTitle.setText("anewarray");
            helpFrameTextArea.setText(HelpFilesConstants.anewarray);
        } else if(value.compareTo("arraylength") == 0){
            helpFrameTitle.setText("arraylength");
            helpFrameTextArea.setText(HelpFilesConstants.arraylength);
        } else if(value.compareTo("athrow") == 0){
            helpFrameTitle.setText("athrow");
            helpFrameTextArea.setText(HelpFilesConstants.athrow);
        } else if(value.compareTo("checkcast") == 0){
            helpFrameTitle.setText("checkcast");
            helpFrameTextArea.setText(HelpFilesConstants.checkcast);
        } else if(value.compareTo("instanceof") == 0){
            helpFrameTitle.setText("instanceof");
            helpFrameTextArea.setText(HelpFilesConstants.instanceofText);
        } else if(value.compareTo("monitorenter") == 0){
            helpFrameTitle.setText("monitorenter");
            helpFrameTextArea.setText(HelpFilesConstants.monitorenter);
        } else if(value.compareTo("monitorexit") == 0){
            helpFrameTitle.setText("monitorexit");
            helpFrameTextArea.setText(HelpFilesConstants.monitorexit);
        } else if(value.compareTo("wide") == 0){
            helpFrameTitle.setText("wide");
            helpFrameTextArea.setText(HelpFilesConstants.wide);
        } else if(value.compareTo("multianewarray") == 0){
            helpFrameTitle.setText("multianewarray");
            helpFrameTextArea.setText(HelpFilesConstants.multianewarray);
        } else if(value.compareTo("ifnull") == 0){
            helpFrameTitle.setText("ifnull");
            helpFrameTextArea.setText(HelpFilesConstants.ifnull);
        } else if(value.compareTo("ifnonnull") == 0){
            helpFrameTitle.setText("ifnonnull");
            helpFrameTextArea.setText(HelpFilesConstants.ifnonnull);
        } else if(value.compareTo("goto_w") == 0){
            helpFrameTitle.setText("goto_w");
            helpFrameTextArea.setText(HelpFilesConstants.goto_w);
        } else if(value.compareTo("jsr_w") == 0){
            helpFrameTitle.setText("jsr_w");
            helpFrameTextArea.setText(HelpFilesConstants.jsr_w);
        } else if(value.compareTo("breakpoint") == 0){
            helpFrameTitle.setText("breakpoint");
            helpFrameTextArea.setText(HelpFilesConstants.breakpoint);
        } else if(value.compareTo("impdep1") == 0){
            helpFrameTitle.setText("impdep1");
            helpFrameTextArea.setText(HelpFilesConstants.impdep1);
        } else if(value.compareTo("impdep2") == 0){
            helpFrameTitle.setText("impdep2");
            helpFrameTextArea.setText(HelpFilesConstants.impdep2);
        } 
    }//GEN-LAST:event_helpFrameTreeValueChanged

    private void selectBPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBPButtonActionPerformed
        int line = bytecodeTextTable.getSelectedRow();
        TreePath methodPath = methodTree.getSelectionPath();
        Object[] path = methodPath.getPath();
        int index = methodTree.getModel().getIndexOfChild(path[path.length - 2], path[path.length - 1]);
        breakPoint = new int[2];
        breakPoint[0] = line;
        breakPoint[1] = index;
        String text = "Method: " + methodPath.getLastPathComponent() + "\n" + "Line: " + stackFrames.get(index).keysOfInstructions.get(line);
        breakPointTextArea.setText(text);
    }//GEN-LAST:event_selectBPButtonActionPerformed

    private void mainPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainPaneStateChanged
        int selectedIndex = mainPane.getSelectedIndex();
        if(selectedIndex == 0){
            breakPointPanel.setVisible(false);
        } else if(selectedIndex == 1){
            breakPointPanel.setVisible(true);
            methodTree.setSelectionRow(1);
            bytecodeTextTable.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_mainPaneStateChanged

    private void runToBPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runToBPButtonActionPerformed
        if(breakPoint != null){
            initClassComponents();
            while(stackFrames.get(breakPoint[1]).currentLineIndex != breakPoint[0]){            
                highlightCurrentLine();
                if(stackFrames.get(currentStackFrameIndex).getFinished()){
                    currentStackFrameIndex++;
                    if(currentStackFrameIndex >= stackFrames.size())
                        errorPopUp("The end of the program has been reached");
                } else {
                    stackFrames.get(currentStackFrameIndex).runInstructions(); 
                }
                updateStackTable();
            }
        } else {
            errorPopUp("Please Select a BreakPoint");
        }
    }//GEN-LAST:event_runToBPButtonActionPerformed

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
    private javax.swing.JScrollPane JScrollPanelBP;
    private javax.swing.JPanel LeftPanel;
    private javax.swing.JButton RunButton;
    private javax.swing.JPanel RunPanel;
    private javax.swing.JButton StackButton;
    private javax.swing.JPanel breakPointPanel;
    private javax.swing.JTextArea breakPointTextArea;
    private javax.swing.JTable bytecodeTextTable;
    private javax.swing.JTextArea constPoolTextArea;
    private javax.swing.JButton fileBtn;
    private javax.swing.JButton helpButton;
    private javax.swing.JFrame helpFrame;
    private javax.swing.JTextArea helpFrameTextArea;
    private javax.swing.JTextField helpFrameTitle;
    private javax.swing.JTree helpFrameTree;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTabbedPane mainPane;
    private javax.swing.JTree methodTree;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton resetBtn;
    private javax.swing.JButton runToBPButton;
    private javax.swing.JButton selectBPButton;
    private javax.swing.JFrame settingsFrame;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JFrame stackFrame;
    private javax.swing.JTextArea stackMapTableTextArea;
    private javax.swing.JTable stackTable;
    private javax.swing.JTabbedPane terminalPane;
    private javax.swing.JTextArea terminalTextArea;
    private javax.swing.JTable textJavaTable;
    private javax.swing.JButton tutorialBtn;
    private javax.swing.JFrame tutorialFrame;
    private javax.swing.JButton tutorialFrameConfirmBtn;
    private javax.swing.JTextArea tutorialFrameTextArea;
    private javax.swing.JTextArea tutorialFrameTextArea1;
    private javax.swing.JTextField tutorialFrameTitle;
    private javax.swing.JTextField tutorialFrameTitle1;
    private javax.swing.JPanel tutorialPanel;
    private javax.swing.JTree tutorialTree;
    private javax.swing.JTree tutorialTree1;
    // End of variables declaration//GEN-END:variables
}
