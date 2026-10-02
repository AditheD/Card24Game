package card24game.card24game;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates arithmetic expressions without executing scripts.
 * <p>
 * Supported operations: +, -, *, / and parentheses.
 * Only positive integer card values are allowed as number literals.
 */

public class ExpressionEvaluator {
    private final String expression;
    private final List<Integer> usedNumbers = new ArrayList<>();
    private int position;

    public ExpressionEvaluator(String expression) {
        this.expression = expression == null ? "" : expression;
    }

    /**
     * Parses the entire expression using standard operator precedence.
     */
    public double evaluate() {
        if (expression.isBlank()) {
            throw new IllegalArgumentException("Please enter an expression.");
        }

        // Prevent excessively long input and deeply nested parentheses.
        if (expression.length() > 200) {
            throw new IllegalArgumentException("The expression is too long.");
        }

        position = 0;
        usedNumbers.clear();

        double result = parseExpression();
        skipSpaces();

        if (position != expression.length()) {
            throw new IllegalArgumentException("Unexpected character at position " + (position + 1) + ". Use only numbers, +, -, *, /, and parentheses.");
        }

        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("The expression produces an invalid result.");
        }

        return result;
    }

    public List<Integer> getUsedNumbers() {
        return new ArrayList<>(usedNumbers);
    }

    // Addition and subtraction have lower precedence.
    private double parseExpression() {
        double value = parseTerm();

        while (true) {
            if (match('+')) {
                value += parseTerm();
            } else if (match('-')) {
                value -= parseTerm();
            } else {
                return value;
            }
        }
    }

    // Multiplication and division are evaluated before + and -.
    private double parseTerm() {
        double value = parseFactor();

        while (true) {
            if (match('*')) {
                value *= parseFactor();
            } else if (match('/')) {
                double divisor = parseFactor();

                if (divisor == 0.0) {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }

                value /= divisor;
            } else {
                return value;
            }
        }
    }

    // A factor is either a card number or a parenthesized expression.
    private double parseFactor() {
        skipSpaces();

        if (match('(')) {
            double value = parseExpression();

            if (!match(')')) {
                throw new IllegalArgumentException("A closing parenthesis is missing.");
            }

            return value;
        }

        int start = position;

        while (position < expression.length()
                && expression.charAt(position) >= '0'
                && expression.charAt(position) <= '9') {
            position++;
        }

        if (start == position) {
            throw new IllegalArgumentException("Expected a card number or opening parenthesis at position " + (position + 1) + ".");
        }

        String numberText = expression.substring(start, position);

        if (numberText.length() > 1 && numberText.startsWith("0")) {
            throw new IllegalArgumentException("Write card numbers without leading zeros.");
        }

        int number;

        try {
            number = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("The number entered is too large.");
        }

        if (number < 1 || number > 13) {
            throw new IllegalArgumentException("Card values must be between 1 and 13.");
        }

        usedNumbers.add(number);
        return number;
    }

    private boolean match(char expected) {
        skipSpaces();

        if (position < expression.length()
                && expression.charAt(position) == expected) {
            position++;
            return true;
        }

        return false;
    }

    private void skipSpaces() {
        while (position < expression.length() && Character.isWhitespace(expression.charAt(position))) {
            position++;
        }
    }
}

