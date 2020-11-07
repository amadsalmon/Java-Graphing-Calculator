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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import grapher.fc.Function;
import grapher.fc.FunctionFactory;

public class Interaction
		implements MouseListener, MouseWheelListener, MouseMotionListener, ListSelectionListener, ActionListener {

	Grapher m_grapher;
	JScrollPane m_listScrollPane;
	ListSelectionModel m_listSelectionModel;
	JFrame m_frame;
	int m_x, m_y;
	int m_button;
	int m_state;
	Point m_start, m_end;

	public Interaction(JScrollPane scrollListView, ListSelectionModel listSelectionModel, Grapher grapher,
			JFrame frame) {
		m_grapher = grapher;
		m_listScrollPane = scrollListView;
		m_listSelectionModel = listSelectionModel;
		m_frame = frame;
		m_button = MouseEvent.NOBUTTON;
		m_start = new Point(0, 0);
		m_end = new Point(0, 0);
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

	/**
	 * Dessine un rectangle en suivant la diagonale dessinée par la souris entre le
	 * moment où l'utilisateur presse la souris et son relâchement.
	 */
	public void draw(Graphics2D g) {
		if (m_state == MouseEvent.MOUSE_DRAGGED && m_button == MouseEvent.BUTTON3) {
			if (m_end.x - m_start.x >= 0 && m_end.y - m_start.y >= 0)
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
			m_grapher.repaint(); // AMAD: Should a repaint be allowed to get called from there?
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
