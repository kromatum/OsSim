package edu.upc.fib.ossim.mcq.view;

import javax.swing.SpringLayout;

import edu.upc.fib.ossim.memory.view.PanelMemory;
import edu.upc.fib.ossim.template.Presenter;

public class PanelMCQViewMemory extends PanelMemory {

	public PanelMCQViewMemory(Presenter presenter, String keyLabelAdd) {
		super(presenter, keyLabelAdd);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void addmcqViewPanel(MCQViewPanel mcqViewPanel){
		layout.putConstraint(SpringLayout.EAST, mcqViewPanel, -10, SpringLayout.EAST, pane);
		layout.putConstraint(SpringLayout.SOUTH, mcqViewPanel, -10, SpringLayout.SOUTH, pane);
		pane.add(mcqViewPanel);
		pane.repaint();
	}
	
	

}
