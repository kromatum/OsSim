package edu.upc.fib.ossim.process.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.Functions;
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
public class StatePainterVertical extends PainterTemplate {
	private static final long serialVersionUID = 1L;
	private static final int ARROW_HEIGHT = 20;
	private static final int ARROW_WIDTH = 20;
	private static final int P_HEIGHT = 30; //? = 70;
	private static final int P_MINWIDTH = 100; // = 70;
	private static final int P_MINBURSTS = 5;
	private BufferedImage arrow;
	private int viewPortwidth;
	private int viewPortheight;
	private String keytitle;
	
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
	public StatePainterVertical(Presenter presenter, Vector<String[]> menuItems, String keytitle, int width, int height) {
		super(presenter, menuItems, width, height);
		arrow = Functions.getInstance().loadImage("arrow1.png");
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
		int last_x = 20; //?w - 20; // Starts at queue's right side 
		int process_y = 40;
		
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
		
		
//->		Iterator<Integer> it = presenter.iterator(0);
		//System.out.println("QueuePainter.repaint() ::: in progress !");
		Iterator<Integer> it = presenter.iterator(1);
		
		int psize;
		while (it.hasNext()) {
			g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			Integer pid = it.next();
			
			psize = ((ProcessPresenter) presenter).getSize(pid);
			
			// Rectangle width 
			if (psize > 5) width = P_MINWIDTH + (psize - 5) * P_MINWIDTH/P_MINBURSTS;
			else width =  P_MINWIDTH;
			
			if (last_x + width + 10 + ARROW_WIDTH < 0) {
				int newWidth = (last_x + width + 10 + ARROW_WIDTH) + 20;
				setPreferredSize(new Dimension(newWidth, h));
				revalidate(); // Updates scroll 
			}
			
			width = 50;
			drawProcessVertical(pid.intValue(), last_x + 15, process_y, width, P_HEIGHT);
			
			// CPU I/O burst cycle
			drawBurstsCycleVertical(pid, psize, last_x + 20, process_y + P_HEIGHT + 30, 70/P_MINBURSTS, 15);
			
			// I/O CPU
			g2.setColor(Color.BLACK);
			g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));

			g2.drawString("CPU", last_x  + 25, process_y + P_HEIGHT + 20);
			g2.drawString("I/O", last_x  + 25 + 25, process_y + P_HEIGHT + 20);
			
			last_x += 60 + ARROW_WIDTH;
		}
		
		
		if (last_x >= viewPortwidth) {
			// Restore initial width
			viewPortwidth = last_x;
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
	private void drawBurstsCycleVertical(int pid, int psize, int x, int y, int width, int height) {
		int current = ((ProcessPresenter) presenter).getCurrent(pid);
//->
		int burstWIDTH = 70;
		int burstMarge = 20;
		for(int i=0; i < psize; i++) {
			boolean cpuBurst = ((ProcessPresenter) presenter).isCPUBurst(pid, i); 
			Rectangle rect = null;

			if (cpuBurst) rect = new Rectangle(x + 10,y + i*(burstWIDTH + burstMarge)/P_MINBURSTS,width,height); 
			else rect = new Rectangle(x + 25,y + i*(burstWIDTH + burstMarge)/P_MINBURSTS,width,height);
			if (i == current) {
				if(pid == ((ProcessPresenter) presenter).getRunning())
					g2.setColor(Color.RED);
				else if(!cpuBurst)
					g2.setColor(Color.ORANGE);
				
			}
			else g2.setColor(presenter.getColor(pid));
			g2.fill(rect);
			g2.setColor(Color.BLACK);
			g2.draw(rect);
//->
			if (i == current) {
				drawArrow(arrow, x - ARROW_WIDTH, (y + i*(burstWIDTH + burstMarge)/P_MINBURSTS), ARROW_HEIGHT, ARROW_WIDTH);
			}
			
			//->redessiner la surface de travail verticalement.
			if (y + (i + 1)*(burstWIDTH + burstMarge)/P_MINBURSTS > viewPortheight) {
				viewPortheight = y + (i + 1)*(burstWIDTH + burstMarge)/P_MINBURSTS;
				setPreferredSize(new Dimension(viewPortwidth, viewPortheight));
				revalidate(); // Updates scroll 
			}
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
		Vector<String> info = presenter.getInfo(pid);
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D bounds;
		int xText = x + 5;
	    int yText = y + 20;
		
		Iterator<String> it = info.iterator();
		String s = it.next();
		bounds = g2.getFont().getStringBounds(s, frc);
		if (bounds.getWidth() > width) {
			s = s.substring(0, 5);
			s += "...";
			bounds = g2.getFont().getStringBounds(s, frc);
		}
		g2.setColor(Color.GRAY);
		g2.drawString(s, xText, yText);
		xText += 20;
	}

	private void drawArrow(BufferedImage arrow, int x, int y, int width, int height) {
		Rectangle rect = new Rectangle(x, y, width, height); 
		g2.setPaint(new TexturePaint(arrow, rect));
		g2.fill(rect);
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


