package gui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

// import
import model.*;
import algorithm.*;
import heuristic.*;
import util.*;

public class MainController {

    @FXML
    private Button loadButton;
    @FXML
    private Label filePathLabel;
    @FXML
    private ComboBox<String> algorithmComboBox;
    @FXML
    private Label heuristicLabel;
    @FXML
    private ComboBox<String> heuristicComboBox;
    @FXML
    private Button solveButton;
    @FXML
    private StackPane boardContainer;
    @FXML
    private Pane boardPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Label nodesVisitedLabel;
    @FXML
    private Label stepsLabel;
    @FXML
    private Label executionTimeLabel;
    @FXML
    private Button playButton;
    @FXML
    private Button stepButton;
    @FXML
    private Button resetButton;
    @FXML
    private Slider speedSlider;
    @FXML
    private ListView<String> moveHistoryListView;
    @FXML
    private Label currentMoveLabel;
    @FXML
    private ProgressBar solutionProgress;

    private Board initialBoard;
    private SolutionPath solution;
    private List<State> statePath;
    private int currentStateIndex = 0;
    private Map<Character, Rectangle> pieceRectangles = new HashMap<>();
    private Timeline animation;
    private final Color primaryPieceColor = Color.RED;
    private final Color[] pieceColors = {
            Color.BLUE, Color.ORANGE, Color.PURPLE, Color.BROWN, Color.CYAN,
            Color.PINK, Color.DARKBLUE, Color.DARKGREEN, Color.DARKORANGE, Color.YELLOW,
            Color.VIOLET, Color.LIGHTBLUE, Color.LIGHTCORAL, Color.LIGHTSEAGREEN, Color.MAGENTA,
            Color.GOLD, Color.GRAY, Color.LIGHTPINK, Color.SIENNA, Color.OLIVE,
            Color.NAVY, Color.PLUM, Color.TEAL, Color.CHOCOLATE
    };
    private final int CELL_SIZE = 60;
    private final int BOARD_MARGIN = 20;

    @FXML
    public void initialize() {
        algorithmComboBox.setItems(FXCollections.observableArrayList(
                "Uniform Cost Search", "Greedy Best-First Search", "A* Search"));
        algorithmComboBox.setValue("Uniform Cost Search");
        algorithmComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean showHeuristic = newVal.contains("Greedy") || newVal.contains("A*");
            heuristicLabel.setVisible(showHeuristic);
            heuristicLabel.setManaged(showHeuristic);
            heuristicComboBox.setVisible(showHeuristic);
            heuristicComboBox.setManaged(showHeuristic);
        });

        heuristicComboBox.setItems(FXCollections.observableArrayList(
                "Manhattan Distance", "Blocking Heuristic"));
        heuristicComboBox.setValue("Manhattan Distance");

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (animation != null) {
                double rate = newVal.doubleValue() / 5.0;
                animation.setRate(rate);
            }
        });
    }

    @FXML
    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Rush Hour Configuration File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                initialBoard = FileHandler.readInputFile(selectedFile.getAbsolutePath());
                filePathLabel.setText(selectedFile.getName());
                solveButton.setDisable(false);
                resetUI();
                drawInitialBoard();
            } catch (Exception e) {
                showAlert("Error loading file", "Could not load the configuration file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void resetUI() {
        // Reset UI components
        statusLabel.setText("Waiting");
        nodesVisitedLabel.setText("0");
        stepsLabel.setText("0");
        executionTimeLabel.setText("0 ms");
        playButton.setDisable(true);
        stepButton.setDisable(true);
        resetButton.setDisable(true);
        solutionProgress.setProgress(0);
        moveHistoryListView.getItems().clear();
        currentMoveLabel.setText("No move");

        // Clear current solution
        solution = null;
        statePath = null;
        currentStateIndex = 0;

        // Clear the board visualization
        pieceRectangles.clear();
        boardPane.getChildren().clear();
    }

    private void drawInitialBoard() {
        if (initialBoard == null)
            return;

        int rows = initialBoard.getRows();
        int cols = initialBoard.getCols();

        // Set the board pane size
        boardPane.setPrefSize(cols * CELL_SIZE + 2 * BOARD_MARGIN, rows * CELL_SIZE + 2 * BOARD_MARGIN);

        // Draw grid lines
        for (int i = 0; i <= rows; i++) {
            javafx.scene.shape.Line horizontalLine = new javafx.scene.shape.Line(
                    BOARD_MARGIN, BOARD_MARGIN + i * CELL_SIZE,
                    BOARD_MARGIN + cols * CELL_SIZE, BOARD_MARGIN + i * CELL_SIZE);
            horizontalLine.setStroke(Color.GRAY);
            boardPane.getChildren().add(horizontalLine);
        }

        for (int i = 0; i <= cols; i++) {
            javafx.scene.shape.Line verticalLine = new javafx.scene.shape.Line(
                    BOARD_MARGIN + i * CELL_SIZE, BOARD_MARGIN,
                    BOARD_MARGIN + i * CELL_SIZE, BOARD_MARGIN + rows * CELL_SIZE);
            verticalLine.setStroke(Color.GRAY);
            boardPane.getChildren().add(verticalLine);
        }

        // Draw exit position
        Position exitPos = initialBoard.getExitPosition();
        Rectangle exitRect = new Rectangle(
                BOARD_MARGIN + exitPos.getCol() * CELL_SIZE,
                BOARD_MARGIN + exitPos.getRow() * CELL_SIZE,
                CELL_SIZE, CELL_SIZE);
        exitRect.setFill(Color.LIGHTGREEN);
        exitRect.setOpacity(0.5);
        boardPane.getChildren().add(exitRect);

        // Draw pieces
        Map<Character, Piece> pieces = initialBoard.getPieces();
        int colorIndex = 0;

        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            char pieceId = entry.getKey();
            Piece piece = entry.getValue();

            Rectangle rect = createPieceRectangle(piece, pieceColors[colorIndex % pieceColors.length], pieceId);

            // Set color - primary piece is always red
            Color pieceColor = piece.isPrimary() ? primaryPieceColor : pieceColors[colorIndex % pieceColors.length];
            if (!piece.isPrimary())
                colorIndex++;
            rect.setFill(pieceColor);

            pieceRectangles.put(pieceId, rect);
            boardPane.getChildren().add(rect);
        }
    }

    private Rectangle createPieceRectangle(Piece piece, Color color, char id) {
        Position anchor = piece.getAnchor();
        int size = piece.getSize();
        boolean isHorizontal = piece.isHorizontal();

        Rectangle rect = new Rectangle(
                BOARD_MARGIN + anchor.getCol() * CELL_SIZE,
                BOARD_MARGIN + anchor.getRow() * CELL_SIZE,
                isHorizontal ? size * CELL_SIZE : CELL_SIZE,
                isHorizontal ? CELL_SIZE : size * CELL_SIZE);

        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        rect.setFill(color);
        rect.setId("piece-" + id);
        return rect;
    }

    @FXML
    private void handleSolve() {
        if (initialBoard == null)
            return;

        resetUI();
        drawInitialBoard();

        // Disable solve button during calculation
        solveButton.setDisable(true);
        statusLabel.setText("Solving...");

        // Run solver in background thread
        new Thread(() -> {
            Algorithm algorithm = createSelectedAlgorithm();
            solution = algorithm.findSolution(initialBoard);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                solveButton.setDisable(false);

                if (solution != null && solution.isSolutionFound()) {
                    statusLabel.setText("Solved!");
                    nodesVisitedLabel.setText(String.valueOf(solution.getNodesVisited()));
                    stepsLabel.setText(String.valueOf(solution.getStepCount()));
                    executionTimeLabel.setText(solution.getExecutionTimeMs() + " ms");

                    statePath = solution.getPath();
                    populateMoveHistory();

                    playButton.setDisable(false);
                    stepButton.setDisable(false);
                    resetButton.setDisable(false);
                } else {
                    statusLabel.setText("No Solution Found");
                }
            });
        }).start();
    }

    private Algorithm createSelectedAlgorithm() {
        String selectedAlgorithm = algorithmComboBox.getValue();

        if (selectedAlgorithm.equals("Uniform Cost Search")) {
            return new UCS();
        } else {
            Heuristic heuristic;
            if (heuristicComboBox.getValue().equals("Manhattan Distance")) {
                heuristic = new DummyHeuristic();
            } else {
                heuristic = new DummyHeuristic();
                // heuristic = new BlockingHeuristic();
            }

            if (selectedAlgorithm.equals("Greedy Best-First Search")) {
                return new GreedyBestFirstSearch(heuristic);
            } else { // A* Search
                // return new AStarSearch(heuristic);
                return new GreedyBestFirstSearch(heuristic);
            }
        }
    }

    private void populateMoveHistory() {
        if (solution == null || !solution.isSolutionFound())
            return;

        ObservableList<String> moveItems = FXCollections.observableArrayList();
        moveItems.add("0. Initial State");

        List<Move> allMoves = new java.util.ArrayList<>();
        for (int i = 1; i < statePath.size(); i++) {
            List<Move> moves = statePath.get(i).getMoveHistory();
            if (!moves.isEmpty()) {
                Move lastMove = moves.get(moves.size() - 1);
                if (!allMoves.contains(lastMove)) {
                    allMoves.add(lastMove);
                    moveItems.add(i + ". Move piece " + lastMove.getPieceId() + " " +
                            lastMove.getDirectionString() + " " + lastMove.getDistance() + " step(s)");
                }
            }
        }

        moveHistoryListView.setItems(moveItems);
    }

    @FXML
    private void handlePlay() {
        if (solution == null || !solution.isSolutionFound())
            return;

        if (animation != null && animation.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            animation.pause();
            playButton.setText("Play");
        } else {
            if (currentStateIndex >= statePath.size() - 1) {
                currentStateIndex = 0;
                updateBoardDisplay(statePath.get(currentStateIndex).getBoard());
            }

            startAnimation();
            playButton.setText("Pause");
        }
    }

    private void startAnimation() {
        if (statePath == null || currentStateIndex >= statePath.size() - 1)
            return;

        animation = new Timeline();
        double speedFactor = speedSlider.getValue() / 5.0;

        for (int i = currentStateIndex + 1; i < statePath.size(); i++) {
            final int index = i;
            State state = statePath.get(index);
            Board board = state.getBoard();

            List<Move> moves = state.getMoveHistory();
            if (!moves.isEmpty()) {
                Move lastMove = moves.get(moves.size() - 1);
                char pieceId = lastMove.getPieceId();

                if (pieceRectangles.containsKey(pieceId)) {
                    Rectangle pieceRect = pieceRectangles.get(pieceId);
                    Piece piece = board.getPieces().get(pieceId);
                    Position newPos = piece.getAnchor();

                    double newX = BOARD_MARGIN + newPos.getCol() * CELL_SIZE;
                    double newY = BOARD_MARGIN + newPos.getRow() * CELL_SIZE;

                    KeyFrame keyFrame = new KeyFrame(
                            Duration.seconds(i - currentStateIndex),
                            event -> {
                                currentStateIndex = index;
                                solutionProgress.setProgress((double) index / (statePath.size() - 1));

                                String moveText = "Move " + (index) + ": Piece " + pieceId + " " +
                                        lastMove.getDirectionString() + " " + lastMove.getDistance() + " step(s)";
                                currentMoveLabel.setText(moveText);

                                moveHistoryListView.getSelectionModel().select(index);
                                moveHistoryListView.scrollTo(index);

                                // Check if solution is complete
                                if (index == statePath.size() - 1) {
                                    playButton.setText("Play");
                                }
                            },
                            new KeyValue(pieceRect.xProperty(), newX),
                            new KeyValue(pieceRect.yProperty(), newY));

                    animation.getKeyFrames().add(keyFrame);
                }
            }
        }

        animation.setRate(speedFactor);
        animation.play();
    }

    @FXML
    private void handleStep() {
        if (solution == null || !solution.isSolutionFound())
            return;

        // Stop any running animation
        if (animation != null) {
            animation.stop();
            playButton.setText("Play");
        }

        if (currentStateIndex < statePath.size() - 1) {
            currentStateIndex++;
            State state = statePath.get(currentStateIndex);
            updateBoardDisplay(state.getBoard());

            solutionProgress.setProgress((double) currentStateIndex / (statePath.size() - 1));

            // Update move info
            List<Move> moves = state.getMoveHistory();
            if (!moves.isEmpty()) {
                Move lastMove = moves.get(moves.size() - 1);
                String moveText = "Move " + currentStateIndex + ": Piece " + lastMove.getPieceId() + " " +
                        lastMove.getDirectionString() + " " + lastMove.getDistance() + " step(s)";
                currentMoveLabel.setText(moveText);

                moveHistoryListView.getSelectionModel().select(currentStateIndex);
                moveHistoryListView.scrollTo(currentStateIndex);
            }
        }
    }

    private void updateBoardDisplay(Board board) {
        Map<Character, Piece> pieces = board.getPieces();

        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            char pieceId = entry.getKey();
            Piece piece = entry.getValue();
            Position anchor = piece.getAnchor();

            if (pieceRectangles.containsKey(pieceId)) {
                Rectangle rect = pieceRectangles.get(pieceId);
                rect.setX(BOARD_MARGIN + anchor.getCol() * CELL_SIZE);
                rect.setY(BOARD_MARGIN + anchor.getRow() * CELL_SIZE);
            }
        }
    }

    @FXML
    private void handleReset() {
        if (solution == null)
            return;

        // Stop any running animation
        if (animation != null) {
            animation.stop();
            playButton.setText("Play");
        }

        // Reset to initial state
        currentStateIndex = 0;
        updateBoardDisplay(statePath.get(0).getBoard());
        solutionProgress.setProgress(0);
        currentMoveLabel.setText("Initial state");
        moveHistoryListView.getSelectionModel().select(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}