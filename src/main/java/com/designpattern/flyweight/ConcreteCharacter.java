package com.designpattern.flyweight;

import lombok.extern.slf4j.Slf4j;

/**
 * 구체적인 플라이웨이트 구현
 * Intrinsic State(고유 상태)인 문자만 저장
 */
@Slf4j
public class ConcreteCharacter implements CharacterFlyweight {
    private final char character; // Intrinsic State: 모든 인스턴스에서 공유되는 상태
    
    public ConcreteCharacter(char character) {
        this.character = character;
    }
    
    @Override
    public void display(CharacterStyle style) {
        log.info("Displaying character '{}' at position ({}, {}) with color: {}, fontSize: {}, fontFamily: {}", 
                character, style.getX(), style.getY(), style.getColor(), style.getFontSize(), style.getFontFamily());
    }
    
    @Override
    public String getCharacter() {
        return String.valueOf(character);
    }
}
