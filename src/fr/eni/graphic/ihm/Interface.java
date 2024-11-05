package fr.eni.graphic.ihm;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class Interface extends JPanel implements MouseListener {

    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static final int MAX_POINTS = 1000;
    public static final float MAX_FORCE = 5.0F;
    public static final float MAX_SPEED = 100.0F;
    public static final int MAX_STEPS = 2000000;
    public static final int LATENCE = 500000;
    public static final int MAX_ASTRES = 200;
    public static final float MIN_DIST = 250.0F;
    public static final float DAMPING = 1.0F;
    public static final float BASE_VEL = 0.025F;

    public static double force = 0.0;
    public static int nb_astres;
    public static int index = 0;
    public static float[] pointX;
    public static float[] pointY;
    public static float[] canvasX;
    public static float[] canvasY;
    public static float[] velX;
    public static float[] velY;
    public static float[] accX;
    public static float[] accY;
    public static boolean init = true;

    public static void setup() {
    	nb_astres = 0;
        canvasX = new float[MAX_POINTS * 10];
        canvasY = new float[MAX_POINTS * 10];
        velX = new float[MAX_ASTRES];
        velY = new float[MAX_ASTRES];
        accX = new float[MAX_ASTRES];
        accY = new float[MAX_ASTRES];
        
        for(int i = 0; i < MAX_POINTS * 10; i++) {
            canvasX[i] = 0;
            canvasY[i] = 0;
        }
        addVector(WIDTH/2,HEIGHT/2,0,0);
    }

    public static void main(String[] args) {
        setup();
        Interface drawingApp = createWindow();
        update(drawingApp);
    }

    public static void update(Interface a) {
        for(int i = 0; i < MAX_STEPS; i++) {
            for(int j = 0; j < LATENCE; j++) {
                if(j == LATENCE-1) {
                    if(nb_astres > 1) {
                        for(int p = 0; p < nb_astres; p++) {
                            applyForce(p);
                            updatePoint(p);
                            if (border(p)) {
                                p--; 
                            }
                        }
                    }
                    swapPoints();
                    a.repaint();
                }
            }
        }
        System.out.println("done");
    }

    public static void applyForce(int i) {
        float totalAccX = 0;
        float totalAccY = 0;
        int n = 0;
        
        for(int j = 0; j < nb_astres; j++) {
            if(j != i) {
                float dx = canvasX[j] - canvasX[i];
                float dy = canvasY[j] - canvasY[i];
                
                float distanceSquared = dx*dx + dy*dy;
                float distance = (float)Math.sqrt(Math.max(distanceSquared, MIN_DIST * MIN_DIST));
                
                float forceMagnitude = Math.min(MAX_FORCE, MAX_FORCE / distanceSquared);
                
                dx /= distance;
                dy /= distance;
                
                totalAccX += dx * forceMagnitude;
                totalAccY += dy * forceMagnitude;
                n++;
            }
        }
        
        if(n > 0) {
            accX[i] = totalAccX / n;
            accY[i] = totalAccY / n;
        }
    }
    
    public static void updatePoint(int i) {
        velX[i] = (velX[i] + accX[i]) * DAMPING;
        velY[i] = (velY[i] + accY[i]) * DAMPING;
        
        float speedSquared = velX[i] * velX[i] + velY[i] * velY[i];
        if(speedSquared > MAX_SPEED * MAX_SPEED) {
            float factor = MAX_SPEED / (float)Math.sqrt(speedSquared);
            velX[i] *= factor;
            velY[i] *= factor;
        }
        
        canvasX[i] += velX[i];
        canvasY[i] += velY[i];
        
        accX[i] = 0;
        accY[i] = 0;
    }
    
    public static boolean border(int i) {
        if(canvasX[i] > WIDTH*1.2 || canvasX[i] < -0.2*WIDTH || canvasY[i] > HEIGHT*1.2 || canvasY[i] < -0.2*HEIGHT) {
            deleteParticle(i);
            return true;
        }
        return false;
    }

    public static void deleteParticle(int i) {
        for(int j = i; j < nb_astres - 1; j++) {
            canvasX[j] = canvasX[j + 1];
            canvasY[j] = canvasY[j + 1];
            velX[j] = velX[j + 1];
            velY[j] = velY[j + 1];
            accX[j] = accX[j + 1];
            accY[j] = accY[j + 1];
        }
        
        nb_astres--;
        index--;
    }

    public static void addVector(float x, float y, float vx, float vy) {
        canvasX[index] = x;
        canvasY[index] = y;
        velX[index] = vx;
        velY[index] = vy;
        nb_astres++;
        index++;
    }

    public static void swapPoints() {
        for(int i = 0; i < nb_astres; i++) {
            pointX[i] = canvasX[i];
            pointY[i] = canvasY[i];
        }
    }

    public Interface() {
        pointX = new float[MAX_POINTS];
        pointY = new float[MAX_POINTS];
        addMouseListener(this);
    }

    public void mousePressed(MouseEvent e) {
        addVector(e.getX(), e.getY(), rand(BASE_VEL), rand(BASE_VEL));
    }
    
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < index; i++) {
            g.fillOval((int)(pointX[i]), (int)(pointY[i]), 15, 15);
        }
    }

    public static float rand(float d) {
        return (float)(Math.random() < 0.5 ? -1 : 1 * Math.random()) * d;
    }

    public static Interface createWindow() {
        JFrame frame = new JFrame("Drawing Application");
        Interface drawingApp = new Interface();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(drawingApp);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        return drawingApp;
    }
}