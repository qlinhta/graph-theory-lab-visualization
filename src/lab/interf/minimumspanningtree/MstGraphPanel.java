package lab.interf.minimumspanningtree;

import lab.algorithms.Algorithm;
import lab.algorithms.MinimumSpanningTree;
import lab.ds.AdjacencyListGraph;
import lab.ds.Edge;
import lab.ds.Node;
import lab.interf.GenericGraphPanel;
import lab.interf.GenericTab;
import lab.utils.Constants;
import lab.utils.ConsumerWithException;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * The square panel where the MST graph is drawn and animated.
 */
public class MstGraphPanel extends GenericGraphPanel {

	private GraphSearchWorker searchWorker;

	public MstGraphPanel(Algorithm algorithm, GenericTab genericTab, AdjacencyListGraph graph) {
		super(algorithm, genericTab, graph, true);

		this.drawTree = true;
		this.hasSearchedNode = false;

		JMenuItem menuItem = new JMenuItem(Constants.SET_TARGET_NODE_LABEL);
		menuItem.addActionListener(this);
		popupMenu.add(menuItem);
	}

	public void executeStart() {
		searchWorker = new GraphSearchWorker();
		searchWorker.execute();
	}

	public void executeStop() {
		searchWorker.cancel(true);
	}

	class GraphSearchWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {

			Boolean isCanceled = new Boolean(false);

			Consumer<Node> visitNode = node -> visitedNodes.add(node);
			Consumer<Node> processNode = node -> processedNodes.add(node);
			ConsumerWithException<Edge> visitEdge = edge -> {
				visitedEdges.add(edge);
			};
			ConsumerWithException<Edge> processEdge = edge -> {
				edgesOnPath.add(edge);
				setProgressBar((int) ((edgesOnPath.size() / (float) graph.getNodes().size() - 1) * 100));
				updateGraph();
			};

			switch (algorithm) {
			case BORUVKA:
				MinimumSpanningTree.boruvka(graph, visitEdge, processEdge, isCanceled);
				break;
			case PRIM:
				MinimumSpanningTree.prim(graph, visitNode, processNode, visitEdge, processEdge, isCanceled);
				break;
			case KRUSKAL:
				MinimumSpanningTree.kruskal(graph, visitEdge, processEdge);
				break;
			}

			setProgressBar(0);
			setOperationAsFinished();
			return null;
		}
	}
}