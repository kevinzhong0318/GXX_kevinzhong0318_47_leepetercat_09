import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class StarWarsGame extends JFrame {
    private CirclePanel circlePanel;

    public StarWarsGame() {
        setTitle("Star Wars Shooter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        circlePanel = new CirclePanel();
        add(circlePanel);
        setVisible(true);
    }

    // --- 星空背景類別 ---
    class Star {
        double x, y, speed;
        int size;
        public Star(int pw, int ph) {
            Random rand = new Random();
            this.x = rand.nextDouble() * pw;
            this.y = rand.nextDouble() * ph;
            this.speed = 1 + rand.nextDouble() * 2;
            this.size = 2 + rand.nextInt(2);
        }
        public void update(int ph) {
            y += speed;
            if (y > ph) {
                y = 0;
                x = new Random().nextDouble() * 800;
            }
        }
        public void draw(Graphics g) {
            g.fillOval((int)x, (int)y, size, size);
        }
    }

    // --- 新增：雷射子彈類別 ---
        // --- 子彈類別 (使用圖片) ---
    class Bullet {
        double x, y;
        // 定義子彈顯示的大小，根據你的圖片比例調整
        int width = 5, height = 40; 
        double speed = 15; // 可以稍微調快一點點
        
        // --- 新增：靜態圖片變數，確保所有子彈共用同一張圖，節省記憶體 ---
        private static Image bulletImg = null;

        public Bullet(double x, double y) {
            this.x = x;
            this.y = y;
            
            // --- 新增：懶人載入模式，第一次建立子彈時才載入圖片 ---
            if (bulletImg == null) {
                // 請確保路徑 correctly 指向你的子彈 PNG 檔 (建議使用去背圖)
                // 例如放在專案根目錄的 image 資料夾下
                bulletImg = new ImageIcon("image/green_blaster.jpg").getImage();
            }
        }

        public void update() { y -= speed; }

        public void draw(Graphics2D g2d) {
            // 計算繪製的左上角座標，使其中心對齊飛船
            int drawX = (int)(x - width / 2);
            int drawY = (int)y;

            if (bulletImg != null && bulletImg.getWidth(null) != -1) {
                // --- 修改：繪製子彈圖片 ---
                g2d.drawImage(bulletImg, drawX, drawY, width, height, null);
            } else {
                // 備案：如果圖片載入失敗，畫一條紅線代替，方便除錯
                g2d.setColor(Color.RED);
                g2d.fillRect(drawX, drawY, 2, height);
            }
        }
    }

    class CirclePanel extends JPanel implements MouseMotionListener, MouseListener {
        private double circleX = 400, circleY = 400;
        private int targetX = 400, targetY = 300;
        private final int PLAYER_SIZE = 60; 
        private final double EASING_FACTOR = 0.15;
        
        private Image playerImg;
        private ArrayList<Star> stars;
        private ArrayList<Bullet> bullets; // 管理子彈清單
        private Timer timer;

        public CirclePanel() {
            setBackground(Color.BLACK);
            addMouseMotionListener(this);
            addMouseListener(this);
            
            // 載入圖片
            playerImg = new ImageIcon("image/character.png").getImage(); 
            
            stars = new ArrayList<>();
            bullets = new ArrayList<>();
            for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
            
            timer = new Timer(16, e -> {
                updateAnimation();
                repaint();
            });
            timer.start();
        }

        private void updateAnimation() {
            // 飛船平滑移動
            circleX += (targetX - circleX) * EASING_FACTOR;
            circleY += (targetY - circleY) * EASING_FACTOR;
            
            // 更新星星
            for (Star star : stars) star.update(getHeight());
            
            // 更新子彈並移除畫面外的子彈
            Iterator<Bullet> it = bullets.iterator();
            while (it.hasNext()) {
                Bullet b = it.next();
                b.update();
                if (b.y < -20) it.remove(); 
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 畫星星
            g2d.setColor(Color.WHITE);
            for (Star star : stars) star.draw(g2d);
            
            // 畫子彈
            for (Bullet b : bullets) b.draw(g2d);
            
            // 畫飛船
            int drawX = (int)(circleX - PLAYER_SIZE / 2);
            int drawY = (int)(circleY - PLAYER_SIZE / 2);
            
            if (playerImg != null) {
                g2d.drawImage(playerImg, drawX, drawY, PLAYER_SIZE, PLAYER_SIZE, this);
            } else {
                g2d.setColor(Color.GREEN);
                g2d.fillRect(drawX, drawY, PLAYER_SIZE, PLAYER_SIZE);
            }
        }

        // --- 滑鼠點擊時新增子彈 ---
        @Override 
        public void mousePressed(MouseEvent e) {
            // 從飛船中心位置發射
            bullets.add(new Bullet(circleX, circleY - PLAYER_SIZE / 2));
        }

        @Override public void mouseMoved(MouseEvent e) { targetX = e.getX(); targetY = e.getY(); }
        @Override public void mouseDragged(MouseEvent e) { targetX = e.getX(); targetY = e.getY(); }
        @Override public void mouseClicked(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StarWarsGame());
    }
}