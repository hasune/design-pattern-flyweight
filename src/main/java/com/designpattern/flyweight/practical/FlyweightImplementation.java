package com.designpattern.flyweight.practical;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Spring Boot에서 플라이웨이트 패턴 구현 핵심 요소들
 */

// 1. 플라이웨이트 인터페이스 - 불변이어야 함!
interface Flyweight {
    void operation(String extrinsicState); // 외재적 상태는 매개변수로
}

// 2. 구체적인 플라이웨이트 - 내재적 상태만 포함
@Slf4j
class ConcreteFlyweight implements Flyweight {
    private final String intrinsicState; // final로 불변 보장!
    
    public ConcreteFlyweight(String intrinsicState) {
        this.intrinsicState = intrinsicState;
        log.info("🏗️ 플라이웨이트 생성: {}", intrinsicState);
    }
    
    @Override
    public void operation(String extrinsicState) {
        log.info("💫 실행: 내재적={}, 외재적={}", intrinsicState, extrinsicState);
    }
}

// 3. 플라이웨이트 팩토리 - Spring Bean으로 관리
@Component
@Slf4j
public class FlyweightFactory {
    
    // 핵심 1: ConcurrentHashMap 사용 (Thread-Safe)
    private final Map<String, Flyweight> flyweights = new ConcurrentHashMap<>();
    
    // 핵심 2: computeIfAbsent 사용 (Atomic 연산)
    public Flyweight getFlyweight(String key) {
        return flyweights.computeIfAbsent(key, k -> {
            log.info("✨ 새로운 플라이웨이트 생성: {}", k);
            return new ConcreteFlyweight(k);
        });
    }
    
    // 모니터링용 메소드 (실무에서 중요!)
    public int getInstanceCount() {
        return flyweights.size();
    }
    
    public void printStatus() {
        log.info("📊 현재 플라이웨이트 인스턴스 수: {}", flyweights.size());
        flyweights.keySet().forEach(key -> 
            log.info("  - {}", key));
    }
}

/*
 * 🔥 핵심 포인트들:
 * 
 * 1. @Component 어노테이션으로 Spring이 싱글톤으로 관리
 * 2. ConcurrentHashMap으로 멀티스레드 안전성 보장
 * 3. 내재적 상태는 final로 불변성 보장
 * 4. 외재적 상태는 메소드 파라미터로만 전달
 * 5. computeIfAbsent로 원자성 보장
 */
