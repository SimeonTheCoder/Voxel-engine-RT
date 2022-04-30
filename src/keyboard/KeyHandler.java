package keyboard;

import renderer.MapEditor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w' -> MapEditor.INPUT_BUFFER = 1;
            case 's' -> MapEditor.INPUT_BUFFER = 2;
            case 'a' -> MapEditor.INPUT_BUFFER = 3;
            case 'd' -> MapEditor.INPUT_BUFFER = 4;
            case 'n' -> MapEditor.INPUT_BUFFER = 5;
            case 'm' -> MapEditor.INPUT_BUFFER = 6;
            case ']' -> MapEditor.INPUT_BUFFER = 7;
            case '[' -> MapEditor.INPUT_BUFFER = 8;
            case 'v' -> MapEditor.INPUT_BUFFER = 9;
            case 't' -> MapEditor.INPUT_BUFFER = 10;
            case 'c' -> MapEditor.INPUT_BUFFER = 11;
        }

        System.out.println(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
