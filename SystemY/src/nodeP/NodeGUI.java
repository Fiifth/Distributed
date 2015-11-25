package nodeP;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class NodeGUI {
	public static Object frame;
	public JTextField textField;
	private JTextField textField_1;
	public String nodenaam;
	
	public NodeGUI(){
		
		JFrame nameframe = new JFrame();
		nameframe.setTitle("Node");
		nameframe.getContentPane().setForeground(Color.BLACK);
		nameframe.setResizable(true);
		nameframe.getContentPane().setBackground(Color.WHITE);
		nameframe.setBackground(Color.WHITE);
		nameframe.setBounds(20, 20, 300, 300);
		nameframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nameframe.getContentPane().setLayout(null);
               
        JTextField NN = new JTextField();
        NN.setBounds(142, 80, 132, 20);
        nameframe.getContentPane().add(NN);
        NN.setColumns(10);
        
        JTextPane txtpnGeefDeNodenaam = new JTextPane();
        txtpnGeefDeNodenaam.setText("Geef de nodenaam in:");
        txtpnGeefDeNodenaam.setBounds(10, 80, 132, 20);
        nameframe.getContentPane().add(txtpnGeefDeNodenaam);
        
        
        
        
        
        JFrame nodeframe = new JFrame();
		nodeframe.getContentPane().setForeground(Color.BLACK);
		nodeframe.setResizable(true);
		nodeframe.getContentPane().setBackground(Color.WHITE);
		nodeframe.setBackground(Color.WHITE);
		nodeframe.setBounds(20, 20, 500, 500);
		nodeframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nodeframe.getContentPane().setLayout(null);
        
        JButton btnStartNode = new JButton("START Node");
        btnStartNode.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		nodenaam = NN.getText();
        		if(nodenaam.contains(" "))
        		{
        			JTextField errortext = new JTextField();
        			errortext.setForeground(Color.RED);
        			errortext.setText(nodenaam + " is geen geldige nodenaam.");
        			errortext.setFont(new Font("Tahoma", Font.BOLD, 13));
        			errortext.setBorder(null);
        	        errortext.setBounds(10, 200, 300, 20);
        	        errortext.setColumns(10);        			
        	        nameframe.getContentPane().add(errortext);      			
        		}
        		else
        		{
        			nameframe.setVisible(false);
        			nodeframe.setTitle("Node " + nodenaam);
        			nodeframe.setVisible(true);        			
        		}
        	}
        });
        btnStartNode.setBounds(90, 140, 120, 23);
        nameframe.getContentPane().add(btnStartNode);
        nameframe.setVisible(true);
		
	}
	
	public static void main(String[] args) 
	{
		NodeGUI ngui = new NodeGUI();
		
	}
}
