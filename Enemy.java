import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Enemy {
    public double x, y;
    public int size = 50;
    private int shootTimer = 0;
    private int shootInterval = 150; 
    private static Image enemyImg;

    public Enemy(double x, double y) {
        this.x = x; this.y = y;
        if (enemyImg == null) enemyImg = new ImageIcon("image\\enemy2_1.png").getImage();
    }

    public void update(ArrayList<Bullet> bullets) {
        x += Math.sin(System.currentTimeMillis() / 600.0) * 1.5;
        shootTimer++;
        if (shootTimer >= shootInterval) {
            bullets.add(new Bullet(x + size/2, y + size, 0, 5, true));
            shootTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (enemyImg != null && enemyImg.getWidth(null) > 0) {
            g2d.drawImage(enemyImg, (int)x, (int)y, size, size, null);
        } else {
            g2d.setColor(Color.ORANGE);
            g2d.fillRect((int)x, (int)y, size, size);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
}