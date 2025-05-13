package com.designpattern.flyweight.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 데이터베이스 서비스
 * 플라이웨이트 패턴을 사용하여 연결을 효율적으로 관리
 */
@Service
@Slf4j
public class DatabaseService {
    
    @Autowired
    private ConnectionPool connectionPool;
    
    private final Random random = new Random();
    
    public String executeQuery(String query) {
        log.info("📞 사용자 요청 접수: {}", query);
        
        DatabaseConnection connection = connectionPool.getConnection();
        
        if (connection == null) {
            String errorMsg = "❌ 모든 연결이 사용 중입니다. 잠시 후 다시 시도해주세요.";
            log.error(errorMsg);
            return errorMsg;
        }
        
        try {
            // 연결 및 쿼리 실행
            connection.connect();
            connection.executeQuery(query);
            
            // 실제 쿼리 실행 시뮬레이션 (랜덤 지연)
            Thread.sleep(random.nextInt(1000) + 500);
            
            String result = "✅ 쿼리 실행 완료: " + query;
            log.info("🎯 {}", result);
            return result;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "❌ 쿼리 실행 중 오류 발생";
        } finally {
            // 연결 반환 (매우 중요!)
            connectionPool.returnConnection(connection);
        }
    }
    
    public void closeAllConnections() {
        log.info("🔧 모든 연결 해제 요청");
        connectionPool.closeAllConnections();
    }
    
    public int getPoolSize() {
        return connectionPool.getPoolSize();
    }
    
    public int getActiveConnections() {
        return connectionPool.getActiveConnections();
    }
    
    public int getAvailableConnections() {
        return connectionPool.getAvailableConnections();
    }
}
