import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Enemy2 {
    public double x, y;
    public int size = 40;
    public int health = 1; // 血量為 1
    private int shootTimer = 0;
    private static Image enemyImg;

    public Enemy2(double x, double y) {
        this.x = x;
        this.y = y;
        if (enemyImg == null) enemyImg = new ImageIcon("image\\enemy2.png").getImage();
    }

    public void update(ArrayList<Bullet> bullets) {
        // 移動邏輯：向下移動並小幅度左右晃動
        y += 2.0;
        x += Math.sin(y / 25.0) * 2.5;

        // 創意射擊：環狀擴散彈 (Ring Shot)
        shootTimer++;
        if (shootTimer >= 100) { // 控制射擊頻率
            int bulletCount = 8; // 一次噴出 8 顆子彈
            for (int i = 0; i < bulletCount; i++) {
                double angle = i * (2 * Math.PI / bulletCount);
                double speed = 3.5;
                double vx = Math.cos(angle) * speed;
                double vy = Math.sin(angle) * speed;
                // 將子彈加入主遊戲的 bullets 清單中
                bullets.add(new Bullet(x + size / 2, y + size / 2, vx, vy, true));
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