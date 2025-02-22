package PersonalProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * 게임 패널 클래스, 게임의 주요 로직과 사용자 인터페이스를 처리합니다.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;

    private Player player; // 플레이어 객체
    private AbstractObstacle currentObstacle; // 현재 장애물 객체
    private Timer updateTimer; // 게임 업데이트를 위한 타이머
    private Timer speedIncreaseTimer; // 장애물 속도를 증가시키기 위한 타이머
    private Random random; // 랜덤 숫자 생성을 위한 객체
    private int obstacleSpeed; // 장애물의 현재 속도
    private long startTime; // 게임 시작 시간을 기록하는 변수
    private Ground ground; // 바닥 객체
    private AudioPlayer audioPlayer; // 오디오 객체

    /**
     * GamePanel 생성자
     * 게임 패널의 초기 설정을 수행합니다.
     */
    public GamePanel() {
        player = new Player(50, 450); // 플레이어 객체 생성
        random = new Random(); // 랜덤 객체 초기화

        obstacleSpeed = 20; // 초기 장애물 속도 설정

        startTime = System.currentTimeMillis(); // 게임 시작 시간 기록

        ground = new Ground(500, 1500, 50, obstacleSpeed); // 바닥 객체 생성

        // 배경음악 재생
        audioPlayer = new AudioPlayer();
        audioPlayer.play("src\\bgm.wav"); // 배경음악 파일 경로

        // 타이머 설정: 20ms마다 업데이트
        updateTimer = new Timer(20, this);
        updateTimer.start();

        // 속도 증가 타이머 설정: 5초마다 장애물 속도 증가
        speedIncreaseTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obstacleSpeed += 1; // 장애물 속도 증가
                if (currentObstacle != null) {
                    currentObstacle.setSpeed(obstacleSpeed); // 현재 장애물의 속도 업데이트
                }
                ground.setObstacleSpeed(obstacleSpeed); // 바닥의 장애물 속도 업데이트
            }
        });
        speedIncreaseTimer.start();

        addKeyListener(this); // 키 이벤트 리스너 추가
        setFocusable(true); // 패널에 포커스를 설정
        setFocusTraversalKeysEnabled(false); // 포커스 이동 키 비활성화

        // 최초 장애물 생성
        spawnRandomObstacle();
    }

    /**
     * 새로운 장애물을 생성합니다.
     */
    private void spawnRandomObstacle() {
        int newObstacleType = random.nextInt(3); // 0, 1, 2 중 하나의 숫자를 랜덤으로 선택

        switch (newObstacleType) {
            case 0:
                currentObstacle = new StandardObstacle(1500, 380, obstacleSpeed);
                break;
            case 1:
                currentObstacle = new HighObstacle(1500, 450, obstacleSpeed);
                break;
            case 2:
                currentObstacle = new NewObstacle(1500, 450, obstacleSpeed);
                break;
        }
    }

    /**
     * 화면을 그리는 메서드
     * @param g 그래픽 객체
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // 부모 클래스의 paintComponent 호출

        ground.draw(g); // 바닥을 그리기

        player.paint(g); // 플레이어를 그림
        if (currentObstacle != null) {
            currentObstacle.paint(g); // 현재 장애물을 그림
        }

        // 경과 시간 표시
        long currentTime = System.currentTimeMillis(); // 현재 시간
        double elapsedTime = (currentTime - startTime) / 1000.0; // 경과 시간을 초 단위로 계산 (소수점 포함)
        String elapsedTimeString = String.format("%.2f", elapsedTime); // 소수점 두 자리까지 포맷

        g.setColor(Color.BLACK); // 글자 색상 설정
        g.setFont(new Font("Arial", Font.BOLD, 20)); // 글자 폰트 설정
        g.drawString("Time: " + elapsedTimeString + "s", 10, 30); // 좌측 상단에 경과 시간 표시
    }

    /**
     * 타이머가 트리거될 때마다 호출되는 메서드
     * @param e 이벤트 객체
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        player.update(); // 플레이어 업데이트
        ground.update(); // 바닥 업데이트

        if (currentObstacle != null) {
            currentObstacle.update(); // 장애물 업데이트

            // 충돌 감지 및 게임 종료 처리
            if (player.getBounds().intersects(currentObstacle.getBounds())) {
                updateTimer.stop(); // 충돌 시 타이머를 정지
                speedIncreaseTimer.stop(); // 속도 증가 타이머도 정지
                audioPlayer.stop(); // 배경음악 정지
                gameOver(); // 게임 종료 처리
            }

            // 장애물이 화면을 벗어나면 제거하고 새로운 장애물 생성
            if (currentObstacle.getBounds().x < -currentObstacle.getBounds().width) {
                currentObstacle = null; // 장애물 제거
                spawnRandomObstacle(); // 새 장애물 생성
            }
        }

        repaint(); // 화면 다시 그리기
    }

    /**
     * 게임 종료 처리 메서드
     */
    private void gameOver() {
        int result = JOptionPane.showConfirmDialog(this, "게임이 종료되었습니다. 다시 시작하시겠습니까?", "게임 종료", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            resetGame(); // 게임 리셋
        } else {
            System.exit(0); // 프로그램 종료
        }
    }

    /**
     * 게임을 리셋합니다.
     */
    private void resetGame() {      
        obstacleSpeed = 20; // 초기 장애물 속도 설정
        player = new Player(50, 450); // 플레이어 객체 새로 생성
        startTime = System.currentTimeMillis(); // 게임 시작 시간 초기화
        ground = new Ground(500, 1500, 50, obstacleSpeed); // 바닥 객체 새로 생성

        // 배경음악 재생
        audioPlayer = new AudioPlayer();
        audioPlayer.play("src\\bgm.wav"); // 배경음악 파일 경로

        updateTimer.start(); // 타이머 재시작
        speedIncreaseTimer.start(); // 속도 증가 타이머 재시작

        spawnRandomObstacle(); // 새로운 장애물 생성
    }

    /**
     * 키가 눌렸을 때 호출되는 메서드
     * @param e 키 이벤트 객체
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.jump(); // 스페이스바가 눌렸을 때 점프 시작
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
