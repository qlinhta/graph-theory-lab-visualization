package lab.interf.traversal;

import lab.algorithms.Algorithm;
import lab.ds.AdjacencyListGraph;
import lab.interf.GenericControlPanel;
import lab.interf.GenericGraphPanel;
import lab.interf.GenericGraphsContainerPanel;
import lab.interf.GenericTab;

import javax.swing.*;

public class TraversalGraphsContainerPanel extends GenericGraphsContainerPanel {

	private final GenericGraphPanel bfsGraph;
	private final GenericGraphPanel dfsGraph;

	public TraversalGraphsContainerPanel(GenericTab traversalTab, GenericControlPanel genericControlPanel) {

		super(genericControlPanel);

		SpringLayout sl = new SpringLayout();
		setLayout(sl);

		dfsGraph = new TraversalGraphPanel(Algorithm.DFS, traversalTab, new AdjacencyListGraph(graph));
		bfsGraph = new TraversalGraphPanel(Algorithm.BFS, traversalTab, new AdjacencyListGraph(graph));
		add(dfsGraph);
		add(bfsGraph);

		addGraphPanel(dfsGraph);
		addGraphPanel(bfsGraph);

		sl.putConstraint(SpringLayout.WEST, dfsGraph, 5, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, dfsGraph, 5, SpringLayout.NORTH, this);

		sl.putConstraint(SpringLayout.WEST, bfsGraph, 5, SpringLayout.EAST, dfsGraph);
		sl.putConstraint(SpringLayout.NORTH, bfsGraph, 5, SpringLayout.NORTH, this);
	}
}
