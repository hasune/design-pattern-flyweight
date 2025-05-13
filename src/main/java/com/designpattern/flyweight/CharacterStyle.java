package com.designpattern.flyweight;

import lombok.Data;

@Data
public class CharacterStyle {
    private final int x;
    private final int y;
    private final String color;
    private final int fontSize;
    private final String fontFamily;
    
    public CharacterStyle(int x, int y, String color, int fontSize, String fontFamily) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
    }
}
