package com.designpattern.flyweight.database;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 연결 풀 (플라이웨이트 팩토리)
 * 데이터베이스 연결을 미리 생성하고 재사용
 */
@Component
@Slf4j
public class ConnectionPool {
    private final List<DatabaseConnection> pool = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final int maxPoolSize = 5;
    private final String defaultHost = "localhost";
    private final int defaultPort = 3306;
    
    // 초기 풀 생성
    public ConnectionPool() {
        initializePool();
    }
    
    private void initializePool() {
        log.info("🏊‍♀️ 연결 풀 초기화 시작 (최대 크기: {})", maxPoolSize);
        for (int i = 0; i < maxPoolSize; i++) {
            DatabaseConnection connection = new MySQLConnection(defaultHost, defaultPort);
            pool.add(connection);
        }
        log.info("✅ 연결 풀 초기화 완료");
    }
    
    public DatabaseConnection getConnection() {
        lock.lock();
        try {
            // 사용 가능한 연결 찾기
            for (DatabaseConnection connection : pool) {
                if (!connection.isInUse()) {
                    connection.setInUse(true);
                    log.info("♻️ [플라이웨이트 재사용] 기존 연결 반환: {}", connection.getId());
                    return connection;
                }
            }
            
            // 모든 연결이 사용 중일 때
            log.warn("⚠️ 사용 가능한 연결이 없습니다. 잠시 대기하거나 새 연결을 생성해야 합니다.");
            return null;
        } finally {
            lock.unlock();
        }
    }
    
    public void returnConnection(DatabaseConnection connection) {
        lock.lock();
        try {
            if (connection != null && pool.contains(connection)) {
                connection.setInUse(false);
                log.info("🔄 연결 반환: {}", connection.getId());
            }
        } finally {
            lock.unlock();
        }
    }
    
    public int getPoolSize() {
        return pool.size();
    }
    
    public int getActiveConnections() {
        return (int) pool.stream().mapToLong(conn -> conn.isInUse() ? 1 : 0).sum();
    }
    
    public int getAvailableConnections() {
        return getPoolSize() - getActiveConnections();
    }
    
    public void closeAllConnections() {
        lock.lock();
        try {
            for (DatabaseConnection connection : pool) {
                connection.disconnect();
                connection.setInUse(false);
            }
            log.info("🔌 모든 연결 해제 완료");
        } finally {
            lock.unlock();
        }
    }
}
