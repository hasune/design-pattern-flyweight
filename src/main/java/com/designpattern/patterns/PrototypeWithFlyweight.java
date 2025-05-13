package com.designpattern.patterns;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

/**
 * 프로토타입 패턴과 플라이웨이트 패턴의 조합 예시
 */
@Slf4j
public class PrototypeWithFlyweight {
    
    // 플라이웨이트: 공유되는 불변 객체
    static class CharacterStyle implements Cloneable {
        private final String fontFamily;
        private final int fontSize;
        private final String color;
        
        public CharacterStyle(String fontFamily, int fontSize, String color) {
            this.fontFamily = fontFamily;
            this.fontSize = fontSize;
            this.color = color;
            log.info("🎨 스타일 생성: {}-{}-{}", fontFamily, fontSize, color);
        }
        
        @Override
        public CharacterStyle clone() {
            try {
                log.info("📋 스타일 복제: {}-{}-{}", fontFamily, fontSize, color);
                return (CharacterStyle) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        
        public String getInfo() {
            return String.format("%s-%dpx-%s", fontFamily, fontSize, color);
        }
    }
    
    // 플라이웨이트 팩토리
    static class StyleFactory {
        private static final Map<String, CharacterStyle> styles = new HashMap<>();
        
        public static CharacterStyle getStyle(String fontFamily, int fontSize, String color) {
            String key = fontFamily + "-" + fontSize + "-" + color;
            
            return styles.computeIfAbsent(key, k -> {
                log.info("✨ 새 스타일 생성 (플라이웨이트)");
                return new CharacterStyle(fontFamily, fontSize, color);
            });
        }
        
        // 프로토타입 패턴으로 스타일 복제
        public static CharacterStyle cloneStyle(CharacterStyle style) {
            log.info("🔄 프로토타입 패턴으로 스타일 복제");
            return style.clone();
        }
    }
    
    /*
     * 언제 함께 사용하나?
     * 
     * 1. 플라이웨이트로 기본 템플릿 관리
     * 2. 프로토타입으로 커스터마이징된 버전 생성
     * 
     * 예: 
     * - 기본 문서 템플릿 (플라이웨이트)
     * - 각 사용자용 커스터마이징 (프로토타입)
     */
    
    public static void demonstratePattern() {
        // 플라이웨이트로 기본 스타일 관리
        CharacterStyle baseStyle = StyleFactory.getStyle("Arial", 12, "black");
        CharacterStyle sameStyle = StyleFactory.getStyle("Arial", 12, "black");
        
        log.info("같은 인스턴스인가? {}", baseStyle == sameStyle);
        
        // 프로토타입으로 커스터마이징
        CharacterStyle customStyle = StyleFactory.cloneStyle(baseStyle);
        log.info("복제된 스타일 정보: {}", customStyle.getInfo());
    }
}
