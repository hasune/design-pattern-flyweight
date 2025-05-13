package com.designpattern.flyweight.practical;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 플라이웨이트 패턴을 위한 Spring 설정
 */
@Configuration
@EnableCaching  // 캐시 활성화
@Slf4j
public class FlyweightConfig {
    
    // 1. 캐시 매니저 설정
    @Bean
    public CacheManager cacheManager() {
        log.info("🚀 캐시 매니저 초기화");
        return new ConcurrentMapCacheManager("flyweights", "icons", "styles");
    }
    
    // 2. 플라이웨이트 팩토리 Bean
    @Bean
    @Scope("singleton")  // 기본값이지만 명시적으로 표현
    public IconFactory iconFactory() {
        log.info("🏭 아이콘 팩토리 Bean 생성");
        return new IconFactory();
    }
    
    // 3. 커스텀 플라이웨이트 매니저
    @Bean
    public WeightManager weightManager() {
        return new WeightManager();
    }
    
    static class IconFactory {
        private final ConcurrentHashMap<String, Icon> icons = new ConcurrentHashMap<>();
        
        public Icon getIcon(String name) {
            return icons.computeIfAbsent(name, k -> {
                log.info("🖼️ 새 아이콘 생성: {}", k);
                return new Icon(k);
            });
        }
    }
    
    static class Icon {
        private final String name;
        private final byte[] data; // 실제로는 이미지 데이터
        
        public Icon(String name) {
            this.name = name;
            this.data = loadIconData(name); // 가상의 로딩
        }
        
        private byte[] loadIconData(String name) {
            // 실제로는 파일 시스템이나 리소스에서 로딩
            return ("icon-data-" + name).getBytes();
        }
        
        public void render(int x, int y) {
            log.info("🎯 아이콘 렌더링: {} at ({}, {})", name, x, y);
        }
    }
    
    static class WeightManager {
        public void logMemoryUsage() {
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            log.info("💾 현재 메모리 사용량: {} MB", usedMemory / (1024 * 1024));
        }
    }
}

/*
 * 핵심 포인트:
 * 1. @EnableCaching으로 Spring Cache 활성화
 * 2. 팩토리 Bean들을 싱글톤으로 관리
 * 3. 메모리 모니터링을 위한 유틸리티 제공
 */
