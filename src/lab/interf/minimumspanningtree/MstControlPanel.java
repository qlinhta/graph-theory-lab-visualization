package lab.interf.minimumspanningtree;

import lab.interf.GenericControlPanel;
import lab.interf.GenericTab;
import lab.interf.Main;

/**
 * The controls of the MST algorithm.
 */
public class MstControlPanel extends GenericControlPanel {

	private static int DEFAULT_SPEED = 50;
	private static int DEFAULT_NODES = 50;
	private static int DEFAULT_EDGES = 8;

	public MstControlPanel(Main main, GenericTab genericTab) {
		super(main, genericTab, "Find", DEFAULT_EDGES, DEFAULT_NODES, DEFAULT_SPEED);
	}
}
