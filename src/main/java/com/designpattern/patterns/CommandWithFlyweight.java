package com.designpattern.patterns;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

/**
 * 커맨드 패턴과 플라이웨이트 패턴의 조합
 * 플라이웨이트가 내재적/외재적 상태를 분리하듯
 * 커맨드도 행동과 데이터를 분리함
 */
@Slf4j
public class CommandWithFlyweight {
    
    // 커맨드 인터페이스
    interface Command {
        void execute(String target, Object... params);
    }
    
    // 플라이웨이트 커맨드 (상태 없는 행동)
    static class PrintCommand implements Command {
        private static PrintCommand instance = new PrintCommand();
        
        private PrintCommand() {
            log.info("📝 PrintCommand 인스턴스 생성");
        }
        
        public static PrintCommand getInstance() {
            return instance;
        }
        
        @Override
        public void execute(String target, Object... params) {
            log.info("🖨️ [{}]에 출력: {}", target, params[0]);
        }
    }
    
    static class DeleteCommand implements Command {
        private static DeleteCommand instance = new DeleteCommand();
        
        private DeleteCommand() {
            log.info("🗑️ DeleteCommand 인스턴스 생성");
        }
        
        public static DeleteCommand getInstance() {
            return instance;
        }
        
        @Override
        public void execute(String target, Object... params) {
            log.info("💥 [{}]에서 삭제: {}", target, params[0]);
        }
    }
    
    // 커맨드 팩토리 (플라이웨이트 관리)
    static class CommandFactory {
        private static final Map<String, Command> commands = new HashMap<>();
        
        static {
            commands.put("print", PrintCommand.getInstance());
            commands.put("delete", DeleteCommand.getInstance());
        }
        
        public static Command getCommand(String type) {
            Command command = commands.get(type);
            if (command != null) {
                log.info("♻️ 기존 커맨드 재사용: {}", type);
            }
            return command;
        }
    }
    
    /*
     * 왜 함께 사용하나?
     * 
     * 플라이웨이트:
     * - 내재적 상태 (불변): 커맨드 타입, 실행 로직
     * - 외재적 상태 (가변): 실행 대상, 파라미터
     * 
     * 커맨드:
     * - 행동을 객체로 캡슐화
     * - 실행 컨텍스트와 분리
     */
    
    public static void demonstratePattern() {
        log.info("=== 커맨드 + 플라이웨이트 패턴 시연 ===");
        
        // 같은 타입의 커맨드는 재사용됨
        Command print1 = CommandFactory.getCommand("print");
        Command print2 = CommandFactory.getCommand("print");
        
        log.info("같은 인스턴스인가? {}", print1 == print2);
        
        // 다른 데이터로 실행 (외재적 상태)
        print1.execute("프린터1", "문서A");
        print2.execute("프린터2", "문서B");
        
        Command delete = CommandFactory.getCommand("delete");
        delete.execute("휴지통", "파일X");
    }
}
