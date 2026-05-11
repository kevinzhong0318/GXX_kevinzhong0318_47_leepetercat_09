import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class StarWarsGame extends JFrame {
    private CirclePanel circlePanel;

    public StarWarsGame() {
        setTitle("Star Wars Shooter - Optimized Version");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        circlePanel = new CirclePanel();
        add(circlePanel);
        setVisible(true);
    }

    // --- 背景星星 ---
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

    // --- 子彈類別 ---
    class Bullet {
        double x, y, vx, vy;
        int width = 6, height = 35; 
        boolean isEnemy; 
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
            // 縮小子彈判定範圍，避免太容易被打到
            return new Rectangle((int)x - width/2, (int)y, width, height);
        }
    }

    // --- 敵人類別 ---
    class Enemy {
        double x, y;
        int size = 50;
        int shootTimer = 0;
        int shootInterval = 150; 
        private static Image enemyImg;

        public Enemy(double x, double y) {
            this.x = x; this.y = y;
            if (enemyImg == null) enemyImg = new ImageIcon("image/enemy2_1.png").getImage();
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

    // --- 遊戲面板 ---
    class CirclePanel extends JPanel implements MouseMotionListener, MouseListener {
        private double circleX = 400, circleY = 400;
        private int targetX = 400, targetY = 300;
        private final int PLAYER_SIZE = 60; 
        private int score = 0;
        private boolean isGameOver = false;

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
            initGame();

            timer = new Timer(16, e -> {
                if (!isGameOver) updateAnimation();
                repaint();
            });
            timer.start();
        }

        private void initGame() {
            score = 0;
            isGameOver = false;
            bullets.clear();
            enemies.clear();
            stars.clear();
            for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
            // 產生 5 個敵人
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(100 + i * 130, 50 + (i % 2) * 50));
            }
        }

        private void updateAnimation() {
            circleX += (targetX - circleX) * 0.15;
            circleY += (targetY - circleY) * 0.15;
            for (Star star : stars) star.update(getHeight());

            // --- 核心修正：縮小玩家碰撞箱 (Hitbox) ---
            // 只取圖片中心 60% 的範圍，避免撞到透明邊緣就死
            int hitboxSize = (int)(PLAYER_SIZE * 0.6); 
            Rectangle playerRect = new Rectangle(
                (int)circleX - hitboxSize/2, 
                (int)circleY - hitboxSize/2, 
                hitboxSize, 
                hitboxSize
            );

            // 1. 更新子彈
            Iterator<Bullet> bIt = bullets.iterator();
            while (bIt.hasNext()) {
                Bullet b = bIt.next();
                b.update();
                if (b.y < -50 || b.y > getHeight() + 50) {
                    bIt.remove();
                    continue;
                }
                // 敵人子彈撞擊玩家判定
                if (b.isEnemy && b.getBounds().intersects(playerRect)) {
                    isGameOver = true;
                }
            }

            // 2. 更新敵人與擊殺消失邏輯 (不重生)
            Iterator<Enemy> eIt = enemies.iterator();
            while (eIt.hasNext()) {
                Enemy e = eIt.next();
                e.update(bullets);

                boolean killed = false;
                Iterator<Bullet> bIt2 = bullets.iterator();
                while (bIt2.hasNext()) {
                    Bullet b = bIt2.next();
                    // 必須是玩家子彈 (!b.isEnemy) 且 碰撞敵人
                    if (!b.isEnemy && b.getBounds().intersects(e.getBounds())) {
                        score++;
                        bIt2.remove(); 
                        killed = true; 
                        break; 
                    }
                }
                if (killed) eIt.remove(); 
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
            
            // 畫玩家
            int drawX = (int)(circleX - PLAYER_SIZE / 2);
            int drawY = (int)(circleY - PLAYER_SIZE / 2);
            if (playerImg != null && playerImg.getWidth(null) > 0) {
                g2d.drawImage(playerImg, drawX, drawY, PLAYER_SIZE, PLAYER_SIZE, this);
            } else {
                g2d.setColor(Color.GREEN);
                g2d.fillRect(drawX, drawY, PLAYER_SIZE, PLAYER_SIZE);
            }

            // 分數顯示
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 22));
            g2d.drawString("SCORE: " + score, getWidth() - 160, 40);

            // 失敗畫面
            if (isGameOver) {
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 60));
                g2d.drawString("MISSION FAILED", getWidth()/2 - 240, getHeight()/2);
                g2d.setFont(new Font("Arial", Font.PLAIN, 20));
                g2d.setColor(Color.WHITE);
                g2d.drawString("Click Anywhere to Restart", getWidth()/2 - 120, getHeight()/2 + 50);
            }
            
            // 勝利畫面 (打完所有敵人)
            if (!isGameOver && enemies.isEmpty()) {
                g2d.setColor(Color.YELLOW);
                g2d.setFont(new Font("Arial", Font.BOLD, 50));
                g2d.drawString("ALL ENEMIES CLEAR!", getWidth()/2 - 280, getHeight()/2);
            }
        }

        @Override 
        public void mousePressed(MouseEvent e) {
            if (isGameOver) { initGame(); return; }
            
            if (SwingUtilities.isLeftMouseButton(e)) {
                bullets.add(new Bullet(circleX, circleY - 20, 0, -15, false));
            } else if (SwingUtilities.isRightMouseButton(e)) {
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