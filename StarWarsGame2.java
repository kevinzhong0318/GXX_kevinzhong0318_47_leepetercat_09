import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class StarWarsGame2 extends JFrame {
    public StarWarsGame2() {
        setTitle("Star Wars Shooter - Stage Mode");
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
    private boolean bossDefeated = false;
    private boolean newGame = true;

    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Timer timer;

    public StagePanel() {
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);

        player = new Player(400, 450);
        initStage();

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
        bossDefeated = false;
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

        for (int i = 0; i < 50; i++) {
            stars.add(new Star(800, 600));
        }

        if (stage == 1) {
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(100 + i * 130, 90));
            }
        } else {
            boss = new Boss(340, 60);
        }
    }

    private void updateAnimation() {
        player.update(mouseX, mouseY);

        for (Star s : stars) {
            s.update(getHeight());
        }

        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next();
            b.update();
            if (b.y < -50 || b.y > getHeight() + 50) {
                bIt.remove();
                continue;
            }

            if (b.isEnemy && b.getBounds().intersects(player.getBounds())) {
                bIt.remove();
                player.health--;
                if (player.health <= 0) {
                    isGameOver = true;
                }
            }
        }

        if (stage == 1) {
            updateStageOne();
        } else {
            updateBossStage();
        }
    }

    private void updateStageOne() {
        Iterator<Enemy> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy e = eIt.next();
            e.update(bullets);
            Iterator<Bullet> bIt2 = bullets.iterator();
            while (bIt2.hasNext()) {
                Bullet b = bIt2.next();
                if (!b.isEnemy && b.getBounds().intersects(e.getBounds())) {
                    score += 10;
                    bIt2.remove();
                    eIt.remove();
                    break;
                }
            }
        }

        if (enemies.isEmpty()) {
            stageCleared = true;
        }
    }

    private void updateBossStage() {
        if (boss == null) {
            return;
        }

        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next();
            if (b.isEnemy) {
                continue;
            }
            if (boss.getBounds().intersects(b.getBounds())) {
                bIt.remove();
                boss.health--;
                score += 20;
                if (boss.health <= 0) {
                    boss = null;
                    bossDefeated = true;
                    stageCleared = true;
                    break;
                }
            }
        }

        if (boss != null) {
            boss.update(bullets);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Star s : stars) {
            s.draw(g2d);
        }
        for (Bullet b : bullets) {
            b.draw(g2d);
        }
        for (Enemy e : enemies) {
            e.draw(g2d);
        }
        if (stage == 2 && boss != null) {
            boss.draw(g2d);
        }

        player.draw(g2d, this);
        drawUI(g2d);

        if (isGameOver) {
            drawGameOver(g2d);
        } else if (stageCleared) {
            drawStageClear(g2d);
        }
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("STAGE: " + stage, 20, 35);
        g2d.drawString("HP:", 20, 60);
        g2d.drawRect(60, 45, 150, 20);
        g2d.setColor(player.health > 1 ? Color.RED : Color.YELLOW);
        g2d.fillRect(61, 46, Math.max(0, player.health * 50 - 2), 18);
        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + score, getWidth() - 180, 35);

        if (stage == 2 && boss != null) {
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

        if (stage == 1) {
            g2d.drawString("STAGE 1 CLEAR!", getWidth() / 2 - 220, getHeight() / 2 - 20);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("CLICK TO ENTER BOSS STAGE", getWidth() / 2 - 190, getHeight() / 2 + 30);
        } else {
            g2d.drawString("BOSS DEFEATED!", getWidth() / 2 - 230, getHeight() / 2 - 20);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("CLICK TO RESTART GAME", getWidth() / 2 - 150, getHeight() / 2 + 30);
        }
    }

    private void advanceStage() {
        if (stage == 1) {
            stage = 2;
            initStage();
        } else {
            stage = 1;
            score = 0;
            newGame = true;
            initStage();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isGameOver) {
            stage = 1;
            score = 0;
            newGame = true;
            initStage();
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
