package lab.interf.connectedcomponents;

import lab.interf.GenericTab;
import lab.interf.Main;

import javax.swing.*;
import java.awt.*;

/**
 * This panel is the Connected Components tab in the main window.
 */
public class CcTab extends GenericTab {

	public CcTab(Main main) {

		super(main);
		controlPanel = new CcControlPanel(main, this);
		graphsContainerPanel = new CcGraphsContainerPanel(this, controlPanel);

		divider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphsContainerPanel, controlPanel);
		divider.setDividerLocation(450);
		add(divider, BorderLayout.CENTER);

		addComponentListener(this);
	}
}
