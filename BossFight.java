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
        boss = new Boss(340, 60);
        for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
        enemies.clear();
        spawnTimer = 0;
    }

    private void updateAnimation() {
    player.update(mouseX, mouseY);
    for (Star s : stars) s.update(getHeight());

    // 1. 生成小怪邏輯 (階段一：Boss 血量高於 10 時才召喚)
    if (boss != null && boss.health > 10) {
        spawnTimer++;
        if (spawnTimer >= 80) { // 約 1.5 秒出一隻
            enemies.add(new Enemy2(Math.random() * (getWidth() - 40), -40));
            spawnTimer = 0;
        }
    }

    // 2. 更新小怪與環狀射擊
    Iterator<Enemy2> eIt = enemies.iterator();
    while (eIt.hasNext()) {
        Enemy2 e = eIt.next();
        e.update(bullets); // 傳入子彈清單讓它射擊
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

    // 3. 子彈碰撞判定 (增加對 Enemy2 的判定)
    Iterator<Bullet> bIt = bullets.iterator();
    while (bIt.hasNext()) {
        Bullet b = bIt.next();
        b.update();
        // ... (省略原有邊界檢查)

        if (!b.isEnemy) { // 玩家的子彈
            for (Enemy2 target : enemies) {
                if (b.getBounds().intersects(target.getBounds())) {
                    target.health--; // Enemy2 會因為 update 裡的判定被移除
                    bIt.remove();    // 子彈消失
                    score += 10;
                    break;
                }
            }
            // ... (原本的 Boss 碰撞檢查)
        }
    }
    // ... (Boss 更新)
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Star s : stars) s.draw(g2d);
        for (Bullet b : bullets) b.draw(g2d);
        if (boss != null) boss.draw(g2d);
        player.draw(g2d, this);

        drawUI(g2d);

        if (isGameOver) drawGameOver(g2d);
        if (!isGameOver && bossDefeated) drawWin(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("HP:", 20, 35);
        g2d.drawRect(60, 20, 150, 20);
        g2d.setColor(player.health > 1 ? Color.RED : Color.YELLOW);
        g2d.fillRect(61, 21, Math.max(0, player.health * 50 - 2), 18);
        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + score, getWidth() - 160, 40);

        if (boss != null) {
            g2d.drawString("BOSS HP: " + boss.health, getWidth() - 160, 70);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        g2d.drawString("MISSION FAILED", getWidth()/2 - 240, getHeight()/2);
    }

    private void drawWin(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        g2d.drawString("BOSS DEFEATED!", getWidth()/2 - 220, getHeight()/2);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isGameOver) { initGame(); return; }
        if (bossDefeated) { initGame(); return; }
        if (SwingUtilities.isLeftMouseButton(e)) {
            bullets.add(new Bullet(player.x, player.y - 20, 0, -15, false));
        } else {
            bullets.add(new Bullet(player.x - 10, player.y, -3, -12, false));
            bullets.add(new Bullet(player.x + 10, player.y, 3, -12, false));
        }
    }

    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
