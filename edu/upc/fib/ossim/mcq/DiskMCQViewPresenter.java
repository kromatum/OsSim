package edu.upc.fib.ossim.mcq;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import edu.upc.fib.ossim.AppSession;
import edu.upc.fib.ossim.disk.DiskPresenter;
import edu.upc.fib.ossim.mcq.view.PanelMCQViewDisk;
import edu.upc.fib.ossim.mcq.view.PanelMCQViewProcess;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.SoSimException;

public class DiskMCQViewPresenter extends DiskPresenter{

	public DiskMCQViewPresenter(boolean openSettings) {
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
	@Override
	public void actionStop() {
		super.actionStop();	
		panel.disableRunning(true);
	}
	@Override
	public boolean actionTimer() {
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
		return  Functions.getInstance().getPropertyString("xml_root_mcq_dsk");
	}
	public Vector<String> getXMLChilds() {
		Vector<String> childs = super.getXMLChilds();
		childs.add("mcq");
		return childs;
	}
	@Override
	public void putXMLData(int child, Vector<Vector<Vector<String>>> data) throws SoSimException {
		System.out.println(data);
		if(child!=2)
			super.putXMLData(child, data);
		else{
			int blockOnStep = new Integer (data.get(0).get(3).get(1)).intValue();
			int nbrAnswers = new Integer(data.get(0).get(4).get(1)).intValue();
			boolean includeAnswers =  data.get(0).get(5).get(1).equals("true");
			ArrayList<String> answers = new ArrayList<String>();
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
			((PanelMCQViewDisk)panel).addmcqViewPanel(MCQSession.getInstance().getmcqViewPanel(MCQSession.getInstance().getMCQChooserDialog().getQuestionNumber(),data.get(0).get(1).get(1), Integer.parseInt(data.get(0).get(2).get(1)), nbrAnswers, answers,blockOnStep,correct_answers));
			panel.disableRunning(true);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		//Do Nothing
	}
	@Override
	public PanelTemplate createPanelComponents() {
		super.createPanelComponents();
		return new PanelMCQViewDisk(this,"dk_42");
	}
	
}
