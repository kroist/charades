package com.charades.client;

import com.charades.tools.ChatMessage;
import com.charades.tools.MyColor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class GameSceneFXMLController {

    @FXML public Canvas canvas;
    @FXML public TextArea gameChat;
    @FXML public TextField enterMessage;
    @FXML public ListView<Pair<String, Integer>> leaderBoard;
    @FXML public Button returnToMenuButton;
    @FXML public Button startGameButton;
    @FXML public Label gameID;
    @FXML public ColorPicker colorPicker;
    @FXML public Button brush;
    @FXML public Button eraser;
    @FXML public ListView<String> whaitingList;
    @FXML public Text gameTimer;
    @FXML public Text gameWord;

    @FXML public Label gameEndMessage;
    @FXML public Label hiddenWord;
    @FXML public Pane gameEndPanel;


    public Controller controller;
    @FXML public Button clearAllButton;

    @FXML
    public void returnToMenuHandler(MouseEvent mouseEvent) {
        controller.returnToMenu("");
    }

    @FXML
    public void startGameHandler(MouseEvent mouseEvent) {
        controller.startGameButton();
    }

    @FXML
    public void colorPickerHandler(ActionEvent actionEvent) {
        System.out.println("handler received new color");
        controller.setBrushColor(new MyColor(colorPicker.getValue()));
        //controller.setColor(new com.charades.tools.MyColor(colorPicker.getValue()));
        //controller.setLineWidth(3);
    }

    @FXML
    public void enterMessageHandler(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            controller.sendChatMessage(new ChatMessage(enterMessage.getText() + "\n"));
            enterMessage.clear();
        }
    }

    @FXML
    public void brushHandler(MouseEvent actionEven){
        controller.setIsBrash(true);
        //controller.();
    }

    @FXML
    public void eraserHandler(MouseEvent actionEvent) {
        controller.setIsBrash(false);
        //controller.setColor(new com.charades.tools.MyColor(Color.web("#f4f4f4")));
        //controller.setLineWidth(10);
    }

    public void closeRoundEndPanel(ActionEvent actionEvent) {
        gameEndPanel.setVisible(false);
    }

    public void clearAllHandler(MouseEvent mouseEvent) {
        controller.clearAllButton();
    }
}