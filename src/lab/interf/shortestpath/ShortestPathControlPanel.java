package lab.interf.shortestpath;

import lab.interf.GenericControlPanel;
import lab.interf.GenericTab;
import lab.interf.Main;

/**
 * The controls of the Shortest Path algorithm.
 */
public class ShortestPathControlPanel extends GenericControlPanel {

    private static int DEFAULT_SPEED = 10;
    private static int DEFAULT_NODES = 50;
    private static int DEFAULT_EDGES = 35;

    public ShortestPathControlPanel(Main main, GenericTab genericTab) {
        super(main, genericTab, "Find", DEFAULT_EDGES, DEFAULT_NODES, DEFAULT_SPEED);
    }
}
