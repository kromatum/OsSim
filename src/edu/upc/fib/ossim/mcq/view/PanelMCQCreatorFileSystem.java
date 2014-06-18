package edu.upc.fib.ossim.mcq.view;

import javax.swing.SpringLayout;

import edu.upc.fib.ossim.filesystem.view.PanelFileSystem;
import edu.upc.fib.ossim.mcq.MCQSession;
import edu.upc.fib.ossim.template.Presenter;

public class PanelMCQCreatorFileSystem extends PanelFileSystem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MCQCreationPanel mcqCreationPanel;
	
	public PanelMCQCreatorFileSystem(Presenter presenter) {
		super(presenter);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void initSpecificLayout() {
		super.initSpecificLayout();
		mcqCreationPanel = MCQSession.getInstance().getmcqCreationPanel();
		layout.putConstraint(SpringLayout.EAST, mcqCreationPanel, -10, SpringLayout.EAST, pane);
		layout.putConstraint(SpringLayout.SOUTH, mcqCreationPanel, -10, SpringLayout.SOUTH, pane);
		pane.add(mcqCreationPanel);
	}

}
