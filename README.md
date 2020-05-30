# Multimedia panel for a car (prototype)
## Here is the sketch for car panel. 

Thanks for this guys very much:
- [Rob Terpilowski](https://github.com/rterp) with his [Google maps api for JavaFX](https://github.com/rterp/GMapsFX) and
- [Bartosz Firyn](https://github.com/sarxos) with his [webcam library](https://github.com/sarxos/webcam-capture).
- [Luigi Derussis](https://github.com/luigidr) with his really cool and helpful [examples](https://github.com/opencv-java/getting-started) and [articles](http://opencv-java-tutorials.readthedocs.io/en/latest/03-first-javafx-application-with-opencv.html)


There will be the next attempt to create by using [Qt platform](https://www.qt.io/) to get something like [this](https://github.com/Evegen55/opencv_widget) or [more fresh project](https://github.com/Evegen55/automotive-sketch-qt) but this project is suitable to rapid prototyping the main idea.

Here is the screen capture for the main ideas:

**Maps (styled)**:

![**Maps styled with map_with_terrain_and_satellites.PNG**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/1_map_with_terrain_and_satellites.PNG)
![**Maps (styled with retro theme)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/2_map_retro_no_routes.PNG)
![**Maps (styled with gray theme)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/3_map_gray_no_routes.PNG)
![**Maps (styled with retro theme)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/4_map_retro_query_route.PNG)
![**Maps (styled with retro theme and routes)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/5_map_retro_query_route_done.PNG)
![**Maps (styled with default theme and old tabs)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/6_map_default_old_tabs.PNG)

**Audio panel**:

![**Audio panel**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/7_music.PNG)

**Browser**:

![**Browser**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/8_browser.PNG)

**Video panel with carcams**:

![**Video panel with carcams**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/9_0_video_and_cams.PNG)

**Photo viewer**:

![**Video panel with carcams**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/9_1_photoviewer.PNG)

**Phone tab**:

![**Phone tab**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/10_phone.PNG)

**Smart tab**:

Warn if OpenCV can't be loaded
![**OpenCV can't be loaded**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/11_warning_opencv.PNG)

Convert stream to a gray
![**Don't recognized gray**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_1_convert_to_gray.PNG)

Face recognition:
![**Recognize**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_2_face_recognition.PNG)

Write video stream to file:
![**Write video stream to file**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_3_writing_video.PNG)
![**Write video stream to file**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_4_writing_video.PNG)

Remove background:
![**Remove background**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_5_background_rem.PNG)
![**Remove background**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_6_background_rem_invert.PNG)

Edges detection:
![**Edge detection**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_7_edges.PNG)

Plates recognition:
![**Recognize plates**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/smart_8_opencv_recognize_plates.PNG)


**Build and run:**

Note: now application supports:

- Minimum required version of Java for APPLICATION is JDK 10
- OpenCV ver.3.4.1

We will use precompiled libs from our project (folder local-maven-repo)

or

from official web-site [OpenCV](https://opencv.org/releases.html)

or feel free to compile OpenCV from source code (as me).

In case of using from web-site, please, Download [OpenCV](https://opencv.org/releases.html) and unpack it to a `<path-to-installed-opencv>`

Note: now there is a conflict with came at video tab and smart tab. Choose only one way to get video stream from cams.

You also need to install java lib to a local maven repository by invoking next command:

for Windows:

`mvn install:install-file -Dfile=<path-to-installed-opencv>\build\java\windows\opencv-341.jar -DgroupId=org.opencv -DartifactId=javawraper -Dversion=3.4.1 -Dpackaging=jar`

or

`mvn install:install-file -Dfile=${project.basedir}\local-maven-repo\windows\opencv-341.jar -DgroupId=org.opencv -DartifactId=javawraper -Dversion=3.4.1 -Dpackaging=jar`

for Linux (invoke from project root folder):

`mvn install:install-file -Dfile=local-maven-repo/linux/opencv-341.jar -DgroupId=org.opencv -DartifactId=javawraper -Dversion=3.4.1 -Dpackaging=jar`

then assembly distributive with all JAVA dependencies:

`package assembly:single -Dmaven.test.skip=true -f pom.xml`

It won't contain native library so it still needs to pass it as JVM argument in order to run it:

`java -Djava.library.path=<path-to-installed-opencv>\<path-fith-native-library-folder> -jar <Path-to-your-jar>/main_carauto_board-1.1-SNAPSHOT-jar-with-dependencies.jar`

## Experiments

Now I'm starting playing around [hardware accelerating](https://youtu.be/ESrkDUqSf84) with next parameters:

    -Dprism.verbose=true    
    -Dprism.forceGPU=true
    -Dprism.order=es2,j2d
    -Dsun.java2d.opengl=true

It also good idea to place prebuilt native libraries into resourse folder and use it without passing as JVM argument

## License

Copyright (C) 2017 - 2018 [Evgenii Lartcev](https://github.com/Evegen55/) and contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
