package edu.upc.fib.ossim.mcq.view;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.mcq.MCQSession;
import edu.upc.fib.ossim.utils.EscapeDialog;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.HelpDialog;
import edu.upc.fib.ossim.utils.SoSimException;

public class MCQChooserDialog extends EscapeDialog implements HyperlinkListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = null;
	private JEditorPane editorPane = null;
	private JScrollPane editorScrollPane = null;
	private static URL LINK = Functions.getInstance().getResourceURL("mcq.html");
	private int questionNumber = 1;
	private int maxQuestions =  0;
	private Vector<String> paths  = new Vector<String>();

	public MCQChooserDialog() {
		this.setTitle("Available MCQs ");
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(this);
		editorScrollPane = new JScrollPane(editorPane);
		this.add(editorScrollPane);
		try{
			editorPane.setPage(LINK);
		}catch (IOException e) {
			e.printStackTrace();
		}

		this.pack();
		this.setSize(300, 500);



	}

	private void loadSimulation(URL resource) {
		this.setVisible(false);

		if (AppSession.getInstance().getPresenter() != null) 
			AppSession.getInstance().getPresenter().closeInfo();
		try {
			Functions.getInstance().openMCQSimulation(resource);
		} catch (SoSimException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(AppSession.getInstance().getApp().getComponent(),ex.toString(),"Error",JOptionPane.ERROR_MESSAGE);
		}     
	}



	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			name = JOptionPane.showInputDialog(null,
					"Enter Your Name:",
					"", JOptionPane.QUESTION_MESSAGE);
			if (name != null && !name.equals("")){
				URL url = e.getURL();
				
				System.out.println(url);
				if (url != null) {
					if (url.toString().endsWith("xml")) {  // Load simulation
						parseXML(url);
						try {
							System.out.println("First Load: "+paths.get(0));
							loadSimulation((new File(paths.get(0)).toURI().toURL()));
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}
	public void getNext(){
		
		try {
			MCQSession.getInstance().destroyMCQViewPanel();
			questionNumber++;
			System.out.println("Next:" + paths.get(questionNumber-1));
			loadSimulation((new File(paths.get(questionNumber-1)).toURI().toURL()));
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public boolean hasNext(){
		return (questionNumber<maxQuestions);	
	}
	public boolean hasPrevious(){
		
		return questionNumber>1;
	}
	public void getPrevious(){
		
		try {
			MCQSession.getInstance().destroyMCQViewPanel();
			questionNumber--;
			System.out.println("Previous:" + paths.get(questionNumber-1));
			loadSimulation((new File(paths.get(questionNumber-1)).toURI().toURL()));
			

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

	}
	public int getQuestionNumber(){
		return questionNumber;
	}
	public String getName(){
		return name;
	}
	private void parseXML(URL url) {
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(new File(url.getFile()));
			maxQuestions = Integer.parseInt(doc.getDocumentElement().getAttribute("totalQuestions"));
			NodeList nList = doc.getElementsByTagName("URL");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				paths.add(eElement.getElementsByTagName("Value").item(0).getTextContent());
				
			}	
			System.out.println(paths);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public int getMaxQuestions(){
		return maxQuestions;
	}
	
}

