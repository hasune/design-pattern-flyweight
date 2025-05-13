package com.designpattern.flyweight.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 실무에서 널리 사용되는 HikariCP 예시
 * - ConnectionPool 자체는 싱글톤 (@Bean으로 관리)
 * - 내부의 연결들은 풀로 관리 (플라이웨이트 패턴)
 */
@Configuration
public class HikariCPExample {
    
    @Bean  // 스프링이 싱글톤으로 관리
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // DB 설정
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setUsername("user");
        config.setPassword("password");
        
        // 풀 설정 (플라이웨이트 패턴)
        config.setMinimumIdle(5);           // 최소 연결 수
        config.setMaximumPoolSize(20);      // 최대 연결 수
        config.setConnectionTimeout(30000); // 연결 대기 시간
        config.setIdleTimeout(600000);      // 유휴 연결 유지 시간
        config.setMaxLifetime(1800000);     // 연결 최대 수명
        
        // 하나의 DataSource 인스턴스 (싱글톤)
        return new HikariDataSource(config);
    }
}
