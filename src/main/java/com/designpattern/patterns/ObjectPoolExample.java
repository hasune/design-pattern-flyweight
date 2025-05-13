package com.designpattern.patterns;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 객체 풀 패턴 예시
 * 플라이웨이트와 매우 유사하지만 미묘한 차이가 있음
 */
@Slf4j
public class ObjectPoolExample {
    
    // 비용이 많이 드는 객체 (예: 암호화 엔진)
    static class ExpensiveObject {
        private final String id;
        
        public ExpensiveObject() {
            this.id = "Object-" + System.currentTimeMillis();
            // 가상의 비용이 많이 드는 초기화
            try {
                Thread.sleep(100); // 초기화 시간 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("🏗️ 비용이 많이 드는 객체 생성: {}", id);
        }
        
        public void doWork(String data) {
            log.info("💼 [{}] 작업 수행: {}", id, data);
        }
    }
    
    // 객체 풀
    static class ExpensiveObjectPool {
        private final BlockingQueue<ExpensiveObject> pool = new LinkedBlockingQueue<>();
        private final int maxSize;
        
        public ExpensiveObjectPool(int maxSize) {
            this.maxSize = maxSize;
            // 미리 몇 개 생성해 둠
            for (int i = 0; i < Math.min(3, maxSize); i++) {
                pool.offer(new ExpensiveObject());
            }
        }
        
        public ExpensiveObject borrowObject() {
            ExpensiveObject obj = pool.poll();
            if (obj == null && pool.size() < maxSize) {
                obj = new ExpensiveObject(); // 새로 생성
            }
            log.info("📤 객체 대여: {}", obj != null ? "성공" : "풀이 꽉 참");
            return obj;
        }
        
        public void returnObject(ExpensiveObject obj) {
            if (obj != null) {
                pool.offer(obj);
                log.info("📥 객체 반납 완료");
            }
        }
    }
    
    /*
     * 플라이웨이트 vs 객체 풀 차이점:
     * 
     * 플라이웨이트:
     * - 동일한 내재적 상태를 가진 객체 공유
     * - 불변 객체 (상태가 바뀌지 않음)
     * - 문자, 아이콘 등
     * 
     * 객체 풀:
     * - 생성 비용이 비싼 객체 재사용
     * - 가변 객체 (상태가 바뀔 수 있음)
     * - DB 연결, 스레드 등
     */
}
