package edu.upc.fib.ossim.process.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Represents a processes queue. Processes enter queue at its appropriate position depending on 
 * scheduler settings, and are laid out horizontally, an arrow is placed between processes 
 * showing queue direction. A brief information is shown over every process. <br/>
 * A pop up menu allows process update and delete 
 * 
 * @author Alex Macia
 * 
 * @see PainterTemplate
 */
public class HistoryPainter extends PainterTemplate {
	private static final long serialVersionUID = 1L;
	private static final int ARROW_WIDTH = 20;
	private static final int P_HEIGHT = 30;
	private static final int P_MINBURSTS = 5;
	private int viewPortwidth;
	private int viewPortheight;
	private String keytitle;
//->
	
	/**
	 * Constructs a ProcessPainter, creates the pop up menu and initialize painter's title.  
	 * 
	 * @param presenter	event manager
	 * @param menuItems	pop up menu items
	 * @param keytitle	reference to queue label string into bundle file	
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 * @see ProcessPresenter#iterator(int i)
	 */
	public HistoryPainter(Presenter presenter, Vector<String[]> menuItems, String keytitle, int width, int height) {
		super(presenter, menuItems, width, height);
		this.viewPortwidth = width;
		this.viewPortheight = height;
		this.keytitle = keytitle;
	}

	/**
	 * Draws processes queue components, a set of colored rectangles represents processes, after every process there is an arrow icon. 
	 * When queue width exceeds canvas width, queue enlarge and revalidates to perform scroll update
	 * 
	 * @param g	graphic context
	 */
	public void paint(Graphics g) {
		g2 = (Graphics2D) g;
		Dimension size = getSize();
		int w = (int)size.getWidth();
		int h = (int)size.getHeight();
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, w, h);
		
		map.clear();

		// Draws a processes queue into area x,y,w,h 
		int width = 0;
		int last_x = 20; //?w - 20; // Starts at queue's left side
		int process_y = 40;
		int intialPosition;
		int position;
		
//->
		// background
		//g2.setPaint(bg);
		g2.setColor(Color.WHITE);
		g2.fillRect(2, 2, w-2, h-2);
		g2.setColor(Color.BLACK);
		g2.drawRect(0, 0, w, h);
		
		// Draw ready queue
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		g2.setColor(Color.GRAY);
		g2.drawString(Translation.getInstance().getLabel(keytitle), w/2 - 30, 20);
		
		
//-> dimensionner le painter
		int burstWIDTH = 70;
		int burstMarge = 20;
		int timeTotal = ((ProcessPresenter) presenter).getTotalTime();
		//System.out.println("getTotalTime : " + ((ProcessPresenter) presenter).getTotalTime());
		viewPortheight = process_y + P_HEIGHT + 30 + (timeTotal * (burstWIDTH + burstMarge)/P_MINBURSTS);
		setPreferredSize(new Dimension(viewPortwidth, viewPortheight));
		revalidate(); // Updates scroll
		
		//write time
		for(int k=0;k<timeTotal;k++)
			g2.drawString(Integer.toString(k+1), 10, process_y + P_HEIGHT + 30 + 12 + (k * (burstWIDTH + burstMarge)/P_MINBURSTS));
		
		
//->		Iterator<Integer> it = presenter.iterator(0);
		Iterator<Integer> it = presenter.iterator(1);
		intialPosition = -1;
		Vector<Hashtable<Integer, Boolean>> historicBurst = ((ProcessPresenter) presenter).getHistoricBurst();
		
		while (it.hasNext()) {
			g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			Integer pid = it.next();
			
			// Rectangle width
			width = 10;
			
			if (last_x + width + 10 + ARROW_WIDTH < 0) {
				int newWidth = (last_x + width + 10 + ARROW_WIDTH) + 20;
				setPreferredSize(new Dimension(newWidth, h));
				revalidate(); // Updates scroll 
			}
			
			// Process
			width = 50;
			drawProcessVertical(pid.intValue(), last_x + 20, process_y, width, P_HEIGHT);
			
			// CPU or I/O burst cycle
//->
			intialPosition++;
			
			for(int i=0; i <historicBurst.size(); i++){
				Hashtable<Integer, Boolean> hBurst = historicBurst.get(i);
				
				Set<Integer> set = hBurst.keySet();
				Iterator<Integer> itr = set.iterator();
				while(itr.hasNext()){
					position = itr.next();
					if (position == intialPosition) drawBurstsCycleVertical(pid, last_x + position + 20, process_y + P_HEIGHT + 30, 70/P_MINBURSTS, 15, i, hBurst.get(position));
				}
			}
			
			// I/O CPU
			g2.setColor(Color.BLACK);
			g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
			
			g2.drawString("CPU", last_x  + 25, process_y + P_HEIGHT + 20);
			
			last_x += 10 + ARROW_WIDTH;
		}
		
		if (last_x >= viewPortwidth) {
			// Restore initial width
			viewPortwidth = last_x + 20;
			setPreferredSize(new Dimension(viewPortwidth, viewPortheight));
			revalidate(); // Updates scroll 
		}
		if (process_y >= viewPortheight) {
			// Restore initial height
			viewPortheight = process_y;
			setPreferredSize(new Dimension(viewPortwidth, viewPortheight));
			revalidate(); // Updates scroll 
		}
	}
	
	
//-> drawBurstsCycleVertical
	private void drawBurstsCycleVertical(int pid, int x, int y, int width, int height, int historicHeight, boolean cpuBurst) {
		float[] dash = {2.0f};
		BasicStroke usualStroke = new BasicStroke();
		BasicStroke dashStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,10.0f,dash,0.0f);
		int burstWIDTH = 70;
		int burstMarge = 20;
		
		Rectangle rect = null;
		rect = new Rectangle(x + 10,y + historicHeight * (burstWIDTH + burstMarge)/P_MINBURSTS,width,height);

		g2.setColor(presenter.getColor(pid));
		if(!cpuBurst) g2.setStroke(dashStroke);
		g2.fill(rect);
		g2.setColor(Color.BLACK);
		g2.draw(rect);
		g2.setStroke(usualStroke);
		
		//->redessiner la surface de travail verticalement.
		if (y + (historicHeight + 1)*(burstWIDTH + burstMarge)/P_MINBURSTS > viewPortheight) {
			viewPortheight = y + historicHeight * (burstWIDTH + burstMarge)/P_MINBURSTS + 40;
			setPreferredSize(new Dimension(viewPortwidth, viewPortheight));
			revalidate(); // Updates scroll 
		}
		
		//->redessiner la surface de travail horizontalement.
		if ((x + width) >= viewPortwidth) {
			// Restore initial width
			viewPortwidth = x + width + 40;
			setPreferredSize(new Dimension(viewPortwidth, viewPortheight));
			revalidate(); // Updates scroll 
		}
	}
	
	
	private void drawProcessVertical(int pid, int x, int y, double width, double height) {
		RoundRectangle2D rect = new RoundRectangle2D.Double(x,y,width,height, 5, 5); // Process Rectangle
		map.put(rect, new Integer(pid));
		
		// Fill
		g2.setColor(presenter.getColor(pid));
		g2.fill(rect);
		
		// Bound
		g2.setColor (Color.BLACK);
		g2.draw(rect);

		// Information
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D bounds;
		int xText = x + 5;
	    int yText = y + 20;

		String s = Integer.toString(pid);
		bounds = g2.getFont().getStringBounds(s, frc);
		if (bounds.getWidth() > width) {
			s = s.substring(0, 5);
			s += "...";
			bounds = g2.getFont().getStringBounds(s, frc);
		}
		g2.setColor(Color.GRAY);
		g2.drawString(s, xText, yText);
	}
	
	/**
 	 * Returns true when any rectangle representing a queued process contains position (x,y) and false otherwise    
	 * 
	 * @param o	rectangle representing a queued process
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	queued process contains (x,y) 
	 */
	public boolean contains(Object o, int x, int y){
		RoundRectangle2D r = (RoundRectangle2D) o;  
		return r.contains(x, y);
	}
	
	/**
	 * Returns painter's viewport width, this width is supposed to be constant (painter placed inside a scroll)  
	 * 
	 * @return painter's viewport width
	 */
	public int getViewPortwidth() {
		return viewPortwidth;
	}

	/**
	 * Returns painter's viewport height, this height is supposed to be constant (painter placed inside a scroll)
	 * 
	 * @return painter's viewport height
	 */
	public int getViewPortheight() {
		return viewPortheight;
	}
}


