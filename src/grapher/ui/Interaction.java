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
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class Interaction implements MouseListener,
									MouseWheelListener, MouseMotionListener, 
									ListSelectionListener, ActionListener {

	final static int MARGIN = 40;

	Grapher m_grapher;
	JFrame m_frame;
	int m_x, m_y;
	int m_button;
	int m_state;
	boolean m_drawR;
	Point m_start, m_end;
	
	JList<String> m_functions;

	public Interaction(Grapher grapher, JFrame frame) {
		m_grapher = grapher;
		m_frame = frame;
		m_button = MouseEvent.NOBUTTON;
		m_start = new Point(0,0);
		m_end = new Point(0,0);
		m_drawR = false;
	}

	public void addFunctions(JList<String> f) {
		m_functions = f;
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
			m_start = new Point(e.getX(), e.getY());
			if(m_start.x >= MARGIN && m_start.y >= MARGIN)
				m_frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			m_button = MouseEvent.BUTTON3;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		m_frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		if (m_button == MouseEvent.BUTTON3 && m_state == MouseEvent.MOUSE_DRAGGED) {
			m_end = new Point(e.getX(), e.getY());
			if (m_start.x >= MARGIN && m_start.y >= MARGIN)
				m_grapher.zoom(m_start, m_end);
		}
		m_button = MouseEvent.NOBUTTON;
		m_state = MouseEvent.NOBUTTON;
		m_drawR = false;
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
			if(m_start.x >= MARGIN && m_start.y >= MARGIN)
				m_drawR = true;
		}
		
		m_state = MouseEvent.MOUSE_DRAGGED;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m_x = e.getX();
		m_y = e.getY();
	}

	public void paint(Graphics2D g, int W, int H) {
		if(m_drawR)
			drawR(g, W, H);
		
		m_grapher.repaint();
	}
	
	public void clip(Point p, int W, int H) {
		
		if(p.x > W + MARGIN)
			p.x = W + MARGIN;
		
		if (p.x < MARGIN)
			p.x = MARGIN;
		
		if(p.y > H + MARGIN)
			p.y = H + MARGIN;
		
		if (p.y < MARGIN)
			p.y = MARGIN;
	}
	
	public void drawR(Graphics2D g, int W, int H) {
		
		Point p0 = m_start, p1 = m_end;

		clip(p1, W, H);
		
		int width = p1.x - p0.x, height = p1.y - p0.y;
		
		
		if (width >= 0 && height >= 0)
				g.drawRect(p0.x, p0.y, width, height);
		
			else if (width < 0 && height >= 0) 
				g.drawRect(p1.x, p0.y, -width, height);
		
		    else if (width >= 0 && height < 0) 
				g.drawRect(p0.x, p1.y, width, -height);
		
			else 
				g.drawRect(p1.x, p1.y, -width, -height);
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			m_grapher.setSelected(m_functions.getSelectedValuesList());
		}
			
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if (actionCommand.equals(new String("+")) || actionCommand.equals(new String ("Add..."))){	
			 String s = JOptionPane.showInputDialog("Nouvelle expression de paramètre (x)");
			
			 if(s != null & s.length() > 0) {
				 try {
					 m_grapher.add(s);
					 ListModel<String> old_functions = m_functions.getModel();
					 DefaultListModel<String> new_functions = new DefaultListModel<String>();
					 
					 for(int i = 0; i < old_functions.getSize(); i++) {
						 new_functions.addElement(old_functions.getElementAt(i).toString());
					 }
					 
					 new_functions.addElement(s);
					 m_functions.setModel(new_functions);
				 } catch (Exception ex) {
					 JOptionPane.showMessageDialog(m_frame, "Fonction non reconnu");
				 }
			 }
			
			
		} else {
			//Removee functions in the grapher
			List<String> to_remove = m_functions.getSelectedValuesList();
			for(String function : to_remove) {
				m_grapher.remove(function);
			}
			
			//Remove functions in the JList
			ListModel<String> functions = m_functions.getModel();
			DefaultListModel<String> new_functions = new DefaultListModel<String>();
			for(int i = 0; i < functions.getSize(); i++) {
				if (!to_remove.contains(functions.getElementAt(i).toString())) {
					new_functions.addElement(functions.getElementAt(i).toString());
				}
			}
			
		
			m_functions.setModel(new_functions);
		}
	}
		
}
