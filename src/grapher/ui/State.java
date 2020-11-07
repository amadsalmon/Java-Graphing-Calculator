package grapher.ui;

import java.awt.event.MouseEvent;

public enum State {
	
	UP {
		State press(MouseEvent e) {
			return DOWN;
		}
	},
	DOWN {
		State release(MouseEvent e) {
			return UP;
		}
	},
	DRAG {
		State move(MouseEvent e) {
			return DRAG;
		}
		
		State release(MouseEvent e) {
			return UP;
		}
	},
	CLIC_OR_DRAG{
		
	};

}
