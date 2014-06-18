package edu.upc.fib.ossim.mcq;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.mcq.view.PanelMCQViewMemory;
import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.SoSimException;

public class MemoryMCQViewPresenter extends MemoryPresenter{

	public MemoryMCQViewPresenter(boolean openSettings) {
		super(false);
		// TODO Auto-generated constructor stub
		for(int it = 0 ; it < AppSession.getInstance().getMenu().getMenuCount() ; it++){
			try{
			AppSession.getInstance().getMenu().getMenu(it).setEnabled(false);
			}catch(Exception exc){
				//WEIRD BUG, Apparantly the code says there are 14 Menus available where in fact only 5 exists
				
			}
		}
		panel.disableRunning(true);
	}
	public void actionStop() {
		super.actionStop();	
		panel.disableRunning(true);
	}
	@Override
	public PanelTemplate createPanelComponents() {
		super.createPanelComponents();
		return new PanelMCQViewMemory(this,"me_42");
	}
	@Override
	public boolean actionTimer() throws SoSimException{
		if(timecontrols.getTime()== MCQSession.getInstance().getmcqViewPanel().getBlock_on_step())
			return true;
		else if(timecontrols.getTime()> MCQSession.getInstance().getmcqViewPanel().getBlock_on_step() && MCQSession.getInstance().getmcqViewPanel().getBlock_on_step()>0){
			actionStop();
			timecontrols.stop();
			return true;
		}
		else 
			return super.actionTimer();
	}
	public String getXMLRoot() {
		// Returns XML root element 
		return  Functions.getInstance().getPropertyString("xml_root_mcq_mem");
	}
	public Vector<String> getXMLChilds() {
		Vector<String> childs = super.getXMLChilds();
		childs.add("mcq");
		return childs;
	}
	@Override
	public void putXMLData(int child, Vector<Vector<Vector<String>>> data) throws SoSimException {
		if(child!=3)
			super.putXMLData(child, data);
		else{
			int blockOnStep = new Integer (data.get(0).get(3).get(1)).intValue();
			int nbrAnswers = new Integer(data.get(0).get(4).get(1)).intValue();
			ArrayList<String> answers = new ArrayList<String>();
			boolean includeAnswers =  data.get(0).get(5).get(1).equals("true");
			String correct_answers = "";
			for(int it = 1; it <= nbrAnswers; it++){
				answers.add(data.get(it).get(1).get(1));
				if(includeAnswers){
					correct_answers+=data.get(it).get(2).get(1);
					if(it!=nbrAnswers)
						correct_answers+=",";
					}
			}
			if(!includeAnswers)
				correct_answers = null;
			//MCQSession.getInstance().getmcqViewPanel(data.get(0).get(1).get(1), Integer.parseInt(data.get(0).get(2).get(1)), nbrAnswers, answers);
			((PanelMCQViewMemory)panel).addmcqViewPanel(MCQSession.getInstance().getmcqViewPanel(MCQSession.getInstance().getMCQChooserDialog().getQuestionNumber(),data.get(0).get(1).get(1), Integer.parseInt(data.get(0).get(2).get(1)), nbrAnswers, answers,blockOnStep,correct_answers));
			panel.disableRunning(true);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		//Do Nothing
	}
	

}
