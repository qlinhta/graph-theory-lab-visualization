package lab.interf;

import lab.algorithms.Algorithm;
import lab.ds.AdjacencyListGraph;
import lab.ds.Edge;
import lab.ds.Node;
import lab.ds.NodeStatus;
import lab.utils.Constants;
import lab.utils.GraphUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static lab.utils.Constants.X_SHIFT;
import static lab.utils.Constants.Y_SHIFT;

public abstract class GenericGraphPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private static final Font ALGORITHM_NAME_FONT = new Font("Consolas", Font.BOLD, 14);
	private static final Font[] KEY_FONT = new Font[30];
	private static final Color[] GRAYS = new Color[256];
	private static final Color[] COLOR_GRADIENT = new Color[256];
	private static final Color DEFAULT_EDGE_COLOR = new Color(100, 100, 100);
	private static final Color KEY_COLOR = new Color(190, 190, 190);
	private static final Border WORKING_BORDER = BorderFactory.createEtchedBorder();
	private static final Border FINISHED_BORDER = BorderFactory.createEtchedBorder(Color.BLUE, Color.LIGHT_GRAY);
	private static final Color WORKING_BACKGROUND_COLOR = new Color(119,136,153);
	private static final Color FINISHED_BACKGROUND_COLOR = new Color(176,196,222);

	static {
		for (int j = 0; j < KEY_FONT.length; j++) {
			KEY_FONT[j] = new Font("Consolas", Font.BOLD, 4 + j);
		}
		for (int j = 0; j < 255; j++) {
			GRAYS[j] = new Color(j, j, j);
			COLOR_GRADIENT[j] = new Color(j, j, 255);
		}
	}

	// algorithm specific values
	protected int bellmanFordStep = 0;
	protected boolean hasSearchedNode;
	public boolean isFinished;
	protected double mf;

	// graph drawing settings
	protected boolean drawThinEdges = true;
	protected boolean drawWorkingEdges = true;
	protected boolean drawShortestPath = false;
	protected boolean drawTree = true;
	protected boolean drawEdgesWithColorGradient = true;
	protected boolean drawEdgesWithGrayShade = false;
	protected int workingEdgesWidth = 4;
	protected float regularWidth = 0.5f;

	// user interaction variables
	protected int clickedNodeX;
	protected int clickedNodeY;
	protected boolean isMousePressed;
	protected Node clickedNode;
	protected int panelSide;
	protected int speed;
	private int nodeSize;
	protected final JPopupMenu popupMenu;

	protected List<Node> visitedNodes = new ArrayList<>();
	protected List<Edge> visitedEdges = new ArrayList<>();
	protected List<Edge> edgesOnPath = new ArrayList<>();
	protected List<Node> processedNodes = new ArrayList<>();

	protected GenericTab genericTab;
	protected AdjacencyListGraph graph;
	protected Algorithm algorithm;

	public GenericGraphPanel(Algorithm algorithm, GenericTab genericTab, AdjacencyListGraph graph,
			boolean hasSearchedNode) {
		this.algorithm = algorithm;
		this.genericTab = genericTab;
		this.graph = graph;
		this.hasSearchedNode = hasSearchedNode;
		setBackground(WORKING_BACKGROUND_COLOR);
		setBorder(WORKING_BORDER);

		popupMenu = new JPopupMenu();

		JMenuItem menuItem = new JMenuItem(Constants.SET_STARTING_NODE_LABEL);
		menuItem.addActionListener(this);
		popupMenu.add(menuItem);

		MouseListener popupListener = new PopupListener();
		this.addMouseListener(popupListener);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setGraph(AdjacencyListGraph graph) {
		this.graph = graph;
		visitedEdges = new ArrayList<>();
		visitedNodes = new ArrayList<>();
		processedNodes = new ArrayList<>();
		edgesOnPath = new ArrayList<>();
		bellmanFordStep = 0;
		setBorder(WORKING_BORDER);
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		switch (genericTab.getGraphsContainer().genericGraphPanels.size()) {
		case 1:
			return getPreferredSizeForOnePanel();
		case 2:
			return getPreferredSizeForTwoPanels();
		case 3:
			return getPreferredSizeForThreePanels();
		case 4:
			return getPreferredSizeForFourPanels();
		default:
			return getPreferredSizeForOnePanel();
		}
	}

	private Dimension getPreferredSizeForOnePanel() {
		Dimension dimension = genericTab.getGraphsContainer().getSize();
		panelSide = (dimension.width < dimension.height ? dimension.width : dimension.height) - 5;
		return new Dimension(panelSide, panelSide);
	}

	private Dimension getPreferredSizeForTwoPanels() {
		Dimension dimension = genericTab.getGraphsContainer().getSize();
		panelSide = (dimension.width < dimension.height * 2 ? dimension.width / 2 : dimension.height) - 5;
		return new Dimension(panelSide, panelSide);
	}

	private Dimension getPreferredSizeForThreePanels() {
		Dimension dimension = genericTab.getGraphsContainer().getSize();
		panelSide = (dimension.width < dimension.height * 3 ? dimension.width / 3 : dimension.height) - 5;
		return new Dimension(panelSide, panelSide);
	}

	private Dimension getPreferredSizeForFourPanels() {
		Dimension dimension = genericTab.getGraphsContainer().getSize();
		panelSide = (dimension.width < dimension.height ? dimension.width / 2 : dimension.height / 2) - 5;
		return new Dimension(panelSide, panelSide);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// makes a local copy of data to avoid concurrent modifications
		List<Edge> edges = new ArrayList<>(visitedEdges);

		// FIXME: when changing window size, a part of the graph is lost over the
		// border!
		mf = getPreferredSize().getHeight() / 600;
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float edgeColorIncrement = 255 / (edges.size() != 0 ? (float) edges.size() : 255f);
		float bellmanFordIncrement = 80 / (float) graph.getEdges().size();
		float colorShade = 0f;
		int grayShade = (int) (180 - (bellmanFordIncrement * bellmanFordStep));

		if (drawThinEdges) {
			g2.setStroke(new BasicStroke(regularWidth));
			graph.getNodes()
					.forEach(node -> node.getEdges().forEach(edge -> drawColoredEdge(g2, edge, mf, Color.BLACK)));
		} else {
			// draws the final step of bellman-ford
			g2.setStroke(new BasicStroke(workingEdgesWidth));
			Color color = GRAYS[100];
			graph.getNodes().forEach(node -> node.getEdges().forEach(edge -> drawColoredEdge(g2, edge, mf, color)));
		}

		// draws working edges (the ones the algorithm is working on during execution)
		if (drawWorkingEdges) {
			g2.setStroke(new BasicStroke(workingEdgesWidth));
			Edge edge = null;
			Iterator<Edge> edgeIterator = edges.iterator();
			while (edgeIterator.hasNext()) {
				edge = edgeIterator.next();
				Color color = DEFAULT_EDGE_COLOR;
				if (edge.hasColor()) {
					color = edge.getColor();
				} else if (drawEdgesWithGrayShade) {
					color = GRAYS[grayShade];
				} else if (drawEdgesWithColorGradient) {
					colorShade += edgeColorIncrement;
					color = COLOR_GRADIENT[(int) colorShade];
				}
				drawColoredEdge(g2, edge, mf, color);
			}

			if (drawEdgesWithGrayShade && edges.size() > 0) {
				drawColoredEdge(g2, edge, mf, Color.BLACK);
			}
		}

		// draws optional paths in the graph
		if (drawShortestPath) {
			drawShortestPath(g2);
		} else if (drawTree) {
			drawTree(g2, new ArrayList<>(edgesOnPath));
		}

		// draws the nodes
		g2.setStroke(new BasicStroke(regularWidth));
		graph.getNodes().forEach(node -> drawColoredNode(g2, mf, (node)));

		// draw algorithm's name
		g.setColor(Color.BLACK);
		g.setFont(ALGORITHM_NAME_FONT);
		g.drawString(algorithm.toString(), 5, 15);
	}

	/**
	 * draws the path from starting node to all its children (for MST)
	 *
	 * @param g2
	 */
	private void drawTree(Graphics2D g2, List<Edge> edges) {
		try {
			g2.setStroke(new BasicStroke(5));
			edges.forEach(edge -> drawColoredEdge(g2, edge, mf, Color.BLUE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawShortestPath(Graphics2D g2) {
		try {
			Node currentNode = GraphUtils.getTargetNode(graph);
			g2.setStroke(new BasicStroke(5));
			while (currentNode.getPathParent() != null) {
				drawColoredEdge(g2, currentNode, currentNode.getPathParent(), mf, Color.BLUE);
				currentNode = currentNode.getPathParent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawColoredNode(Graphics g, double mf, Node node) {
		drawColoredNode(g, mf, node, null);
	}

	private void drawColoredNode(Graphics g, double mf, Node node, Color nodeColor) {
		nodeSize = (int) (25 * mf);
		Color color;
		if (node.hasColor()) {
			color = node.getColor();
		} else if (node.isStartNode()) {
			color = Color.BLUE;
		} else if (node.isTargetNode() && hasSearchedNode) {
			color = Color.GREEN;
		} else {
			color = nodeColor == null ? node.getStatus().color : nodeColor;
		}

		if (node == clickedNode) {
			color = new Color(0, 100, 200);
		}
		g.setColor(color);
		g.fillOval(((int) (node.getX() * mf)) - nodeSize / 2 + X_SHIFT,
				((int) (node.getY() * mf)) - nodeSize / 2 + Y_SHIFT, nodeSize, nodeSize);
		g.setColor(Color.BLACK);
		g.drawOval(((int) (node.getX() * mf)) - nodeSize / 2 + X_SHIFT,
				((int) (node.getY() * mf)) - nodeSize / 2 + Y_SHIFT, nodeSize, nodeSize);
		g.setColor(KEY_COLOR);
		g.setFont(KEY_FONT[(int) (8 * mf)]);
		FontMetrics fontMetrics = g.getFontMetrics();
		int width = fontMetrics.stringWidth("" + node.getKey());
		g.drawString("" + node.getKey(), (int) (node.getX() * mf) + (nodeSize / 8 - width) / 2 + X_SHIFT,
				(int) (node.getY() * mf) + nodeSize / 4 + Y_SHIFT);
	}

	private void drawColoredEdge(Graphics g, Edge edge, double mf, Color edgeColor) {
		drawColoredEdge(g, edge.getSource(), edge.getDestination(), mf, edgeColor);
	}

	private void drawColoredEdge(Graphics g, Node sourceNode, Node destinationNode, double mf, Color edgeColor) {
		g.setColor(edgeColor);
		g.drawLine((int) (sourceNode.getX() * mf) + X_SHIFT, (int) (sourceNode.getY() * mf) + Y_SHIFT,
				(int) (destinationNode.getX() * mf) + X_SHIFT, (int) (destinationNode.getY() * mf) + Y_SHIFT);

		// draws an arrow for edge direction
		if (graph.isDirected()) {

			int deltaY = destinationNode.getY() - sourceNode.getY();
			int deltaX = destinationNode.getX() - sourceNode.getX();
			int edgeLength = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY) - (int) (nodeSize / 2 / mf);

			double angle = Math.atan2(deltaY, deltaX);
			double angleDelta = 2 * Math.PI / 20;
			int arrowArmLength = 20;
			if (edgeLength < nodeSize * 2 / mf) {
				arrowArmLength = edgeLength / 3;
			}
			int nodeContactX = sourceNode.getX() + (int) (Math.cos(angle) * (edgeLength));
			int nodeContactY = sourceNode.getY() + (int) (Math.sin(angle) * (edgeLength));

			int leftArrowArmX = nodeContactX - (int) (Math.cos(angle - angleDelta) * arrowArmLength);
			int leftArrowArmY = nodeContactY - (int) (Math.sin(angle - angleDelta) * arrowArmLength);

			int rightArrowArmX = nodeContactX - (int) (Math.cos(angle + angleDelta) * arrowArmLength);
			int rightArrowArmY = nodeContactY - (int) (Math.sin(angle + angleDelta) * arrowArmLength);

			g.drawLine((int) (nodeContactX * mf) + X_SHIFT, (int) (nodeContactY * mf) + Y_SHIFT,
					(int) (leftArrowArmX * mf) + X_SHIFT, (int) (leftArrowArmY * mf) + Y_SHIFT);

			g.drawLine((int) (nodeContactX * mf) + X_SHIFT, (int) (nodeContactY * mf) + Y_SHIFT,
					(int) (rightArrowArmX * mf) + X_SHIFT, (int) (rightArrowArmY * mf) + Y_SHIFT);
		}
	}

	public void reset() {
		visitedNodes = new ArrayList<>();
		visitedEdges = new ArrayList<>();
		processedNodes = new ArrayList<>();
		edgesOnPath = new ArrayList<>();
		this.isFinished = false;
		setBorder(WORKING_BORDER);
		graph.getNodes().forEach(node -> {
			node.setStatus(NodeStatus.UNKNOWN);
			node.setPathParent(null);
			node.setColor(null);
			node.getEdges().forEach(edge -> edge.setColor(null));
		});
		setBackground(WORKING_BACKGROUND_COLOR);
		repaint();
	}

	public void newGraph() {
		setBackground(WORKING_BACKGROUND_COLOR);
	}

	protected void updateGraph() throws Exception {
		repaint();
		if (speed > 0) {
			Thread.sleep(speed);
		}
	}

	public void setOperationAsFinished() {
		this.isFinished = true;
		setBorder(FINISHED_BORDER);
		setBackground(FINISHED_BACKGROUND_COLOR);
		genericTab.setOperationAsFinished();
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setProgressBar(int value) {
		genericTab.setProgressBar(value);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Node clickedNode = graph.getNodes().stream()
				.filter(node -> node.getX() == clickedNodeX && node.getY() == clickedNodeY).findFirst().get();
		if (e.getActionCommand().equals(Constants.SET_STARTING_NODE_LABEL)) {
			graph.getNodes().forEach(node -> node.setStartNode(false));
			clickedNode.setStartNode(true);
		} else if (e.getActionCommand().equals(Constants.SET_TARGET_NODE_LABEL)) {
			graph.getNodes().forEach(node -> node.setTargetNode(false));
			clickedNode.setTargetNode(true);
		}
		repaint();
	}

	private Node getClickedNodeFromCoords(int x, int y) {
		Node node = null;
		for (Node n : graph.getNodes()) {
			if (Math.abs(n.getX() - x / mf + nodeSize + X_SHIFT) < nodeSize
					&& Math.abs(n.getY() - y / mf + nodeSize + Y_SHIFT) < nodeSize) {
				node = n;
			}
		}
		return node;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		clickedNode = getClickedNodeFromCoords(e.getX(), e.getY());
		if (clickedNode != null) {
			isMousePressed = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.isMousePressed = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isMousePressed) {
			clickedNode.setX((int) (e.getX() / mf) - X_SHIFT);
			clickedNode.setY((int) (e.getY() / mf) - Y_SHIFT);
			repaint();
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		clickedNode = getClickedNodeFromCoords(e.getX(), e.getY());
		if (clickedNode != null) {
			Node startingNode = graph.getNodes().stream().filter(node -> node.isStartNode()).findFirst().get();
			genericTab.main.updateStatusBar(clickedNode.toString(GraphUtils.getDistance(startingNode, clickedNode)));
		} else {
			if (genericTab.graphsContainerPanel.genericGraphPanels.stream().allMatch(graph -> graph.isFinished)) {
				genericTab.main.updateStatusBar("Ready");
			} else {
				genericTab.main.updateStatusBar(
						genericTab.getControlPanel().getOperationName() + Constants.IN_PROGRESS_MESSAGE);
			}
		}
		repaint();
	}

	public void start() {
		setBorder(WORKING_BORDER);
		visitedEdges = new ArrayList<>();
		visitedNodes = new ArrayList<>();
		processedNodes = new ArrayList<>();
		edgesOnPath = new ArrayList<>();
		isFinished = false;
		graph.getNodes().forEach(node -> {
			node.setPathParent(null);
			node.setStatus(NodeStatus.UNKNOWN);
			node.setColor(null);
		});
		graph.getEdges().forEach(edge -> edge.setColor(null));
		setBackground(WORKING_BACKGROUND_COLOR);

		// recomputes all the edges costs (if any node has been moved on the canvas,
		// cost has changed since it's the euclidean distance)
		graph.getNodes().forEach(node -> node.getEdges().forEach(edge -> edge.recomputeCost()));

		executeStart();
	}

	public void stop() {
		setBorder(WORKING_BORDER);
		isFinished = true;
		executeStop();
	}

	protected abstract void executeStart();

	protected abstract void executeStop();

	public void setWorkingEdgesWidth(int workingEdgesWidth) {
		this.workingEdgesWidth = workingEdgesWidth;
	}

	public void setDrawThinEdges(boolean drawThinEdges) {
		this.drawThinEdges = drawThinEdges;
	}

	public void setDrawEdgesWithColorGradient(boolean drawEdgesWithColorGradient) {
		this.drawEdgesWithColorGradient = drawEdgesWithColorGradient;
	}

	public void setDrawEdgesWithGrayShade(boolean drawEdgesWithGrayShade) {
		this.drawEdgesWithGrayShade = drawEdgesWithGrayShade;
	}

	public class PopupListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			clickedNode = getClickedNodeFromCoords(e.getX(), e.getY());
			if (clickedNode != null) {
				clickedNodeX = clickedNode.getX();
				clickedNodeY = clickedNode.getY();
				maybeShowPopup(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			clickedNode = null;
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}