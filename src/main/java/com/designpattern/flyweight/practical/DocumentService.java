package com.designpattern.flyweight.practical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * 플라이웨이트 패턴을 실제 서비스에서 활용하는 예시
 */
@Service
@Slf4j
public class DocumentService {
    
    // 1. 플라이웨이트 팩토리 주입
    @Autowired
    private FlyweightFactory flyweightFactory;
    
    @Autowired
    private SpringCacheFlyweight styleFlyweight;
    
    // 2. 문서 생성 (플라이웨이트 활용)
    public void createDocument(String content) {
        log.info("📄 문서 생성 시작");
        
        List<Character> document = new ArrayList<>();
        
        // 각 문자마다 플라이웨이트 적용
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            
            // 문자별 스타일 결정 (외재적 상태)
            String font = (i % 2 == 0) ? "Arial" : "Times";
            int size = (ch == ' ') ? 12 : 14;
            String color = (ch >= 'A' && ch <= 'Z') ? "red" : "black";
            
            // 플라이웨이트 획득
            Flyweight charFlyweight = flyweightFactory.getFlyweight(
                ch + "-" + font + "-" + size + "-" + color
            );
            
            // 외재적 상태와 함께 실행
            charFlyweight.operation("position:" + i);
        }
        
        flyweightFactory.printStatus();
        log.info("📊 총 문자 수: {}, 플라이웨이트 인스턴스 수: {}", 
            content.length(), flyweightFactory.getInstanceCount());
    }
    
    // 3. 스타일 적용 (Spring Cache 활용)
    public void applyStylesToDocument(String content) {
        log.info("🎨 문서 스타일링 시작");
        
        // 같은 스타일은 캐시에서 재사용됨
        var style1 = styleFlyweight.createFontStyle("Arial", 12, "black");
        var style2 = styleFlyweight.createFontStyle("Arial", 12, "black"); // 캐시 히트!
        var style3 = styleFlyweight.createFontStyle("Times", 14, "red");   // 새로 생성
        
        log.info("style1 == style2? {}", style1 == style2); // true (캐시됨)
        
        // 다양한 위치에서 스타일 사용 (외재적 상태)
        style1.render("Hello", 10, 20);
        style2.render("World", 50, 20);
        style3.render("Spring", 10, 40);
    }
    
    // 4. 성능 모니터링
    public void measurePerformance() {
        log.info("⚡ 성능 측정 시작");
        
        long startTime = System.currentTimeMillis();
        
        // 플라이웨이트 없이 (매번 새로 생성)
        for (int i = 0; i < 1000; i++) {
            new SpringCacheFlyweight.FontStyle("Arial", 12, "black");
        }
        
        long withoutFlyweight = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        
        // 플라이웨이트 사용 (캐시됨)
        for (int i = 0; i < 1000; i++) {
            styleFlyweight.createFontStyle("Arial", 12, "black");
        }
        
        long withFlyweight = System.currentTimeMillis() - startTime;
        
        log.info("📈 플라이웨이트 없이: {}ms", withoutFlyweight);
        log.info("📈 플라이웨이트 사용: {}ms", withFlyweight);
        log.info("🚀 성능 개선: {}%", 
            ((double)(withoutFlyweight - withFlyweight) / withoutFlyweight) * 100);
    }
}

/*
 * 실무 사용 팁:
 * 
 * 1. @Autowired로 팩토리 주입받아 사용
 * 2. 비즈니스 로직에서는 외재적 상태만 관리
 * 3. 성능 모니터링 로직 포함
 * 4. 캐시 히트율 확인
 */
