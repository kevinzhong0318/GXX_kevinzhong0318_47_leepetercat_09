import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Boss {
    public double x, y;
    public int size = 120;
    public int health = 20;
    private int shootTimer = 0;
    private static Image bossImg;

    public Boss(double x, double y) {
        this.x = x;
        this.y = y;
        if (bossImg == null) bossImg = new ImageIcon("image\\enemy2_10.png").getImage();
    }

    public void update(ArrayList<Bullet> bullets) {
        x += Math.sin(System.currentTimeMillis() / 800.0) * 2.0;
        shootTimer++;
        if (shootTimer >= 30) {
            for (int i = -2; i <= 2; i++) {
                bullets.add(new Bullet(x + size / 2, y + size, i * 2, 6, true));
            }
            bullets.add(new Bullet(x + size / 2, y + size, 0, 8, true));
            shootTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (bossImg != null && bossImg.getWidth(null) > 0) {
            g2d.drawImage(bossImg, (int)x, (int)y, size, size, null);
        } else {
            g2d.setColor(Color.MAGENTA);
            g2d.fillOval((int)x, (int)y, size, size);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
}
