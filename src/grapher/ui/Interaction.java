/**
 * 
 */
package grapher.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import grapher.fc.Function;

public class Interaction implements MouseListener, MouseWheelListener, MouseMotionListener, ListSelectionListener {

	JSplitPane m_splitPane;
	Grapher m_grapher;
	JList<Function> m_listScrollPane;
	JFrame m_frame;
	int m_x, m_y;
	int m_button;
	int m_state;
	Point m_start, m_end;

	public Interaction(JSplitPane splitPane, JFrame frame) {
		m_splitPane = splitPane;
		m_grapher = (Grapher) splitPane.getRightComponent();
		m_listScrollPane = (JList<Function>) splitPane.getLeftComponent();
		m_frame = frame;
		m_button = MouseEvent.NOBUTTON;
		m_start = new Point(0,0);
		m_end = new Point(0,0);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point p = new Point(e.getX(), e.getY());
		m_grapher.zoom(p, e.getWheelRotation()); // Effectue zoom relatif au taux de scroll et centré sur p le curseur de la souris. 
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
			m_grapher.drawRectangle(m_start, m_end);
		}
		
		m_state = MouseEvent.MOUSE_DRAGGED;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m_x = e.getX();
		m_y = e.getY();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Function selectedFunction = m_listScrollPane.getSelectedValue();
		m_grapher.m_selectedFunction = selectedFunction;
		m_grapher.repaint(); // AMAD: Should a repaint be allowed to get called from there?
	}

}
