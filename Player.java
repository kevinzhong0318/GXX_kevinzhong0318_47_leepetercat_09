import java.awt.*;
import java.io.File;
import javax.swing.ImageIcon;

public class Player {
    public double x, y;
    public int health = 3;
    public final int SIZE = 60;
    private Image img;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.img = new ImageIcon("image" + File.separator + "character.png").getImage();
    }

    // 限制玩家不能飛進 UI 區域 (頂部 80 像素)
    public void update(int targetX, int targetY) {
        int safeYTop = 80;
        int currentTargetY = Math.max(safeYTop + SIZE / 2, targetY);

        // 平滑移動 (Easing)
        this.x += (targetX - this.x) * 0.15;
        this.y += (currentTargetY - this.y) * 0.15;
    }

    public void draw(Graphics2D g2d, Component c) {
        if (img != null && img.getWidth(null) > 0) {
            g2d.drawImage(img, (int)x - SIZE/2, (int)y - SIZE/2, SIZE, SIZE, c);
        } else {
            g2d.setColor(Color.GREEN);
            g2d.fillRect((int)x - SIZE/2, (int)y - SIZE/2, SIZE, SIZE);
        }
    }

    // 取得縮小後的碰撞箱 (Hitbox)
    public Rectangle getBounds() {
        int hitboxSize = (int)(SIZE * 0.6); 
        return new Rectangle((int)x - hitboxSize/2, (int)y - hitboxSize/2, hitboxSize, hitboxSize);
    }
    
    public void reset(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.health = 3;
    }
}