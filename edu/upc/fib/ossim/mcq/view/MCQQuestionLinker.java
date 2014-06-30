package edu.upc.fib.ossim.mcq.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

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

public class MCQQuestionLinker extends EscapeDialog {

	public class CustomTableModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CustomTableModel(String[] columnNames, int i) {
			// TODO Auto-generated constructor stub
			super(columnNames, i);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String PATH = "/Users/IssaCamara/Desktop";
	private JLabel LPATH = new JLabel("Path: " + PATH);
	private JFileChooser chooser = null;

	private String[] columnNames = { "Question", "Difficulty" };

	private JTable existingTable = null;
	private CustomTableModel existingTableModel = new CustomTableModel(
			columnNames, 0);

	private JTable mcqTable = null;
	private CustomTableModel mcqTableModel = new CustomTableModel(columnNames,
			0);

	private JButton up = null;
	private JButton down = null;
	private JButton add = null;
	private JButton remove = null;
	private JButton browse = null;
	private static MCQQuestionLinker instance = null;
	private Hashtable<String, URL> mcqHashTable = new Hashtable<String, URL>();
	private Hashtable<String, URL> existingHashTable = new Hashtable<String, URL>();
	private JButton browseFile = null;
	private File saveFile = null;

	static Boolean isEditing = false;
	URL edited = null;

	private class saveListener implements ActionListener {
	
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			OpenSaveDialog dialog = new OpenSaveDialog(instance);
			saveFile = dialog.showSaveFileChooser();
			saveQuestionList();

		}
	}

	private class ChangeDirectoryListener implements ActionListener {
	
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			File myFile;
			chooser = new JFileChooser(PATH);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setVisible(true);
			chooser.setDialogTitle("Select target directory");
			int returnVal = chooser.showOpenDialog(instance);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				myFile = chooser.getSelectedFile();
				PATH = myFile.getPath();
				LPATH.setText("Path: " + PATH);
				System.out.println(PATH);
				existingTableModel.getDataVector().clear();
				fillModel();
				mcqTable.revalidate();
				existingTable.revalidate();
				//revalidate();
			}
		}

	}

	private class addListener implements ActionListener {
		
		@SuppressWarnings("unchecked")

		public void actionPerformed(ActionEvent e) {
			String s = (String) existingTableModel.getValueAt(
					existingTable.getSelectedRow(), 0);
			Object o = existingTableModel.getDataVector().remove(
					existingTable.getSelectedRow());
			mcqTableModel.getDataVector().add(o);
			mcqHashTable.put(s, existingHashTable.get(s));
			existingHashTable.remove(s);

			existingTable.updateUI();
			mcqTable.updateUI();
		}
	}

	private class removeListener implements ActionListener {

		
	
		public void actionPerformed(ActionEvent e) {

			if (mcqTable.getSelectedRow() < 0
					|| mcqTable.getSelectedRow() >= mcqTable.getRowCount())
				return;
			String s = (String) mcqTableModel.getValueAt(
					mcqTable.getSelectedRow(), 0);
			System.out.println(s);
			existingHashTable.put(s, mcqHashTable.get(s));
			mcqHashTable.remove(s);
			Object o = mcqTableModel.getDataVector().remove(
					mcqTable.getSelectedRow());
			existingTableModel.getDataVector().add(o);

			existingTable.updateUI();
			mcqTable.updateUI();

		}
	}

	private class moveUpListener implements ActionListener {
		@SuppressWarnings("unchecked")
	
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			int pos = mcqTable.getSelectedRow();
			if (pos < 0 || pos > mcqTable.getRowCount())
				return;
			Object o = mcqTableModel.getDataVector().remove(pos);
			mcqTableModel.getDataVector().add(pos - 1, o);
			mcqTable.updateUI();

		}
	}

	private class MoveDownListener implements ActionListener {
		@SuppressWarnings("unchecked")
	
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			int pos = mcqTable.getSelectedRow();
			if (pos < 0 || pos > mcqTable.getRowCount())
				return;
			Object o = mcqTableModel.getDataVector().remove(pos);
			mcqTableModel.getDataVector().add(pos + 1, o);
			mcqTable.updateUI();

		}
	}

	private MCQQuestionLinker() {
		super();
		setTitle("MCQ Creator Tool");

		fillModel();

		existingTable = new JTable(existingTableModel);
		existingTable.setShowVerticalLines(false);
		existingTable.setShowHorizontalLines(false);
		existingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		existingTable.setSelectionModel(new DefaultListSelectionModel());
		existingTable.setColumnSelectionAllowed(false);
		existingTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = existingTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < existingTable.getRowCount()) {
					existingTable.setRowSelectionInterval(r, r);
				} else {
					existingTable.clearSelection();
				}

				int rowindex = existingTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					JPopupMenu popup = createYourPopUp(existingTable,
							existingHashTable);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		mcqTable = new JTable(mcqTableModel);
		mcqTable.setShowVerticalLines(false);
		mcqTable.setShowHorizontalLines(false);
		mcqTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mcqTable.setSelectionModel(new DefaultListSelectionModel());
		mcqTable.setColumnSelectionAllowed(false);
		mcqTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = mcqTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < mcqTable.getRowCount()) {
					mcqTable.setRowSelectionInterval(r, r);
				} else {
					mcqTable.clearSelection();
				}

				int rowindex = mcqTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					JPopupMenu popup = createYourPopUp(mcqTable, mcqHashTable);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		up = new JButton("Up");
		up.setFont(up.getFont().deriveFont(18.0f));
		up.addActionListener(new moveUpListener());
		down = new JButton("Down");
		down.setFont(down.getFont().deriveFont(18.0f));
		down.addActionListener(new MoveDownListener());
		add = new JButton(">");
		add.setFont(down.getFont().deriveFont(18.0f));
		add.addActionListener(new addListener());
		remove = new JButton("<");
		remove.setFont(down.getFont().deriveFont(18.0f));
		remove.addActionListener(new removeListener());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(add);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(remove);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(up);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(down);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pathPanel.add(LPATH);

		browse = new JButton("Browse Directory");
		browse.addActionListener(new ChangeDirectoryListener());
		JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		browsePanel.add(browse);

		topPanel.add(pathPanel);
		topPanel.add(browsePanel);

		add(topPanel, BorderLayout.NORTH);
		setSize(500, 500);
		setPreferredSize(new Dimension(500, 500));// hardCoded sizing
		setMaximumSize(new Dimension(500, 500)); // hardCoded sizing
		setMinimumSize(new Dimension(500, 500)); // hardCoded sizing

		this.setLocationRelativeTo((Frame) AppSession.getInstance().getApp());
		JScrollPane existingListScrollPane = new JScrollPane(existingTable);
		existingListScrollPane.setBorder(BorderFactory
				.createTitledBorder("Available Questions:"));

		// JScrollPane mcqListScrollPane = new JScrollPane(mcqList);
		JScrollPane mcqListScrollPane = new JScrollPane(mcqTable);
		mcqListScrollPane.setBorder(BorderFactory
				.createTitledBorder("Chosen Questions:"));

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
		add(savePanel, BorderLayout.SOUTH);
		setVisible(true);
	}

	public void initComponents() {

	}

	protected JPopupMenu createYourPopUp(final JTable table,
			final Hashtable<String, URL> hash) {
		// TODO Auto-generated method stub
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Edit", Functions.getInstance()
				.createImageIcon("pen2.png"));
		menuItem.addActionListener(new ActionListener() {

			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				isEditing = true;
				try {
					edited = hash.get(table.getModel().getValueAt(
							table.getSelectedRow(), 0));
					Functions.getInstance().openSimulation(edited);
					AppSession.getInstance().getMenu().allowSaving();
				} catch (SoSimException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		menu.add(menuItem);

		return menu;
	}

	public static MCQQuestionLinker getInstance() {
		if (instance == null)
			instance = new MCQQuestionLinker();
		return instance;
	}

	private void fillModel() {
		File dir = new File(PATH);
		existingTableModel.getDataVector().clear();
		// list out all the file name and filter by the extension
		File[] list = dir.listFiles(new FilenameFilter() {

			
			public boolean accept(File dir, String name) {
				if (name.matches(".*.xml"))
					return true;
				return false;
			}
		});
		XMLParserJDOM parser;
		if(list!=null)
		for (int it = 0; it < list.length; it++) {
			try {
				parser = new XMLParserJDOM(list[it].toURI().toURL());
				String sroot = parser.getRoot();
				String question = null;
				String difficulty = "5";
				if (sroot.equals(Functions.getInstance().getPropertyString(
						"xml_root_mcq_pro"))) {
					question = parser.getElements("mcq").get(0).get(1).get(1);
					difficulty = parser.getElements("mcq").get(0).get(6).get(1);
				}
				if (sroot.equals(Functions.getInstance().getPropertyString(
						"xml_root_mcq_dsk"))) {
					question = parser.getElements("mcq").get(0).get(1).get(1);
					difficulty = parser.getElements("mcq").get(0).get(6).get(1);
				}
				if (sroot.equals(Functions.getInstance().getPropertyString(
						"xml_root_mcq_mem"))) {
					question = parser.getElements("mcq").get(0).get(1).get(1);
					difficulty = parser.getElements("mcq").get(0).get(6).get(1);
				}
				if (sroot.equals(Functions.getInstance().getPropertyString(
						"xml_root_mcq_fs"))) {
					question = parser.getElements("mcq").get(0).get(1).get(1);
					difficulty = parser.getElements("mcq").get(0).get(6).get(1);
				}
				if (question != null) {
					Object[] o = { question, difficulty };
					existingTableModel.addRow(o);

					existingHashTable.put(question, list[it].toURI().toURL());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SoSimException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		existingTableModel.fireTableDataChanged();

	}

	private void saveQuestionList() {
		Element root = new Element("MCQ_URLs");
		Document doc = new Document(root);
		root.setAttribute("totalQuestions", "" + mcqTable.getRowCount());
		doc.setRootElement(root);
		Element url = null;
		for(int it = 0; it< mcqTableModel.getRowCount() ; it++){
		//Enumeration<URL> enumeration = mcqHashTable.elements();
		//while (enumeration.hasMoreElements()) {
			url = new Element("URL");
			/*url.addContent(new Element("Value").setText((enumeration
					.nextElement().getFile()).substring(1).replace("%20", " ")));*/
			url.addContent(new  Element("Value").setText(mcqHashTable.get(mcqTableModel.getValueAt(it, 0)).toString()));
			//url.addContent("/"+new Element("Value").setText("/"));
			doc.getRootElement().addContent(url);
		}
		XMLOutputter xmlOutput = new XMLOutputter();

		// display nice nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			if (saveFile != null) {
				xmlOutput.output(doc, new FileWriter(saveFile));

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("File Saved!");

	}

	public static void destroyInstance() {
		instance = null;
	}

	public static boolean isEditing() {
		return isEditing;
	}

	public static void doneEditing() {
		if (instance != null) {
			isEditing = false;
			instance.setVisible(true);
		}
	}

}
