package edu.upc.fib.ossim.mcq.view;

import javax.swing.SpringLayout;

import edu.upc.fib.ossim.mcq.MCQSession;
import edu.upc.fib.ossim.memory.view.PanelMemory;
import edu.upc.fib.ossim.template.Presenter;

public class PanelMCQCreatorMemory extends PanelMemory{

	public PanelMCQCreatorMemory(Presenter presenter, String keyLabelAdd) {
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
		layout.putConstraint(SpringLayout.WEST, mcqCreationPanel, 10, SpringLayout.EAST, scroll2);
		
		pane.add(mcqCreationPanel);
	}
}
