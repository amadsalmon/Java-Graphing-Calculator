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
		
		State move(MouseEvent e) {
			return DRAG;
		}
	},
	DRAG {
		State release(MouseEvent e) {
			return UP;
		}
		
		State move(MouseEvent e) {
			return DRAG;
		}
	};

	static Interaction inter;
	static State init(Interaction i) {
		inter = i;
		return UP;
	}
	
	State press(MouseEvent e) { throw new RuntimeException(); } 
	State move(MouseEvent e) { throw new RuntimeException(); }
	State release(MouseEvent e) { throw new RuntimeException(); }
	
}
