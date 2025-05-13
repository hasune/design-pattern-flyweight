package com.designpattern.patterns;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

/**
 * 팩토리 패턴과 플라이웨이트 패턴의 조합
 * 플라이웨이트 패턴은 보통 팩토리 패턴과 함께 사용됨
 */
@Slf4j
public class FactoryWithFlyweight {
    
    // 플라이웨이트 인터페이스
    interface Shape {
        void draw(int x, int y, int radius);
    }
    
    // 구체적인 플라이웨이트
    static class Circle implements Shape {
        private final String color; // 내재적 상태 (intrinsic)
        
        public Circle(String color) {
            this.color = color;
            log.info("🔵 원 객체 생성: {} 색상", color);
        }
        
        @Override
        public void draw(int x, int y, int radius) {
            // x, y, radius는 외재적 상태 (extrinsic)
            log.info("🎨 {} 원 그리기: 위치=({}, {}), 반지름={}", color, x, y, radius);
        }
    }
    
    // 플라이웨이트 팩토리
    static class ShapeFactory {
        private static final Map<String, Shape> shapes = new HashMap<>();
        
        // 팩토리 메소드 + 플라이웨이트 패턴
        public static Shape getCircle(String color) {
            Circle circle = (Circle) shapes.get(color);
            
            if (circle == null) {
                log.info("✨ 새로운 {} 원 생성", color);
                circle = new Circle(color);
                shapes.put(color, circle);
            } else {
                log.info("♻️ 기존 {} 원 재사용", color);
            }
            
            return circle;
        }
        
        public static int getCreatedShapeCount() {
            return shapes.size();
        }
    }
    
    /*
     * 팩토리 + 플라이웨이트가 자주 함께 쓰이는 이유:
     * 
     * 1. 객체 생성 제어가 필요 (팩토리)
     * 2. 동일한 객체 재사용 (플라이웨이트)
     * 3. 캐시 관리 (둘 다)
     */
    
    public static void demonstratePattern() {
        log.info("=== 팩토리 + 플라이웨이트 패턴 시연 ===");
        
        // 여러 원을 그리지만, 색상이 같으면 같은 객체 재사용
        for (int i = 0; i < 5; i++) {
            String[] colors = {"red", "blue", "red", "green", "blue"};
            Shape circle = ShapeFactory.getCircle(colors[i]);
            circle.draw(i * 10, i * 20, i + 5);
        }
        
        log.info("📊 총 생성된 원 객체 수: {}", ShapeFactory.getCreatedShapeCount());
    }
}
