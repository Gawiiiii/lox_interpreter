package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;
    //程序入口
    public static void main(String[] args)  throws IOException{
        if(args.length>1){
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }else if(args.length==1){
            runFile(args[0]);
        }else{
            runPrompt();
        }
    }

    //以文件形式编译
    private static void runFile(String path) throws IOException{
        byte[] bytes=Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
    }

    //从命令行编译
    private static void runPrompt() throws IOException{
        InputStreamReader input=new InputStreamReader(System.in);
        BufferedReader reader=new BufferedReader(input);
        for(;;){
            System.out.print("> ");
            String line=reader.readLine();
            if(line==null) break;
            run(line);
            hadError = false;
        }
    }

    //核心运行函数
    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        /*tobedone: 暂时只打印token */
        /*for(Token token : tokens){
            System.out.println(token);
        }*/

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if(hadError) return;
        System.out.println(new AstPrinter().print(expression));
    }

    //错误处理函数
    static void error(int line, String message){
        report(line, "", message);
    }

    //error()的工具方法
    private static void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if(token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}