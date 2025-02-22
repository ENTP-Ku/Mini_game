package qwerqwerqwer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

// 게임의 기본 클래스를 정의합니다.
public class DodgeGame extends JPanel implements ActionListener, KeyListener {

    private Timer timer; // 게임 루프를 위한 타이머
    private Timer movementTimer; // 비행기 이동을 위한 타이머
    private Plane plane; // 비행기 객체
    private ArrayList<Bullet> bullets = new ArrayList<>(); // 총알 객체를 담을 리스트
    private Random random = new Random(); // 난수 생성기

    private boolean moveLeft = false; // 왼쪽 이동 플래그
    private boolean moveRight = false; // 오른쪽 이동 플래그

    // 생성자: 초기 설정을 합니다.
    public DodgeGame() {
        plane = new Plane(300, 500); // 비행기를 화면의 초기 위치에 배치
        timer = new Timer(20, this); // 20 밀리초마다 actionPerformed 메서드를 호출
        timer.start(); // 타이머 시작

        // 비행기 이동을 위한 타이머를 생성합니다. 100 밀리초마다 이동을 수행합니다.
        movementTimer = new Timer(10, e -> {
            if (moveLeft) {
                plane.moveLeft(); // 왼쪽 이동
            }
            if (moveRight) {
                plane.moveRight(getWidth()); // 오른쪽 이동
            }
            repaint(); // 화면을 다시 그립니다.
        });
        movementTimer.start(); // 이동 타이머 시작

        addKeyListener(this); // 키 입력을 처리하기 위해 KeyListener 추가
        setFocusable(true); // 포커스를 받을 수 있도록 설정
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // 기본 배경을 그립니다.
        plane.draw(g); // 비행기를 그립니다.
        drawBullets(g); // 총알을 그립니다.
    }

    // 총알을 그리는 메서드
    private void drawBullets(Graphics g) {
        for (Bullet bullet : bullets) {
            bullet.draw(g); // 각 총알을 그립니다.
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 총알 이동 및 제거
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.move(); // 총알을 이동시킵니다.
            if (bullet.isOffScreen(getHeight())) { // 총알이 화면 밖으로 나갔는지 확인
                bulletsToRemove.add(bullet); // 제거할 총알 리스트에 추가
            }
        }
        bullets.removeAll(bulletsToRemove); // 화면 밖으로 나간 총알을 리스트에서 제거

        // 새로운 총알 추가
        if (random.nextInt(100) < 5) { // 랜덤하게 새로운 총알을 추가할 확률
            bullets.add(new Bullet(random.nextInt(getWidth()), 0)); // 새로운 총알을 리스트에 추가
        }

        // 충돌 검사
        Rectangle planeRect = plane.getRectangle(); // 비행기의 위치를 가져옵니다.
        for (Bullet bullet : bullets) {
            if (planeRect.intersects(bullet.getRectangle())) { // 비행기와 총알이 충돌했는지 확인
                System.out.println("Game Over"); // 게임 종료 메시지 출력
                System.exit(0); // 게임 종료
            }
        }

        repaint(); // 화면을 다시 그립니다.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // 눌린 키의 코드 값을 가져옵니다.
        if (keyCode == KeyEvent.VK_LEFT) { // 왼쪽 화살표 키를 눌렀을 때
            moveLeft = true; // 왼쪽 이동 플래그를 설정
        } else if (keyCode == KeyEvent.VK_RIGHT) { // 오른쪽 화살표 키를 눌렀을 때
            moveRight = true; // 오른쪽 이동 플래그를 설정
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode(); // 떼어진 키의 코드 값을 가져옵니다.
        if (keyCode == KeyEvent.VK_LEFT) { // 왼쪽 화살표 키를 떼었을 때
            moveLeft = false; // 왼쪽 이동 플래그를 해제
        } else if (keyCode == KeyEvent.VK_RIGHT) { // 오른쪽 화살표 키를 떼었을 때
            moveRight = false; // 오른쪽 이동 플래그를 해제
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {} // 키를 타이핑할 때의 동작은 없습니다.

    // 메인 메서드: 게임을 시작하는 부분입니다.
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dodge Game"); // 게임 창을 생성합니다.
        DodgeGame gamePanel = new DodgeGame(); // 게임 패널을 생성합니다.
        frame.add(gamePanel); // 게임 패널을 창에 추가합니다.
        frame.setSize(600, 600); // 창의 크기를 설정합니다.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창을 닫을 때 프로그램 종료
        frame.setVisible(true); // 창을 화면에 표시합니다.
    }
}
