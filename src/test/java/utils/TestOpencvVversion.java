package utils;

import org.junit.Test;
import org.opencv.core.Core;

/**
 * For Windows
 * <p>
 * -Djava.library.path=C:\Other_IT\opencv-3.3.1\opencv\build\java\x64
 * <p>
 * mvn install:install-file -Dfile=${project.basedir}\local-maven-repo\opencv-331.jar -DgroupId=org.opencv -DartifactId=opencv.win10_64 -Dversion=3.3.1 -Dpackaging=jar
 * <p>
 * mvn assembly:assembly -Djava.library.path=C:\Other_IT\opencv-3.3.1\opencv\build\java\x64
 * -Dmaven.test.skip=true
 * <p>
 * For Linux Ubuntu
 * <p>
 * mvn install:install-file -Dfile=local-maven-repo/opencv-341.jar -DgroupId=org.opencv -DartifactId=opencv.win10_64 -Dversion=3.4.1 -Dpackaging=jar
 * <p>
 * -Djava.library.path=/usr/local/share/OpenCV/java
 */
public class TestOpencvVversion {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void main() {
        System.out.println(Core.getBuildInformation());
        System.out.println("Welcome to OpenCV " + Core.VERSION);
    }
}