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
        setTitle("Star Wars Shooter - Enemy Incoming");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        circlePanel = new CirclePanel();
        add(circlePanel);
        setVisible(true);
    }

    // --- 星空背景 ---
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
            if (y > ph) { y = 0; x = new Random().nextDouble() * 800; }
        }
        public void draw(Graphics g) { g.fillOval((int)x, (int)y, size, size); }
    }

    // --- 子彈類別 (通用) ---
    class Bullet {
        double x, y, vx, vy;
        int width = 5, height = 30;
        boolean isEnemy; 
        private static Image pBulletImg, eBulletImg;

        public Bullet(double x, double y, double vx, double vy, boolean isEnemy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.isEnemy = isEnemy;
            if (pBulletImg == null) pBulletImg = new ImageIcon("image/green_blaster.jpg").getImage();
            // 建議找一張紅色的子彈給敵人用
            if (eBulletImg == null) eBulletImg = new ImageIcon("image/red_blaster.png").getImage();
        }

        public void update() { x += vx; y += vy; }

        public void draw(Graphics2D g2d) {
            Image img = isEnemy ? eBulletImg : pBulletImg;
            if (img != null && img.getWidth(null) != -1) {
                g2d.drawImage(img, (int)x - width/2, (int)y, width, height, null);
            } else {
                g2d.setColor(isEnemy ? Color.RED : Color.GREEN);
                g2d.fillRect((int)x - width/2, (int)y, width, height);
            }
        }
    }

    // --- 敵人類別 ---
    class Enemy {
        double x, y;
        int size = 50;
        int shootTimer = 0;
        int shootInterval = 180; // 大約三秒射一次 (60 frames)
        private static Image enemyImg;

        public Enemy(double x, double y) {
            this.x = x;
            this.y = y;
            if (enemyImg == null) enemyImg = new ImageIcon("image/enemy2_1.png").getImage();
        }

        public void update(ArrayList<Bullet> bullets) {
            // 簡單的左右晃動
            x += Math.sin(System.currentTimeMillis() / 500.0) * 2;
            
            // 自動射擊邏輯
            shootTimer++;
            if (shootTimer >= shootInterval) {
                // 敵人子彈 vy 為正數，往下飛
                bullets.add(new Bullet(x + size/2, y + size, 0, 7, true));
                shootTimer = 0;
            }
        }

        public void draw(Graphics2D g2d) {
            if (enemyImg != null && enemyImg.getWidth(null) != -1) {
                g2d.drawImage(enemyImg, (int)x, (int)y, size, size, null);
            } else {
                g2d.setColor(Color.ORANGE);
                g2d.fillRect((int)x, (int)y, size, size);
            }
        }
    }

    class CirclePanel extends JPanel implements MouseMotionListener, MouseListener {
        private double circleX = 400, circleY = 400;
        private int targetX = 400, targetY = 300;
        private final int PLAYER_SIZE = 60; 
        private Image playerImg;
        private ArrayList<Star> stars = new ArrayList<>();
        private ArrayList<Bullet> bullets = new ArrayList<>();
        private ArrayList<Enemy> enemies = new ArrayList<>();
        private Timer timer;

        public CirclePanel() {
            setBackground(Color.BLACK);
            addMouseMotionListener(this);
            addMouseListener(this);
            playerImg = new ImageIcon("image/character.png").getImage(); 
            for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
            
            // 初始化幾個敵人
            enemies.add(new Enemy(200, 50));
            enemies.add(new Enemy(400, 80));
            enemies.add(new Enemy(600, 50));

            timer = new Timer(16, e -> { updateAnimation(); repaint(); });
            timer.start();
        }

        private void updateAnimation() {
            circleX += (targetX - circleX) * 0.15;
            circleY += (targetY - circleY) * 0.15;
            for (Star star : stars) star.update(getHeight());

            // 更新子彈
            Iterator<Bullet> bIt = bullets.iterator();
            while (bIt.hasNext()) {
                Bullet b = bIt.next();
                b.update();
                if (b.y < -50 || b.y > getHeight() + 50) bIt.remove();
            }

            // 更新敵人
            for (Enemy enemy : enemies) {
                enemy.update(bullets);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (Star star : stars) star.draw(g2d);
            for (Bullet b : bullets) b.draw(g2d);
            for (Enemy e : enemies) e.draw(g2d);
            
            int drawX = (int)(circleX - PLAYER_SIZE / 2);
            int drawY = (int)(circleY - PLAYER_SIZE / 2);
            if (playerImg != null) g2d.drawImage(playerImg, drawX, drawY, PLAYER_SIZE, PLAYER_SIZE, this);
        }

        @Override 
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                bullets.add(new Bullet(circleX, circleY - 20, 0, -15, false));
            } else {
                bullets.add(new Bullet(circleX - 10, circleY, -3, -12, false));
                bullets.add(new Bullet(circleX + 10, circleY, 3, -12, false));
            }
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