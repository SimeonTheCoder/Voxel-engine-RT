package renderer;

import javax.swing.*;
import java.awt.*;


public class GradientColor{
    public static void main(String[] args) {
        GradientColor gd = new GradientColor();
    }

    public GradientColor(){
        JFrame frame = new JFrame("Drawing with a Gradient Color");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MyComponent());
        frame.setSize(400,400);
        frame.setVisible(true);
    }

    public class MyComponent extends JComponent  {
        public void paint(Graphics g){
            Graphics2D g2d = (Graphics2D)g;
            Color s1 = Color.red;
            Color e = Color.green;
            GradientPaint gradient = new GradientPaint(0,0,s1,1920,1080,e,true);
            g2d.setPaint(gradient);
            g2d.fillRect(0,0,1920,1080);
//            Color s2 = Color.yellow;
//            Color e1 = Color.pink;
//            GradientPaint gradient1 = new GradientPaint(10,10,s2,30,30,e1,true);
//            g2d.setPaint(gradient1);
//            g2d.fillRect(99,99,199,119);
        }
    }
}