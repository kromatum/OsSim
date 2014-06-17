package edu.upc.fib.ossim.mcq.view;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.utils.EscapeDialog;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.OpenSaveDialog;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.XMLParserJDOM;

public class MCQQuestionLinker extends EscapeDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String PATH = "C:\\Users\\pc distributor\\Desktop\\WareHouse";
	private JLabel LPATH = new JLabel("Path: "+PATH);
	private JFileChooser chooser = null;
	
	@SuppressWarnings("rawtypes")
	private JList existingList = null;
	private DefaultListModel<String> existingModel = null;
	
	@SuppressWarnings("rawtypes")
	private JList mcqList = null;
	private DefaultListModel<String> mcqModel = null;
	
	private JButton up = null;
	private JButton down = null;
	private JButton add = null;
	private JButton remove = null;
	private JButton browse = null;
	private static MCQQuestionLinker instance = null;
	private Hashtable<String,URL> questionTable = new Hashtable<String,URL>();
	private JButton browseFile = null;
	private File saveFile = null;
	
	private class saveListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			OpenSaveDialog dialog = new OpenSaveDialog(instance);
			saveFile = dialog.showSaveFileChooser();
			saveQuestionList();
			
			
			
			
		}
	}
	private class ChangeDirectoryListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			File myFile;
			chooser = new JFileChooser(PATH);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setVisible(true);
			chooser.setDialogTitle("Select target directory");
			int returnVal = chooser.showOpenDialog(instance);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				myFile = chooser.getSelectedFile();
				PATH = myFile.getPath();
				LPATH.setText("Path: "+PATH);
				System.out.println(PATH);
				existingModel.clear();
				fillModel();
			}
		}
		
		
	}
	
	private class addListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("rawtypes")
			List selected = existingList.getSelectedValuesList();
			for(Object it : selected){
				mcqModel.addElement((String) it);
				existingModel.removeElement(it);
			}
			existingList.updateUI();
			mcqList.updateUI();
		}
	}
	private class removeListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("rawtypes")
			List selected = mcqList.getSelectedValuesList();
			for(Object it : selected){
				existingModel.addElement((String) it);
				mcqModel.removeElement(it);
			}
			existingList.updateUI();
			mcqList.updateUI();
			
		}
	}
	private class moveUpListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			List selected = mcqList.getSelectedValuesList();
			for(Object it : selected){
				String toChange = mcqModel.elementAt(mcqModel.indexOf(it));
				int index = mcqModel.indexOf(it);
				if(index>0){
					mcqModel.remove(index);
					mcqModel.add(index-1, toChange);
					mcqList.setSelectedIndex(index-1);
				}
				
			}
		}
	}
	private class MoveDownListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			List selected = mcqList.getSelectedValuesList();
			for(Object it : selected){
				String toChange = mcqModel.elementAt(mcqModel.indexOf(it));
				int index = mcqModel.indexOf(it);
				if(index < mcqModel.getSize()-1){
					mcqModel.remove(index);
					mcqModel.add(index+1, toChange);
					mcqList.setSelectedIndex(index+1);
					
				}
				
			}
		}
	}
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MCQQuestionLinker(){
		super();
		setTitle("MCQ Creator Tool");
		
		existingModel = new DefaultListModel<String>();
		fillModel();
		existingList = new JList(existingModel);
		existingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		mcqModel = new DefaultListModel<String>();
		mcqList = new JList(mcqModel);
		mcqList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		up = new JButton("↑");
		up.setFont(up.getFont().deriveFont(18.0f));
		up.addActionListener(new moveUpListener());
		down = new JButton("↓");
		down.setFont(down.getFont().deriveFont(18.0f));
		down.addActionListener(new MoveDownListener());
		add = new JButton(">");
		add.setFont(down.getFont().deriveFont(18.0f));
		add.addActionListener(new addListener());
		remove = new JButton ("<");
		remove.setFont(down.getFont().deriveFont(18.0f));
		remove.addActionListener(new removeListener());

		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.PAGE_AXIS));
		buttonPanel.add(add);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(remove);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(up);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(down);
		
		//add(buttonPanel);
		add(new JScrollPane(existingList));
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		JPanel pathPanel = new JPanel (new FlowLayout(FlowLayout.LEFT));
		pathPanel.add(LPATH);
		
		browse = new JButton("Browse Directory");
		browse.addActionListener(new ChangeDirectoryListener());
		JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		browsePanel.add(browse);
		
		topPanel.add(pathPanel);
		topPanel.add(browsePanel);
		
		
		add(topPanel,BorderLayout.NORTH);
		setSize(500, 500);
		setPreferredSize(new Dimension(500, 500));// hardCoded sizing
		setMaximumSize(new Dimension(500, 500));  // hardCoded sizing
		setMinimumSize(new Dimension(500, 500));  // hardCoded sizing
		
		this.setLocationRelativeTo((Frame)AppSession.getInstance().getApp());
		JScrollPane existingListScrollPane = new JScrollPane(existingList);
		existingListScrollPane.setBorder(BorderFactory.createTitledBorder("Available Questions:"));
		
		JScrollPane mcqListScrollPane = new JScrollPane(mcqList);
		mcqListScrollPane.setBorder(BorderFactory.createTitledBorder("Chosen Questions:"));
		
		JPanel centerPanel = new JPanel();
		
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		centerPanel.add(existingListScrollPane);
		centerPanel.add(buttonPanel);
		centerPanel.add(mcqListScrollPane);
		
		
		JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		browseFile = new JButton("Save As");
		browseFile.addActionListener(new saveListener());
		
		
		savePanel.add(browseFile);
		
		
		add(centerPanel);
		add(savePanel,BorderLayout.SOUTH);
		setVisible(true);
	}
	public static MCQQuestionLinker getMCQQuestionLinker(){
		if(instance==null) instance = new MCQQuestionLinker();
		return instance;
	}
	
	
	private void fillModel(){
		File dir = new File(PATH);
 
		// list out all the file name and filter by the extension
		File[] list = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
					if(name.matches(".*.xml"))
						return true;
					return false;
			}
		});
		XMLParserJDOM parser;
		for(int it = 0 ; it < list.length ; it++){
			 try {
				parser = new XMLParserJDOM(list[it].toURI().toURL());
				String sroot = parser.getRoot();
				String question = null;
				if (sroot.equals(Functions.getInstance().getPropertyString("xml_root_mcq_pro"))){
					question = parser.getElements("mcq").get(0).get(1).get(1);
					
				}
				if(sroot.equals(Functions.getInstance().getPropertyString("xml_root_mcq_dsk"))){
					question = parser.getElements("mcq").get(0).get(1).get(1);
					
				}
				if(sroot.equals(Functions.getInstance().getPropertyString("xml_root_mcq_mem"))){
					question = parser.getElements("mcq").get(0).get(1).get(1);
				}
				if(question !=null){
					existingModel.addElement(question);
					System.out.println(question);
					questionTable.put(question, list[it].toURI().toURL());
				}
				
			} catch (MalformedURLException | SoSimException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private void saveQuestionList(){
		Object[] selected = mcqModel.toArray();
		Element root = new Element("MCQ_URLs");
		Document doc = new Document(root);
		root.setAttribute("totalQuestions", ""+selected.length);
		doc.setRootElement(root);
		Element url =null;
		for(Object it : selected){
			url = new Element("URL");
			url.addContent(new Element("Value").setText((questionTable.get(it).getFile()).substring(1).replace("%20", " ")));
			System.out.println(questionTable.get(it).getFile());
			doc.getRootElement().addContent(url);
		}
		XMLOutputter xmlOutput = new XMLOutputter();
		 
		// display nice nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		try {	
			if(saveFile != null){
				xmlOutput.output(doc, new FileWriter(saveFile));
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		System.out.println("File Saved!");
		

		
		
	}
	
	
	

}
