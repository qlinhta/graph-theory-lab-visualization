package lab.ds;

import java.awt.*;

public enum NodeStatus {
	UNKNOWN(Color.BLACK), DISCOVERED(new Color(0,128,128)), PROCESSED(new Color(144,238,144));

	public Color color;

	NodeStatus(Color color) {
		this.color = color;
	}
}