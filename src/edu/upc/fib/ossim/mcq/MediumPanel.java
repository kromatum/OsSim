package edu.upc.fib.ossim.mcq;


import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.utils.EscapeDialog;

public class MediumPanel extends EscapeDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String questionType = "Answers Configuration:";
	private JCheckBox uniqueChoice = null;
	private JCheckBox multipleChoice = null;
	private JCheckBox plainText = null;
	private ButtonGroup choiceGroup = null;

	private JLabel lnbrAnswers = null;
	private JSpinner nbrAnswers = null; 

	private static String simulationType = "Type of Simulation:";
	private JButton process = null;
	private JButton memory = null;
	private JButton fs = null;
	private JButton disk = null;
	private JButton createQuestionSeries = null;

	public  MediumPanel(){
		initSpecifics();
	}



	public void initSpecifics() {
		this.setTitle("MCQ Control Panel");
		//TODO move the labels to a seperate langugage file and use the Translation class to change between languges
		uniqueChoice = new JCheckBox("Unique Choice");
		uniqueChoice.setSelected(true);
		multipleChoice = new JCheckBox ("Multiple Choice");
		plainText = new JCheckBox ("Plain Text");

		choiceGroup = new ButtonGroup();
		choiceGroup.add(uniqueChoice);
		choiceGroup.add(multipleChoice);
		choiceGroup.add(plainText);


		uniqueChoice.addActionListener(this);
		multipleChoice.addActionListener(this);
		plainText.addActionListener(this);

		lnbrAnswers = new JLabel("Number of Answers:");
		SpinnerModel spmodel = new SpinnerNumberModel(2, //initial value
				2, //min
				10, //max
				1);
		nbrAnswers = new JSpinner(spmodel);

		process = new JButton("Process Simulation");
		memory = new JButton("Memory Sumulation");
		fs = new JButton ("File System Simulation");
		disk = new JButton ("Disk Simulation");


		process.addActionListener(AppSession.getInstance().getMenu());
		memory.addActionListener(AppSession.getInstance().getMenu());
		fs.addActionListener(AppSession.getInstance().getMenu());
		disk.addActionListener(AppSession.getInstance().getMenu());

		disk.setActionCommand("dskmcq");
		memory.setActionCommand("memmcq");
		fs.setActionCommand("fsmcq");
		process.setActionCommand("prmcq");

		sortComponents();

		this.pack();
		this.setVisible(true);
		this.setResizable(false);
		//TODO check this function behavior in an applet environment
		this.setLocationRelativeTo((Frame)AppSession.getInstance().getApp());

	}

	public void sortComponents() {
		Container pane = this.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

		JPanel typePanel = new JPanel(new GridLayout(4,1));
		//typePanel.setLayout(new BoxLayout(typePanel,BoxLayout.Y_AXIS));
		TitledBorder typeBorder = BorderFactory.createTitledBorder(questionType);
		typePanel.setBorder(typeBorder);
		typePanel.add(uniqueChoice);
		typePanel.add(multipleChoice);
		typePanel.add(plainText);

		JPanel nbrquestionsPanel = new JPanel(new FlowLayout());
		nbrquestionsPanel.add(lnbrAnswers);
		nbrquestionsPanel.add(nbrAnswers);

		typePanel.add(nbrquestionsPanel);
		pane.add(typePanel);


		JPanel simulationPanel = new JPanel(new GridLayout(4,1));
		TitledBorder simulationBorder = BorderFactory.createTitledBorder(simulationType);
		simulationPanel.setBorder(simulationBorder);
		simulationPanel.add(process);
		simulationPanel.add(memory);
		simulationPanel.add(disk);
		simulationPanel.add(fs);
		
		JPanel chainToolPanel = new JPanel();
		chainToolPanel.setBorder(BorderFactory.createTitledBorder("Chaining Tool"));
		createQuestionSeries = new JButton("Question Chaining Tool");
		createQuestionSeries.addActionListener(AppSession.getInstance().getMenu());
		createQuestionSeries.setActionCommand("mcqchain");
		chainToolPanel.add(createQuestionSeries);
		
		pane.add(simulationPanel);
		pane.add(chainToolPanel);


	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource().equals(plainText))
		{
			nbrAnswers.setEnabled(false);
			lnbrAnswers.setEnabled(false);
		}
		else
		{
			nbrAnswers.setEnabled(true);
			lnbrAnswers.setEnabled(true);
		}
	}
	public int getnbrAnswers(){
		
		return (int)nbrAnswers.getValue();
	}
	public int getAnswerType(){
		if(uniqueChoice.isSelected())
			return 1;
		if(multipleChoice.isSelected())
			return 2;
		if(plainText.isSelected())
			return 3;
		return 0;
	}
}
