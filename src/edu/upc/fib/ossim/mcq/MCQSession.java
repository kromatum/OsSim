package edu.upc.fib.ossim.mcq;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.upc.fib.ossim.mcq.view.MCQChooserDialog;
import edu.upc.fib.ossim.mcq.view.MCQCreationPanel;
import edu.upc.fib.ossim.mcq.view.MCQViewPanel;

public class MCQSession {

	private MediumPanel medium = null;
	private MCQCreationPanel mcqCreationPanel = null;
	private MCQViewPanel mcqViewPanel = null;
	private Hashtable<Integer,String> answers = new Hashtable<Integer, String>();
	private static MCQSession instance = null;
	private MCQChooserDialog chooser = null;

	private MCQSession(){}
	
	public MediumPanel getMediumPanel(){
		if(medium ==null) medium = new MediumPanel();
			return medium;
	}
	public void hideMediumPanel(){
		if(medium!=null) medium.setVisible(false);
	}
	
	public MCQCreationPanel getmcqCreationPanel(){
		if(mcqCreationPanel ==null)
			mcqCreationPanel = new MCQCreationPanel(MCQSession.getInstance().getMediumPanel().getAnswerType(), MCQSession.getInstance().getMediumPanel().getnbrAnswers());
		return mcqCreationPanel;
	}
	public MCQCreationPanel getmcqCreationPanel(int type,int nbrAnswers){
		mcqCreationPanel = new MCQCreationPanel(type, nbrAnswers);
		return mcqCreationPanel;
	}
	public MCQViewPanel getmcqViewPanel(int nbrQuestion, String question, int answerType,int nbrAnswers, List<String> answers,int blockOnStep,String correctAnswer){
		mcqViewPanel = new MCQViewPanel(question, answerType, nbrAnswers, answers, blockOnStep,correctAnswer);
		return mcqViewPanel;
	}
	public MCQViewPanel getmcqViewPanel(){
		if(mcqViewPanel == null)
			mcqViewPanel = new MCQViewPanel();
		return mcqViewPanel;
	}
	public void destroyMCQViewPanel(){
		mcqViewPanel = null;
	}

	public static MCQSession getInstance(){
		if(instance == null) instance = new MCQSession();
			return instance;
	}
	
	public static void destroyInstance(){
		instance = null;
	}
	public MCQChooserDialog getMCQChooserDialog(){
		if(chooser == null) chooser = new MCQChooserDialog();
		return chooser;
	}
	public void addAnswer(int nbr, String Answer){
		answers.put(nbr, Answer);
	}
	public String getAnswer(int nbr){
		return answers.get(nbr);
	}
	public void saveXML(){
		Element root = new Element("qcm_answers");
		Document doc = new Document(root);
		doc.setRootElement(root);
		int it = 1;
		Element answer ; 
		while(answers.get(it)!=null){
			answer = new Element("question");
			answer.setAttribute("id",""+it);
			answer.addContent(new Element("answer").setText(answers.get(it)));
			doc.getRootElement().addContent(answer);
			it++;
		}
		XMLOutputter xmlOutput = new XMLOutputter();
		 
		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			xmlOutput.output(doc, new FileWriter("c:\\Users\\pc distributor\\Desktop\\"+chooser.getName()+".xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		System.out.println("File Saved!");
		
		
	}
	public void destroyMCQChooserDialog(){
		chooser = null;
	}
	
}
