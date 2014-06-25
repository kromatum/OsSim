package edu.upc.fib.ossim.process.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;

import edu.upc.fib.ossim.process.ProcessPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;


/**
 * Paints CPU. Processes enter into CPU and are executed while time goes, 
 * process execution time remaining is highlighted, and a brief process information is shown 
 * 
 * @author Alex Macia
 */
public class HistoryStatsPainter extends PainterTemplate {
	private static final long serialVersionUID = 1L;
	private int viewPortwidth;
	private int viewPortheight;
	
	/**
	 * Constructs a ProcessorPainter
	 * 
	 * @param presenter	event manager
	 * @param width		canvas width
	 * @param height	canvas height
	 */
	public HistoryStatsPainter(Presenter presenter, int width, int height) {
		super(presenter, width, height);
//->
		viewPortwidth = width;
		viewPortheight = height;
	}

	/**
	 * Draws processor components, mainly a rectangle that represents a process  
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
		
				
		int pid = ((ProcessPresenter) presenter).getRunning();
		
//->
		//dessiner les ressources de la semaphore
		if(((ProcessPresenter) presenter).getVerrouNumber() > 0){
			g2.setPaint(Color.BLACK);
			g2.drawString("Resource: ", 2, 10);
			drawProcessSemaphore(pid, 60, 5, 20, 20);
		}
		
		//dessiner l'historique des opérations sur la variable partagée
		if(((ProcessPresenter) presenter).isSharedVariable()){
			g2.setPaint(Color.BLACK);
			g2.drawString("Variable: ", 2, 45);
			drawProcessVariable(pid, 60, 40, 20, 20);
		}
		
	}
	
//->	
	private void drawProcessSemaphore(int pid, int x, int y, int width, int height) {
		int[] sem = ((ProcessPresenter) presenter).getSemaphoreResource();
		Color color;
		
		for(int i=0; i<sem.length; i++){
			//dessiner le tableau des ressources de la semaphore
			
			if(sem[i] != 0) color = ((ProcessPresenter) presenter).getColor(sem[i]);
			else color = Color.GRAY;
			drawBurstResource(pid, x + (i*(width + 5)),y,width,height, color, "");
		}
	}
	
	private void drawBurstResource(int pid, int x, int y, int width, int height, Color color, String content) {		
		Rectangle rect = null;
		rect = new Rectangle(x,y,(int)width,(int)height);
		
//->		g2.setColor(presenter.getColor(pid));
		
		g2.setColor(color);
		
		g2.fill(rect);
		g2.setColor(Color.BLACK);
		g2.draw(rect);
		
		if(content.equals("1")) {
			//g2.setColor(Color.WHITE);
			g2.setColor(Color.RED);
			g2.drawString("r", x + 5, y + 15);
			g2.setColor(Color.BLACK);
		} else if(content.equals("2")) {
			g2.drawString("w", x + 5, y + 15);
		}
				
		//->redessiner la surface de travail verticalement.
		if (y + height >= viewPortheight) {
			viewPortheight = y + height + 40;
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
	
//->
	private void drawProcessVariable(int pid, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		Iterator<String> it = ((ProcessPresenter) presenter).getHistoricVariable().iterator();
		int i = 0;
		while(it.hasNext()){
			String[] s = it.next().split(";");
			
			//dessiner le cadre de la variable
			drawBurstResource(pid, x + (i*(width + 5)),y,width,height, ((ProcessPresenter) presenter).getColor(Integer.parseInt(s[1])), s[2]);
			
			//écrire le temps
			g2.setColor(Color.GRAY);
			g2.drawString(s[0], x + (i*(width + 5)) + 5, y + 30 + 15);
			
			i++;
		}
	}

	/**
 	 * Returns true when the rectangle representing process at cpu contains position (x,y) and false otherwise    
	 * 
	 * @param o	graphic object
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	process at cpu contains (x,y) 
	 */
	public boolean contains(Object o, int x, int y){
		RoundRectangle2D r = (RoundRectangle2D) o;  
		return r.contains(x, y);
	}
	
//->>>
	/**
	 * --------------------------------->
	 * Returns painter's viewport width, this width is supposed to be constant (painter placed inside a scroll)  
	 * 
	 * @return painter's viewport width
	 */
	public int getViewPortwidth() {
		return viewPortwidth;
	}

	/**
	 * --------------------------------->
	 * Returns painter's viewport height, this height is supposed to be constant (painter placed inside a scroll)
	 * 
	 * @return painter's viewport height
	 */
	public int getViewPortheight() {
		return viewPortheight;
	}
}


