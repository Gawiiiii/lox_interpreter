package com.craftinginterpreters.lox;

public class Interpreter implements Expr.Visitor<Object> {
    //将字面量树节点转换为运行时值
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    //在表达式中显式使用括号时产生的语法树节点
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    //利用访问者模式特性递归计算表达式
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    //一元表达式求值
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        //后序遍历的体现：每个节点在自己求值之前必须先对子节点求值

        switch(expr.operator.type){
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
                //java是静态类型，Object只有在运行时才被确定类型，这时必须进行强制类型转换才能编译通过
        }

        return null;
    }

    //检查操作数
    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    //类似Ruby的真假划分：false和nil为假，其余为真
    private boolean isTruthy(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean)object;
        return true;
    }

    //二元操作符求值
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case GREATER:
                checkNumberOperand(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperand(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperand(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperand(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperand(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS://不能假设操作数是某种类型然后强制转换，而是要动态检查类型并选择操作
                if(left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if(left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");//有可能出现语法错误没有进入上面两个任何一个分支
            case SLASH:
                checkNumberOperand(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperand(expr.operator, left, right);
                return (double)left * (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            //因为等式运算符支持混合类型操作数，所以需要单独的方法
        }

        return null;
    }

    //验证器，与一元那个基本相同
    private void checkNumberOperand(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isEqual(Object a, Object b) {
        if(a == null && b == null) return true;
        if(a == null) return false;
        //对nil/null做特殊处理，这样就不会在对null调用equals()方法时抛出NullPointerException

        return a.equals(b);//Lox不会在等式中做隐式转换，Java也不会
    }

    //包上一层皮，以便与程序的其他部分对接
    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    //将Lox值转换为字符串
    private String stringify(Object object) {
        if(object == null) return "nil";

        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0")){
                text=text.substring(0, text.length() - 2);;
            }
            return text;
        }
        return object.toString();
    }
}
