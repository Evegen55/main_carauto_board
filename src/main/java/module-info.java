module main.carauto.board {
    requires java.base;
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


    exports app;
    exports controllers.imageViewer;
    exports controllers.mapListeners;
    exports controllers.openvc;
    exports controllers.phone;
    exports controllers.settings;
    exports entities;
    exports utils;
}