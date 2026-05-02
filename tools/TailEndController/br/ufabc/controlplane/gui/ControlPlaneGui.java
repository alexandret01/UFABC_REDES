package br.ufabc.controlplane.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.ufabc.controlplane.ControlPlane;

public class ControlPlaneGui implements ActionListener {
	public static final int IPV4 = 4;
	public static final int IPV6 = 6;
	
//	  private static int instanceCount = 1;
	  

	  private ControlPlane controller;

	  private JFrame mainFrame;

	  private JButton exit, clearDisplay, sendMessage;

	  private JTextArea display;

	  private JTextField inputTextField;

//	  private InputChannel inputChannel;

//	  public OutputChannel getOutputChannel() {
//	    return routerClient;
//	  }

	  public ControlPlaneGui() {
	    controller = ControlPlane.getInstance();
	  }

	  public void createGui() {
	    mainFrame = new JFrame("GMPLS Control Plane - GUI ");
	    Container content = mainFrame.getContentPane();
	    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

	    JPanel displayPanel = new JPanel();
	    display = new JTextArea(10, 40);
	    JScrollPane displayArea = new JScrollPane(display);
	    display.setEditable(false);
	    displayPanel.add(displayArea);
	    content.add(displayPanel);

	    JPanel dataPanel = new JPanel();
	    dataPanel.add(new JLabel("Destination Address:"));
	    inputTextField = new JTextField(30);
	    dataPanel.add(inputTextField);
	    content.add(dataPanel);

	    JPanel controlPanel = new JPanel();
	    sendMessage = new JButton("Send Path Message");
	    clearDisplay = new JButton("Clear");
	    exit = new JButton("Exit");
	    controlPanel.add(sendMessage);
	    controlPanel.add(clearDisplay);
	    controlPanel.add(exit);
	    content.add(controlPanel);

	    sendMessage.addActionListener(this);
	    clearDisplay.addActionListener(this);
	    exit.addActionListener(this);
	    inputTextField.addActionListener(this);

	    mainFrame.addWindowListener(new WindowCloseManager());
	    mainFrame.pack();
	    mainFrame.setVisible(true);
	  }

	  public void actionPerformed(ActionEvent evt) {
	    Object source = evt.getSource();
	    if (source == sendMessage) {
	      sendMessage();
	    } else if (source == inputTextField) {
	      sendMessage();
	    } else if (source == clearDisplay) {
	      clearDisplay();
	    } else if (source == exit) {
	      exitApplication();
	    }
	  }

	  private class WindowCloseManager extends WindowAdapter {
	    public void windowClosing(WindowEvent evt) {
	      exitApplication();
	    }
	  }

	  private void exitApplication() {
	    System.exit(0);
	  }

	  private void clearDisplay() {
	    inputTextField.setText("");
	    display.setText("");
	  }

	  private void sendMessage() {
	    String data = inputTextField.getText();
	    System.out.println(data);
	    if (dtv_ValidaIp(data, 4)){
	    	try {
				controller.sendPath(data);
				//	inputTextField.setText("");
			} catch (Exception e) {
				inputTextField.setText(e.getMessage());
				
			}
//	    	inputTextField.setText("");
	    } else {
	    	display.setText("Digite um endereço IPv4 válido");
	    	
	    }
	    
	  }
	  
	  // valida um ip IPV4/IPV6  
	  public static boolean dtv_ValidaIp(String ip, int IPV )  
	  {  
		  String[] ipSplit = {""};  
		  switch(IPV)  
		  {  
		  case IPV4:  
			  //Minimo 1.1.1.1 Máximo 255.255.255.255  
			  if(ip.length()<7 || ip.length()>15)  
				  return false;  
			  ipSplit = ip.split("\\.");
			
			  if(ipSplit.length!=4) 
				  return false;  
			  
			  break;  
		  case IPV6:  
			  //Minimo 1.1.1.1.1.1 Máximo 255.255.255.255.255.255  
			  if(ip.length()<11 || ip.length()>23)  
				  return false;  
			  ipSplit = ip.split("\\.");  
			  if(ipSplit.length!=6)  
				  return false;  
			  break;  
		  }  

		  try  
		  {  
			  for (String ipContext: ipSplit)  
				  if (Integer.parseInt(ipContext)<1 || Integer.parseInt(ipContext)>255)
					  return false;  
				  
		  }  
		  catch(NumberFormatException ex)  
		  {  
			  return false;  
		  }  

		  return true;  
	  }  

//	        public void receiveMessage(Message message) {
//	    display.append(message.getMessage() + "\n");
//	  }
	}