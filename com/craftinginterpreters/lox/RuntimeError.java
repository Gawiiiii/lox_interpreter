package com.craftinginterpreters.lox;


//当检查失败时，代码会抛出一个以下的错误：
public class RuntimeError extends RuntimeException {
        final Token token;

        RuntimeError(Token token, String message) {
            super(message);
            this.token = token;//与Java异常转换不同，此处会跟踪token，指明错误位置
        }
}
