package edu.upc.fib.ossim.mcq.view;

import javax.swing.SpringLayout;

import edu.upc.fib.ossim.filesystem.view.PanelFileSystem;
import edu.upc.fib.ossim.template.Presenter;

public class PanelMCQViewFileSystem extends PanelFileSystem{

	public PanelMCQViewFileSystem(Presenter presenter) {
		super(presenter);
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
