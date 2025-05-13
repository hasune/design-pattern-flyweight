package com.designpattern.flyweight.database;

import lombok.extern.slf4j.Slf4j;

/**
 * MySQL 연결 구현 (구체적인 플라이웨이트)
 * Intrinsic State: 데이터베이스 타입, 기본 연결 정보
 */
@Slf4j
public class MySQLConnection implements DatabaseConnection {
    private final String connectionId;
    private final String host;
    private final int port;
    private boolean inUse = false;
    
    public MySQLConnection(String host, int port) {
        this.host = host;
        this.port = port;
        this.connectionId = "mysql-" + host + ":" + port + "-" + System.currentTimeMillis();
        log.info("📦 [플라이웨이트 생성] MySQL Connection 객체 생성: {}", connectionId);
    }
    
    @Override
    public void connect() {
        log.info("🔗 MySQL 연결 시작: {} ({}:{})", connectionId, host, port);
    }
    
    @Override
    public void disconnect() {
        log.info("🔌 MySQL 연결 해제: {} ({}:{})", connectionId, host, port);
    }
    
    @Override
    public void executeQuery(String query) {
        log.info("🔍 [{}] 쿼리 실행: {}", connectionId, query);
    }
    
    @Override
    public boolean isInUse() {
        return inUse;
    }
    
    @Override
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
        log.info("📋 [{}] 사용 상태 변경: {}", connectionId, inUse ? "사용중" : "사용가능");
    }
    
    @Override
    public String getId() {
        return connectionId;
    }
}
