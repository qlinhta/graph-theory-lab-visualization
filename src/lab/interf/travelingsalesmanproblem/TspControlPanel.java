package lab.interf.travelingsalesmanproblem;

import lab.interf.GenericControlPanel;
import lab.interf.GenericTab;
import lab.interf.Main;

/**
 * The controls of the TSP algorithm.
 */
public class TspControlPanel extends GenericControlPanel {

	private static int DEFAULT_SPEED = 15;
	private static int DEFAULT_NODES = 12;
	private static int DEFAULT_EDGES = 7;

	public TspControlPanel(Main main, GenericTab genericTab) {
		super(main, genericTab, "Find", DEFAULT_EDGES, DEFAULT_NODES, DEFAULT_SPEED);
	}
}
