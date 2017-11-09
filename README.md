# Multimedia panel for a car (prototype)
## Here is the sketch for car panel. 

Thank you very much:
- [Rob Terpilowski](https://github.com/rterp) with his [Google maps api for JavaFX](https://github.com/rterp/GMapsFX) and
- [Bartosz Firyn](https://github.com/sarxos) with his [webcam library](https://github.com/sarxos/webcam-capture).


There will be the next attempt to create by using [Qt platform](https://www.qt.io/) but this project is suitable to rapid prototyping the main idea.

Here is the screen capture for the main ideas:

**Maps**:

![**Maps**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/1.PNG)

**Audio panel**:

![**Audio panel**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/2.PNG)

**Browser**:

![**Browser**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/3.PNG)

**Video panel with carcams**:

![**Video panel with carcams**](https://raw.githubusercontent.com/Evegen55/main_carauto_board/master/src/test/resources/for_readme/4.PNG)

**Build and run:**

`mvn assembly:assembly`

`java -jar <Path-to-your-jar>/main_carauto_board-1.0-SNAPSHOT-jar-with-dependencies.jar`