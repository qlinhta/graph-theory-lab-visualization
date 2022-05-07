package lab.interf.connectedcomponents;

import lab.algorithms.Algorithm;
import lab.ds.AdjacencyListGraph;
import lab.interf.GenericControlPanel;
import lab.interf.GenericGraphPanel;
import lab.interf.GenericGraphsContainerPanel;
import lab.interf.GenericTab;

import javax.swing.*;

/**
 * The panel that contains all the GenericGraphPanel for the Connected
 * Components.
 */
public class CcGraphsContainerPanel extends GenericGraphsContainerPanel {

	private final GenericGraphPanel bfsConnectedComponentsGraph;

	public CcGraphsContainerPanel(GenericTab ccTab, GenericControlPanel genericControlPanel) {

		super(genericControlPanel);

		SpringLayout sl = new SpringLayout();
		setLayout(sl);

		bfsConnectedComponentsGraph = new CcGraphPanel(Algorithm.CONNECTED_COMPONENTS_BFS, ccTab,
				new AdjacencyListGraph(graph));
		add(bfsConnectedComponentsGraph);

		sl.putConstraint(SpringLayout.WEST, bfsConnectedComponentsGraph, 5, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, bfsConnectedComponentsGraph, 5, SpringLayout.NORTH, this);

		addGraphPanel(bfsConnectedComponentsGraph);
	}
}
