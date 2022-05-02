package lab.interf.shortestpath;

import lab.algorithms.Algorithm;
import lab.ds.AdjacencyListGraph;
import lab.ds.Edge;
import lab.ds.Node;
import lab.interf.GenericGraphPanel;
import lab.interf.GenericTab;
import lab.utils.Constants;
import lab.utils.ConsumerWithException;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;


/**
 * The square panel where the shortest path graph is drawn and animated.
 */
public class ShortestPathGraphPanel extends GenericGraphPanel {

    private ShortestPathWorker shortestPathWorker;

    public ShortestPathGraphPanel(Algorithm algorithm, GenericTab genericTab, AdjacencyListGraph graph) {
        super(algorithm, genericTab, graph, true);

        this.drawShortestPath = true;

        // adds a new item to the popup menu
        JMenuItem menuItem = new JMenuItem(Constants.SET_TARGET_NODE_LABEL);
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);
    }

    public void executeStart() {
        bellmanFordStep = 0;
        shortestPathWorker = new ShortestPathWorker();
        shortestPathWorker.execute();
    }


    @Override
    public void reset() {
        super.reset();
        this.drawThinEdges = true;
    }

    @Override
    public void newGraph() {
        super.newGraph();
        this.drawThinEdges = true;
    }

    public void executeStop() {
        shortestPathWorker.cancel(true);
        bellmanFordStep = 0;
    }

    class ShortestPathWorker extends SwingWorker<Void, Void> {

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
            Callable incrementStep = () -> {
                bellmanFordStep++;
                return null;
            };

            switch (algorithm) {
                case DIJKSTRA:
                    lab.algorithms.ShortestPath.dijkstra(graph, visitNode, visitEdge, processNode, isCanceled);
                    break;
                case BELLMANFORD:
                    ShortestPathGraphPanel.this.drawThinEdges = true;
                    lab.algorithms.ShortestPath.bellmanFord(graph, visitNode, visitEdge, processNode, incrementStep, isCanceled);
                    ShortestPathGraphPanel.this.drawThinEdges = false;
                    break;
            }

            setProgressBar(0);
            setOperationAsFinished();
            return null;
        }
    }
}