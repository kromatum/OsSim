package edu.upc.fib.ossim.mcq;

import java.util.ArrayList;
import java.util.Vector;

import edu.upc.fib.ossim.filesystem.FileSystemPresenter;
import edu.upc.fib.ossim.mcq.view.PanelMCQCreatorFileSystem;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.SoSimException;

public class FileSystemMCQCreatorPresenter extends FileSystemPresenter{

	public FileSystemMCQCreatorPresenter(boolean openSettings) {
		super(openSettings);
		// TODO Auto-generated constructor stub
	}
	@Override
	public PanelTemplate createPanelComponents() {
		super.createPanelComponents();
		return new PanelMCQCreatorFileSystem(this);
	}
	@Override
	public String getXMLRoot() {
		// Returns XML root element 
		return  Functions.getInstance().getPropertyString("xml_root_mcq_fs");
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
		if(child!=3)
			super.putXMLData(child, data);
		else{
			int blockOnStep = new Integer (data.get(0).get(3).get(1)).intValue();
			int nbrAnswers = new Integer(data.get(0).get(4).get(1)).intValue();
			boolean includeAnswers = data.get(0).get(5).get(1).equals("true");
			String question = data.get(0).get(1).get(1);
			ArrayList<String> answers = new ArrayList<String>();
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
					answers.add(data.get(it).get(2).get(1));
				}
			}
			MCQSession.getInstance().getmcqCreationPanel().fillData(question,answerType, answers, answerbool,includeAnswers,blockOnStep,difficulty);
		}
	}
}
