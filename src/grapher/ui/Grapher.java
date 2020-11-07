package grapher.ui;

import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import grapher.fc.Function;
import grapher.fc.FunctionFactory;

public class Grapher extends JPanel {
	
	private static final long serialVersionUID = 4881504888613379968L;
	
	static final int MARGIN = 40;
	static final int STEP = 5;

	static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);
	static final BasicStroke DASH_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.f,
			new float[] { 4.f, 4.f }, 0.f);

	static final BasicStroke SELECTED_CURVE_STROKE = new BasicStroke(4.0f);
	static final BasicStroke DEFAULT_CURVE_STROKE = new BasicStroke(1.3f);

	static final Color DEFAULT_COLOR = new Color(0, 0, 0);
	static final Color DEFAULT_CURVE_COLOR = new Color(0, 0, 0);
	static final Color SELECTED_CURVE_COLOR = new Color(0, 0, 255);
	static final Color GRID_COLOR = new Color(102, 102, 102);

	protected int W = 1080;
	protected int H = 720;

	protected double xmin, xmax;
	protected double ymin, ymax;

	protected int m_rectX, m_rectY, m_rectW, m_rectH;
	protected boolean m_drawR;

	public Function m_selectedFunction; // TODO (Amad): make it a list of selected functions as it is possible to select
										// mutliple sidelistitems

	Vector<Vector<Object>> data; // used for data from database
	Vector<String> header; // used to store data header
	DefaultTableModel model;

	protected Interaction m_interaction;

	public Grapher() {
		xmin = -PI / 2.;
		xmax = 3 * PI / 2;
		ymin = -1.5;
		ymax = 1.5;

		header = new Vector<String>();
		header.add("Expression");
		header.add("Color");
		data = new Vector<Vector<Object>>();

		model = new DefaultTableModel(data, header);

		m_selectedFunction = null;
	}

	public void setInteraction(Interaction i) {
		m_interaction = i;
	}

	public void add(String expression) {
		add(FunctionFactory.createFunction(expression));
	}

	public void add(Function function) {
		Vector<Object> singleVector = new Vector<Object>();
		singleVector.add(function);
		singleVector.add(DEFAULT_CURVE_COLOR);
		data.addElement(singleVector);


		repaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(W, H);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		W = getWidth();
		H = getHeight();

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, W, H);

		// Stroke color
		g2.setColor(DEFAULT_CURVE_COLOR);

		// box
		g2.translate(MARGIN, MARGIN);
		W -= 2 * MARGIN;
		H -= 2 * MARGIN;
		if (W < 0 || H < 0) {
			return;
		}

		g2.drawRect(0, 0, W, H);

		g2.drawString("x", W, H + 10);
		g2.drawString("y", -10, 0);

		// plot
		g2.clipRect(0, 0, W, H);
		g2.translate(-MARGIN, -MARGIN);

		// x values
		final int N = W / STEP + 1;
		final double dx = dx(STEP);
		double xs[] = new double[N];
		int Xs[] = new int[N];
		for (int i = 0; i < N; i++) {
			double x = xmin + i * dx;
			xs[i] = x;
			Xs[i] = X(x);
		}

		for (int i = 0; i < model.getRowCount(); i++) {
			Function functionToGraph = null;
			Object objectToEvaluate = model.getValueAt(i, 0);
			if (objectToEvaluate instanceof String) {
				functionToGraph = FunctionFactory.createFunction((String) objectToEvaluate);
			} else {
				functionToGraph = (Function) objectToEvaluate;
			}

			Color colorToGraph = (Color) model.getValueAt(i, 1);

			g2.setColor(colorToGraph);
			g2.setStroke(DEFAULT_STROKE);

			// y values
			int Ys[] = new int[N];
			for (int j = 0; j < N; j++) {
				Ys[j] = Y(functionToGraph.y(xs[j]));
			}
			if (functionToGraph == m_selectedFunction) {
				g2.setStroke(SELECTED_CURVE_STROKE);
			}
			g2.drawPolyline(Xs, Ys, N);
		}

		g2.setStroke(DEFAULT_STROKE);
		g2.setColor(DEFAULT_COLOR);

		g2.setClip(null);

		// axes
		g2.setColor(GRID_COLOR);
		drawXTick(g2, BigDecimal.ZERO);
		drawYTick(g2, BigDecimal.ZERO);

		BigDecimal xstep = unit((xmax - xmin) / 10);
		BigDecimal ystep = unit((ymax - ymin) / 10);

		g2.setStroke(DASH_STROKE);
		g2.setColor(GRID_COLOR);
		for (BigDecimal x = xstep; x.doubleValue() < xmax; x = x.add(xstep)) {
			drawXTick(g2, x);
		}
		for (BigDecimal x = xstep.negate(); x.doubleValue() > xmin; x = x.subtract(xstep)) {
			drawXTick(g2, x);
		}
		for (BigDecimal y = ystep; y.doubleValue() < ymax; y = y.add(ystep)) {
			drawYTick(g2, y);
		}
		for (BigDecimal y = ystep.negate(); y.doubleValue() > ymin; y = y.subtract(ystep)) {
			drawYTick(g2, y);
		}
		g2.setColor(DEFAULT_COLOR);
		g2.setStroke(DASH_STROKE);
		m_interaction.paint(g2, W, H);
	}

	protected double dx(int dX) {
		return (double) ((xmax - xmin) * dX / W);
	}

	protected double dy(int dY) {
		return -(double) ((ymax - ymin) * dY / H);
	}

	protected double x(int X) {
		return xmin + dx(X - MARGIN);
	}

	protected double y(int Y) {
		return ymin + dy((Y - MARGIN) - H);
	}

	protected int X(double x) {
		int Xs = (int) round((x - xmin) / (xmax - xmin) * W);
		return Xs + MARGIN;
	}

	protected int Y(double y) {
		int Ys = (int) round((y - ymin) / (ymax - ymin) * H);
		return (H - Ys) + MARGIN;
	}

	protected void drawXTick(Graphics2D g2, BigDecimal x) {
		double _x = x.doubleValue();
		if (_x > xmin && _x < xmax) {
			final int X0 = X(_x);
			g2.drawLine(X0, MARGIN, X0, H + MARGIN);
			g2.drawString(x.toString(), X0, H + MARGIN + 15);
		}
	}

	protected void drawYTick(Graphics2D g2, BigDecimal y) {
		double _y = y.doubleValue();
		if (_y > ymin && _y < ymax) {
			final int Y0 = Y(_y);
			g2.drawLine(0 + MARGIN, Y0, W + MARGIN, Y0);
			g2.drawString(y.toString(), 5, Y0);
		}
	}

	protected static BigDecimal unit(double w) {
		int scale = (int) floor(log10(w));
		w /= pow(10, scale);
		BigDecimal value;
		if (w < 2) {
			value = new BigDecimal(2);
		} else if (w < 5) {
			value = new BigDecimal(5);
		} else {
			value = new BigDecimal(10);
		}
		return value.movePointRight(scale);
	}

	protected void translate(int dX, int dY) {
		double dx = dx(dX);
		double dy = dy(dY);
		xmin -= dx;
		xmax -= dx;
		ymin -= dy;
		ymax -= dy;
		repaint();
	}

	protected void zoom(Point center, int dz) {
		double x = x(center.x);
		double y = y(center.y);
		double ds = exp(dz * .01);
		xmin = x + (xmin - x) / ds;
		xmax = x + (xmax - x) / ds;
		ymin = y + (ymin - y) / ds;
		ymax = y + (ymax - y) / ds;
		repaint();
	}

	protected void zoom(Point p0, Point p1) {
		double x0 = x(p0.x);
		double y0 = y(p0.y);
		double x1 = x(p1.x);
		double y1 = y(p1.y);
		xmin = min(x0, x1);
		xmax = max(x0, x1);
		ymin = min(y0, y1);
		ymax = max(y0, y1);
		repaint();
	}

	/**
	 * 
	 * This method will look for the index of the vector in data which contains the
	 * function f. This method is necessary because data is a Vector of
	 * Vector<Object>.
	 * 
	 * @param f : the function (of class Function) that is searched for.
	 * @return index of f in data if found, -1 otherwise.
	 * @author amadsalmon
	 */
	public int indexOfFunction(Function f) {
		Vector<Vector<Object>> v = this.data;
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i).get(0) == f) {
				return i;
			}
		}
		return -1;
	}
}
