package se233.Asteroids_Project.model.Entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.Asteroids_Project.model.GameObject;

public class Asteroid extends GameObject {
    private static final Logger logger = LogManager.getLogger(Asteroid.class);
    private double rotationSpeed;
    private double speedX;
    private double speedY;
    private int size; // 1:Large, 2:Medium, 3:Small
    private int points; // Points awarded when destroyed
    private boolean markedForDestruction;
    private Image asteroidImage;

    private static final String asteroid1 = "/se233/Asteroids_Project/asset/asteroid1.png";
    private static final String asteroid2 = "/se233/Asteroids_Project/asset/asteroid2.png";
    private static final String asteroid3 = "/se233/Asteroids_Project/asset/asteroid3.png";

    public Asteroid(double x, double y, int size) {
        super(getImagePathForSize(size), x, y, getAsteroidSize(size), getAsteroidSize(size));
        this.size = size;
        this.markedForDestruction = false;
        initializeAsteroid();
        loadAsteroidImage();
    }

    private void loadAsteroidImage() {
        try {
            String imagePath = getImagePathForSize(size);
            this.asteroidImage = new Image(getClass().getResourceAsStream(imagePath));
            if (this.asteroidImage == null) {
                logger.error("Failed to load asteroid image for size: " + size);
            }
        } catch (Exception e) {
            logger.error("Error loading asteroid image: " + e.getMessage());
        }
    }

    private static String getImagePathForSize(int size) {
        return switch(size) {
            case 1 -> asteroid1; // Small
            case 2 -> asteroid2; // Medium
            case 3 -> asteroid3; // Large

            default -> throw new IllegalArgumentException("Invalid asteroid image: " + size);
        };
    }



    private static double getAsteroidSize(int size) {
        return switch(size) {
            case 1 -> 25.0; // Small
            case 2 -> 50.0; // Medium
            case 3 -> 100.0; // Large

            default -> throw new IllegalArgumentException("Invalid asteroid size: " + size);
        };
    }

    private void initializeAsteroid() {
        // Random movement direction
        double angle = Math.random() * Math.PI * 2;
        double speed = 3 + Math.random() * 2;

        switch(this.size) {
            case 1: // Large
                speed *= 1.5;
                points = 10;
                break;
            case 2: // Medium
                speed *= 1.0;
                points = 50;
                break;
            case 3: // Medium
                speed *= 0.5;
                points = 100;
                break;
            default:
                throw new IllegalArgumentException("Invalid asteroid size: " + size);
        }

        speedX = Math.cos(angle) * speed;
        speedY = Math.sin(angle) * speed;
        rotationSpeed = (Math.random() - 0.5) * 5;
        rotation = Math.random() * 360;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;
        rotation += rotationSpeed;
        wrapAroundScreen();
    }

    private void wrapAroundScreen() {
        if (x < -width) x = 800;
        if (x > 800) x = -width;
        if (y < -height) y = 600;
        if (y > 600) y = -height;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (asteroidImage != null) {
            gc.save();

            // Calculate the center point for rotation
            double centerX = x + width / 2;
            double centerY = y + height / 2;

            // Translate to rotation point
            gc.translate(centerX, centerY);
            gc.rotate(rotation);
            gc.translate(-centerX, -centerY);

            // Draw the image
            gc.drawImage(asteroidImage,
                    x, y,
                    width, height);

            gc.restore();
        } else {
            logger.warn("Asteroid sprite is null, cannot render");
        }
    }

    public Asteroid[] split() {
        if (size >= 2) return new Asteroid[0];
        Asteroid[] fragments = new Asteroid[2];
        fragments[0] = new Asteroid(x, y, 2);
        fragments[1] = new Asteroid(x, y, 2);
        return fragments;
    }

    public void markForDestruction() {
        this.markedForDestruction = true;
    }

    public boolean isMarkedForDestruction() {
        return markedForDestruction;
    }

    public int getPoints() {
        return points;
    }

    public int getSize() {
        return size;
    }
}