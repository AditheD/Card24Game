package card24game.card24game;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Deals cards, verifies expressions, and searches for solutions.
 */
public class Card24Controller {

    private static final double EPSILON = 0.000000001;

    @FXML
    private ImageView card1;

    @FXML
    private ImageView card2;

    @FXML
    private ImageView card3;

    @FXML
    private ImageView card4;

    @FXML
    private TextField expressionField;

    @FXML
    private TextField solutionField;

    @FXML
    private Label valuesLabel;

    private final List<Card> deck = new ArrayList<>();
    private final List<Integer> currentValues = new ArrayList<>();

    /**
     * Runs automatically after the FXML controls are loaded.
     */
    @FXML
    private void initialize() {
        String[] suits = {
                "clubs", "diamonds", "hearts", "spades"
        };

        for (String suit : suits) {
            for (int value = 1; value <= 13; value++) {
                String rank = switch (value) {
                    case 1 -> "ace";
                    case 11 -> "jack";
                    case 12 -> "queen";
                    case 13 -> "king";
                    default -> String.valueOf(value);
                };

                String fileName = rank + "_of_" + suit + ".png";
                deck.add(new Card(value, fileName));
            }
        }

        refreshCards();
    }

    /**
     * Shuffles a 52-card deck and deals four distinct physical cards.
     * Different cards may have the same numeric value.
     */
    @FXML
    private void refreshCards() {
        Collections.shuffle(deck);
        currentValues.clear();

        ImageView[] imageViews = {
                card1, card2, card3, card4
        };

        for (int i = 0; i < imageViews.length; i++) {
            Card card = deck.get(i);
            currentValues.add(card.value());

            URL imageUrl = getClass().getResource("/png/" + card.fileName());

            if (imageUrl == null) {
                throw new IllegalStateException("Missing card image: " + card.fileName() + ". Check the resources/cards folder.");
            }

            imageViews[i].setImage(new Image(imageUrl.toExternalForm()));

            imageViews[i].setAccessibleText(card.fileName().replace("_", " "));
        }

        valuesLabel.setText("Card values: " + currentValues);

        expressionField.clear();
        solutionField.clear();
    }

    /**
     * Checks that every displayed value is used exactly once,
     * including repeated values, and that the answer equals 24.
     */
    @FXML
    private void verifyExpression() {
        try {
            ExpressionEvaluator evaluator = new ExpressionEvaluator(expressionField.getText());

            double result = evaluator.evaluate();

            List<Integer> enteredNumbers = evaluator.getUsedNumbers();
            List<Integer> requiredNumbers = new ArrayList<>(currentValues);

            Collections.sort(enteredNumbers);
            Collections.sort(requiredNumbers);

            if (!enteredNumbers.equals(requiredNumbers)) {
                showDialog(Alert.AlertType.ERROR,
                        "Incorrect card values",
                        "Use each displayed card value exactly once.\n\n"
                                + "Required values: " + currentValues + "\n"
                                + "Values you used: "
                                + evaluator.getUsedNumbers());
                return;
            }

            if (Math.abs(result - 24.0) < EPSILON) {
                showDialog(Alert.AlertType.INFORMATION,
                        "Correct!",
                        "Great job! Your expression evaluates to 24."
                );
            } else {
                showDialog(Alert.AlertType.WARNING,
                        "Try again",
                        String.format(
                                Locale.US,
                                "Your expression evaluates to %.6f, not 24.",
                                result
                        )
                );
            }

        } catch (IllegalArgumentException exception) {
            showDialog(Alert.AlertType.ERROR,
                    "Invalid expression",
                    exception.getMessage()
            );
        }
    }

    /**
     * Finds a solution for the current hand, when one exists.
     */
    @FXML
    private void findSolution() {
        List<Calculation> numbers = new ArrayList<>();

        for (int value : currentValues) {
            numbers.add(new Calculation(value, String.valueOf(value)));
        }

        String solution = solve(numbers);

        if (solution == null) {
            solutionField.setText("No solution");

            showDialog(Alert.AlertType.INFORMATION,
                    "No solution",
                    "This hand cannot make 24 with the allowed operations.\n"
                            + "Click Refresh to deal another hand."
            );
        } else {
            solutionField.setText(solution);
        }
    }

    /**
     * Recursively combines pairs of numbers.
     * Both orders are tried for subtraction and division.
     * Combining pairs covers different parenthesis arrangements.
     */
    private String solve(List<Calculation> numbers) {
        if (numbers.size() == 1) {
            return Math.abs(numbers.get(0).value() - 24.0) < EPSILON ? numbers.get(0).expression() : null;
        }

        for (int i = 0; i < numbers.size(); i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                Calculation a = numbers.get(i);
                Calculation b = numbers.get(j);

                List<Calculation> remaining = new ArrayList<>();

                for (int k = 0; k < numbers.size(); k++) {
                    if (k != i && k != j) {
                        remaining.add(numbers.get(k));
                    }
                }

                List<Calculation> combinations = new ArrayList<>();

                combinations.add(combine(a, b, '+'));
                combinations.add(combine(a, b, '-'));
                combinations.add(combine(b, a, '-'));
                combinations.add(combine(a, b, '*'));

                if (b.value() != 0.0) {
                    combinations.add(combine(a, b, '/'));
                }

                if (a.value() != 0.0) {
                    combinations.add(combine(b, a, '/'));
                }

                for (Calculation combination : combinations) {
                    remaining.add(combination);

                    String solution = solve(remaining);

                    if (solution != null) {
                        return solution;
                    }

                    remaining.remove(remaining.size() - 1);
                }
            }
        }

        return null;
    }

    private Calculation combine(Calculation a, Calculation b, char operator) {

        double value = switch (operator) {
            case '+' -> a.value() + b.value();
            case '-' -> a.value() - b.value();
            case '*' -> a.value() * b.value();
            case '/' -> a.value() / b.value();
            default -> throw new IllegalArgumentException("Unsupported operator.");
        };

        String expression = "(" + a.expression() + operator + b.expression() + ")";

        return new Calculation(value, expression);
    }

    private void showDialog(Alert.AlertType type, String title, String message) {

        Alert alert = new Alert(type);
        alert.initOwner(expressionField.getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private record Card(int value, String fileName) {
    }

    private record Calculation(double value, String expression) {
    }

}
