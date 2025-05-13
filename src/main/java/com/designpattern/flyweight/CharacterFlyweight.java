package com.designpattern.flyweight;

/**
 * 플라이웨이트 인터페이스
 * Intrinsic State(고유 상태)를 포함하고, Extrinsic State(외부 상태)를 파라미터로 받아 동작
 */
public interface CharacterFlyweight {
    void display(CharacterStyle style);
    String getCharacter();
}
