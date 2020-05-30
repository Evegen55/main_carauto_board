module main.carauto.board.app {
    // allow only module javafx.graphics access module in app via qualified export
    exports app to javafx.graphics;

    requires transitive javafx.graphics;
    requires transitive javafx.media;
    requires transitive javafx.deploy;
    requires transitive javafx.web;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.swing;
    requires slf4j.api;
    requires GMapsFX;
    requires jdk.jsobject;
    requires opencv.javawraper;
    requires org.apache.commons.lang3;
    requires org.apache.commons.configuration2;
    requires commons.beanutils;
    requires commons.io;
    requires webcam.capture;
    requires javax.json;
}