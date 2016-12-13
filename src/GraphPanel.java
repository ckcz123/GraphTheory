import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

/**
 * Created by oc on 2016/12/12.
 */
public class GraphPanel extends JPanel {

    private Point[] borders;
    private Point[] cores;

    private Graph graph;
    private String string;

    private static int LENGTH = 80;
    private static int RADIUS = 5;
    private int BASE_X = 80;
    private int BASE_Y = 300;

    public void setGraph(Graph graph) {
        this.graph=graph;
        borders=new Point[12];
        cores=new Point[graph.n];
        int totalLength=(graph.n+1)*LENGTH;
        int delta=totalLength/5;
        int[] dy={-1, -2, -2, -2, -2, -1, 1, 2, 2, 2, 2, 1};
        for (int i=0;i<12;i++)
            borders[i]=new Point(BASE_X+delta*Math.min(i, 11-i),
                    BASE_Y+dy[i]*LENGTH);
        for (int i=0;i<graph.n;i++)
            cores[i]=new Point(BASE_X+LENGTH*(i+1), BASE_Y);
    }

    public String getString() {return string;}

    public void setString(String string) {
        this.string=string;
    }

    private void drawPoint(Graphics2D g2d, Point point, Color color, int r, int id, int dx, int dy) {
        g2d.setColor(color);
        g2d.fillOval(point.x-r, point.y-r, 2*r, 2*r);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(""+id, point.x+dx, point.y+dy);
    }

    private void drawLine(Graphics2D g2d, Point p1, Point p2) {
        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (graph==null) return;

        Graphics2D g2d=(Graphics2D)g;
        for (int i=0;i<12;i++) {
            int dx=0, dy=0;
            if (i==0 || i==11) dx=-4*RADIUS;
            else if (i==5||i==6) dx=2*RADIUS;
            else if (i>=1 && i<=4) dy=-2*RADIUS;
            else dy=4*RADIUS;
            drawPoint(g2d, borders[i], i%2==0?Color.RED:Color.BLUE, i==graph.lastx1||i==graph.lastx2?2*RADIUS:RADIUS, i, dx, dy);
        }
        for (int i=0;i<graph.n;i++) {
            int dx, dy=-2*RADIUS;
            if (i<graph.n/2) dx=-4*RADIUS;
            else dx=2*RADIUS;
            drawPoint(g2d, cores[i], i%2==0?Color.GREEN:Color.MAGENTA, i==graph.lasty1-1||i==graph.lasty2-1?2*RADIUS:RADIUS,
                    i+1, dx, dy);
        }
        for (int i=0;i<12;i++) {
            for (int j: graph.links[i]) {
                drawLine(g2d, borders[i], cores[j-1]);
            }
        }
        for (int i=0;i<12;i++) drawLine(g2d, borders[i], borders[(i+1)%12]);
        for (int i=0;i<graph.n-1;i++) drawLine(g2d, cores[i], cores[i+1]);

        g2d.setFont(new Font("Arial", Font.PLAIN, 22));
        g2d.drawString("Degrees: " + graph.getDegrees().toString()
                +"   {1-3: "+graph.getComponents13()+", 1-4: "+graph.getComponents13()+"};"
                + (graph.valid()?"   YES":"   NO"), BASE_X, 50);
        if (graph.lastx1>=0) {
            g2d.drawString(String.format("Last exchange: [%d, %d] x [%d, %d]", graph.lastx1, graph.lasty1, graph.lastx2, graph.lasty2), BASE_X, 85);
        }

        if (!"".equals(string))
            g2d.drawString(string, BASE_X, getHeight()-90);

    }
}
