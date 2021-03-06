package lab.interf.connectedcomponents;

import lab.interf.GenericControlPanel;
import lab.interf.GenericTab;
import lab.interf.Main;

/**
 * The controls of the Connected Components algorithm.
 */
public class CcControlPanel extends GenericControlPanel {

	private static int DEFAULT_SPEED = 10;
	private static int DEFAULT_NODES = 60;
	private static int DEFAULT_EDGES = 3;

	public CcControlPanel(Main main, GenericTab genericTab) {
		super(main, genericTab, "Find", DEFAULT_EDGES, DEFAULT_NODES, DEFAULT_SPEED);
	}
}
