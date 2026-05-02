package br.ufabc.controlplane.metropad;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;




public class ServidorUI  implements ActionListener  {
  
    
    
    JButton button = new JButton();
    JButton button1 = new JButton();
    JButton button2 = new JButton();
    JTextField textfield = new JTextField();
    JTextField textfield1 = new JTextField();
    JLabel label = new JLabel();

public static void main(String args[]) {
    
    ServidorUI  serverUI = new ServidorUI ();
    Servidor server = Servidor.getInstance();

    JFrame frame = new JFrame();
    frame.setVisible(true); 
    frame.addWindowListener(new WindowAdapter() 
          {
              public void windowClosing(WindowEvent evt)
              { 
                  System.exit(0);
              } 
          });

    frame.setSize(615,400); 
    frame.setTitle( "Servidor - Gerência Padtec"  ); 
    frame.getContentPane().setLayout(null);

    frame.getContentPane().add(serverUI.label);
    serverUI.label.setBounds(50,40,105,25);
    serverUI.label.setText("jLabel");

    frame.getContentPane().add(serverUI.textfield1);
    serverUI.textfield1.setBounds(210,40,140,25);

    frame.getContentPane().add(serverUI.textfield);
    serverUI.textfield.setBounds(30,80,490,85);

    frame.getContentPane().add(serverUI.button);
    serverUI.button.setBounds(80,195,300,35);
    serverUI.button.setText("Desbloquear Supervisor");
    
//    frame.getContentPane().add(serverUI.button1);
//    serverUI.button1.setBounds(185,195,105,35);
//    serverUI.button1.setText("GET NEXT");
//
//    frame.getContentPane().add(serverUI.button2);
//    serverUI.button2.setBounds(290,195,105,35);
//    serverUI.button2.setText("Clear");
    
  
    serverUI.button.addActionListener(serverUI); 
//    serverUI.button1.addActionListener(serverUI); 
//    serverUI.button2.addActionListener(serverUI); 
    
frame.show();
 }
    
    public void actionPerformed(ActionEvent e) {
	
	if (e.getActionCommand().equals("Desbloquear Supervisor")) {
		
		button.setText("Bloquear Supervisor");
//		Servidor.getInstance().sendCommandUnlockSupervisor(1316, 3);
	    
	}
	
//	else if (e.getActionCommand().equals("GET NEXT")) {
//	    
//	    textfield.setText(target.snmpGetNext());
//
//	}
//	
//	else if (e.getActionCommand().equals("Clear")) {
//
//	     textfield.setText("");
//
//	}

    }
    
 } 




