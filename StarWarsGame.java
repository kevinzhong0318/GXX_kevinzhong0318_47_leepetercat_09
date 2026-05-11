
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
            g.fillOval((int) x, (int) y, size, size);
        }
    }

    // --- 新增：雷射子彈類別 ---
    // --- 子彈類別 (使用圖片) ---
    class Bullet {

        double x, y;
        double vx; // 新增：水平速度 (0 為直線，正值往右斜，負值往左斜)
        int width = 6, height = 35;
        double speed = 15;

        private static Image bulletImg = null;

        public Bullet(double x, double y, double vx) {
            this.x = x;
            this.y = y;
            this.vx = vx; // 接收水平速度

            if (bulletImg == null) {
                bulletImg = new ImageIcon("image/green_blaster.jpg").getImage();
            }
        }

        public void update() {
            y -= speed; // 向上移動
            x += vx;    // 根據 vx 決定斜率
        }

        public void draw(Graphics2D g2d) {
            int drawX = (int) (x - width / 2);
            int drawY = (int) y;

            if (bulletImg != null && bulletImg.getWidth(null) != -1) {
                // 如果想要更精緻，可以根據 vx 旋轉圖片，這裡先簡單處理繪製
                g2d.drawImage(bulletImg, drawX, drawY, width, height, null);
            } else {
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
            for (int i = 0; i < 50; i++) {
                stars.add(new Star(800, 600));
            }

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
            for (Star star : stars) {
                star.update(getHeight());
            }

            // 更新子彈並移除畫面外的子彈
            Iterator<Bullet> it = bullets.iterator();
            while (it.hasNext()) {
                Bullet b = it.next();
                b.update();
                if (b.y < -20) {
                    it.remove();
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 畫星星
            g2d.setColor(Color.WHITE);
            for (Star star : stars) {
                star.draw(g2d);
            }

            // 畫子彈
            for (Bullet b : bullets) {
                b.draw(g2d);
            }

            // 畫飛船
            int drawX = (int) (circleX - PLAYER_SIZE / 2);
            int drawY = (int) (circleY - PLAYER_SIZE / 2);

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
            if (SwingUtilities.isLeftMouseButton(e)) {
                // --- 左鍵：發射中間直線 ---
                // vx 設為 0
                bullets.add(new Bullet(circleX, circleY - PLAYER_SIZE / 2, 0));

            } else if (SwingUtilities.isRightMouseButton(e)) {
                // --- 右鍵：發射兩邊斜線 ---
                // 左斜子彈 (vx 為負)
                bullets.add(new Bullet(circleX - 10, circleY - PLAYER_SIZE / 4, -3));
                // 右斜子彈 (vx 為正)
                bullets.add(new Bullet(circleX + 10, circleY - PLAYER_SIZE / 4, 3));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            targetX = e.getX();
            targetY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            targetX = e.getX();
            targetY = e.getY();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StarWarsGame());
    }
}
