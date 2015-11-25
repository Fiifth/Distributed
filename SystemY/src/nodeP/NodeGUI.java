package nodeP;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class NodeGUI {
	private static Object frame;
	private JTextField textField;
	
	public NodeGUI(){
		
		JFrame frame = new JFrame();
        frame.setTitle("Node");
        frame.getContentPane().setForeground(Color.BLACK);
        frame.setResizable(true);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setBackground(Color.WHITE);
        frame.setBounds(20, 20, 300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        JButton btnStartNode = new JButton("START Node");
        btnStartNode.setBounds(82, 179, 100, 23);
        frame.getContentPane().add(btnStartNode);
        
        textField = new JTextField();
        textField.setBounds(122, 114, 122, 20);
        frame.getContentPane().add(textField);
        textField.setColumns(10);
        
        JTextPane txtpnGeefDeNodenaam = new JTextPane();
        txtpnGeefDeNodenaam.setText("Geef de nodenaam in:");
        txtpnGeefDeNodenaam.setBounds(10, 114, 112, 20);
        frame.getContentPane().add(txtpnGeefDeNodenaam);
		
	}
	
	public static void main(String[] args) 
	{
		NodeGUI ngui = new NodeGUI();
		
		
	}
}
