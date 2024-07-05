module gui_module {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.base;
    requires businesslogic_module;
    requires java.sql;
    requires spring.security.crypto;
    requires org.apache.pdfbox;

    opens gui to javafx.fxml;
    exports gui;
}