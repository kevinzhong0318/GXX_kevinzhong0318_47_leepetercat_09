import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
public class Enemy2 {
    public double x, y;
    public int size = 40;
    public int health = 1;
    private int shootTimer = 0;
    
    // 控制移動速度
    private double vy = 1.8; 
    private static Image enemyImg;

    public Enemy2(double x, double y) {
        this.x = x;
        this.y = y;
        if (enemyImg == null) {
            enemyImg = new ImageIcon("image" + File.separator + "enemy2_8.png").getImage();
        }
    }

    public void update(ArrayList<Bullet> bullets) {
        // --- 1. 移動邏輯 ---
        y += vy;
        // 蛇形震盪 (由 Sin 函數產生左右晃動)
        x += Math.sin(y / 20.0) * 4.0; 

        // --- 2. 垂直邊界保護 (防止上下消失) ---
        // 限制在畫面上半部 (30 ~ 280 像素之間)
        if (y > 280) {
            y = 280;
            vy = -Math.abs(vy); // 強制轉向朝上
        } else if (y < 30) {
            y = 30;
            vy = Math.abs(vy);  // 強制轉向朝下
        }

        // --- 3. 水平邊界保護 (防止左右消失) ---
        // 假設視窗寬度為 800
        if (x < 15) {
            x = 15;
        } else if (x > 800 - size - 15) {
            x = 800 - size - 15;
        }

        // --- 4. 射擊邏輯 (環狀擴散) ---
        shootTimer++;
        if (shootTimer >= 90) { // 每 90 幀射擊一次
            int bulletCount = 8; 
            for (int i = 0; i < bulletCount; i++) {
                // 計算 360 度平分的弧度
                double angle = i * (2 * Math.PI / bulletCount);
                double speed = 3.5;
                double bulletVx = Math.cos(angle) * speed;
                double bulletVy = Math.sin(angle) * speed;
                
                // 加入子彈清單 (isEnemy = true)
                bullets.add(new Bullet(x + size / 2, y + size / 2, bulletVx, bulletVy, true));
            }
            shootTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (enemyImg != null && enemyImg.getWidth(null) > 0) {
            g2d.drawImage(enemyImg, (int)x, (int)y, size, size, null);
        } else {
            // 如果沒圖片，畫一顆橘色圓球
            g2d.setColor(Color.ORANGE);
            g2d.fillOval((int)x, (int)y, size, size);
            g2d.setColor(Color.WHITE);
            g2d.drawOval((int)x, (int)y, size, size);
        }
    }

    public Rectangle getBounds() {
        // 回傳碰撞箱
        return new Rectangle((int)x, (int)y, size, size);
    }
}