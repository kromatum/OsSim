package edu.upc.fib.ossim.utils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.process.view.HistoryPainter;
import edu.upc.fib.ossim.process.view.HistoryStatsPainter;
import edu.upc.fib.ossim.template.Presenter;

public class HistoryDialog extends EscapeDialog {
	private static final long serialVersionUID = 1L;

	private JPopupMenu popup;
	private Vector<JMenuItem> items;
	private Vector<String[]> menuItems;
	private Presenter presenter;
	protected JPanel pane;
	protected SpringLayout layout;
	
	public HistoryDialog(Presenter presenter, Vector<String[]> menuItems, String keyTitle, String keyHelp, boolean modal, int width, int height) {
		super();
		createPopupMenu(presenter);
		historyDialog(presenter, keyTitle, keyHelp, modal, width, height);
	}
	
	public HistoryDialog(Presenter presenter, String keyTitle, String keyHelp, boolean modal, int width, int height) {
		super();
		historyDialog(presenter, keyTitle, keyHelp, modal, width, height);
	}
	
	private void historyDialog(Presenter presenter, String keyTitle, String keyHelp, boolean modal, int width, int height) {
		this.presenter = presenter;
		this.setTitle(Translation.getInstance().getLabel(keyTitle));
		this.setModal(modal);
		JPanel pn = init(width, height);
		this.setContentPane(pn);
		this.pack();
	}
	
	private JPanel init(int width, int height) {
		JPanel pn = initLayout();
		pn.setPreferredSize(new Dimension(width,height));
		return pn;
		
	}
	
	private JPanel initLayout() {
		this.setLayout(new BorderLayout());
		
		pane = new JPanel();
		layout = new SpringLayout();
		pane.setLayout(layout);
		
		HistoryStatsPainter sprocs = (HistoryStatsPainter) presenter.getPainter(ProcessPresenter.HIST_STATS_PAINTER);
		JScrollPane scroll = new JScrollPane(sprocs);
		scroll.setPreferredSize(new Dimension(sprocs.getViewPortwidth()+10, sprocs.getViewPortheight()+10));
		layout.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.NORTH, pane);
		layout.putConstraint(SpringLayout.WEST, scroll, 5, SpringLayout.WEST, pane);
		pane.add(scroll);

		HistoryPainter hprocs = (HistoryPainter) presenter.getPainter(ProcessPresenter.HIST_PAINTER);
		JScrollPane scroll1 = new JScrollPane(hprocs);
		scroll1.setPreferredSize(new Dimension(hprocs.getViewPortwidth()+10, hprocs.getViewPortheight()+10));
		layout.putConstraint(SpringLayout.NORTH, scroll1, 20, SpringLayout.SOUTH, scroll);
		layout.putConstraint(SpringLayout.WEST, scroll1, 5, SpringLayout.WEST, scroll);
		
		pane.add(scroll1);		

		
		return pane;
	}
	
	
	private void createPopupMenu(Presenter presenter) {
		//Create the popup menu.
		// Each element items has 3 String's : command, label, icon
		popup = new JPopupMenu();
		items = new Vector<JMenuItem>();
		for (int i = 0; i < menuItems.size(); i++) {
			JMenuItem item = new JMenuItem(menuItems.get(i)[1],Functions.getInstance().createImageIcon(menuItems.get(i)[2]));
			item.setActionCommand(menuItems.get(i)[0]);
			item.addActionListener(presenter);
			popup.add(item);
			items.add(item);
		}
	}
}
