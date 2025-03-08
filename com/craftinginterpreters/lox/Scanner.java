package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner {
    private final String source; //将源代码存储为一个string
    private final List<Token> tokens = new ArrayList<>(); //准备一个列表来保存扫描时产生的标记

    private int start = 0; //当前lexeme的第一个char的位置（在源代码string中的偏移量，后同）
    private int current = 0; //正在扫描的char的位置
    private int line = 1; //当前lexeme位于的原文件行数

    //构造函数
    Scanner(String source) {
        this.source = source;
    }

    //扫描循环
    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current; //在这里更新的start
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line)); //在最后添加eof的token
        return tokens;
    }

    //辅助函数：判断是否位于文件末尾
    private boolean isAtEnd() {
        return current >= source.length();
    }

    //辅助函数：识别单个token
    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break; 
            //可能一个可能两个的token
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if(match('/')){
                    while(peek() != '\n' && !isAtEnd()) advance(); //忽略注释
                } else {
                    addToken(SLASH); //否则是除号
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if(isDigit(c)){
                    number();
                }else if(isAlpha(c)){
                    //先假设除了上面那些，其他任何以字母或下划线开头的lexeme都是一个标识符
                    identifier();
                }else{
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    //辅助函数：消费字符
    private char advance(){
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type){
        addToken(type, null);
    }

    private void addToken(TokenType type,Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    //前瞻并比较一个字符但不消费
    private boolean match(char expected) {
        if(isAtEnd()) return false; //现在的current已经是advance中++后的
        if(source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    //前瞻直到空格但不消费字符
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    //消费字符串字面量
    private void string(){
        while(peek() != '"' && !isAtEnd()){
            if(peek()=='\n') line++; //支持多行字符串
            advance();
        }

        if(isAtEnd()){
            Lox.error(line,"Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    //判断数字
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    //消费数字字面量
    private void number() {
        while(isDigit(peek())) advance();

        if(peek() == '.' && isDigit(peekNext())) {
            advance();
            while(isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    //前瞻并返回一个字符但不消费
    private char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    //消费标识符
    private void identifier() {
        while(isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER; //如果匹配就使用map中的关键字token标记，否则就是一个普通的用户自定义identifier
        addToken(type);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    //用于检查identifier的lexeme是否是reserved之一
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
}
