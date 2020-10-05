package grapher.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import grapher.fc.Function;


public class Main extends JFrame {
	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		
		Grapher grapher = new Grapher();	
		for(String expression : expressions) {
			grapher.add(expression);
		}
		
		JList<Function> listScrollPane = new JList<Function>(grapher.functions);
		
		
		/****************  MENU BAR  ****************/
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItemAdd, menuItemRemove;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the menu.
		menu = new JMenu("Expressions");
		menuBar.add(menu);
		menuItemAdd = new JMenuItem("Add expression");
		menu.add(menuItemAdd);
		menuItemRemove = new JMenuItem("Remove selected expression");
		menu.add(menuItemRemove);
		setJMenuBar(menuBar);
		
		
		
		/****************  SPLIT PANE  ****************/
		// Create a split pane with the two panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				                           listScrollPane, grapher);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		// Provide minimum sizes for the two components in the split pane
		Dimension minimumSize1 = new Dimension(100, 50);
		listScrollPane.setMinimumSize(minimumSize1);
		Dimension minimumSize2 = new Dimension(100, 50);
		grapher.setMinimumSize(minimumSize2);
		

		
		/****************  TOOLBAR CONTAINING +/- BUTTONS  ****************/
		JToolBar toolbar = new JToolBar();
	    toolbar.setRollover(true);
	    toolbar.setFloatable(false);
	    JButton minusButton = new JButton("-");
	    JButton plusButton = new JButton("+");
	    toolbar.add(minusButton);
	    toolbar.add(plusButton);	    
	    getContentPane().add(toolbar,BorderLayout.SOUTH);

	    
	    
	    /****************  ACTION LISTENING  ********/
		Interaction i = new Interaction(splitPane,this);

		grapher.setInteraction(i);
		grapher.addMouseListener(i);
		grapher.addMouseMotionListener(i);
		grapher.addMouseWheelListener(i);
		listScrollPane.addListSelectionListener(i);
		minusButton.addActionListener(i);
		plusButton.addActionListener(i);
		menuItemAdd.addActionListener(i);
		menuItemRemove.addActionListener(i);
			
		
		add(splitPane);
		pack();
	}

	public static void main(String[] argv) {
		final String[] expressions = new String[] {"cos(x)", "x*x", "atan(x)"};
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				new Main("grapher", expressions).setVisible(true); 
			}
		});
	}
}
