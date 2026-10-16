@echo off
title PawMart Pet Shop Management System
echo =====================================================================
echo  PAWMART - Pet Shop Management System
echo  Connecting to MySQL (WampServer)...
echo =====================================================================
java -jar target\petshop-1.0-SNAPSHOT-jar-with-dependencies.jar
if %errorlevel% neq 0 (
    echo.
    echo Application exited with error code %errorlevel%.
    pause
)
