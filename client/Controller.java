package crypt1c.client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller{
    private Connection ircServer = null;
    private Main main;
    @FXML
    private TextArea msgInput;
    @FXML
    private Button sendBtn;
    @FXML
    private TextField serverName;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField serverChannel;
    @FXML
    private Button connectBtn;
    @FXML
    private TextArea chatBox;
    @FXML
    private TextField nickField;
    @FXML
    private TextField userField;
    @FXML
    private Button joinBtn;
    @FXML
    private Button leaveBtn;
    @FXML
    private void initialize(){
        connectBtn.setOnAction(this::connectToServer);
        sendBtn.setOnAction(this::sendMsg);
        joinBtn.setOnAction(this::joinChan);
        leaveBtn.setOnAction(this::leaveChan);
    }
    public void addMsg(String msg){
        chatBox.appendText(msg + "\n");
    }
    public void setMain(Main main) {
        this.main = main;
    }

    public void connectToServer(ActionEvent actionEvent) {
        this.ircServer = new Connection(serverName.getText(),Integer.valueOf(serverPort.getText()),nickField.getText(),userField.getText());
        this.ircServer.start();
        this.ircServer.setViewController(this);
    }
    public void sendMsg(ActionEvent actionEvent){
        this.ircServer.sendMessage(msgInput.getText(),"test-channel");
        this.msgInput.clear();
    }
    public void joinChan(ActionEvent actionEvent){
        this.ircServer.joinChannel(serverChannel.getText());
        this.serverChannel.clear();
    }
    public void leaveChan(ActionEvent actionEvent){
        this.ircServer.leaveChannel(serverChannel.getText());
        this.serverChannel.clear();
    }
    public void stopConnection(){
        if(this.ircServer != null) {
            this.ircServer.close();
        }
    }
}