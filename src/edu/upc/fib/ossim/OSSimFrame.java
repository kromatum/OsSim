package edu.upc.fib.ossim;

import java.awt.Component;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.*; 

/**
 * Frame based application. Desktop environments 
 * 
 * @author Alex
 */
public class OSSimFrame extends JFrame implements OSSim { 

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs OSSimFrame, initialize home panel and menu
	 *  
	 */
	public OSSimFrame() { 
		super(); 
		initialize(); 
	} 

	private void initialize() { 
		this.setTitle("OS Sim"); 
		this.setSize(1000,800); //Mida 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		this.setResizable(true);
		AppSession.getInstance().setApp(this);
		Menu menu = new Menu();
		this.setJMenuBar(menu);
		this.setContentPane(new Home(menu));
		this.setVisible(true); // Propietat de visibilitat 
	} 

	/**
	 * Removes previous views and loads new one into container.  
	 * 
	 * @param view	view to load
	 */
	public void loadView(JPanel view) {
		this.getContentPane().removeAll();
		this.setContentPane(view);
		this.setVisible(true);
	}
	
	/**
	 * Shows a message into a message dialog 	 
	 * 
	 * @param msg	message content
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
	
	/**
	 * Application (JFrame) input. 
	 * 
	 * @param args	unused
	 */
	@SuppressWarnings("unused")
	public static void main (String args[]) { 
		
		try{
		    ServerSocket socket = 
		        new ServerSocket(AppSession.port, 10, InetAddress.getLocalHost());
		    OSSimFrame mainFrame = new OSSimFrame(); 
		}catch(java.net.BindException b){
		    System.out.println("Already Running...");
		}catch(Exception e){
			e.printStackTrace();
		    System.out.println(e.toString());
		    System.exit(1);
		}
		
	} 
	
	/**
	 * Gets itself
	 * 
	 * @return this
	 */
	public Component getComponent() {
		return this;
	}
	
	/**
	 * Frame allow opening and saving simulations
	 * 
	 * @return true
	 */
	public boolean allowOpenSave() {
		return true;
	}
} 
