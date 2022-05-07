package lab.interf.traversal;

import lab.interf.GenericTab;
import lab.interf.Main;

import javax.swing.*;
import java.awt.*;

public class TraversalTab extends GenericTab {

	public TraversalTab(Main main) {

		super(main);
		controlPanel = new TraversalControlPanel(main, this);
		graphsContainerPanel = new TraversalGraphsContainerPanel(this, controlPanel);

		divider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphsContainerPanel, controlPanel);
		divider.setDividerLocation(450);
		add(divider, BorderLayout.CENTER);

		addComponentListener(this);
	}
}
