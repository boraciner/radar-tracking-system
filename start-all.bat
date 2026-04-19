@echo off
setlocal enabledelayedexpansion
color 0A

echo.
echo  ==========================================
echo    RADAR TRACKING SYSTEM - STARTUP CHECK
echo  ==========================================
echo.

set ROOT=%~dp0
set ERRORS=0

REM ─────────────────────────────────────────────
REM  CHECK 1: Java
REM ─────────────────────────────────────────────
echo [CHECK 1/4] Java...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo   [FAIL] Java not found in PATH.
    echo          Install from: https://adoptium.net  or  run: scoop install temurin21-jdk
    set ERRORS=1
) else (
    for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        echo   [OK]   Java found: %%v
    )
)

REM ─────────────────────────────────────────────
REM  CHECK 2: Maven
REM ─────────────────────────────────────────────
echo [CHECK 2/4] Maven...
mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo   [FAIL] Maven not found in PATH.
    echo          Install via: scoop install maven  or  choco install maven
    set ERRORS=1
) else (
    for /f "tokens=3" %%v in ('mvn -version 2^>^&1 ^| findstr /i "Apache Maven"') do (
        echo   [OK]   Maven found: %%v
    )
)

REM ─────────────────────────────────────────────
REM  CHECK 3: Docker running
REM ─────────────────────────────────────────────
echo [CHECK 3/4] Docker...
docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo   [FAIL] Docker is not running.
    echo          Start Docker Desktop, wait for the tray icon to stabilise, then re-run this script.
    set ERRORS=1
) else (
    echo   [OK]   Docker is running.
)

REM ─────────────────────────────────────────────
REM  CHECK 4: Kafka on port 9092
REM ─────────────────────────────────────────────
echo [CHECK 4/4] Kafka on localhost:9092...
powershell -Command "try { $t = New-Object Net.Sockets.TcpClient('localhost', 9092); $t.Close(); exit 0 } catch { exit 1 }" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo   [WARN] Kafka not reachable on port 9092. Attempting docker-compose up -d ...
    docker-compose -f "%ROOT%docker-compose.yml" up -d
    if %ERRORLEVEL% NEQ 0 (
        echo   [FAIL] docker-compose failed. Check Docker is running and try again.
        set ERRORS=1
    ) else (
        echo   [OK]   Kafka containers started. Waiting 10s for broker to be ready...
        timeout /t 10 /nobreak >nul
        powershell -Command "try { $t = New-Object Net.Sockets.TcpClient('localhost', 9092); $t.Close(); exit 0 } catch { exit 1 }" >nul 2>&1
        if %ERRORLEVEL% NEQ 0 (
            echo   [FAIL] Kafka still not reachable after startup. Check docker-compose logs.
            set ERRORS=1
        ) else (
            echo   [OK]   Kafka is up.
        )
    )
) else (
    echo   [OK]   Kafka is already running.
)

REM ─────────────────────────────────────────────
REM  ABORT if any hard requirement is missing
REM ─────────────────────────────────────────────
echo.
if %ERRORS% NEQ 0 (
    echo  [ABORT] Fix the issues above and re-run this script.
    echo.
    pause
    exit /b 1
)

echo  All checks passed. Starting services...
echo.

REM ─────────────────────────────────────────────
REM  START SERVICES
REM ─────────────────────────────────────────────
echo [1/5] naming-service    (Eureka :8761)
start "naming-service" cmd /k "cd /d %ROOT%naming-service && mvn spring-boot:run"

echo        Waiting for Eureka to be ready...
:wait_eureka
timeout /t 3 /nobreak >nul
powershell -Command "try { $r = Invoke-WebRequest -Uri http://localhost:8761/actuator/health -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
if %ERRORLEVEL% NEQ 0 goto wait_eureka
echo        Eureka is up.
echo.

echo [2/5] plot-listener-service (:8100)
start "plot-listener-service" cmd /k "cd /d %ROOT%plot-listener-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [3/5] tracker-service       (:8200)
start "tracker-service" cmd /k "cd /d %ROOT%tracker-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [4/5] map-viewer-service    (:8080)
start "map-viewer-service" cmd /k "cd /d %ROOT%map-viewer-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo [5/5] radar-service         (:8000)  ^<-- starts last so pipeline is ready
start "radar-service" cmd /k "cd /d %ROOT%radar-service && mvn spring-boot:run"

echo.
echo  ==========================================
echo    All services launched.
echo  ==========================================
echo.
echo    Radar scope UI  :  http://localhost:8080/index.html
echo    Eureka dashboard:  http://localhost:8761
echo.
echo  Tip: close any service window to stop that service.
echo.

endlocal
