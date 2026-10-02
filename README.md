# Card24Game

A JavaFX game where the player uses four randomly selected
playing cards to create an arithmetic expression equal to 24.

## Rules
- Use each displayed card value exactly once.
- Ace = 1, Jack = 11, Queen = 12, King = 13.
- Allowed operations: +, -, *, /, and parentheses.

## Controls
- Verify checks the card values and expression result.
- Refresh deals four new cards and clears the text fields.
- Find a Solution displays a solution if one exists.

## Running
Open the project in IntelliJ, load the Maven dependencies,
and run Card24Application.

## Project Structure
- Card24Application.java starts the application.
- Card24Controller.java handles cards and button actions.
- ExpressionEvaluator.java parses arithmetic expressions.
- game.fxml defines the interface.
- style.css styles the interface.