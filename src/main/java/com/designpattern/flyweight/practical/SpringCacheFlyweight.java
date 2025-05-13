package com.designpattern.flyweight.practical;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Cache를 활용한 플라이웨이트 패턴
 * 더 간단하고 Spring스러운 방법!
 */

// 의존성 추가 필요: spring-boot-starter-cache

// 1. 캐시 설정
@CacheConfig(cacheNames = "flyweights")
@Component
@Slf4j
public class SpringCacheFlyweight {
    
    // 핵심: @Cacheable로 자동 캐싱!
    @Cacheable(key = "#fontFamily + '-' + #fontSize + '-' + #color")
    public FontStyle createFontStyle(String fontFamily, int fontSize, String color) {
        log.info("🎨 새로운 폰트 스타일 생성: {}-{}-{}", fontFamily, fontSize, color);
        // 실제로는 비용이 많이 드는 작업
        return new FontStyle(fontFamily, fontSize, color);
    }
    
    // 플라이웨이트 객체 - 불변이어야 함!
    public static class FontStyle {
        private final String fontFamily;
        private final int fontSize;
        private final String color;
        
        public FontStyle(String fontFamily, int fontSize, String color) {
            this.fontFamily = fontFamily;
            this.fontSize = fontSize;
            this.color = color;
        }
        
        public void render(String text, int x, int y) {
            log.info("📝 렌더링: '{}' at ({},{}) with {}-{}-{}", 
                text, x, y, fontFamily, fontSize, color);
        }
    }
}

/*
 * 설정 파일에 추가 필요 (application.yml):
 * 
 * spring:
 *   cache:
 *     type: simple  # 또는 redis, ehcache 등
 *     cache-names:
 *       - flyweights
 * 
 * 그리고 메인 클래스에 @EnableCaching 추가
 */
