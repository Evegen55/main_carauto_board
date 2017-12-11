package utils;

import org.junit.Test;
import org.opencv.core.Core;

public class TestOpencvVversion {

    //-Djava.library.path=C:\Other_IT\opencv-3.3.1\opencv\build\java\x64

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void main() {
        System.out.println(Core.getBuildInformation());
        System.out.println("Welcome to OpenCV " + Core.VERSION);
    }
}