package grapher.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Grapher grapher = new Grapher();		
		for(String expression : expressions) {
			grapher.add(expression);
		}
		
		Interaction i = new Interaction(grapher,this);
		grapher.setInteraction(i);
		grapher.addMouseListener(i);
		grapher.addMouseMotionListener(i);
		grapher.addMouseWheelListener(i);
		
		add(grapher);
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
