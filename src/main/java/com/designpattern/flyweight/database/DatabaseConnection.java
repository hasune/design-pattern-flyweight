package com.designpattern.flyweight.database;

/**
 * 데이터베이스 연결 인터페이스 (플라이웨이트)
 */
public interface DatabaseConnection {
    void connect();
    void disconnect();
    void executeQuery(String query);
    boolean isInUse();
    void setInUse(boolean inUse);
    String getId();
}
