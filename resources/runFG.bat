@ECHO OFF
set PATH_TO_FX= "C:\Program Files\Java\openjfx-16_windows-x64_bin-sdk\javafx-sdk-16\lib"
java -jar --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml project.jar