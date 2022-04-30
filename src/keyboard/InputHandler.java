package keyboard;

import renderer.Window;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w' -> Window.INPUT_BUFFER = 1;
            case 's' -> Window.INPUT_BUFFER = 2;
            case 'a' -> Window.INPUT_BUFFER = 3;
            case 'd' -> Window.INPUT_BUFFER = 4;
            case 'q' -> Window.INPUT_BUFFER = 5;
            case 'e' -> Window.INPUT_BUFFER = 6;
            case ']' -> Window.INPUT_BUFFER = 7;
            case '[' -> Window.INPUT_BUFFER = 8;
            case 'l' -> Window.INPUT_BUFFER = 9;
            case 'f' -> Window.INPUT_BUFFER = 10;
            case '1' -> Window.INPUT_BUFFER = 11;
            case '2' -> Window.INPUT_BUFFER = 12;
            case 'o' -> Window.INPUT_BUFFER = 13;
            case 'i' -> Window.INPUT_BUFFER = 14;
            case 'm' -> Window.INPUT_BUFFER = 15;
            case 'r' -> Window.INPUT_BUFFER = 16;
        }

        System.out.println(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
