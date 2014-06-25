package edu.upc.fib.ossim.mcq.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import edu.upc.fib.ossim.utils.Functions;

public class MCQCreationPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	//Common Components
	private JTextArea question = null;
	private JCheckBox includeAnswer = null;
	private List<JCheckBox> radioGroup = null;
	private List<JTextArea> answerGroup = null;
	private static String  lanswer = "Answers";
	private static String  lquestion = "Question";
	private JButton save = null;
	private int nbrAnswers = 3;
	private int answerType = 0;
	//ControlGroup
	private ButtonGroup buttonGroup = null;
	private JLabel lblock_on_step = null;
	private JSpinner block_on_step = null; 
	private JSpinner difficulty = null; 
	private JButton add = null;
	private JButton remove = null;



	public MCQCreationPanel(int type,int nbrAnswers){

		this.nbrAnswers = nbrAnswers;
		this.answerType = type;
		initComponents();
		sortComponents();
		setSize(290, 480);
		setPreferredSize(new Dimension(290, 480));// hardCoded sizing
		setMaximumSize(new Dimension(290, 480));  // hardCoded sizing
		setMinimumSize(new Dimension(290, 480));  // hardCoded sizing
		this.setVisible(true);
	}


	public void initComponents(){
		lblock_on_step = new JLabel("Block On Step:");
		SpinnerModel spmodel = new SpinnerNumberModel(-1, //initial value
				-1, //min
				99, //max
				1);
		block_on_step = new JSpinner(spmodel);
		SpinnerModel spdiffmodel = new SpinnerNumberModel(1, //initial value
				1, //min
				99, //max
				1);
		difficulty = new JSpinner(spdiffmodel);
		question = new JTextArea(6, 20);
		radioGroup = new ArrayList<JCheckBox>();
		answerGroup = new ArrayList<JTextArea>();
		buttonGroup = new ButtonGroup();
		if(answerType==3)
			nbrAnswers=1;
		for(int it = 0; it < nbrAnswers ; it++){
			radioGroup.add(new JCheckBox());
			answerGroup.add(new JTextArea(5,20));
			if(answerType == 1)
				buttonGroup.add(radioGroup.get(it));
		}
		includeAnswer = new JCheckBox("Include Answer in Generated XML");	

		add = new JButton(Functions.getInstance().createImageIcon("plus.png"));
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				radioGroup.add(new JCheckBox());
				if(answerType==1)
					buttonGroup.add(radioGroup.get(nbrAnswers));
				answerGroup.add(new JTextArea(5,20));
				nbrAnswers++;
				removeAll();
				sortComponents();
				revalidate();
				if(nbrAnswers>2)
					remove.setEnabled(true);
			}
		});
		remove = new JButton(Functions.getInstance().createImageIcon("minus.png"));
		remove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				radioGroup.remove(nbrAnswers-1);
				answerGroup.remove(nbrAnswers-1);
				nbrAnswers--;
				if(nbrAnswers==2)
					remove.setEnabled(false);
				removeAll();
				sortComponents();
				revalidate();

			}
		});

	}
	public void sortComponents(){
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		TitledBorder questionBorder = BorderFactory.createTitledBorder(lquestion);


		JPanel questionPanel = new JPanel();
		questionPanel.setLayout(new BoxLayout(questionPanel,BoxLayout.PAGE_AXIS));
		questionPanel.setBorder(questionBorder);

		JPanel stepPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		stepPanel.add(lblock_on_step);
		stepPanel.add(block_on_step);

		JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		difficultyPanel.add(new JLabel("Difficulty"));
		difficultyPanel.add(difficulty);



		JPanel panel1 = new JPanel(new GridLayout(2,1));
		panel1.add(stepPanel);
		panel1.add(difficultyPanel);


		//questionPanel.add(stepPanel);
		//questionPanel.add(difficultyPanel);
		questionPanel.add(panel1);


		JScrollPane questionScrollPane = new JScrollPane(question);
		questionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		questionPanel.add(questionScrollPane);

		questionPanel.setSize(280, 210);
		questionPanel.setPreferredSize(new Dimension(280, 210));// hardCoded sizing
		questionPanel.setMaximumSize(new Dimension(280, 210));  // hardCoded sizing
		questionPanel.setMinimumSize(new Dimension(280, 210));  // hardCoded sizing


		this.add(questionPanel);






		TitledBorder answerBorder = BorderFactory.createTitledBorder(lanswer);
		JPanel answerPanel = new JPanel(new BorderLayout());
		JPanel answerView = new JPanel(new GridLayout(nbrAnswers,1));
		JPanel pair = null;
		for(int it = 0; it<nbrAnswers ; it++){
			pair = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if(it>=radioGroup.size()){
				radioGroup.add(new JCheckBox());
				if(answerType==1)
					buttonGroup.add(radioGroup.get(it));
				answerGroup.add(new JTextArea());
			}

			pair.add(radioGroup.get(it));
			pair.add(answerGroup.get(it));
			if(answerType==3){
				radioGroup.get(it).setVisible(false);
			}
			answerView.add(pair);

		}
		JPanel answerControl = new JPanel(new FlowLayout());
		answerControl.add(remove);
		answerControl.add(add);
		if(answerType!=3)
			answerPanel.add(answerControl,BorderLayout.NORTH);
		answerPanel.add(answerView);


		JScrollPane scrollPane = new JScrollPane(answerPanel);

		scrollPane.setBorder(answerBorder);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);



		this.add(scrollPane);
		JPanel includeAnswerPanel = new JPanel(new FlowLayout());
		includeAnswerPanel.add(includeAnswer);
		this.add(includeAnswerPanel);

		this.setBorder(BorderFactory.createBevelBorder(1));
		if(nbrAnswers==2)
			remove.setEnabled(false);
	}

	public Vector<Vector<Vector<String>>> getXMLData(){
		Vector<Vector<Vector<String>>> data = new Vector<Vector<Vector<String>>>();


		Vector<Vector<String>> param = new Vector<Vector<String>>();
		Vector<String> attribute = new Vector<String>();	

		//Question Identifier
		attribute.add("type");
		attribute.add("question");

		//Question Text
		param.add(attribute);
		attribute = new Vector<String>();
		attribute.add("text");
		attribute.add(question.getText());
		param.add(attribute);


		//Answer Type
		attribute = new Vector<String>();
		attribute.add("AnswerType");
		attribute.add(""+answerType);
		param.add(attribute);

		//Block On Step
		attribute = new Vector<String>();
		attribute.add("BlockOnStep");
		attribute.add(""+(int)block_on_step.getValue());
		param.add(attribute);

		//Number of Answers
		attribute = new Vector<String>();
		attribute.add("AnswerNumber");
		attribute.add(""+nbrAnswers);
		param.add(attribute);

		//Include Answers Identifier
		attribute = new Vector<String>();
		attribute.add("includeAnswers");
		if(includeAnswer.isSelected())
			attribute.add("true");
		else
			attribute.add("false");
		param.add(attribute);


		//difficulty
		attribute = new Vector<String>();
		attribute.add("difficulty");
		attribute.add(""+difficulty.getValue());
		param.add(attribute);

		data.add(param);

		//Answers



		for(int it = 0 ; it < nbrAnswers ; it++)
		{
			param = new Vector<Vector<String>>();
			attribute = new Vector<String>();
			attribute.add("type");
			attribute.add("answer");
			param.add(attribute);
			attribute = new Vector<String>();
			attribute.add("text");
				if(answerType!=3)
					attribute.add(answerGroup.get(it).getText());
				else
					attribute.add("");
				param.add(attribute);
				if(includeAnswer.isSelected()){
					attribute = new Vector<String>();
					attribute.add("value");
					if(answerType!=3){
						if(radioGroup.get(it).isSelected())
							attribute.add("true");
						else
							attribute.add("false");
					}
					else{
						attribute.add(answerGroup.get(it).getText());
					}
					param.add(attribute);
				}
			
			data.add(param);
		}
		return data;
	}
	public void fillData(String question,int answerType, List<String>Answers, List<Boolean> answers,boolean includeAnswers, int Block_On_Step, int difficulty){
		this.question.setText(question);
		nbrAnswers = Answers.size();
		this.answerType = answerType;
		if(answerType!=3){
		for(int it = 0; it < Answers.size() ; it++){
			if(it>=answerGroup.size())
				answerGroup.add(new JTextArea(5,20));
			answerGroup.get(it).setText(Answers.get(it));
			if(answerType!=3){
				if(includeAnswers){
					if(it>=radioGroup.size())
						radioGroup.add(new JCheckBox());
					radioGroup.get(it).setSelected(answers.get(it));
				}
			}
		}
		}
		else{
			nbrAnswers = 1;
			answerGroup.clear();
			answerGroup.add(new JTextArea(5,20));
			answerGroup.get(0).setText(Answers.get(0));
			radioGroup.clear();
			radioGroup.add(new JCheckBox());
		}
		this.includeAnswer.setSelected(includeAnswers);
		block_on_step.setValue(Block_On_Step);
		this.difficulty.setValue(difficulty);
		this.removeAll();
		sortComponents();
		repaint();
		if(nbrAnswers>2)
			remove.setEnabled(true);
	}

}
