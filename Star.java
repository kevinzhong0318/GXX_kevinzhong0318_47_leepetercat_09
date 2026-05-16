import java.awt.*;
import java.util.Random;

public class Star {
    public double x, y, speed;
    public int size;

    public Star(int pw, int ph) {
        Random rand = new Random();
        this.x = rand.nextDouble() * pw;
        this.y = rand.nextDouble() * ph;
        this.speed = 1 + rand.nextDouble() * 2;
        this.size = 2 + rand.nextInt(2);
    }

    public void update(int ph) {
        y += speed;
        if (y > ph) { y = 0; x = new Random().nextDouble() * 800; }
    }

    public void draw(Graphics g) {
        g.fillOval((int)x, (int)y, size, size);
    }
}