@echo off
REM Simple Maven wrapper script for Windows

REM Set default Maven options
if "%MAVEN_OPTS%"=="" set MAVEN_OPTS=-Xmx1024m

REM Find Maven executable
if not "%MAVEN_HOME%"=="" (
    set MVN_CMD="%MAVEN_HOME%\bin\mvn.cmd"
) else (
    where mvn >nul 2>&1
    if errorlevel 1 (
        echo Error: Maven not found. Please install Maven or set MAVEN_HOME environment variable
        exit /b 1
    )
    set MVN_CMD=mvn
)

REM Execute Maven with all provided arguments
%MVN_CMD% %* 