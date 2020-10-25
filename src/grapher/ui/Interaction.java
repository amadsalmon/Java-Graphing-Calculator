/**
 * 
 */
package grapher.ui;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

public class Interaction implements MouseListener, MouseWheelListener, MouseMotionListener {

	Grapher m_grapher;
	JFrame m_frame;
	int m_x, m_y;
	int m_button;
	int m_state;
	boolean m_drawR;
	Point m_start, m_end;

	public Interaction(Grapher grapher, JFrame frame) {
		m_grapher = grapher;
		m_frame = frame;
		m_button = MouseEvent.NOBUTTON;
		m_start = new Point(0,0);
		m_end = new Point(0,0);
		m_drawR = false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point p = new Point(e.getX(), e.getY());
		m_grapher.zoom(p, e.getWheelRotation()); 
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY());
		if (e.getButton() == MouseEvent.BUTTON1)
			m_grapher.zoom(p, 5);
		else if (e.getButton() == MouseEvent.BUTTON3)
			m_grapher.zoom(p, -5);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			m_frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
			m_button = MouseEvent.BUTTON1;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			m_frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			m_button = MouseEvent.BUTTON3;
			m_start = new Point(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		m_frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		if (m_button == MouseEvent.BUTTON3 && m_state == MouseEvent.MOUSE_DRAGGED) {
			m_end = new Point(e.getX(), e.getY());
			m_grapher.zoom(m_start, m_end);
		}
		m_button = MouseEvent.NOBUTTON;
		m_state = MouseEvent.NOBUTTON;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (m_button == MouseEvent.BUTTON1)
			m_grapher.translate(-m_x + e.getX(), -m_y + e.getY());
		
		m_x = e.getX();
		m_y = e.getY();
		
		if (m_button == MouseEvent.BUTTON3) {
			m_end.setLocation(m_x, m_y);
			m_drawR = true;
		}
		
		m_state = MouseEvent.MOUSE_DRAGGED;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m_x = e.getX();
		m_y = e.getY();
	}

	public void drawR(Graphics2D g) {
		
		if(m_drawR) {
			Point p0 = m_start, p1 = m_end;
			
			if(p1.x - p0.x >= 0 && p1.y - p0.y >= 0)
					g.drawRect(p0.x, p0.y, p1.x - p0.x, p1.y - p0.y);
			
				else if (p1.x - p0.x < 0 && p1.y - p0.y >= 0) 
					g.drawRect(p1.x, p0.y, p0.x - p1.x, p1.y - p0.y);
			
			    else if (p1.x - p0.x >= 0 && p1.y - p0.y < 0) 
					g.drawRect(p0.x, p1.y, p1.x - p0.x, p0.y - p1.y);
			
				else 
					g.drawRect(p1.x, p1.y, p0.x - p1.x, p0.y - p1.y);
		}	
		
		m_drawR = false;
		m_grapher.repaint();
	}
}
