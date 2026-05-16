<<<<<<< HEAD
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 7767fede722e2ea2be0fc397950aed97343e34d1
=======
>>>>>>> 7767fede722e2ea2be0fc397950aed97343e34d1
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class StarWarsGame extends JFrame {
    public StarWarsGame() {
        setTitle("Star Wars Shooter - Fully Refactored");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new GamePanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StarWarsGame::new);
    }
}

class GamePanel extends JPanel implements MouseMotionListener, MouseListener {
    private Player player; // 使用 Player 類別
    private int mouseX, mouseY;
    private int score = 0;
    private boolean isGameOver = false;

    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Timer timer;

    public GamePanel() {
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);
        
        player = new Player(400, 450); // 初始化角色
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
        player.reset(400, 450);
        bullets.clear();
        enemies.clear();
        stars.clear();
        for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
        for (int i = 0; i < 5; i++) enemies.add(new Enemy(100 + i * 130, 90));
    }

    private void updateAnimation() {
        // 1. 更新玩家位置
        player.update(mouseX, mouseY);
        
        // 2. 更新星星
        for (Star s : stars) s.update(getHeight());

        // 3. 更新子彈與玩家碰撞
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
                if (player.health <= 0) isGameOver = true;
            }
        }

        // 4. 更新敵人與擊殺判斷
        Iterator<Enemy> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy e = eIt.next();
            e.update(bullets);
            Iterator<Bullet> bIt2 = bullets.iterator();
            while (bIt2.hasNext()) {
                Bullet b = bIt2.next();
                if (!b.isEnemy && b.getBounds().intersects(e.getBounds())) {
                    score++;
                    bIt2.remove();
                    eIt.remove();
                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Star s : stars) s.draw(g2d);
        for (Bullet b : bullets) b.draw(g2d);
        for (Enemy e : enemies) e.draw(g2d);
        
        player.draw(g2d, this); // 畫玩家

        // UI 繪製
        drawUI(g2d);

        if (isGameOver) drawGameOver(g2d);
        if (!isGameOver && enemies.isEmpty()) drawWin(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("HP:", 20, 35);
        g2d.drawRect(60, 20, 150, 20);
        g2d.setColor(player.health > 1 ? Color.RED : Color.YELLOW);
        g2d.fillRect(61, 21, player.health * 50 - 2, 18);
        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + score, getWidth() - 160, 40);
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
        g2d.drawString("ALL ENEMIES CLEAR!", getWidth()/2 - 280, getHeight()/2);
    }

    @Override 
    public void mousePressed(MouseEvent e) {
        if (isGameOver) { initGame(); return; }
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
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> 7767fede722e2ea2be0fc397950aed97343e34d1
=======
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class StarWarsGame extends JFrame {
    public StarWarsGame() {
        setTitle("Star Wars Shooter - Fully Refactored");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new GamePanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StarWarsGame::new);
    }
}

class GamePanel extends JPanel implements MouseMotionListener, MouseListener {
    private Player player; // 使用 Player 類別
    private int mouseX, mouseY;
    private int score = 0;
    private boolean isGameOver = false;

    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Timer timer;

    public GamePanel() {
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);
        
        player = new Player(400, 450); // 初始化角色
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
        player.reset(400, 450);
        bullets.clear();
        enemies.clear();
        stars.clear();
        for (int i = 0; i < 50; i++) stars.add(new Star(800, 600));
        for (int i = 0; i < 5; i++) enemies.add(new Enemy(100 + i * 130, 90));
    }

    private void updateAnimation() {
        // 1. 更新玩家位置
        player.update(mouseX, mouseY);
        
        // 2. 更新星星
        for (Star s : stars) s.update(getHeight());

        // 3. 更新子彈與玩家碰撞
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
                if (player.health <= 0) isGameOver = true;
            }
        }

        // 4. 更新敵人與擊殺判斷
        Iterator<Enemy> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy e = eIt.next();
            e.update(bullets);
            Iterator<Bullet> bIt2 = bullets.iterator();
            while (bIt2.hasNext()) {
                Bullet b = bIt2.next();
                if (!b.isEnemy && b.getBounds().intersects(e.getBounds())) {
                    score++;
                    bIt2.remove();
                    eIt.remove();
                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Star s : stars) s.draw(g2d);
        for (Bullet b : bullets) b.draw(g2d);
        for (Enemy e : enemies) e.draw(g2d);
        
        player.draw(g2d, this); // 畫玩家

        // UI 繪製
        drawUI(g2d);

        if (isGameOver) drawGameOver(g2d);
        if (!isGameOver && enemies.isEmpty()) drawWin(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("HP:", 20, 35);
        g2d.drawRect(60, 20, 150, 20);
        g2d.setColor(player.health > 1 ? Color.RED : Color.YELLOW);
        g2d.fillRect(61, 21, player.health * 50 - 2, 18);
        g2d.setColor(Color.WHITE);
        g2d.drawString("SCORE: " + score, getWidth() - 160, 40);
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
        g2d.drawString("ALL ENEMIES CLEAR!", getWidth()/2 - 280, getHeight()/2);
    }

    @Override 
    public void mousePressed(MouseEvent e) {
        if (isGameOver) { initGame(); return; }
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
>>>>>>> a72d435a421909bc2c463d8a5e01210a14c63f19
<<<<<<< HEAD
>>>>>>> 7767fede722e2ea2be0fc397950aed97343e34d1
=======
>>>>>>> 7767fede722e2ea2be0fc397950aed97343e34d1
}