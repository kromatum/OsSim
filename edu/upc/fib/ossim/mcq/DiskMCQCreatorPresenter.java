package edu.upc.fib.ossim.mcq;

import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.mcq.view.MCQQuestionLinker;
import edu.upc.fib.ossim.mcq.view.PanelMCQCreatorDisk;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.SoSimException;

public class DiskMCQCreatorPresenter extends DiskPresenter{

	public DiskMCQCreatorPresenter(boolean openSettings) {
		super(openSettings);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PanelTemplate createPanelComponents() {
		super.createPanelComponents();
		return new PanelMCQCreatorDisk(this,"dk_42");
	}
	@Override
	public String getXMLRoot() {
		// Returns XML root element 
		return  Functions.getInstance().getPropertyString("xml_root_mcq_dsk");
	}
	@Override
	public Vector<String> getXMLChilds() {
		Vector<String> childs = super.getXMLChilds();
		childs.add("mcq");
		return childs;
	}
	@Override
	public Vector<Vector<Vector<String>>> getXMLData(int child) {
		Vector<Vector<Vector<String>>> data = null;
		if(child!=2)
			data = super.getXMLData(child);
		else{
			data = MCQSession.getInstance().getmcqCreationPanel().getXMLData();
		}
		return data;
	}
	
	
	
	@Override
	public void putXMLData(int child, Vector<Vector<Vector<String>>> data) throws SoSimException {
		System.out.println(data);
		if(child!=2)
			super.putXMLData(child, data);
		else{
			int blockOnAnswer = new Integer(data.get(0).get(3).get(1)).intValue();
			int nbrAnswers = new Integer(data.get(0).get(4).get(1)).intValue();
			boolean includeAnswers = data.get(0).get(5).get(1).equals("true");
			String question = data.get(0).get(1).get(1);
			ArrayList<String> answers = new ArrayList<String>();
			System.out.println("Question: " + data.get(0).get(1).get(1));
			System.out.println("AnswerType:" + data.get(0).get(2).get(1));
			int answerType = Integer.parseInt(data.get(0).get(2).get(1));
			ArrayList<Boolean> answerbool = new ArrayList<Boolean>();
			int difficulty = new Integer(data.get(0).get(6).get(1)).intValue();
			for(int it = 1 ; it <= nbrAnswers; it++){
				if(answerType!=3){
					answers.add(data.get(it).get(1).get(1));
					if(includeAnswers){
						if(data.get(it).get(2).get(1).equals("true"))
							answerbool.add(new Boolean(true));
						else
							answerbool.add(new Boolean(false));
					}
				}
				else{
					try{
					answers.add(data.get(it).get(2).get(1));
					}catch(Exception exc){
						answers.add("");
					}
					
				}
			}	
			MCQSession.getInstance().getmcqCreationPanel().fillData(question,answerType, answers, answerbool,includeAnswers, blockOnAnswer,difficulty);
		}
	}



}
