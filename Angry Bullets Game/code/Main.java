/**
 * Program executes the Angry Bullet Game.
 * @author Aysu Keskin, Student ID: 2023400042
 * @since Date: 14.03.2024
 */
import java.awt.event.KeyEvent;
public class Main {
    public static void main(String[] args) {
        boolean value = true;
        int width = 1600; //screen width
        int height = 800; // screen height
        double gravity = 9.80665; // gravity
        double x0 = 120; // x and y coordinates of the bullet’s starting position on the platform double
        double y0 = 120;
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angel
        double[][] obstacleArray = { // x coordinates, y coordinates of the lower left corners of the obstacles, width, height
                {1200, 0, 60, 220},
                {1000, 0, 60, 160},
                {600, 0, 60, 80},
                {600, 180, 60, 160},
                {220, 0, 120, 180}
        };
        double[][] targetArray = { // x coordinates, y coordinates of the lower left corners of the targets, width, height
                {1160, 0, 30, 30},
                {730, 0, 30, 30},
                {150, 0, 20, 20},
                {1480, 0, 60, 60},
                {340, 80, 60, 30},
                {1500, 600, 60, 60}
        };
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0.0, width);
        StdDraw.setYscale(0.0, height);
        //shooting platform
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.filledRectangle(x0 / 2, y0 / 2, x0 / 2, y0 / 2);
        StdDraw.line(x0, y0, x0 + bulletVelocity * Math.cos(Math.toRadians(bulletAngle))/3, y0 + bulletVelocity * Math.sin(Math.toRadians(bulletAngle))/3); // shooting line
        //obstacles
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        for (int i = 0; i < obstacleArray.length; i++) {
            double halfWidth = obstacleArray[i][2] / 2;
            double halfHeight = obstacleArray[i][3] / 2;
            double xCor = obstacleArray[i][0] + halfWidth;
            double yCor = obstacleArray[i][1] + halfHeight;
            StdDraw.filledRectangle(xCor, yCor, halfWidth, halfHeight);
        }
        //targets
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        for (int i = 0; i < targetArray.length; i++) {
             double halfWidth = targetArray[i][2] / 2;
             double halfHeight = targetArray[i][3] / 2;
             double xCor = targetArray[i][0] + halfWidth;
             double yCor = targetArray[i][1] + halfHeight;
             StdDraw.filledRectangle(xCor, yCor, halfWidth, halfHeight);
        }
        StdDraw.enableDoubleBuffering();
        int pauseDuration = 100; // duration of the pause
        while (value) {
            bulletVelocity = 180;
            bulletAngle = 45.0;
            while (true) {
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    StdDraw.pause(pauseDuration);
                    break;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    StdDraw.pause(pauseDuration);
                    bulletVelocity += 1.0;
                }

                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                    StdDraw.pause(pauseDuration);
                    bulletVelocity -= 1.0;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                    StdDraw.pause(pauseDuration);
                    bulletAngle += 1.0;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                    StdDraw.pause(pauseDuration);
                    bulletAngle -= 1.0;
                }
                StdDraw.clear();
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.setPenRadius(0.01);
                StdDraw.filledRectangle(x0 / 2, y0 / 2, x0 / 2, y0 / 2);
                StdDraw.line(x0, y0, x0 + (bulletVelocity * (Math.cos(Math.toRadians(bulletAngle)))) / 3, y0 + (bulletVelocity * Math.sin(Math.toRadians(bulletAngle))) / 3); // shooting line
                StdDraw.setPenColor(StdDraw.DARK_GRAY); //obstacles
                for (int i = 0; i < obstacleArray.length; i++) {
                    double halfWidth = obstacleArray[i][2] / 2;
                    double halfHeight = obstacleArray[i][3] / 2;
                    double xCor = obstacleArray[i][0] + halfWidth;
                    double yCor = obstacleArray[i][1] + halfHeight;
                    StdDraw.filledRectangle(xCor, yCor, halfWidth, halfHeight);
                }
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); //targets
                for (int i = 0; i < targetArray.length; i++) {
                    double halfWidth = targetArray[i][2] / 2;
                    double halfHeight = targetArray[i][3] / 2;
                    double xCor = targetArray[i][0] + halfWidth;
                    double yCor = targetArray[i][1] + halfHeight;
                    StdDraw.filledRectangle(xCor, yCor, halfWidth, halfHeight);

                }
                StdDraw.setPenColor(StdDraw.WHITE);
                String s = String.format("a: %.1f", bulletAngle);
                String m = String.format("v: %.1f", bulletVelocity);
                StdDraw.text(x0 / 2, y0 / 2, s);
                StdDraw.text(x0 / 2, y0 / 2 - 19, m);
                StdDraw.show();
            }
            double time = 0.0;
            double x = x0; // x coordinate
            double y = y0; // y coordinate
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.001);
            pauseDuration = 30; // duration of the pause
            boolean breakOrNot = false;
            while (true) {
                StdDraw.pause(pauseDuration);
                if (breakOrNot) {
                    StdDraw.show();
                    break;
                }
                time += 0.20;
                double bulletAngleRadians = Math.toRadians(bulletAngle);
                double oldX = x; // previous x and y coordinates
                double oldY = y;
                x = x0 + (bulletVelocity / 1.8) * Math.cos(bulletAngleRadians) * time;
                y = y0 + (bulletVelocity / 1.8) * Math.sin(bulletAngleRadians) * time - gravity * Math.pow(time, 2) / 2;
                StdDraw.line(oldX, oldY, x, y);
                StdDraw.filledCircle(x, y, 4);
                StdDraw.show();
                for (int i = 0; i < obstacleArray.length; i++) {
                    double xFirst = obstacleArray[i][0];
                    double xLast = obstacleArray[i][0] + obstacleArray[i][2];
                    double yFirst = obstacleArray[i][1];
                    double yLast = obstacleArray[i][1] + obstacleArray[i][3];
                    if (x >= xFirst && xLast >= x && y >= yFirst && yLast >= y) {
                        StdDraw.textLeft(40, 750, "Hit an obstacle. Press 'r' to shoot again.");
                        breakOrNot = true;
                        break;
                    }
                }
                for (int i = 0; i < targetArray.length; i++) {
                    double xFirst = targetArray[i][0];
                    double xLast = targetArray[i][0] + targetArray[i][2];
                    double yFirst = targetArray[i][1];
                    double yLast = targetArray[i][1] + targetArray[i][3];
                    if (x >= xFirst && xLast >= x && y >= yFirst && yLast >= y) {
                        StdDraw.textLeft(40, 750, "Congratulations: You hit the target!");
                        breakOrNot = true;
                        break;
                    }
                }
                if (y < 0.0) {
                    StdDraw.textLeft(40, 750, "Hit the ground. Press 'r' to shoot again.");
                    breakOrNot = true;
                }
                else if (x > width) {
                    StdDraw.textLeft(40, 750, "Max X reached. Press 'r' to shoot again.");
                    breakOrNot = true;
                }
            }
            while(!StdDraw.isKeyPressed(KeyEvent.VK_R)) {
            }
            value = true;
        }
    }
}