module com.example.chatclientserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chatclientserver to javafx.fxml;
    exports com.example.chatclientserver;
}