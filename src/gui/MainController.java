package gui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private Button resetButton;
    @FXML
    private Slider speedSlider;
    @FXML
    private ListView<String> moveHistoryListView;
    @FXML
    private Label currentMoveLabel;
    @FXML
    private ProgressBar solutionProgress;
    @FXML
    private Button stepBackButton;
    @FXML
    private Button stepForwardButton;
    @FXML
    private Button saveSolutionButton;

    private Board initialBoard;
    private SolutionPath solution;
    private List<State> statePath;
    private int currentStateIndex = 0;
    private Map<Character, Rectangle> pieceRectangles = new HashMap<>();
    private SequentialTransition animation;

    private final Color primaryPieceColor = Color.RED;
    private final Color[] pieceColors = {
            Color.BLUE, Color.ORANGE, Color.PURPLE, Color.BROWN, Color.CYAN,
            Color.PINK, Color.DARKBLUE, Color.DARKGREEN, Color.DARKORANGE, Color.YELLOW,
            Color.VIOLET, Color.LIGHTBLUE, Color.LIGHTCORAL, Color.LIGHTSEAGREEN, Color.MAGENTA,
            Color.GOLD, Color.GRAY, Color.LIGHTPINK, Color.SIENNA, Color.OLIVE,
            Color.NAVY, Color.PLUM, Color.TEAL, Color.CHOCOLATE
    };

    private int CELL_SIZE = 40;
    private final int BOARD_MARGIN = 60;

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
                animation.setRate(newVal.doubleValue() / 5.0);
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
        statusLabel.setText("Waiting");
        nodesVisitedLabel.setText("0");
        stepsLabel.setText("0");
        executionTimeLabel.setText("0 ms");
        playButton.setDisable(true);
        stepBackButton.setDisable(true);
        stepForwardButton.setDisable(true);
        resetButton.setDisable(true);
        solutionProgress.setProgress(0);
        moveHistoryListView.getItems().clear();
        currentMoveLabel.setText("No move");
        solution = null;
        statePath = null;
        currentStateIndex = 0;
        pieceRectangles.clear();
        boardPane.getChildren().clear();
    }

    private void drawInitialBoard() {
        if (initialBoard == null)
            return;

        int rows = initialBoard.getRows();
        int cols = initialBoard.getCols();

        double availableWidth = boardContainer.getWidth() - 2 * BOARD_MARGIN;
        double availableHeight = boardContainer.getHeight() - 2 * BOARD_MARGIN;

        // Jika belum ditampilkan, panggil setelah scene ditampilkan
        if (availableWidth <= 0 || availableHeight <= 0) {
            Platform.runLater(this::drawInitialBoard);
            return;
        }

        // CELL_SIZE = (int) Math.min(availableWidth / cols, availableHeight / rows);

        double boardWidth = cols * CELL_SIZE + 2 * BOARD_MARGIN;
        double boardHeight = rows * CELL_SIZE + 2 * BOARD_MARGIN;

        boardPane.setPrefSize(boardWidth, boardHeight);

        for (int i = 0; i <= rows; i++) {
            Line hLine = new Line(BOARD_MARGIN, BOARD_MARGIN + i * CELL_SIZE,
                    BOARD_MARGIN + cols * CELL_SIZE, BOARD_MARGIN + i * CELL_SIZE);
            hLine.setStroke(Color.GRAY);
            boardPane.getChildren().add(hLine);
        }

        for (int i = 0; i <= cols; i++) {
            Line vLine = new Line(BOARD_MARGIN + i * CELL_SIZE, BOARD_MARGIN,
                    BOARD_MARGIN + i * CELL_SIZE, BOARD_MARGIN + rows * CELL_SIZE);
            vLine.setStroke(Color.GRAY);
            boardPane.getChildren().add(vLine);
        }

        // Gambar exit
        Position exitPos = initialBoard.getExitPosition();
        double exitX = BOARD_MARGIN + exitPos.getCol() * CELL_SIZE;
        double exitY = BOARD_MARGIN + exitPos.getRow() * CELL_SIZE;

        if (exitPos.getCol() < 0)
            exitX = BOARD_MARGIN - CELL_SIZE;
        if (exitPos.getCol() >= cols)
            exitX = BOARD_MARGIN + cols * CELL_SIZE;
        if (exitPos.getRow() < 0)
            exitY = BOARD_MARGIN - CELL_SIZE;
        if (exitPos.getRow() >= rows)
            exitY = BOARD_MARGIN + rows * CELL_SIZE;

        Rectangle exitRect = new Rectangle(exitX, exitY, CELL_SIZE, CELL_SIZE);
        exitRect.setFill(Color.LIGHTGREEN);
        exitRect.setOpacity(0.5);
        exitRect.setStroke(Color.GREEN);
        exitRect.setStrokeWidth(1.5);
        boardPane.getChildren().add(exitRect);

        Label exitLabel = new Label("Exit");
        exitLabel.setTextFill(Color.DARKGREEN);
        exitLabel.setStyle("-fx-font-weight: normal;");
        exitLabel.setLayoutX(exitX + CELL_SIZE / 4.0);
        exitLabel.setLayoutY(exitY + CELL_SIZE / 4.0);

        boardPane.getChildren().add(exitLabel);

        Map<Character, Piece> pieces = initialBoard.getPieces();
        int colorIndex = 0;

        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            char pieceId = entry.getKey();
            Piece piece = entry.getValue();

            Rectangle rect = createPieceRectangle(piece, pieceColors[colorIndex % pieceColors.length], pieceId);

            Color pieceColor = piece.isPrimary() ? primaryPieceColor : pieceColors[colorIndex % pieceColors.length];
            if (!piece.isPrimary())
                colorIndex++;

            rect.setFill(pieceColor);
            pieceRectangles.put(pieceId, rect);
            boardPane.getChildren().add(rect);
        }

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(boardPane.widthProperty());
        clip.heightProperty().bind(boardPane.heightProperty());
        boardPane.setClip(clip);
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
        rect.setStrokeWidth(0.5);
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
                    statePath = solution.getPath();
                    populateMoveHistory();

                    playButton.setDisable(false);
                    saveSolutionButton.setDisable(false);
                    stepBackButton.setDisable(false);
                    stepForwardButton.setDisable(false);
                    resetButton.setDisable(false);
                    handlePlay();
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

        animation = new SequentialTransition();
        double speedFactor = speedSlider.getValue() / 5.0;
        Duration stepDuration = Duration.seconds(0.5 / speedFactor); // 0.5 detik tiap langkah

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

                    KeyValue kvX = new KeyValue(pieceRect.xProperty(), newX);
                    KeyValue kvY = new KeyValue(pieceRect.yProperty(), newY);
                    KeyFrame kf = new KeyFrame(stepDuration, kvX, kvY);

                    Timeline timeline = new Timeline(kf);
                    timeline.setOnFinished(e -> {
                        currentStateIndex = index;
                        solutionProgress.setProgress((double) index / (statePath.size() - 1));
                        String moveText = "Move " + index + ": Piece " + pieceId + " " +
                                lastMove.getDirectionString() + " " + lastMove.getDistance() + " step(s)";
                        currentMoveLabel.setText(moveText);
                        moveHistoryListView.getSelectionModel().select(index);
                        moveHistoryListView.scrollTo(index);
                    });

                    animation.getChildren().add(timeline);

                    // Tambahkan jeda antar langkah (opsional tapi kelihatan halus)
                    animation.getChildren().add(new PauseTransition(Duration.millis(100)));
                }
            }
        }

        animation.setOnFinished(e -> {
            playButton.setText("Play");

            Platform.runLater(() -> {
                statusLabel.setText("Solved!");
                nodesVisitedLabel.setText(String.valueOf(solution.getNodesVisited()));
                stepsLabel.setText(String.valueOf(solution.getStepCount()));
                executionTimeLabel.setText(solution.getExecutionTimeMs() + " ms");

                showSuccessDialog(solution.getStepCount(), solution.getExecutionTimeMs());
            });
        });

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

    private void showSuccessDialog(int steps, long timeMs) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Success");

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 36px;");

        Label title = new Label("Puzzle Solved!");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label body = new Label("Solved in " + steps + " steps.\nExecution Time: " + timeMs + " ms.");
        body.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");

        Button okButton = new Button("OK");
        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> dialogStage.close());

        VBox content = new VBox(15, icon, title, body, okButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        Scene scene = new Scene(content);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    @FXML
    private void handleStepForward() {
        if (solution == null || !solution.isSolutionFound())
            return;

        if (animation != null) {
            animation.stop();
            playButton.setText("Play");
        }

        if (currentStateIndex < statePath.size() - 1) {
            currentStateIndex++;
            updateBoardDisplay(statePath.get(currentStateIndex).getBoard());

            solutionProgress.setProgress((double) currentStateIndex / (statePath.size() - 1));

            Move lastMove = getLastMove(currentStateIndex);
            currentMoveLabel.setText("Move " + currentStateIndex + ": Piece " +
                    lastMove.getPieceId() + " " + lastMove.getDirectionString() + " " + lastMove.getDistance()
                    + " step(s)");

            moveHistoryListView.getSelectionModel().select(currentStateIndex);
            moveHistoryListView.scrollTo(currentStateIndex);
        }
    }

    @FXML
    private void handleStepBack() {
        if (solution == null || !solution.isSolutionFound())
            return;

        if (animation != null) {
            animation.stop();
            playButton.setText("Play");
        }

        if (currentStateIndex > 0) {
            currentStateIndex--;
            updateBoardDisplay(statePath.get(currentStateIndex).getBoard());

            solutionProgress.setProgress((double) currentStateIndex / (statePath.size() - 1));

            if (currentStateIndex > 0) {
                Move lastMove = getLastMove(currentStateIndex);
                currentMoveLabel.setText("Move " + currentStateIndex + ": Piece " +
                        lastMove.getPieceId() + " " + lastMove.getDirectionString() + " " + lastMove.getDistance()
                        + " step(s)");
            } else {
                currentMoveLabel.setText("Initial state");
            }

            moveHistoryListView.getSelectionModel().select(currentStateIndex);
            moveHistoryListView.scrollTo(currentStateIndex);
        }
    }

    private Move getLastMove(int index) {
        List<Move> moves = statePath.get(index).getMoveHistory();
        return moves.isEmpty() ? null : moves.get(moves.size() - 1);
    }
    
    @FXML
    private void handleSaveSolution() {
        if (solution == null || !solution.isSolutionFound())
            return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Solution As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("solution.txt");

        File file = fileChooser.showSaveDialog(boardPane.getScene().getWindow());

        if (file != null) {
            try {
                // Misal kamu pakai UCS, sesuaikan dengan algorithm yang aktif
                String algoName = algorithmComboBox.getValue();
                FileHandler.writeSolutionToFile(
                        file.getAbsolutePath(),
                        initialBoard,
                        solution.getPath(),
                        algoName,
                        solution.getNodesVisited(),
                        solution.getExecutionTimeMs());
                showInfo("Saved", "Solution saved to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Save Failed", "Could not save file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
}