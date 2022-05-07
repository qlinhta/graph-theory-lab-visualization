package lab.interf.search;

import lab.interf.GenericControlPanel;
import lab.interf.GenericTab;
import lab.interf.Main;

/**
 * The controls of the Search algorithm.
 */
public class SearchControlPanel extends GenericControlPanel {

	private static int DEFAULT_SPEED = 30;
	private static int DEFAULT_NODES = 150;
	private static int DEFAULT_EDGES = 8;

	public SearchControlPanel(Main main, GenericTab genericTab) {
		super(main, genericTab, "Search", DEFAULT_EDGES, DEFAULT_NODES, DEFAULT_SPEED);
	}
}
