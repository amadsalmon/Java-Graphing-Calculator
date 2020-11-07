/**
 * 
 */
package grapher.ui;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import grapher.fc.Function;
import grapher.fc.FunctionFactory;

public class Interaction
		 extends MouseAdapter implements ListSelectionListener, ActionListener {

	final static int MARGIN = 40;
	
	Grapher m_grapher;
	JScrollPane m_listScrollPane;
	ListSelectionModel m_listSelectionModel;
	JFrame m_frame;
	JButton m_minusButton;
	int m_x, m_y;
	int m_button;
	int m_state; //TODO: replace int with real state
	Point m_start, m_end;
	boolean m_drawR;

	public Interaction(JScrollPane scrollListView, ListSelectionModel listSelectionModel, Grapher grapher,
			JFrame frame, JButton minusButton) {
		m_grapher = grapher;
		m_listScrollPane = scrollListView;
		m_listSelectionModel = listSelectionModel;
		m_frame = frame;
		m_minusButton = minusButton;
		m_button = MouseEvent.NOBUTTON;
		m_start = new Point(0, 0);
		m_end = new Point(0, 0);
		m_drawR = false;
	}

	/**
	 * Effectue un zoom relatif au taux de scroll et centré sur p le curseur de la
	 * souris.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point p = new Point(e.getX(), e.getY());
		m_grapher.zoom(p, e.getWheelRotation());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY()); 
		int button = e.getButton();
		
		switch (button) {
			case MouseEvent.BUTTON1:
				m_grapher.zoom(p, 5);
				break;
			case MouseEvent.BUTTON3:
				m_grapher.zoom(p, -5);
				break;
		}
		m_button = MouseEvent.NOBUTTON;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		m_button = e.getButton();
		
		switch (m_button) {
		case MouseEvent.BUTTON1:
			m_frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
			m_button = MouseEvent.BUTTON1;
			break;
		case MouseEvent.BUTTON3:
			m_frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			if(m_start.x >= MARGIN && m_start.y >= MARGIN)
				m_frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			m_start = new Point(e.getX(), e.getY());
			break;
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
	
	/**
	 * Dessine un rectangle en suivant la diagonale dessinée par la souris entre le
	 * moment où l'utilisateur presse la souris et son relâchement.
	 */
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

	/**
	 * Est invoquée lorsqu'une nouvelle cellule en barre latérale est selectionnée.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (m_listSelectionModel.getSelectedIndices().length > 0) {

			int selectedIndex = m_listSelectionModel.getSelectedIndices()[0];

			Function selectedFunction = null;
			Object objectToEvaluate = m_grapher.model.getValueAt(selectedIndex, 0);
			if (objectToEvaluate instanceof String) {
				selectedFunction = FunctionFactory.createFunction((String) objectToEvaluate);
			} else {
				try {
					selectedFunction = (Function) objectToEvaluate;
				} catch (ClassCastException e2) {
					System.out.println("Interaction.valueChanged - Cast Exception: " + e);
				}

			}
			m_grapher.m_selectedFunction = selectedFunction;
			if (m_grapher.m_selectedFunction != null) {
				m_minusButton.setEnabled(true);
			}
			m_grapher.repaint();
		}
	}

	/**
	 * Méthode invoquée lors de l'actionnement d'un bouton de l'interface graphique.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean uiUpdateNeeded = false; // Boolean state to limit useless though costly UI updates.

		String actionCommand = e.getActionCommand();
		if (actionCommand == "-" || actionCommand == "Remove selected expression") {
			// TODO (Amad): make it impossible to click the minus button if no function is
			// selected in the listScrollPane.
			if (m_grapher.m_selectedFunction != null) {
				int indexOfSelectedFunction = m_grapher.indexOfFunction(m_grapher.m_selectedFunction);
				if (indexOfSelectedFunction != -1) { // only if selectedFunction was successfully found
					m_grapher.model.removeRow(indexOfSelectedFunction);
					m_grapher.m_selectedFunction = null;
					m_grapher.model.fireTableRowsDeleted(indexOfSelectedFunction, indexOfSelectedFunction);
					m_minusButton.setEnabled(false);
					uiUpdateNeeded = true;
				}
			}
		} else if (actionCommand == "+" || actionCommand == "Add expression") {
			String s = (String) JOptionPane.showInputDialog(m_frame,
					"What mathematical function do you wish to add to the graph?\n "
							+ "Please type its standard expression in lowercase, with x as its unique variable.",
					"Add a function to graph", JOptionPane.PLAIN_MESSAGE);

			// If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
				try {
					m_grapher.add(s);
					m_grapher.model.fireTableDataChanged();
					uiUpdateNeeded = true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(m_frame, "Unknown expression. Please try again.", "Inane error",
							JOptionPane.ERROR_MESSAGE);
					System.err.println("Unknown expression resulted in an exception: " + e2);
				}
			}
		} else {
			System.err.println("Unhandled ActionEvent:" + '\n' + e);
		}

		if (uiUpdateNeeded) {
			m_grapher.repaint();
		}
	}

}
