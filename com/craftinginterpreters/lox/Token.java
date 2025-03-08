package com.craftinginterpreters.lox;

public class Token {
    final TokenType type; //token：词法分析器为每个lexeme分配的分类标识
    final String lexeme; //词素：从源代码中识别出的基本单元
    final Object literal; //字面量：直接出现在代码中的固定值，表示固定数据的词素
    final int line; //行数位置

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
