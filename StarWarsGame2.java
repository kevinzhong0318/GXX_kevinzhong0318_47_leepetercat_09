import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class StarWarsGame2 extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    private StagePanel gamePanel;

    public StarWarsGame2() {
        setTitle("星際小樂園 - 終極完整版");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 頁面切換設定
        JPanel menuPanel = new MenuPanel(this);
        gamePanel = new StagePanel(this);

        mainContainer.add(menuPanel, "Menu");
        mainContainer.add(gamePanel, "Game");

        add(mainContainer);
        cardLayout.show(mainContainer, "Menu");
        setVisible(true);
    }

    public void startStage(int s) {
        gamePanel.setStage(s);
        cardLayout.show(mainContainer, "Game");
        // 強制取得鍵盤焦點
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow(); 
    }

    public void backToMenu() {
        cardLayout.show(mainContainer, "Menu");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StarWarsGame2::new);
    }
}

// --- 歡迎選單面版 ---
class MenuPanel extends JPanel {
    private ArrayList<Star> menuStars = new ArrayList<>();

    public MenuPanel(StarWarsGame2 parent) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);

        for (int i = 0; i < 50; i++) menuStars.add(new Star(800, 600));
        
        new Timer(16, e -> {
            for (Star s : menuStars) s.update(600);
            repaint();
        }).start();

        add(Box.createVerticalGlue());
        JLabel title = new JLabel("Star Wars Shooter");
        title.setFont(new Font("Serif", Font.BOLD, 65));
        title.setForeground(Color.YELLOW);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(40));

        String[] labels = {"第一關: 突擊小隊", "第二關: 彈幕風暴", "第三關: 最終決戰", "第四關: 全軍出擊", "退出遊戲"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i + 1;
            JButton btn = new JButton(labels[i]);
            btn.setMaximumSize(new Dimension(320, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            if (i == 4) btn.addActionListener(e -> System.exit(0));
            else btn.addActionListener(e -> parent.startStage(index));
            
            add(btn);
            add(Box.createVerticalStrut(12));
        }
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Star s : menuStars) s.draw(g);
    }
}

// --- 遊戲核心面版 ---
class StagePanel extends JPanel implements MouseMotionListener, MouseListener {
    private StarWarsGame2 parentFrame;
    private Player player;
    private Boss boss;
    private int mouseX, mouseY, score = 0, stage = 1;
    private boolean isGameOver = false, stageCleared = false, isInvincible = false;
    private boolean isPaused = false; 

    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();  
    private ArrayList<Enemy2> enemies2 = new ArrayList<>(); 
    private Timer timer;

    public StagePanel(StarWarsGame2 frame) {
        this.parentFrame = frame;
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);
        
        // 強化鍵盤監聽與焦點取得
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                // A 鍵無敵切換
                if (code == KeyEvent.VK_A) isInvincible = !isInvincible;

                // ESC 暫停切換
                if (code == KeyEvent.VK_ESCAPE) {
                    if (!isGameOver && !stageCleared) isPaused = !isPaused;
                }

                // 暫停模式下的特殊控制
                if (isPaused) {
                    if (code == KeyEvent.VK_L) isPaused = false; // L 繼續
                    if (code == KeyEvent.VK_M) { // M 回選單
                        isPaused = false;
                        parentFrame.backToMenu();
                    }
                }
                repaint();
            }
        });

        player = new Player(400, 450);
        timer = new Timer(16, e -> {
            if (!isPaused && !isGameOver && !stageCleared) updateAnimation();
            repaint();
        });
        timer.start();
    }

    public void setStage(int s) { 
        this.stage = s; 
        this.isPaused = false;
        initStage(); 
    }

    private void initStage() {
        isGameOver = false; stageCleared = false;
        player.reset(400, 450);
        bullets.clear(); stars.clear(); enemies.clear(); enemies2.clear(); boss = null;

        for (int i = 0; i < 60; i++) stars.add(new Star(800, 600));

        // 第四關大混戰邏輯：結合所有關卡內容
        if (stage == 1 || stage == 4) {
            int count = (stage == 4) ? 3 : 5;
            for (int i = 0; i < count; i++) enemies.add(new Enemy(100 + i * 150, 80));
        }
        if (stage == 2 || stage == 4) {
            int count = (stage == 4) ? 2 : 3;
            for (int i = 0; i < count; i++) enemies2.add(new Enemy2(200 + i * 250, 100));
        }
        if (stage == 3 || stage == 4) {
            boss = new Boss(340, 60);
        }
    }

    private void updateAnimation() {
        if (isInvincible) player.health = 3; 
        player.update(mouseX, mouseY);
        for (Star s : stars) s.update(getHeight());

        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next(); b.update();
            if (b.y < -50 || b.y > getHeight() + 50) { bIt.remove(); continue; }
            if (b.isEnemy && b.getBounds().intersects(player.getBounds())) {
                bIt.remove();
                if (!isInvincible) { player.health--; if (player.health <= 0) isGameOver = true; }
            }
        }
        updateEnemies();
        if (enemies.isEmpty() && enemies2.isEmpty() && boss == null) stageCleared = true;
    }

    private void updateEnemies() {
        // 普通怪更新
        Iterator<Enemy> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy e = eIt.next(); e.update(bullets);
            if (checkHit(e.getBounds())) { eIt.remove(); score += 10; }
        }
        // 環狀彈怪更新
        Iterator<Enemy2> e2It = enemies2.iterator();
        while (e2It.hasNext()) {
            Enemy2 e2 = e2It.next(); e2.update(bullets);
            if (e2.getBounds().intersects(player.getBounds())) {
                e2It.remove(); if (!isInvincible) { player.health--; if (player.health <= 0) isGameOver = true; }
                continue;
            }
            if (checkHit(e2.getBounds())) { e2It.remove(); score += 20; }
        }
        // Boss 更新
        if (boss != null) {
            boss.update(bullets);
            if (checkHit(boss.getBounds())) {
                boss.health--; score += 5;
                if (boss.health <= 0) boss = null;
            }
        }
    }

    private boolean checkHit(Rectangle targetBounds) {
        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next();
            if (!b.isEnemy && b.getBounds().intersects(targetBounds)) { bIt.remove(); return true; }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (Star s : stars) s.draw(g2d);
        for (Bullet b : bullets) b.draw(g2d);
        for (Enemy e : enemies) e.draw(g2d);
        for (Enemy2 e2 : enemies2) e2.draw(g2d);
        if (boss != null) boss.draw(g2d);
        player.draw(g2d, this);
        
        // 無敵保護圈
        if (isInvincible) {
            g2d.setColor(Color.CYAN);
            g2d.drawOval((int)player.x - 25, (int)player.y - 25, 50, 50);
        }
        
        drawUI(g2d);

        // 疊加頁面判定
        if (isGameOver) drawOverlay(g2d, "任務失敗", Color.RED, "點擊滑鼠回到主選單");
        else if (stageCleared) drawOverlay(g2d, "關卡完成！", Color.YELLOW, "點擊滑鼠回到主選單");
        else if (isPaused) drawOverlay(g2d, "遊戲暫停", Color.WHITE, "按 L 鍵繼續 / 按 M 鍵回選單");
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("關卡: " + stage, 20, 35);
        g2d.drawString("HP:", 20, 60);
        g2d.drawRect(60, 45, 150, 20);
        g2d.setColor(player.health > 1 ? Color.RED : Color.YELLOW);
        g2d.fillRect(61, 46, Math.max(0, player.health * 50 - 2), 18);
        g2d.setColor(Color.WHITE);
        g2d.drawString("分數: " + score, getWidth() - 150, 35);
        if (isInvincible) { g2d.setColor(Color.CYAN); g2d.drawString("無敵模式開啟", 20, 90); }
        if (boss != null) { g2d.setColor(Color.WHITE); g2d.drawString("BOSS: " + boss.health, getWidth() - 150, 60); }
    }

    private void drawOverlay(Graphics2D g2d, String text, Color color, String subText) {
        g2d.setColor(new Color(0,0,0,180));
        g2d.fillRect(0,0,800,600);
        g2d.setColor(color);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 50));
        g2d.drawString(text, 250, 280);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString(subText, 265, 330);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 重要：點擊滑鼠確保焦點回到面版
        this.requestFocusInWindow();

        if (isGameOver || stageCleared) { parentFrame.backToMenu(); return; }
        if (isPaused) return; 

        if (SwingUtilities.isLeftMouseButton(e)) bullets.add(new Bullet(player.x, player.y - 20, 0, -15, false));
        else {
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