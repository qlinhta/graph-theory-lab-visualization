package lab.interf.traversal;

import lab.interf.GenericControlPanel;
import lab.interf.GenericTab;
import lab.interf.Main;

public class TraversalControlPanel extends GenericControlPanel {

	private static final long serialVersionUID = 1L;
	private static int DEFAULT_SPEED = 25;
	private static int DEFAULT_NODES = 100;
	private static int DEFAULT_EDGES = 10;

	public TraversalControlPanel(Main main, GenericTab genericTab) {
		super(main, genericTab, "Traverse", DEFAULT_EDGES, DEFAULT_NODES, DEFAULT_SPEED);
	}
}
