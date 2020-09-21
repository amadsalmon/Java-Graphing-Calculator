package grapher.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

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
		
		Interaction i = new Interaction(grapher,this);
		grapher.setInteraction(i);
		grapher.addMouseListener(i);
		grapher.addMouseMotionListener(i);
		grapher.addMouseWheelListener(i);
		
		add(splitPane);
		pack();
	}

	public static void main(String[] argv) {
		final String[] expressions = new String[] {"cos(x)","sin(x)","tan(x)", "x*x", "atan(x)"};
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				new Main("grapher", expressions).setVisible(true); 
			}
		});
	}
}
