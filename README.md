# Multimedia panel for a car (prototype)
## Here is the sketch for car panel. 

Thanks for this guys very much:
- [Rob Terpilowski](https://github.com/rterp) with his [Google maps api for JavaFX](https://github.com/rterp/GMapsFX) and
- [Bartosz Firyn](https://github.com/sarxos) with his [webcam library](https://github.com/sarxos/webcam-capture).


There will be the next attempt to create by using [Qt platform](https://www.qt.io/) but this project is suitable to rapid prototyping the main idea.

Here is the screen capture for the main ideas:

**Maps (styled)**:

![**Maps (styled)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/1.PNG)

**Maps (styled by default)**:

![**Maps (styled by default)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/1.1.PNG)

**Maps (styled by retro)**:

![**Maps (styled by retro)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/retro1.png)
![**Maps (styled by retro)**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/retro2.png)

**Audio panel**:

![**Audio panel**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/2.PNG)

**Browser**:

![**Browser**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/web.PNG)

**Video panel with carcams**:

![**Video panel with carcams**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/4.PNG)

**Photo viewer**:

![**Video panel with carcams**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/Photos.PNG)

**Settings panel**:

![**Video panel with carcams**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/settings.PNG)

**Build and run:**

Download [OpenCV](https://opencv.org/releases.html) and unpack it to a `<path-to-installed-opencv-331>`

Note: now application supports OpenCV ver.3.3.1

`mvn install:install-file -Dfile=<path-to-installed-opencv-331>\build\java\opencv-331.jar -DgroupId=org.opencv -DartifactId=opencv.win10_64 -Dversion=3.3.1 -Dpackaging=jar`

or

`mvn install:install-file -Dfile=${project.basedir}\local-maven-repo\opencv-331.jar -DgroupId=org.opencv -DartifactId=opencv.win10_64 -Dversion=3.3.1 -Dpackaging=jar`

then assembly distributive:

`mvn assembly:assembly -Dmaven.test.skip=true`

and run it

`java -Djava.library.path=<path-to-installed-opencv-331>\build\java\x64 -jar <Path-to-your-jar>/main_carauto_board-1.1-SNAPSHOT-jar-with-dependencies.jar`

## License

Copyright (C) 2017 - 2017 Evgenii Lartcev (https://github.com/Evegen55/) and contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.