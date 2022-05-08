package lab.interf.traversal;

import lab.algorithms.Algorithm;
import lab.algorithms.Search;
import lab.ds.AdjacencyListGraph;
import lab.ds.Edge;
import lab.ds.Node;
import lab.interf.GenericGraphPanel;
import lab.interf.GenericTab;
import lab.utils.ConsumerWithException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TraversalGraphPanel extends GenericGraphPanel {

	private GraphTraversalWorker traversalWorker;

	public TraversalGraphPanel(Algorithm algorithm, GenericTab traversalTab, AdjacencyListGraph graph) {
		super(algorithm, traversalTab, graph, false);
		this.algorithm = algorithm;
		this.genericTab = traversalTab;
		this.graph = graph;
		setBorder(BorderFactory.createEtchedBorder());
		setBackground(new Color(176,196,222));

		MouseListener popupListener = new PopupListener();
		this.addMouseListener(popupListener);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void executeStart() {
		visitedEdges = new ArrayList<>();
		visitedNodes = new ArrayList<>();
		processedNodes = new ArrayList<>();
		repaint();
		traversalWorker = new GraphTraversalWorker(visitedNodes, visitedEdges, processedNodes);
		traversalWorker.execute();
	}

	public void executeStop() {
		traversalWorker.cancel(true);
	}

	class GraphTraversalWorker extends SwingWorker<Void, Void> {

		List<Node> visitedNodes;
		List<Edge> visitedEdges;
		List<Node> processedNodes;

		public GraphTraversalWorker(List<Node> visitedNodes, List<Edge> visitedEdges, List<Node> processedNodes) {
			this.visitedNodes = visitedNodes;
			this.visitedEdges = visitedEdges;
			this.processedNodes = processedNodes;
		}

		@Override
		protected Void doInBackground() throws Exception {

			Boolean isCanceled = new Boolean(false);

			ConsumerWithException<Node> visitNode = node -> {
				visitedNodes.add(node);
				setProgressBar((int) ((visitedNodes.size() / (float) graph.getNodes().size()) * 100));
				updateGraph();
			};
			Consumer<Node> processNode = node -> processedNodes.add(node);
			ConsumerWithException<Edge> visitEdge = edge -> {
				visitedEdges.add(edge);
				updateGraph();
			};

			switch (algorithm) {
			case BFS:
				Search.bfs(graph, visitNode, visitEdge, processNode, isCanceled, false);
				break;
			case DFS:
				Search.dfs(graph, visitNode, visitEdge, processNode, isCanceled, false);
				break;
			}

			setProgressBar(0);
			setOperationAsFinished();
			return null;
		}

	}
}