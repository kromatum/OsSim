package edu.upc.fib.ossim.process.view;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PanelTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Main process scheduling panel. Tool bar allows process creation, scheduler settings managing, 
 * information view and time control apart from common actions such as: loading and saving simulations. <br/>
 * This panel contains 3 elements (painters) a main process queue (ready queue),
 * a secondary one with incoming processes and the cpu.   
 *  
 * @author Alex Macia
 * 
 * @see PanelTemplate
 * @see QueuePainter
 * @see ProcessorPainter
 */
public class PanelProcess extends PanelTemplate { 
	private static final long serialVersionUID = 1L;
	private JButton readyView;
	private JButton stateView;
	private JScrollPane mainView;
	
	
	/**
	 * Constructs a PanelProcess 
	 * 
	 * @param presenter	event manager
	 * @param keyLabelAdd	reference to add label string into bundle file	
	 */
	public PanelProcess(Presenter presenter, String keyLabelAdd) {
		//super(presenter, keyLabelAdd, "scheduling", true, true);
//->	affichage du boutton pour l'historique
		super(presenter, keyLabelAdd, "scheduling", true, true, true);
	}

	/**
	 * gets main view object
	 * @return
	 */
	public JScrollPane getMainView() {
		return mainView;
	}
	
	/**
	 * sets main view
	 * @param mainView
	 */
	public void setMainView(JScrollPane mainView) {
		this.mainView = mainView;
	}

	/**
	 * Adds components to panel, ready queue, incoming queue and cpu 
	 */
	public void initSpecificLayout() {
		
//->	//ajouter les bouttons pour basculer entre les vues : "ready" et "state"
		readyView = new JButton(Functions.getInstance().createImageIcon("ready.png"));
		readyView.setToolTipText(Translation.getInstance().getLabel("all_10"));
		readyView.setActionCommand("show_ready_view");
		readyView.addActionListener(presenter);
		header.add(readyView);
		stateView = new JButton(Functions.getInstance().createImageIcon("state.png"));
		stateView.setToolTipText(Translation.getInstance().getLabel("all_10"));
		stateView.setActionCommand("show_state_view");
		stateView.addActionListener(presenter);
		header.add(stateView);
		
		
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(ProcessPresenter.WAITING_PAINTER), 0, SpringLayout.WEST, header);
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.WAITING_PAINTER), 35, SpringLayout.SOUTH, header);
		pane.add(presenter.getPainter(ProcessPresenter.WAITING_PAINTER));
		
		
		QueuePainter procs = (QueuePainter) presenter.getPainter(ProcessPresenter.PROCS_PAINTER);
		JScrollPane scroll1 = new JScrollPane(procs);
		scroll1.setPreferredSize(new Dimension(procs.getViewPortwidth()+10, procs.getViewPortheight()+10));
		layout.putConstraint(SpringLayout.WEST, scroll1, 10, SpringLayout.EAST, presenter.getPainter(ProcessPresenter.WAITING_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, scroll1, 10, SpringLayout.SOUTH, header);
		pane.add(scroll1);
//->
		mainView = scroll1;
		
		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER), 0, SpringLayout.WEST, header);
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER), 40, SpringLayout.SOUTH, scroll1);
		pane.add(presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));

		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(ProcessPresenter.IO_PAINTER), 10, SpringLayout.EAST, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.IO_PAINTER), 0, SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));
		pane.add(presenter.getPainter(ProcessPresenter.IO_PAINTER));

		layout.putConstraint(SpringLayout.WEST, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER), 10, SpringLayout.EAST, presenter.getPainter(ProcessPresenter.IO_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER), 0, SpringLayout.NORTH, presenter.getPainter(ProcessPresenter.IO_PAINTER));
		pane.add(presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		
		
//->
		JLabel up3 = new JLabel(Functions.getInstance().createImageIcon("up3.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up3, 50, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.WAITING_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up3, 10, SpringLayout.SOUTH, header);
		pane.add(up3);
		
		
		JLabel up2 = new JLabel(Functions.getInstance().createImageIcon("up2.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up2, 50, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.WAITING_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up2, 0, SpringLayout.SOUTH, presenter.getPainter(ProcessPresenter.WAITING_PAINTER));
		pane.add(up2);
		
		
		JLabel up = new JLabel(Functions.getInstance().createImageIcon("up.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up, 70, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.ARRIVING_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up, 5, SpringLayout.SOUTH, scroll1);
		pane.add(up);

		JLabel up_io = new JLabel(Functions.getInstance().createImageIcon("up.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up_io, 0, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.IO_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up_io, 0, SpringLayout.NORTH, up);
		pane.add(up_io);
		
		JLabel left = new JLabel(Functions.getInstance().createImageIcon("left.png"));
		layout.putConstraint(SpringLayout.WEST, left, -35, SpringLayout.WEST, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, left, 15, SpringLayout.SOUTH, scroll1);
		pane.add(left);

		JLabel down = new JLabel(Functions.getInstance().createImageIcon("down.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, down, 10, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, down, 0, SpringLayout.NORTH, up);
		pane.add(down);
		
		JLabel up_cpu = new JLabel(Functions.getInstance().createImageIcon("up.png"));
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, up_cpu, -10, SpringLayout.HORIZONTAL_CENTER, presenter.getPainter(ProcessPresenter.PROCESSOR_PAINTER));
		layout.putConstraint(SpringLayout.NORTH, up_cpu, 0, SpringLayout.NORTH, up);
		pane.add(up_cpu);
	}

} 

