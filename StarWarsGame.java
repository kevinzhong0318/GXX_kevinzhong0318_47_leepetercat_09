import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
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

    class CirclePanel extends JPanel implements MouseMotionListener, MouseListener {
        private double circleX = 500, circleY = 400;
        private int targetX = 400, targetY = 300;
        private final int PLAYER_SIZE = 80; // 設定圖片顯示的大小
        private final double EASING_FACTOR = 0.1;
        private boolean isBoosting = false;
        
        // --- 新增：角色圖片變數 ---
        private Image playerImg;
        private ArrayList<Star> stars;
        private Timer timer;

        public CirclePanel() {
            setBackground(Color.BLACK);
            addMouseMotionListener(this);
            addMouseListener(this);
            
            // --- 載入圖片 (請確保檔案路徑正確，例如放在專案根目錄) ---
            // 建議使用去背的 PNG 檔案 (如：ship.png)
            playerImg = new ImageIcon("image/character.png").getImage(); 
            
            stars = new ArrayList<>();
            for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
            
            timer = new Timer(16, e -> {
                updateAnimation();
                repaint();
            });
            timer.start();
        }

        private void updateAnimation() {
            circleX += (targetX - circleX) * EASING_FACTOR;
            circleY += (targetY - circleY) * EASING_FACTOR;
            for (Star star : stars) star.update(getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(Color.WHITE);
            for (Star star : stars) star.draw(g2d);
            
            // --- 修改：繪製圖片取代紅色圓圈 ---
            int drawSize = isBoosting ? PLAYER_SIZE * 2 : PLAYER_SIZE;
            int drawX = (int)(circleX - drawSize / 2);
            int drawY = (int)(circleY - drawSize / 2);
            
            if (playerImg != null) {
                // 繪製角色圖片
                g2d.drawImage(playerImg, drawX, drawY, drawSize, drawSize, this);
            } else {
                // 如果圖片讀取失敗，畫個黃框框替代
                g2d.setColor(Color.RED);
                g2d.drawRect(drawX, drawY, drawSize, drawSize);
            }
        }

        @Override public void mouseMoved(MouseEvent e) { targetX = e.getX(); targetY = e.getY(); }
        @Override public void mouseDragged(MouseEvent e) { targetX = e.getX(); targetY = e.getY(); }
        @Override public void mouseClicked(MouseEvent e) {
            if (!isBoosting) {
                isBoosting = true;
                Timer boostTimer = new Timer(200, event -> { isBoosting = false; repaint(); });
                boostTimer.setRepeats(false);
                boostTimer.start();
                repaint();
            }
        }
        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StarWarsGame());
    }
}