@echo off
set JAVA_HOME="C:\Program Files\Java\jdk-17"
set PATH=%JAVA_HOME%\bin;%PATH%
set FX_HOME="C:\javafx-sdk-17.0.2\lib"

java --module-path %FX_HOME% --add-modules javafx.controls,javafx.fxml -cp "target/classes" com.esprit.core.Main
pause
