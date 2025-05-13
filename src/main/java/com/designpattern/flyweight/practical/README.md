# Spring Boot 플라이웨이트 패턴 구현 체크리스트

## 🎯 반드시 해야 할 것들

### 1. Thread Safety 보장
```java
// ❌ 잘못된 예
private Map<String, Flyweight> cache = new HashMap<>(); // Thread Unsafe!

// ✅ 올바른 예
private final ConcurrentHashMap<String, Flyweight> cache = new ConcurrentHashMap<>();
```

### 2. 불변성(Immutable) 보장
```java
// ❌ 잘못된 예
public class BadFlyweight {
    private String state; // 변경 가능!
    public void setState(String state) { this.state = state; }
}

// ✅ 올바른 예
public class GoodFlyweight {
    private final String intrinsicState; // final로 불변!
    public GoodFlyweight(String state) { this.intrinsicState = state; }
}
```

### 3. 외재적 상태를 매개변수로만 전달
```java
// ❌ 잘못된 예
public class BadFlyweight {
    private String extrinsicState; // 플라이웨이트에 저장하면 안됨!
}

// ✅ 올바른 예
@Override
public void operation(String extrinsicState) { // 매개변수로만!
    // ...
}
```

## 🚀 Spring Boot 특화 최적화

### 1. @Component 사용
```java
@Component // Spring이 싱글톤으로 관리
public class FlyweightFactory {
    // ...
}
```

### 2. @Cacheable 활용
```java
@Cacheable(key = "#param1 + '-' + #param2")
public Flyweight getFlyweight(String param1, String param2) {
    // ...
}
```

### 3. 의존성 주입 활용
```java
@Service
public class MyService {
    @Autowired  // 생성자 주입이 더 좋음
    private FlyweightFactory factory;
    
    // 또는
    public MyService(FlyweightFactory factory) {
        this.factory = factory;
    }
}
```

## 📊 모니터링과 디버깅

### 1. 캐시 상태 확인
```java
@RestController
public class FlyweightMonitorController {
    
    @GetMapping("/flyweight/status")
    public Map<String, Object> getStatus() {
        return Map.of(
            "totalInstances", factory.getInstanceCount(),
            "memoryUsage", getMemoryUsage()
        );
    }
}
```

### 2. 로깅 설정
```yaml
# application.yml
logging.level:
  com.designpattern.flyweight: DEBUG
```

## ⚠️ 피해야 할 실수들

### 1. 플라이웨이트에 가변 상태 저장
```java
// 🚫 절대 하지 마세요
public class BadFlyweight {
    private List<String> userSpecificData = new ArrayList<>(); // 위험!
}
```

### 2. 팩토리를 여러 번 생성
```java
// 🚫 잘못된 방법
public class BadService {
    public void doSomething() {
        FlyweightFactory factory = new FlyweightFactory(); // 매번 새로 생성!
    }
}

// ✅ 올바른 방법
@Service
public class GoodService {
    @Autowired
    private FlyweightFactory factory; // 주입받아 사용
}
```

### 3. 너무 작은 객체에 플라이웨이트 적용
```java
// 🚫 오버엔지니어링
public class TinyFlyweight {
    private final boolean flag; // 단순한 boolean 값에는 불필요
}
```

## 🎪 실무 사용 예시

### 1. 폰트/스타일 관리
- UI 컴포넌트의 공통 스타일
- 문서 편집기의 문자 스타일

### 2. 아이콘/이미지 캐싱
- 애플리케이션 아이콘
- 반복 사용되는 이미지

### 3. 설정 객체
- 데이터베이스 연결 설정
- 외부 API 엔드포인트

## 💡 성능 최적화 팁

1. **예측 로딩**: 자주 사용되는 플라이웨이트는 미리 생성
2. **가비지 컬렉션 고려**: 캐시 크기 제한 설정
3. **메모리 모니터링**: JVisualVM이나 JProfiler로 확인

```java
@PostConstruct
public void preloadCommonFlyweights() {
    // 자주 사용되는 것들 미리 로딩
    factory.getFlyweight("Arial-12-black");
    factory.getFlyweight("Arial-14-black");
}
```
