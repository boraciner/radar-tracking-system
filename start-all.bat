@echo off
setlocal

echo Starting Radar Tracking System...
echo.

set ROOT=%~dp0

REM --- Eureka first, everything else depends on it
echo [1/5] Starting naming-service (Eureka)...
start "naming-service" cmd /k "cd /d %ROOT%naming-service && mvn spring-boot:run"

echo Waiting for Eureka to come up...
timeout /t 20 /nobreak >nul

REM --- Kafka bridge, no dependencies on other app services
echo [2/5] Starting plot-listener-service...
start "plot-listener-service" cmd /k "cd /d %ROOT%plot-listener-service && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

REM --- Tracker needs plot-listener to be producing to Kafka
echo [3/5] Starting tracker-service...
start "tracker-service" cmd /k "cd /d %ROOT%tracker-service && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

REM --- Map viewer just reads from Kafka and serves the UI
echo [4/5] Starting map-viewer-service...
start "map-viewer-service" cmd /k "cd /d %ROOT%map-viewer-service && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

REM --- Radar starts last so the pipeline is ready to receive plots
echo [5/5] Starting radar-service...
start "radar-service" cmd /k "cd /d %ROOT%radar-service && mvn spring-boot:run"

echo.
echo All services started.
echo.
echo   Eureka dashboard : http://localhost:8761
echo   Map viewer UI    : http://localhost:8080
echo.
echo Make sure Kafka is running on localhost:9092 before using the UI.
echo (run docker-compose up -d from the project root if not already running)

endlocal
