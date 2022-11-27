package h06;

import static h06.EvaluationResult.Type.INVALID_CHARACTER;
import static h06.EvaluationResult.Type.NO_OPENING_BRACKET;
import static org.tudalgo.algoutils.student.Student.crash;

public class BracketExpression {

    private final char[] expression;

    public BracketExpression(String expression) {
        this.expression = expression.toCharArray();
    }

    private static boolean isOpeningBracket(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    private static boolean isClosingBracket(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    private static boolean isValidCharacter(char c) {
        return isOpeningBracket(c) || isClosingBracket(c);
    }

    private static char getClosingBracket(char c) {
        if (c == '(') {
            return ')';
        } else if (c == '[') {
            return ']';
        } else if (c == '{') {
            return '}';
        }
        //noinspection ConstantConditions
        return crash("invalid opening bracket");
    }

    public final EvaluationResult evaluate() {
        return evaluate(0);
    }

    public final EvaluationResult evaluate(int i) {
        // character at first index
        var char0 = expression[i];
        if (!isOpeningBracket(char0)) {
            // case 1 + 2: no opening bracket at index i
            return new EvaluationResult(!isValidCharacter(char0) ? INVALID_CHARACTER : NO_OPENING_BRACKET, i);
        } else if (i == expression.length - 1) {
            // case 3: first index is equal to last index, there cannot be a closing bracket
            return new EvaluationResult(EvaluationResult.Type.NO_CLOSING_BRACKET, i);
        }
        // character at second index
        var char1 = expression[i + 1];
        if (getClosingBracket(char0) == char1) {
            // case 4: second index is the closing bracket of the opening bracket
            if (i + 2 < expression.length && isOpeningBracket(expression[i + 2])) {
                // case 8: there is a closing bracket after sub-expression, but there is another character after the closing bracket
                return evaluate(i + 2);
            }
            return new EvaluationResult(EvaluationResult.Type.CORRECT, i + 2);
        } else if (isClosingBracket(char1)) {
            // case 5: second index is a closing bracket, but not the closing bracket of the opening bracket
            return new EvaluationResult(EvaluationResult.Type.INVALID_CLOSING_BRACKET, i + 1);
        }
        // evaluate sub-expression
        var next = evaluate(i + 1);
        if (next.type() != EvaluationResult.Type.CORRECT) {
            // case 6: sub-expression is not correct, return result of sub-expression
            return next;
        }
        if (next.nextIndex() >= expression.length) {
            // case 7: there is no index after sub-expression, there cannot be a closing bracket
            return new EvaluationResult(EvaluationResult.Type.NO_CLOSING_BRACKET, next.nextIndex());
        }
        var nextChar = expression[next.nextIndex()];
        if (getClosingBracket(char0) == nextChar) {
            if (next.nextIndex() + 1 < expression.length && isOpeningBracket(expression[next.nextIndex() + 1])) {
                // case 8: there is a closing bracket after sub-expression, but there is another character after the closing bracket
                return evaluate(next.nextIndex() + 1);
            }
            // case 8: next index is the closing bracket of the opening bracket
            return new EvaluationResult(EvaluationResult.Type.CORRECT, next.nextIndex() + 1);
        } else if (isClosingBracket(nextChar)) {
            // case 9:
            return new EvaluationResult(EvaluationResult.Type.INVALID_CLOSING_BRACKET, next.nextIndex());
        } else if (isOpeningBracket(nextChar)) {
            // case 10
            return new EvaluationResult(EvaluationResult.Type.NO_CLOSING_BRACKET, next.nextIndex());
        }
        return new EvaluationResult(INVALID_CHARACTER, next.nextIndex());
    }
}
