package com.designpattern.flyweight;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 플라이웨이트 팩토리
 * 생성된 플라이웨이트 객체들을 캐싱하여 재사용
 */
@Component
@Slf4j
public class CharacterFactory {
    private final Map<Character, CharacterFlyweight> characterCache = new HashMap<>();
    
    public CharacterFlyweight getCharacter(char c) {
        CharacterFlyweight character = characterCache.get(c);
        
        if (character == null) {
            character = new ConcreteCharacter(c);
            characterCache.put(c, character);
            log.info("Created new character flyweight for: '{}'", c);
        } else {
            log.info("Reused existing character flyweight for: '{}'", c);
        }
        
        return character;
    }
    
    public int getCacheSize() {
        return characterCache.size();
    }
    
    public void clearCache() {
        characterCache.clear();
        log.info("Character cache cleared");
    }
}
