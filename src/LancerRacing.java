import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class LancerRacing {
    // global variables for the game

    // foundation variables
    private static Boolean endgame;
    private static BufferedImage background;
    private static BufferedImage player1; // Elliott car
    private static BufferedImage player2; // Byron car
    private static BufferedImage barrierBuild1;
    private static BufferedImage barrierBuild2;
    private static BufferedImage barrierPond;
    private static BufferedImage logo;
    private static int maxlaps;

    // player 1 controls
    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;

    // player 2 controls
    private static Boolean wPressed;
    private static Boolean sPressed;
    private static Boolean aPressed;
    private static Boolean dPressed;

    // players
    private static ImageObject p1;
    private static double p1width;
    private static double p1height;
    private static double p1originalX;
    private static double p1originalY;
    private static double p1velocity;
    private static int p1laps;

    private static ImageObject p2;
    private static double p2width;
    private static double p2height;
    private static double p2originalX;
    private static double p2originalY;
    private static double p2velocity;
    private static int p2laps;

    // buildings/barriers
    private static ImageObject buildings1;
    private static double buildings1width;
    private static double buildings1height;
    private static double buildings1x;
    private static double buildings1y;
    private static ImageObject buildings2;
    private static double buildings2width;
    private static double buildings2height;
    private static double buildings2x;
    private static double buildings2y;
    private static ImageObject pond;
    private static double pondwidth;
    private static double pondheight;
    private static double pondx;
    private static double pondy;
    private static Vector<BufferedImage> barriers = new Vector<>();
    private static Vector<ImageObject> barrierObjs = new Vector<>();

    private static Long audioLifetime;
    private static Long lastAudioStart;
    private static Clip clip;

    private static double pi;
    private static double quarterPi;
    private static double halfPi;
    private static double threequartersPi;
    private static double fivequartersPi;
    private static double threehavlesPi;
    private static double sevenquartersPi;
    private static double twoPi;

    private static int XOFFSET;
    private static int YOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;

    private static JFrame appFrame;
    private static String backgroundState;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    public LancerRacing() {
        setup();
    }

    public static void setup() {
        appFrame = new JFrame("Lancer Racing");
        XOFFSET = 0;
        YOFFSET = 40;
        WINWIDTH = 750;
        WINHEIGHT = 750;
        pi = 3.1459265358979;
        quarterPi = 0.25 * pi;
        halfPi = 0.5 * pi;
        threequartersPi = 0.75 * pi;
        fivequartersPi = 1.25 * pi;
        threehavlesPi = 1.5 * pi;
        sevenquartersPi = 1.75 * pi;
        twoPi = 2.0 * pi;
        endgame = false;
        p1width = 25;
        p1height = 10;
        p1originalX = 95; // (double)XOFFSET + ((double)WINWIDTH / 2.0) - (p1width / 2.0);
        p1originalY = (double)YOFFSET + ((double)WINHEIGHT / 2.0) - 4 * (p1height / 2.0);
        p1laps = 3;
        p2width = 25;
        p2height = 10;
        p2originalX = 110; //(double)XOFFSET + ((double)WINWIDTH / 2.0) - (p2width / 2.0) + p1width;
        p2originalY = (double)YOFFSET + ((double)WINHEIGHT / 2.0) - 4 * (p2height / 2.0);
        p2laps = 3;
        buildings1width = 174;
        buildings1height = 97;
        buildings1x = WINWIDTH - (int)(buildings1width * 1.125);
        buildings1y = YOFFSET + (int)buildings1height;
        buildings2width = 174;
        buildings2height = 97;
        buildings2x = WINWIDTH / 2 - (int)buildings2width - 10 * XOFFSET;
        buildings2y = WINHEIGHT / 2 - 4 * YOFFSET;
        pondwidth = 162;
        pondheight = 81;
        pondx = (int)WINWIDTH / 2;
        pondy = (int)WINHEIGHT / 2;
        barrierObjs.add(buildings1);
        barrierObjs.add(buildings2);
        barrierObjs.add(pond);
        audioLifetime = Long.valueOf(78000);
        backgroundState = "C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\UproarbyMichaelBriguglio.wav";

        try {
            background = ImageIO.read(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\basicTrack.png"));
            player1 = ImageIO.read(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\finalElliottCar.png"));
            player2 = ImageIO.read(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\finalByronCar.png"));
            logo = ImageIO.read(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\LancerRacingLogo.png"));
            barrierBuild1 = ImageIO.read(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\building1.png"));
            barrierBuild2 = ImageIO.read(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\building2.png"));
            barrierPond = ImageIO.read(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\basicPond.png"));
            barriers.add(barrierBuild1);
            barriers.add(barrierBuild2);
            barriers.add(barrierPond);
        } catch (IOException ioe) { }

    }

    private static class Animate implements Runnable {
        public void run() {
            while (endgame == false) {
                backgroundDraw();
                barriersDraw();
                player1Draw();
                player2Draw();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) { }
            }
        }
    }

    private static class AudioLooper implements Runnable {
        public void run() {
            while (endgame == false) {
                Long currTime = Long.valueOf(System.currentTimeMillis());
                if (currTime - lastAudioStart > audioLifetime) {
                    playAudio(backgroundState);
                }
            }
        }
    }

    private static void playAudio(String backgroundState) {
        try {
            clip.stop();
        } catch (Exception e) {
            // NOP
        }

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("C:\\Users\\sarah\\Desktop\\CSC313\\Homework\\Homework 1\\UproarbyMichaelBriguglio.wav"));
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            lastAudioStart = System.currentTimeMillis();
            audioLifetime = Long.valueOf(78000); // same as before
        } catch (Exception e)  {
            // NOP
        }

    }

    private static class Player1Mover implements Runnable {
        private double velocitystep;
        private double rotatestep;

        public Player1Mover() {
            velocitystep = .5;
            rotatestep = .5;
        }

        public void run() {
            while (endgame == false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { }


                // TODO: the left and right controls now don't do anything hahaha
                // p1 controls
                if (upPressed || downPressed || leftPressed || rightPressed) {
                    p1velocity = velocitystep;
                    if (upPressed) {
//                        p1velocity = p1velocity + velocitystep;
                        if (leftPressed) {
                            p1.setInternalAngle(fivequartersPi);
                        } else if (rightPressed) {
                            p1.setInternalAngle(5.49779);
                        } else {
                            p1.setInternalAngle(threehavlesPi);
                        }
                    }
                    if (downPressed) {
//                        p1velocity = p1velocity - velocitystep;
                        if (leftPressed) {
                            p1.setInternalAngle(2.35619);
                        } else if (rightPressed) {
                            p1.setInternalAngle(quarterPi);
                        } else {
                            p1.setInternalAngle(halfPi);
                        }
                    }
                    if (leftPressed) {
//                        if (p1velocity < 0) {
//                            p1.rotate(-rotatestep);
//                        } else {
//                            p1.rotate(rotatestep);
//                        }
                        if (upPressed) {
                            p1.setInternalAngle(fivequartersPi);
                        } else if (downPressed) {
                            p1.setInternalAngle(threequartersPi);
                        } else {
                            p1.setInternalAngle(pi);
                        }
                    }
                    if (rightPressed) {
//                        if (p1velocity < 0) {
//                            p1.rotate(rotatestep);
//                        } else {
//                            p1.rotate(-rotatestep);
//                        }
                        if (upPressed) {
                            p1.setInternalAngle(5.49779);
                        } else if (downPressed) {
                            p1.setInternalAngle(quarterPi);
                        } else {
                            p1.setInternalAngle(0.0);
                        }
                    }
                }
                else {
                    p1velocity = 0.0;
                    p1.setInternalAngle(threehavlesPi);
                }

                p1.updateBounce();
                p1.move(p1velocity * Math.cos(p1.getInternalAngle()), p1velocity * Math.sin(p1.getInternalAngle()));

            }
        }
    }

    private static class Player2Mover implements Runnable {
        private double velocitystep;

        public Player2Mover() { velocitystep = .5; }

        public void run() {
            while (endgame == false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { }

                // p2 controls
                if (wPressed || sPressed || aPressed || dPressed) {
                    p2velocity = velocitystep;
                    if (wPressed) {
                        if (aPressed) {
                            p2.setInternalAngle(fivequartersPi);
                        } else if (dPressed) {
                            p2.setInternalAngle(5.49779);
                        } else {
                            p2.setInternalAngle(threehavlesPi);
                        }
                    }
                    if (sPressed) {
                        if (aPressed) {
                            p2.setInternalAngle(2.35619);
                        } else if (aPressed) {
                            p2.setInternalAngle(quarterPi);
                        } else {
                            p2.setInternalAngle(halfPi);
                        }
                    }
                    if (aPressed) {
                        if (wPressed) {
                            p2.setInternalAngle(fivequartersPi);
                        } else if (sPressed) {
                            p2.setInternalAngle(threequartersPi);
                        } else {
                            p2.setInternalAngle(pi);
                        }
                    }
                    if (dPressed) {
                        if (wPressed) {
                            p2.setInternalAngle(5.49779);
                        } else if (sPressed) {
                            p2.setInternalAngle(quarterPi);
                        } else {
                            p2.setInternalAngle(0.0);
                        }
                    }
                } else {
                    p2velocity = 0.0;
                    p2.setInternalAngle(threehavlesPi);
                }

                p2.updateBounce();
                p2.move(p2velocity * Math.cos(p2.getInternalAngle()), p2velocity * Math.sin(p2.getInternalAngle()));

            }
        }
    }

    private static class CollisionChecker implements Runnable {
        public void run() {
            while (endgame == false) {
                try {
                    // TODO: compare p1 to buildings
                    checkMoversAgainstBuildings(barrierObjs);
                    // TODO: compare p2 to buildings
                    checkMoversAgainstBuildings(barrierObjs);
                } catch (java.lang.NullPointerException jlnpe) { }
            }

        }

//        private static boolean collisionOccurs(ImageObject obj1, ImageObject obj2) {
//            boolean ret = false;
//            if (collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
//                    obj1.getY() + obj1.getHeight(), obj2.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
//                    obj2.getY() + obj2.getHeight()) == true) {
//                ret = true;
//            }
//            return ret;
//        }

        // similar method to checkMoversAgainstWalls() in Zelda
        private void checkMoversAgainstBuildings(Vector<ImageObject> buildingsInput) {
            for (int i = 0; i < buildingsInput.size(); i++) {
                if (collisionOccurs(p1, buildingsInput.elementAt(i))) {
                    p1.setBounce(true);
                }
                if (collisionOccurs(p2, buildingsInput.elementAt(i))) {
                    p2.setBounce(true);
                }
            }
        }

    }

    private static class WinChecker implements Runnable {
        public void run() {
            // TODO: probably need some time mechanism to decide who wins in a tie
            while (endgame == false) {
                if (p1laps == 0 || p2laps == 0) {
                    endgame = true;
                    if (p1laps == 0) {
                        System.out.println("Player 1 Wins! Better luck next time Player 2.");
                    } else if (p2laps == 0) {
                        System.out.println("Player 1 Wins! Better luck next time Player 2.");
                    }
                }
            }
        }
    }


    private static void lockrotateObjAroundObjbottom (ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + (dist + objInner.getWidth() / 2.0) *
                        Math.cos(-objInner.getAngle() + pi / 2.0) + objOuter.getWidth() / 2.0,
                objInner.getY() + (dist + objInner.getHeight() / 2.0) *
                        Math.sin(-objInner.getAngle() + pi / 2.0) + objOuter.getHeight() / 2.0);
        objOuter.setAngle(objInner.getAngle());
    }

    // page 89
    // dist is a distance between the two objects at the top of the inner object
    private static void lockrotateObjAroundObjtop(ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + objOuter.getWidth() + (objInner.getWidth() / 2.0 +
                        (dist + objInner.getWidth() / 2.0) * Math.cos(objInner.getAngle() + pi / 2.0)) / 2.0,
                objInner.getY() - objOuter.getHeight() + (dist + objInner.getHeight() / 2.0) *
                        Math.sin(objInner.getAngle() / 2.0));
        objOuter.setAngle(objInner.getAngle());
    }

    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(),
                obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static AffineTransformOp spinImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(), obj.getWidth() / 2.0,
                obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    // draw background
    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, XOFFSET, YOFFSET, null);
        g2D.drawImage(logo, XOFFSET * 2, WINHEIGHT - 4*YOFFSET, null);
    }

    // draw players
    private static void player1Draw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p1).filter(player1, null), (int)(p1.getX() + 0.5),
                (int)(p1.getY() + 0.5), null);
    }

    private static void player2Draw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p2).filter(player2, null), (int)(p2.getX() + 0.5),
                (int)(p2.getY() + 0.5), null);
    }

    // draw barriers
    private static void barriersDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics g2D = (Graphics2D) g;
        g2D.drawImage(barrierBuild1, (int)buildings1x, (int)buildings1y, null);
        g2D.drawImage(barrierPond, (int)pondx, (int)pondy, null);
        g2D.drawImage(barrierBuild2, (int)buildings2x, (int)buildings2y, null);
    }

    // TODO: probably will need to draw the time
    // TODO: need to draw player's best lap time too

    // TODO: draw logo on the background as well

    private static class KeyPressed extends AbstractAction {
        private String action;

        public KeyPressed() { action = ""; }
        public KeyPressed(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            // player 1 keys
            if (action.equals("UP")) {
                upPressed = true;
            }
            if (action.equals("DOWN")) {
                downPressed = true;
            }
            if (action.equals("LEFT")) {
                leftPressed = true;
            }
            if (action.equals("RIGHT")) {
                rightPressed = true;
            }

            // player 2 keys
            if (action.equals("W")) {
                wPressed = true;
            }
            if (action.equals("S")) {
                sPressed = true;
            }
            if (action.equals("A")) {
                aPressed = true;
            }
            if (action.equals("D")) {
                dPressed = true;
            }
        }
    }

    private static class KeyReleased extends AbstractAction {
        private String action;

        public KeyReleased() { action = ""; }
        public KeyReleased(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            // player 1 keys
            if (action.equals("UP")) {
                upPressed = false;
            }
            if (action.equals("DOWN")) {
                downPressed = false;
            }
            if (action.equals("LEFT")) {
                leftPressed = false;
            }
            if (action.equals("RIGHT")) {
                rightPressed = false;
            }

            // player 2 keys
            if (action.equals("W")) {
                wPressed = false;
            }
            if (action.equals("S")) {
                sPressed = false;
            }
            if (action.equals("A")) {
                aPressed = false;
            }
            if (action.equals("D")) {
                dPressed = false;
            }
        }
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) { endgame = true; }
    }

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // reset game before someone starts playing
            endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            wPressed = false;
            sPressed = false;
            aPressed = false;
            dPressed = false;
            p1 = new ImageObject(p1originalX, p1originalY, p1width, p1height, 0.0);
            p1velocity = 0.0;
            p2 = new ImageObject(p2originalX, p2originalY, p2width, p2height, 0.0);
            p2velocity = 0.0;

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) { }

            lastAudioStart = System.currentTimeMillis();
            playAudio(backgroundState);
            endgame = false;
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new Player1Mover());
            Thread t3 = new Thread(new Player2Mover());
            Thread t4 = new Thread(new CollisionChecker());
            Thread t5 = new Thread(new AudioLooper());
            Thread t6 = new Thread(new WinChecker());

            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
            t6.start();
        }
    }

    private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2) {
        Boolean ret = false;
        if (p1x > p2x1 && p1x < p2x2) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        if (p1x > p2x2 && p1x < p2x1) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        return ret;
    }

    private static Boolean collisionOccursCoordinates(double p1x1, double p1y1, double p1x2, double p1y2,
                                                      double p2x1, double p2y1, double p2x2, double p2y2) {
        Boolean ret = false;
        if (isInside(p1x1, p1y1, p2x1, p2y1, p2x2, p2y2) == true) {
            ret = true;
        }
        if (isInside(p1x1, p1y2, p2x1, p2y1, p2x2, p2y2) == true) {
            ret = true;
        }
        if (isInside(p1x2, p1y1, p2x1, p2y1, p2x2, p2y2) == true) {
            ret = true;
        }
        if (isInside(p1x2, p1y2, p2x1, p2y1, p2x2, p2y2) == true) {
            ret = true;
        }
        if (isInside(p2x1, p2y1, p1x1, p1y1, p1x2, p1y2) == true) {
            ret = true;
        }
        if (isInside(p2x1, p2y2, p1x1, p1y1, p1x2, p1y2) == true) {
            ret = true;
        }
        if (isInside(p2x2, p2y1, p1x1, p1y1, p1x2, p1y2) == true) {
            ret = true;
        }
        if (isInside(p2x2, p2y2, p1x1, p1y1, p1x2, p1y2) == true) {
            ret = true;
        }
        return ret;
    }

    private static Boolean collisionOccurs(ImageObject obj1, ImageObject obj2) {
        Boolean ret = false;
        if (collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
                obj1.getY() + obj1.getHeight(), obj2.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
                obj2.getY() + obj2.getHeight()) == true) {
            ret = true;
        }
        return true;
    }

    // ImageObject class to hold all of our images
    private static class ImageObject {
        private double x;
        private double y;
        // TODO: I'm not sure if I need the lastposx and lastposy -> probably not bc that would be for switching bkgds
        private double lastposx;
        private double lastposy;
        private double xwidth;
        private double yheight;
        private double angle; // in Radians
        private double internalangle; // in Radians
        private Vector<Double> coords;
        private Vector<Double> triangles;
        private double comX;
        private double comY;

        private Boolean bounce;

        public ImageObject() {
            bounce = false;
        }

        public ImageObject(double xinput, double yinput, double xwidthinput, double yheightinput, double angleinput) {
            this();
            x = xinput;
            y = yinput;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public double getlastposx() { return lastposx; }
        public double getlastposy() { return lastposy; }
        public void setlastposx (double input) { lastposx = input; }
        public void setlastposy (double input) { lastposy = input; }
        public double getWidth() { return xwidth; }
        public double getHeight() { return yheight; }
        public double getAngle() { return angle; }
        public double getInternalAngle() { return internalangle; }
        public void setAngle(double angleinput) { angle = angleinput; }
        public void setInternalAngle(double internalangleinput) { internalangle = internalangleinput; }
        public Vector<Double> getCoords() { return coords; }
        public void setCoords(Vector<Double> coordsinput) {
            coords = coordsinput;
            generateTriangles();
            // printTriangles();
        }
        public Boolean getBounce() { return bounce; }
        public void setBounce(Boolean input) { bounce = input; }

        // TODO: with this, I'm not sure I'll need it, but we'll see
        public void updateBounce() {
            if (getBounce()) {
                moveto(getlastposx(), getlastposy());
            } else {
                setlastposx(getX());
                setlastposy(getY());
            }
            setBounce(false);
        }

        public void generateTriangles() {
            triangles = new Vector<Double>();
            // format: (0, 1), (2, 3), (4, 5) is the (x, y) coords of a triangle

            // get center point of all coordinates
            comX = getComX();
            comY = getComY();

            for (int i = 0; i < coords.size(); i = i + 2) {
                triangles.addElement(coords.elementAt(i));
                triangles.addElement(coords.elementAt(i + 1));

                triangles.addElement(coords.elementAt((i+2) % coords.size()));
                triangles.addElement(coords.elementAt((i+3) % coords.size()));

                triangles.addElement(comX);
                triangles.addElement(comY);
            }
        }

        public double getComX() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        public double getComY() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 1; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        public void move(double xinput, double yinput) {
            x = x + xinput;
            y = y + yinput;
        }

        public void moveto(double xinput, double yinput) {
            x = xinput;
            y = yinput;
        }

        // Asteroid's screenWrap
        public void screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
            if (x > rightEdge) {
                moveto(leftEdge, getY());
            }

            if (x < leftEdge) {
                moveto(rightEdge, getY());
            }

            if (y > bottomEdge) {
                moveto(getX(), topEdge);
            }

            if (y < topEdge) {
                moveto(getX(), bottomEdge);
            }
        }

        public void rotate(double angleinput) {
            angle = angle + angleinput;
            while (angle > twoPi) {
                angle = angle - twoPi;
            }

            while (angle < 0) {
                angle = angle + twoPi;
            }
        }

        public void spin(double internalangleinput) {
            internalangle = internalangle + internalangleinput;
            while (internalangle > twoPi) {
                internalangle = internalangle - twoPi;
            }

            while (internalangle < 0) {
                internalangle = internalangle + twoPi;
            }
        }

    }

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    private static class Laps implements ActionListener {
        public int decodeLaps(String input) {
            int ret = 3;
            if (input.equals("One")) {
                ret = 1;
            }
            if (input.equals("Two")) {
                ret = 2;
            }
            if (input.equals("Three")) {
                ret = 3;
            }
            if (input.equals("Four")) {
                ret = 4;
            }
            if (input.equals("Five")) {
                ret = 5;
            }
            if (input.equals("Six")) {
                ret = 6;
            }
            if (input.equals("Seven")) {
                ret = 7;
            }
            if (input.equals("Eight")) {
                ret = 8;
            }
            if (input.equals("Nine")) {
                ret = 9;
            }
            if (input.equals("Ten")) {
                ret = 10;
            }
            return ret;
        }

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            String textLaps = (String)cb.getSelectedItem();
            maxlaps = decodeLaps(textLaps);
        }

    }


    public static void main(String[] args) {
        setup();

        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(750, 750);
        JPanel myPanel = new JPanel();

        // max laps
        String [] levels = { "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten" };
        JComboBox<String> levelMenu = new JComboBox<String>(levels) ;
        levelMenu.setSelectedIndex(2);
        levelMenu.addActionListener(new Laps());
        myPanel.add(levelMenu);

        // new Game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        // quit button
        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        // set keys to chosen directions
        // player 1's controls
        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
        // player 2's controls
        bindKey(myPanel, "W");
        bindKey(myPanel, "S");
        bindKey(myPanel, "A");
        bindKey(myPanel, "D");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }


}
