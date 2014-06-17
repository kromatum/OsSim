package edu.upc.fib.ossim.mcq.view;

import javax.swing.SpringLayout;

import edu.upc.fib.ossim.mcq.MCQSession;
import edu.upc.fib.ossim.process.view.PanelProcess;
import edu.upc.fib.ossim.template.Presenter;

public class PanelMCQCreatorProcess extends PanelProcess{

	public PanelMCQCreatorProcess(Presenter presenter, String keyLabelAdd) {
		super(presenter, keyLabelAdd);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private MCQCreationPanel mcqCreationPanel;
	
	@Override
	public void initSpecificLayout() {
		super.initSpecificLayout();
		mcqCreationPanel = MCQSession.getInstance().getmcqCreationPanel();
		layout.putConstraint(SpringLayout.EAST, mcqCreationPanel, -10, SpringLayout.EAST, pane);
		layout.putConstraint(SpringLayout.SOUTH, mcqCreationPanel, -10, SpringLayout.SOUTH, pane);
		pane.add(mcqCreationPanel);
	}
	

}
