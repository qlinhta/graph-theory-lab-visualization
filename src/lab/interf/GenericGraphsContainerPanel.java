package lab.interf;

import lab.ds.AdjacencyListGraph;
import lab.utils.Constants;
import lab.utils.GraphUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GenericGraphsContainerPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected AdjacencyListGraph graph;
	public List<GenericGraphPanel> genericGraphPanels;
	private GenericControlPanel controlPanel;

	public GenericGraphsContainerPanel(GenericControlPanel controlPanel) {
		this.controlPanel = controlPanel;
		genericGraphPanels = new ArrayList<>();
		graph = GraphUtils.createRandomGraph(controlPanel.getNodesNumber(), controlPanel.getEdgesNumber(),
				Constants.MAX_NODE_VALUE, controlPanel.getIsDirected());
	}

	public void newGraph() {
		AdjacencyListGraph graph1 = GraphUtils.createRandomGraph(controlPanel.getNodesNumber(),
				controlPanel.getEdgesNumber(), Constants.MAX_NODE_VALUE, controlPanel.getIsDirected());
		genericGraphPanels.forEach(panel -> panel.setGraph(new AdjacencyListGraph(graph1)));
		genericGraphPanels.forEach(panel -> panel.newGraph());
	}

	public void addGraphPanel(GenericGraphPanel genericGraphPanel) {
		genericGraphPanels.add(genericGraphPanel);
	}

	public void reset() {
		genericGraphPanels.forEach(panel -> panel.reset());
	}

	public void start() {
		genericGraphPanels.forEach(panel -> panel.start());
	}

	public void stop() {
		genericGraphPanels.forEach(panel -> panel.stop());
	}

	public void updateSpeed() {
		genericGraphPanels.forEach(panel -> panel.setSpeed(controlPanel.getSpeed()));
	}
}
