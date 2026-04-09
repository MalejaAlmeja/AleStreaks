@ECHO OFF
where gradle >NUL 2>&1
IF %ERRORLEVEL% NEQ 0 (
  ECHO Error: Gradle is not installed on PATH.
  ECHO Install Gradle or open the project in Android Studio and run a Gradle sync/build.
  EXIT /B 1
)

gradle %*
