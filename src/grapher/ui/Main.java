package grapher.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;



public class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		Grapher grapher = new Grapher();		
		for(String expression : expressions) {
			grapher.add(expression);
		}
		
		Interaction i = new Interaction(grapher,this);
		JList<String> functions = new JList<String>(expressions);
		i.addFunctions(functions);
		
		grapher.setInteraction(i);
		grapher.addMouseListener(i);
		grapher.addMouseMotionListener(i);
		grapher.addMouseWheelListener(i);
		functions.addListSelectionListener(i);
		
		//ToolBar and its buttons
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		toolBar.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		
		JButton addButton = new JButton("+");
		JButton minusButton = new JButton("-");
		toolBar.add(addButton);
		toolBar.add(minusButton);
		addButton.addActionListener(i);
		minusButton.addActionListener(i);
		
		//MenuBar and its items
		JMenuBar menuBar = new JMenuBar();
		
		JMenu expression = new JMenu("Expression");
		
		JMenuItem addExp = new JMenuItem("Add...");
		addExp.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem removeExp = new JMenuItem("Remove");
		removeExp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		addExp.addActionListener(i);
		removeExp.addActionListener(i);
		expression.add(addExp);
		expression.add(removeExp);
		menuBar.add(expression);
		setJMenuBar(menuBar);
		
		
		//Packing everything
		toolBar.setPreferredSize(new Dimension(120, 40));
		functions.setPreferredSize(new Dimension(120, 300));
		
		JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPaneLeft.add(functions);
		splitPaneLeft.add(toolBar);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(splitPaneLeft);
		splitPane.add(grapher);
		
		add(splitPane);
		pack();
	}
	
	public static void main(String[] argv) {
		final String[] expressions = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				new Main("grapher", expressions).setVisible(true); 
			}
		});
	}
}
