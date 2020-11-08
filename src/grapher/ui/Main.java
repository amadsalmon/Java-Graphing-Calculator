package grapher.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

public class Main extends JFrame {

	private static final long serialVersionUID = 1995697716256600004L;

	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		
		/****************  MENU BAR  ****************/
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItemAdd, menuItemRemove;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the menu.
		menu = new JMenu("Expressions");
		menuBar.add(menu);
		menuItemAdd = new JMenuItem("Add expression");
		menuItemAdd.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		menu.add(menuItemAdd);
		menuItemRemove = new JMenuItem("Remove selected expression");
		menuItemRemove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		menu.add(menuItemRemove);
		setJMenuBar(menuBar);		
		
		
		/****************  RIGHT PANE  ****************/
		Grapher grapher = new Grapher();	
		for(String expression : expressions) {
			grapher.add(expression);
		}
		
			
		/****************  LEFT PANE  ****************/
		/***  Scroll list view  ***/
	    JTable table = new JTable(grapher.model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
		//Create the scroll pane and add the table to it.
		JScrollPane scrollListView = new JScrollPane(table);
		ListSelectionModel listSelectionModel = table.getSelectionModel();

		//Set up renderer and editor for the Color column.
		TableColumn colorColumn = table.getColumnModel().getColumn(1);
		colorColumn.setCellRenderer(new ColorRenderer(true));
		colorColumn.setCellEditor(new ColorEditor());
		
		/***  TOOLBAR CONTAINING +/- BUTTONS  ***/
		JToolBar toolbar = new JToolBar();
	    toolbar.setRollover(true);
	    toolbar.setFloatable(false);
	    JButton minusButton = new JButton("-");
	    JButton plusButton = new JButton("+");
	    toolbar.add(minusButton);
	    toolbar.add(plusButton);	    
	    minusButton.setEnabled(false);
	    toolbar.setLayout(new FlowLayout());
	    
	    
	    /***  FULL LEFT PANE  ***/
	    JPanel leftPanel = new JPanel(new BorderLayout());
	    leftPanel.setOpaque(true);
		leftPanel.add(scrollListView, BorderLayout.CENTER);
		leftPanel.add(toolbar, BorderLayout.SOUTH);
		
		
		/****************  SPLIT PANE  ****************/
		
		// Create a split pane with the two panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, grapher);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		// Provide minimum sizes for the two components in the split pane
		Dimension minimumSize1 = new Dimension(100, 50);
		scrollListView.setMinimumSize(minimumSize1);
		Dimension minimumSize2 = new Dimension(100, 50);
		grapher.setMinimumSize(minimumSize2);
		splitPane.setOpaque(true);	
	    
		
		/****************  ACTION LISTENING  ********/
		Interaction i = new Interaction(scrollListView, listSelectionModel, grapher, this, minusButton);

		grapher.setInteraction(i);
		grapher.addMouseListener(i);
		grapher.addMouseMotionListener(i);
		grapher.addMouseWheelListener(i);
		listSelectionModel.addListSelectionListener(i);
		minusButton.addActionListener(i);
		plusButton.addActionListener(i);
		menuItemAdd.addActionListener(i);
		menuItemRemove.addActionListener(i);
		
		
		/****************  WINDOW & CONTAINER  ****************/
		add(splitPane);
		pack();
	}

	public static void main(String[] argv) {
		final String[] expressions = new String[] {"cos(x)", "x*x", "atan(x)"};
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				new Main("Grapher", expressions).setVisible(true); 
			}
		});
	}
}
