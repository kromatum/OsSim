package edu.upc.fib.ossim.mcq.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.mcq.MCQSession;

public class MCQViewPanel extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int nbrQuestion = 0;
	private JLabel question = null;
	private List<JCheckBox> radioGroup = null;
	private List<JLabel> answerGroup = null;
	private static String  lanswer = "Answers";
	private static String  lquestion = "Question";
	private JButton save = null;
	private JButton next = null;
	private JButton previous = null;
	private int nbrAnswers = 3;
	private int answerType = 0;
	private int block_on_step = -1;
	private JTextArea answer = null;
	private String correct_answer = null;
	private JPanel answerPane = null;
	private JLabel correctAnswerLabel = null;
	//ControlGroup
	private ButtonGroup buttonGroup = null;
	public MCQViewPanel(){}
	public MCQViewPanel(String question, int answerType,int nbrAnswers, List<String> answers,int block,String correct_answer){
		this.nbrQuestion = MCQSession.getInstance().getMCQChooserDialog().getQuestionNumber();
		this.correct_answer=correct_answer;
		block_on_step = block;
		this.question = new JLabel(question);
		this.answerType = answerType;
		if(this.answerType==3){
			this.nbrAnswers = 1;
			answer = new JTextArea(5,20);
			sortComponents(false);
		}
		else {
			this.nbrAnswers = nbrAnswers;
			radioGroup = new ArrayList<JCheckBox>();
			answerGroup = new ArrayList<JLabel>();
			for(int it = 0; it<this.nbrAnswers;++it){
				answerGroup.add(new JLabel(answers.get(it)));
				radioGroup.add(new JCheckBox());
			}
			sortComponents(true);
		}
		if(answerType == 1){
			buttonGroup = new ButtonGroup();
			for(JCheckBox it : radioGroup)
				buttonGroup.add(it);
		}
		setSize(290, 480);
		setPreferredSize(new Dimension(290, 480));// hardCoded sizing
		setMaximumSize(new Dimension(290, 480));  // hardCoded sizing
		setMinimumSize(new Dimension(290, 480));  // hardCoded sizing
		this.setVisible(true);
		if(MCQSession.getInstance().getAnswer(nbrQuestion)!=null){
			if(answerType!=3){
				String[] split = MCQSession.getInstance().getAnswer(nbrQuestion).split(",");
				for(int it = 0 ; it < split.length ; it++){
					if(it>=nbrAnswers)
						radioGroup.add(new JCheckBox());
					radioGroup.get(it).setSelected(split[it].equals("true"));
				}
			}else
				answer.setText(MCQSession.getInstance().getAnswer(nbrQuestion));


		}

	}
	public void sortComponents(Boolean type){
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		TitledBorder questionBorder = BorderFactory.createTitledBorder(lquestion);
		JLabel nbrquestion = new JLabel("Question : "+ MCQSession.getInstance().getMCQChooserDialog().getQuestionNumber() +" / "+MCQSession.getInstance().getMCQChooserDialog().getMaxQuestions());
		JLabel time = new JLabel("Time:");
		JPanel infoPanel = new JPanel(new GridLayout(2,1));
		infoPanel.add(nbrquestion);
		infoPanel.add(time);
		this.add(infoPanel);
		JPanel questionPanel = new JPanel();
		questionPanel.setBorder(questionBorder);
		JScrollPane questionScrollPane = new JScrollPane(question);
		questionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		questionPanel.add(questionScrollPane);
		this.add(questionPanel);

		TitledBorder answerBorder = BorderFactory.createTitledBorder(lanswer);
		JPanel answerPanel = new JPanel(new GridLayout(nbrAnswers,1));
		if(type == true){
			JPanel pair = null;
			for(int it = 0; it<nbrAnswers ; it++){
				pair = new JPanel(new FlowLayout());
				pair.add(radioGroup.get(it));
				pair.add(answerGroup.get(it));
				answerPanel.add(pair);
			}
			JScrollPane scrollPane = new JScrollPane(answerPanel);
			scrollPane.setBorder(answerBorder);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			this.add(scrollPane);
		}
		else{
			answerPane = new JPanel(new GridLayout(2,1));
			JScrollPane answerScrollPane = new JScrollPane(answer);
			answerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			answerPane.add(answerScrollPane);
			answerPanel.add(answerPane);
			correctAnswerLabel = new JLabel();
			answerPane.add(correctAnswerLabel);
			answerPane.setBorder(BorderFactory.createTitledBorder(lanswer));
			this.add(answerPanel);
		}
		JPanel controlPanel = new JPanel(new FlowLayout());
		save = new JButton();
		if(correct_answer == null)
			save.setText("Save & Exit");

		else{
			save.setText("Show Correct Answer");
			for(int it = 0 ; it < AppSession.getInstance().getMenu().getMenuCount() ; it++){
				try{
					AppSession.getInstance().getMenu().getMenu(it).setEnabled(true);
				}catch(Exception exc){
					//WEIRD BUG, Apparantly the code says there are 14 Menus available where in fact only 5 exists
					System.out.println(it);
				}

			}
		}
		save.addActionListener(this);
		previous = new JButton("previous");
		previous.setEnabled(MCQSession.getInstance().getMCQChooserDialog().hasPrevious());
		previous.addActionListener(this);

		next = new JButton("next");
		next.addActionListener(this);
		next.setEnabled(MCQSession.getInstance().getMCQChooserDialog().hasNext());
		controlPanel.add(previous);
		controlPanel.add(save);
		controlPanel.add(next);
		this.add(controlPanel);
		this.setBorder(BorderFactory.createBevelBorder(1));
	}

	public String getAnswer(){
		if(answerType==3){
			return answer.getText();
		}else{
			String answer ="";
			for(int it=0;it<nbrAnswers;it++){
				if(radioGroup.get(it).isSelected()){
					answer+=it+" ";
				}
			}
			return answer;
		}
	}
	public int getBlock_on_step() {
		return block_on_step;
	}
	public void setBlock_on_step(int block_on_step) {
		this.block_on_step = block_on_step;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == save){
			if(correct_answer != null){
				showCorrectAnswer();
			}
			else{
				saveAnswer();
				for(int it = 0 ; it < AppSession.getInstance().getMenu().getMenuCount() ; it++){
					try{
						AppSession.getInstance().getMenu().getMenu(it).setEnabled(true);
					}catch(Exception exc){
						//WEIRD BUG, Apparantly the code says there are 14 Menus available where in fact only 5 exists
						System.out.println(it);
					}

				}


				AppSession.getInstance().getMenu().getMenu(0).getItem(3).doClick();
				if(AppSession.getInstance().getPresenter()==null){
					MCQSession.getInstance().saveXML();
					MCQSession.destroyInstance();

				}

			}
		}
		if(arg0.getSource() == next){
			saveAnswer();
			AppSession.getInstance().getApp().setDefaultSize();
			MCQSession.getInstance().getMCQChooserDialog().getNext();
		}
		if(arg0.getSource() == previous){
			saveAnswer();
			AppSession.getInstance().getApp().setDefaultSize();
			MCQSession.getInstance().getMCQChooserDialog().getPrevious();
		}

	}
	private void showCorrectAnswer(){
		switch(answerType){
		case 1:
		case 2:
			String[] sub = correct_answer.split(",");
			for(int i = 0; i<sub.length; i++)
			{
				radioGroup.get(i).setEnabled(false);
				if(sub[i].equals("false")){
					answerGroup.get(i).setEnabled(false);
				}
			}
			break;
		case 3:
			answer.setEnabled(false);
			correctAnswerLabel.setText("Correct Answer: "+correct_answer);

			break;

		}
		block_on_step = -1;
		save.setEnabled(false);
		this.repaint();
	}
	private void saveAnswer(){
		switch(answerType){
		case 1:
		case 2:
			String answerString = "";
			for(int it = 0 ; it < radioGroup.size(); it++){
				if(((JCheckBox)radioGroup.get(it)).getSelectedObjects()!=null)
					answerString+="true";
				else
					answerString+="false";
				if(it < radioGroup.size()-1)
					answerString+=",";		
			}
			MCQSession.getInstance().addAnswer(nbrQuestion, answerString);
			break;
		case 3:
			MCQSession.getInstance().addAnswer(nbrQuestion, answer.getText());
			break;
		}
	}
}
