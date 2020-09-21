/**
 * 
 */
package grapher.ui;

import java.awt.Cursor;
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
	
	public Interaction(Grapher grapher, JFrame frame){
		m_grapher = grapher;
		m_frame = frame;
	}
	 

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY());
		m_grapher.zoom(p, 10);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			m_frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else if (e.getButton() == MouseEvent.BUTTON3){
			m_frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		m_frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
		m_grapher.translate(- m_x + e.getX(),- m_y + e.getY());
		m_x = e.getX();
		m_y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m_x = e.getX();
		m_y = e.getY();
	}

	
}
