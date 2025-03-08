package com.craftinginterpreters.lox;

//token类型的枚举类
enum TokenType {
    //单字符token类型
    LEFT_PAREN, // (
    RIGHT_PAREN, // )
    LEFT_BRACE, // {
    RIGHT_BRACE, // }
    COMMA, // ,
    DOT, // .
    MINUS, // -
    PLUS, // +
    SEMICOLON, // ;
    SLASH, // /
    STAR, // *
    
    //单或双字符
    BANG, // !
    BANG_EQUAL, // !=
    EQUAL, // =
    EQUAL_EQUAL, // ==
    GREATER, // >
    GREATER_EQUAL, // >=
    LESS, // <
    LESS_EQUAL, // <=

    //字面量
    IDENTIFIER, // 标识符
    STRING, //字符串
    NUMBER, //数字

    //关键字
    AND, // &&
    CLASS, // class
    ELSE, // else
    FALSE, // false
    FUN, // fun
    FOR, // for
    IF, // if
    NIL, // 空值nil
    OR, // ||
    PRINT, // print
    RETURN, // return 
    SUPER, // 父类
    THIS, // this
    TRUE, // true
    VAR, // 变量声明var
    WHILE, // while

    //
    EOF // end of file
}
