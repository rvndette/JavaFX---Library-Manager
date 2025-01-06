module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.mail;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports student;
    opens student to javafx.fxml;
    exports email;
    opens email to javafx.fxml;
    exports database;
    opens database to javafx.fxml;
    exports adminstudent;
    opens adminstudent to javafx.fxml;
    exports adminbook;
    opens adminbook to javafx.fxml;
    exports visitor;
    opens visitor to javafx.fxml;
}
