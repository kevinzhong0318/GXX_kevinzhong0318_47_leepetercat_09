import java.awt.*;
import javax.swing.ImageIcon;

public class Bullet {
    public double x, y, vx, vy;
    public int width = 6, height = 35; 
    public boolean isEnemy; 
    private static Image pBulletImg, eBulletImg;

    public Bullet(double x, double y, double vx, double vy, boolean isEnemy) {
        this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.isEnemy = isEnemy;
        if (pBulletImg == null) pBulletImg = new ImageIcon("image/green_blaster.jpg").getImage();
        if (eBulletImg == null) eBulletImg = new ImageIcon("image/red_blaster.jpg").getImage();
    }

    public void update() { x += vx; y += vy; }

    public void draw(Graphics2D g2d) {
        Image img = isEnemy ? eBulletImg : pBulletImg;
        if (img != null && img.getWidth(null) > 0) {
            g2d.drawImage(img, (int)x - width/2, (int)y, width, height, null);
        } else {
            g2d.setColor(isEnemy ? Color.RED : Color.CYAN);
            g2d.fillRect((int)x - width/2, (int)y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x - width/2, (int)y, width, height);
    }
}