/**
 * 
 */
package grapher.ui;

import java.awt.Cursor;
import java.awt.Graphics2D;
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
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import grapher.fc.Function;

public class Interaction implements MouseListener, MouseWheelListener, MouseMotionListener, ListSelectionListener, ActionListener {

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
		
		m_end.setLocation(m_x, m_y);
		m_state = MouseEvent.MOUSE_DRAGGED;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m_x = e.getX();
		m_y = e.getY();
	}
	
	public void draw(Graphics2D g) {
		if(m_state == MouseEvent.MOUSE_DRAGGED && m_button == MouseEvent.BUTTON3) {
			if(m_end.x - m_start.x >= 0 && m_end.y - m_start.y >= 0)
				g.drawRect(m_start.x, m_start.y, m_end.x - m_start.x, m_end.y - m_start.y);
			else if (m_end.x - m_start.x < 0 && m_end.y - m_start.y >= 0) {
				g.drawRect(m_end.x, m_start.y, m_start.x - m_end.x, m_end.y - m_start.y);
			} else if (m_end.x - m_start.x >= 0 && m_end.y - m_start.y < 0) {
				g.drawRect(m_start.x, m_end.y, m_end.x - m_start.x, m_start.y - m_end.y);
			} else {
				g.drawRect(m_end.x, m_end.y, m_start.x - m_end.x, m_start.y - m_end.y);
			}
		}
		m_grapher.repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Function selectedFunction = m_listScrollPane.getSelectedValue();
		m_grapher.m_selectedFunction = selectedFunction;
		m_grapher.repaint(); // AMAD: Should a repaint be allowed to get called from there?
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean uiUpdateNeeded = false; // State boolean to limit useless but costly UI updates.
		
		String actionCommand = e.getActionCommand();
		if (actionCommand == "-" || actionCommand == "Remove expression") {
			// TODO (Amad): make it impossible to click minus button if no function is selected in the listScrollPane.
			if (m_grapher.m_selectedFunction != null) {
				m_grapher.functions.removeElement(m_grapher.m_selectedFunction);
				m_grapher.m_selectedFunction = null;
			    uiUpdateNeeded = true;
			}
		} else if (actionCommand == "+" || actionCommand == "Add expression") {
			String s = (String) JOptionPane.showInputDialog(
                    m_frame,
                    "What mathematical function do you wish to add to the graph?\n "
                    + "Please type its standard name followed by \"(x)\", all in lowercase.",
                    "Add a function to graph",
                    JOptionPane.PLAIN_MESSAGE);

			// If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
				try {
					m_grapher.add(s);
					uiUpdateNeeded = true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(m_frame,
						    "Unknown expression. Please try again.",
						    "Inane error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			System.out.println(e);
		}
		
		if (uiUpdateNeeded) {
			// TODO (Amad): update listScrollPane to show updated list of functions. Still a bit buggy: UI is updated on window change only.
			m_grapher.repaint();
		}
	}

}
