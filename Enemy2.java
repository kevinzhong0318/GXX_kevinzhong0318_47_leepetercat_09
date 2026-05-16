import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Enemy2 {
    public double x, y;
    public int size = 40;
    public int health = 1;
    private int shootTimer = 0;
    private double vy = 1.5; // 新增：垂直移動速度
    private static Image enemyImg;

    public Enemy2(double x, double y) {
        this.x = x;
        this.y = y;
        if (enemyImg == null) enemyImg = new ImageIcon("image\\enemy2.png").getImage();
    }

    public void update(ArrayList<Bullet> bullets) {
        // --- 移動邏輯修正：在畫面上半部彈跳 ---
        y += vy;
        x += Math.sin(y / 20.0) * 3.0; // 蛇形移動

        // 如果撞到頂部(30)或接近中部(250)，就反彈
        if (y > 250 || y < 30) {
            vy *= -1;
        }

        // --- 射擊邏輯：環狀擴散 ---
        shootTimer++;
        if (shootTimer >= 100) {
            int bulletCount = 8;
            for (int i = 0; i < bulletCount; i++) {
                double angle = i * (2 * Math.PI / bulletCount);
                double speed = 3.0;
                bullets.add(new Bullet(x + size / 2, y + size / 2, 
                            Math.cos(angle) * speed, Math.sin(angle) * speed, true));
            }
            shootTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (enemyImg != null && enemyImg.getWidth(null) > 0) {
            g2d.drawImage(enemyImg, (int)x, (int)y, size, size, null);
        } else {
            g2d.setColor(Color.ORANGE);
            g2d.fillOval((int)x, (int)y, size, size);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
}