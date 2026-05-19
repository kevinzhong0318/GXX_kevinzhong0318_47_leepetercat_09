import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class BossFight {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BossGame::new);
    }
}

class BossGame extends JFrame {
    public BossGame() {
        setTitle("Star Wars Shooter - Boss Stage");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new BossPanel());
        setVisible(true);
    }
}

class BossPanel extends JPanel implements MouseMotionListener, MouseListener {
    private Player player;
    private Boss boss;
    private int mouseX, mouseY;
    private int score = 0;
    private boolean isGameOver = false;
    private boolean bossDefeated = false;

    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    
    // --- 關鍵新增：宣告漏掉的變數 ---
    private ArrayList<Enemy2> enemies = new ArrayList<>(); // 儲存小怪名單
    private int spawnTimer = 0;                             // 控制產出頻率
    // ----------------------------

    private Timer timer;

    public BossPanel() {
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);

        player = new Player(400, 450);
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
        bossDefeated = false;
        player.reset(400, 450);
        bullets.clear();
        stars.clear();
        enemies.clear(); // 現在找得到了
        spawnTimer = 0;  // 現在找得到了
        boss = new Boss(340, 60);
        for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
    }

    private void updateAnimation() {
        player.update(mouseX, mouseY);
        for (Star s : stars) s.update(getHeight());

        // 1. 生成小怪邏輯
        if (boss != null && boss.health > 10) {
            spawnTimer++;
            if (spawnTimer >= 80) {
                enemies.add(new Enemy2(Math.random() * (getWidth() - 40), -40));
                spawnTimer = 0;
            }
        }

        // 2. 更新小怪
        Iterator<Enemy2> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy2 e = eIt.next();
            e.update(bullets);
            if (e.y > getHeight() || e.health <= 0) {
                eIt.remove();
                continue;
            }
            if (e.getBounds().intersects(player.getBounds())) {
                player.health--;
                eIt.remove();
                if (player.health <= 0) isGameOver = true;
            }
        }

        // 3. 子彈與碰撞
        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next();
            b.update();
            
            // 邊界檢查防止子彈過多
            if (b.y < -50 || b.y > getHeight() + 50) {
                bIt.remove();
                continue;
            }

            if (!b.isEnemy) {
                boolean hitAnything = false;
                for (Enemy2 target : enemies) {
                    if (b.getBounds().intersects(target.getBounds())) {
                        target.health--;
                        bIt.remove();
                        score += 10;
                        hitAnything = true;
                        break;
                    }
                }
                if (hitAnything) continue;

                if (boss != null && b.getBounds().intersects(boss.getBounds())) {
                    boss.health--;
                    bIt.remove();
                    if (boss.health <= 0) {
                        boss = null;
                        bossDefeated = true;
                    }
                    continue;
                }
            } else {
                // 敵人子彈打玩家
                if (b.getBounds().intersects(player.getBounds())) {
                    player.health--;
                    bIt.remove();
                    if (player.health <= 0) isGameOver = true;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (Star s : stars) s.draw(g2d);
        for (Bullet b : bullets) b.draw(g2d);
        for (Enemy2 e : enemies) e.draw(g2d); // 記得要把小怪畫出來
        if (boss != null) boss.draw(g2d);
        player.draw(g2d, this);

        drawUI(g2d);
        if (isGameOver) drawGameOver(g2d);
        if (!isGameOver && bossDefeated) drawWin(g2d);
    }
    
    // ... (其餘 drawUI, drawGameOver, drawWin, Mouse 事件維持不變)
}