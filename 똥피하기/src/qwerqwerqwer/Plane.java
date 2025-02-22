package qwerqwerqwer;

import java.awt.*;

// 비행기를 관리하는 클래스
public class Plane {
    private Rectangle rectangle; // 비행기의 위치와 크기를 정의하는 Rectangle 객체
    private final int width = 50; // 비행기의 너비
    private final int height = 30; // 비행기의 높이
    private final int moveAmount = 5; // 비행기 이동 거리 (1 픽셀씩 이동)

    // 생성자: 비행기의 초기 위치를 설정합니다.
    public Plane(int x, int y) {
        rectangle = new Rectangle(x, y, width, height); // 비행기의 크기를 설정
    }

    // 비행기의 Rectangle 객체를 반환합니다.
    public Rectangle getRectangle() {
        return rectangle;
    }

    // 비행기를 왼쪽으로 이동시킵니다.
    public void moveLeft() {
        rectangle.x -= moveAmount;
    }

    // 비행기를 오른쪽으로 이동시킵니다.
    public void moveRight(int screenWidth) {
        rectangle.x += moveAmount;
    }

    // 비행기를 그립니다.
    public void draw(Graphics g) {
        g.setColor(Color.BLUE); // 비행기 색상을 파란색으로 설정
        g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height); // 비행기를 사각형으로 그립니다.
    }
}
