package com.designpattern.flyweight.database;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 실제 JDBC Connection Pool의 생명주기를 보여주는 예시
 */
@Component
@Slf4j
public class DetailedConnectionPool {
    private final String url = "jdbc:mysql://localhost:3306/test";
    private final String username = "user";
    private final String password = "password";
    
    private final int minPoolSize = 5;
    private final int maxPoolSize = 10;
    private final long maxWaitTime = 5000; // 5초
    
    // 사용 가능한 연결들을 관리하는 큐
    private final BlockingQueue<Connection> availableConnections = new LinkedBlockingQueue<>();
    // 사용 중인 연결들을 추적
    private final List<Connection> usedConnections = new ArrayList<>();
    
    public DetailedConnectionPool() {
        initializePool();
    }
    
    /**
     * 1단계: 풀 초기화 - 최소 연결 수만큼 미리 생성
     */
    private void initializePool() {
        log.info("🚀 Connection Pool 초기화 시작 (최소: {}, 최대: {})", minPoolSize, maxPoolSize);
        
        for (int i = 0; i < minPoolSize; i++) {
            try {
                Connection conn = createNewConnection();
                availableConnections.offer(conn);
                log.info("✅ Connection #{} 생성 완료", i + 1);
            } catch (SQLException e) {
                log.error("❌ Connection 생성 실패: {}", e.getMessage());
            }
        }
        log.info("🎉 Connection Pool 초기화 완료: {} 개 연결 준비됨", availableConnections.size());
    }
    
    /**
     * 2단계: 연결 대여 (getConnection)
     */
    public Connection getConnection() throws SQLException {
        log.info("📞 Connection 요청 접수");
        
        // 2-1. 사용 가능한 연결이 있다면 바로 반환
        Connection conn = availableConnections.poll();
        
        if (conn != null && isConnectionValid(conn)) {
            usedConnections.add(conn);
            log.info("♻️ [재사용] 기존 Connection 반환: {}", getConnectionId(conn));
            return conn;
        }
        
        // 2-2. 사용 가능한 연결이 없고, 풀이 만료하지 않았다면 새로 생성
        if (getTotalConnectionCount() < maxPoolSize) {
            conn = createNewConnection();
            usedConnections.add(conn);
            log.info("🆕 [새로 생성] 새 Connection 반환: {}", getConnectionId(conn));
            return conn;
        }
        
        // 2-3. 풀이 꽉 찬 경우 대기
        log.warn("⏳ 사용 가능한 Connection이 없습니다. 대기 중... (최대 {}ms)", maxWaitTime);
        try {
            conn = availableConnections.poll(maxWaitTime, TimeUnit.MILLISECONDS);
            if (conn != null && isConnectionValid(conn)) {
                usedConnections.add(conn);
                log.info("⏰ [대기 후 획득] Connection 반환: {}", getConnectionId(conn));
                return conn;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        throw new SQLException("Connection pool exhausted!");
    }
    
    /**
     * 3단계: 연결 반납 (close/return)
     * 실제 JDBC에서는 close()가 호출되면 풀로 반환됨
     */
    public void returnConnection(Connection conn) {
        if (conn == null) return;
        
        try {
            // 3-1. 연결 상태 확인
            if (!isConnectionValid(conn)) {
                log.warn("🚨 유효하지 않은 Connection 감지, 새로 생성함");
                // 유효하지 않은 연결은 제거하고 새로 생성
                removeInvalidConnection(conn);
                createAndAddNewConnection();
                return;
            }
            
            // 3-2. 연결 정리 (트랜잭션 롤백, 스테이트먼트 닫기 등)
            resetConnectionState(conn);
            
            // 3-3. 사용 중 목록에서 제거
            usedConnections.remove(conn);
            
            // 3-4. 사용 가능한 연결 풀로 반환
            availableConnections.offer(conn);
            
            log.info("🔄 Connection 반환 완료: {}", getConnectionId(conn));
            
        } catch (SQLException e) {
            log.error("❌ Connection 반환 중 오류: {}", e.getMessage());
            removeInvalidConnection(conn);
        }
    }
    
    /**
     * Connection 생성
     */
    private Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        
        // 연결 설정
        conn.setAutoCommit(true);
        
        log.info("🔨 새 Connection 생성: {}", getConnectionId(conn));
        return conn;
    }
    
    /**
     * Connection 유효성 검사
     */
    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Connection 상태 리셋
     */
    private void resetConnectionState(Connection conn) throws SQLException {
        // 1. 자동 커밋 모드 복원
        if (!conn.getAutoCommit()) {
            conn.rollback();
            conn.setAutoCommit(true);
        }
        
        // 2. 사용자 정의 상태 제거 (예: 세션 변수, 임시 테이블 등)
        // conn.createStatement().execute("RESET CONNECTION");
        
        // 3. 캐시 정리
        conn.clearWarnings();
    }
    
    /**
     * 풀 상태 정보
     */
    public void printPoolStatus() {
        log.info("📊 Pool Status:");
        log.info("  - 사용 가능: {} 개", availableConnections.size());
        log.info("  - 사용 중: {} 개", usedConnections.size());
        log.info("  - 전체: {} 개", getTotalConnectionCount());
    }
    
    private int getTotalConnectionCount() {
        return availableConnections.size() + usedConnections.size();
    }
    
    private String getConnectionId(Connection conn) {
        return conn.toString();
    }
    
    private void removeInvalidConnection(Connection conn) {
        usedConnections.remove(conn);
        try {
            conn.close();
        } catch (SQLException e) {
            // 이미 닫힌 연결일 수 있음
        }
    }
    
    private void createAndAddNewConnection() {
        try {
            Connection newConn = createNewConnection();
            availableConnections.offer(newConn);
        } catch (SQLException e) {
            log.error("❌ 새 Connection 생성 실패: {}", e.getMessage());
        }
    }
}
