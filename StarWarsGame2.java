import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class StarWarsGame2 extends JFrame {
    public StarWarsGame2() {
        setTitle("Star Wars Shooter - Stage Mode (God Mode Enabled)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new StagePanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StarWarsGame2::new);
    }
}

class StagePanel extends JPanel implements MouseMotionListener, MouseListener {
    private Player player;
    private Boss boss;
    private int mouseX, mouseY;

    private int score = 0;
    private int stage = 1; 
    private boolean isGameOver = false;
    private boolean stageCleared = false;
    private boolean newGame = true;
    
    // --- 無敵模式：宣告在最外層，確保不會被 initStage 重置 ---
    private boolean isInvincible = false;

    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();  
    private ArrayList<Enemy2> enemies2 = new ArrayList<>(); 
    private Timer timer;

    public StagePanel() {
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);

        // 設定焦點與鍵盤監聽
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    isInvincible = !isInvincible;
                    System.out.println("Cheat Switch: " + isInvincible);
                }
            }
        });

        player = new Player(400, 450);
        initStage();
        
        // 核心遊戲迴圈
        timer = new Timer(16, e -> {
            if (!isGameOver && !stageCleared) {
                updateAnimation();
            }
            repaint();
        });
        timer.start();
    }

    private void initStage() {
        isGameOver = false;
        stageCleared = false;
        // 注意：這裡不可將 isInvincible 設為 false，否則換關會失效
        
        if (newGame) {
            player.reset(400, 450);
            newGame = false;
        } else {
            player.x = 400;
            player.y = 450;
        }
        
        bullets.clear();
        stars.clear();
        enemies.clear();
        enemies2.clear();
        boss = null;

        for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));

        if (stage == 1) {
            for (int i = 0; i < 5; i++) enemies.add(new Enemy(100 + i * 130, 90));
        } else if (stage == 2) {
            for (int i = 0; i < 3; i++) enemies2.add(new Enemy2(150 + i * 200, 80));
        } else if (stage == 3) {
            boss = new Boss(340, 60);
        }
    }

    private void updateAnimation() {
        // --- 終極保險：若無敵開啟，每幀鎖定血量 ---
        if (isInvincible) {
            player.health = 3; 
        }

        player.update(mouseX, mouseY);
        for (Star s : stars) s.update(getHeight());

        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next();
            b.update();
            if (b.y < -50 || b.y > getHeight() + 50) {
                bIt.remove();
                continue;
            }
            
            // 敵人子彈傷害判定
            if (b.isEnemy && b.getBounds().intersects(player.getBounds())) {
                bIt.remove();
                if (!isInvincible) { // 攔截點 1
                    player.health--;
                    if (player.health <= 0) isGameOver = true;
                }
            }
        }

        if (stage == 1) updateStageOne();
        else if (stage == 2) updateStageTwo();
        else if (stage == 3) updateStageThree();
    }

    private void updateStageOne() {
        Iterator<Enemy> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy e = eIt.next();
            e.update(bullets);
            if (checkHit(e.getBounds())) {
                eIt.remove();
                score += 10;
            }
            // 碰撞判定
            if (e.getBounds().intersects(player.getBounds())) {
                if (!isInvincible) {
                    player.health--;
                    if (player.health <= 0) isGameOver = true;
                }
            }
        }
        if (enemies.isEmpty()) stageCleared = true;
    }

    private void updateStageTwo() {
        Iterator<Enemy2> e2It = enemies2.iterator();
        while (e2It.hasNext()) {
            Enemy2 e2 = e2It.next();
            e2.update(bullets);
            
            // 碰撞玩家判定 (攔截點 2)
            if (e2.getBounds().intersects(player.getBounds())) {
                e2It.remove();
                if (!isInvincible) {
                    player.health--;
                    if (player.health <= 0) isGameOver = true;
                }
                continue;
            }

            if (checkHit(e2.getBounds())) {
                e2It.remove();
                score += 20;
            }
        }
        if (enemies2.isEmpty()) stageCleared = true;
    }

    private void updateStageThree() {
        if (boss == null) return;
        boss.update(bullets);
        
        // 檢查 Boss 是否撞到玩家
        if (boss.getBounds().intersects(player.getBounds())) {
            if (!isInvincible) {
                player.health--;
                if (player.health <= 0) isGameOver = true;
            }
        }

        if (checkHit(boss.getBounds())) {
            boss.health--;
            score += 5;
            if (boss.health <= 0) {
                boss = null;
                stageCleared = true;
            }
        }
    }

    private boolean checkHit(Rectangle targetBounds) {
        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next();
            if (!b.isEnemy && b.getBounds().intersects(targetBounds)) {
                bIt.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Star s : stars) s.draw(g2d);
        for (Bullet b : bullets) b.draw(g2d);
        for (Enemy e : enemies) e.draw(g2d);
        for (Enemy2 e2 : enemies2) e2.draw(g2d);
        if (boss != null) boss.draw(g2d);
        
        player.draw(g2d, this);
        drawUI(g2d);

        if (isGameOver) drawGameOver(g2d);
        else if (stageCleared) drawStageClear(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("STAGE: " + stage, 20, 35);
        g2d.drawString("HP:", 20, 60);
        g2d.drawRect(60, 45, 150, 20);
        
        // 血條顏色
        g2d.setColor(player.health > 1 ? Color.RED : Color.YELLOW);
        g2d.fillRect(61, 46, Math.max(0, player.health * 50 - 2), 18);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + score, getWidth() - 180, 35);

        // --- 無敵模式視覺提示 ---
        if (isInvincible) {
            g2d.setColor(Color.CYAN);
            g2d.drawString("GOD MODE ACTIVATED (A)", 20, 90);
            g2d.drawRect((int)player.x - 25, (int)player.y - 25, 50, 50); // 主角身邊畫個保護框
        }

        if (stage == 3 && boss != null) {
            g2d.drawString("BOSS HP: " + boss.health, getWidth() - 180, 60);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        g2d.drawString("MISSION FAILED", getWidth() / 2 - 240, getHeight() / 2);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("CLICK TO RESTART", getWidth() / 2 - 140, getHeight() / 2 + 50);
    }

    private void drawStageClear(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        
        String txt = (stage == 3) ? "ALL CLEAR!" : "STAGE " + stage + " CLEAR!";
        g2d.drawString(txt, getWidth() / 2 - 200, getHeight() / 2 - 20);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String sub = (stage == 3) ? "YOU SAVED THE GALAXY!" : "CLICK TO PROCEED";
        g2d.drawString(sub, getWidth() / 2 - 130, getHeight() / 2 + 30);
    }

    private void advanceStage() {
        if (stage < 3) {
            stage++;
        } else {
            stage = 1;
            score = 0;
            newGame = true;
        }
        initStage();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 點擊滑鼠時強制讓鍵盤焦點回到遊戲畫面
        this.requestFocusInWindow();

        if (isGameOver) {
            stage = 1; score = 0; newGame = true; initStage();
            return;
        }
        if (stageCleared) {
            advanceStage();
            return;
        }
        
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