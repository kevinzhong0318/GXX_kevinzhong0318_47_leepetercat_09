import java.awt.*;
import java.util.Random;

public class Star {
    public double x, y, speed;
    public int size;
    private static Random rand = new Random(); 

    public Star(int pw, int ph) {
        this.x = rand.nextDouble() * pw;
        this.y = rand.nextDouble() * ph;
        this.speed = 1 + rand.nextDouble() * 2;
        this.size = 2 + rand.nextInt(2);
    }

    public void update(int ph) {
        y += speed;
        // 修正：如果 ph 為 0 (視窗尚未完全載入時)，暫時不重置
        if (ph > 0 && y > ph) { 
            y = -size; // 從頂部外一點點滑入，比較自然
            x = rand.nextDouble() * 800; 
        }
    }

    public void draw(Graphics g) {
        // --- 關鍵修正：一定要設定顏色，否則會繼承上一個元件的顏色 ---
        g.setColor(Color.WHITE); 
        g.fillOval((int)x, (int)y, size, size);
    }
}