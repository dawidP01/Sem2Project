/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication2;

/**
 *
 * @author C00273530
 */
import java.awt.TextArea;
import java.io.OutputStream;
import java.io.PrintStream;

public class TextAreaPrintStream extends PrintStream {
    private TextArea textArea;

    public TextAreaPrintStream(TextArea textArea, OutputStream out) {
        super(out);
        this.textArea = textArea;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        String message = new String(buf, off, len);
        // Append the message to the TextArea
        textArea.append(message);
    }
}
