:: Copyright 2009-2012, by the California Institute of Technology.
:: ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
:: Any commercial use must be negotiated with the Office of Technology Transfer
:: at the California Institute of Technology.
::
:: This software is subject to U. S. export control laws and regulations
:: (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
:: is subject to U.S. export control laws and regulations, the recipient has
:: the responsibility to obtain export licenses or other export authority as
:: may be required before exporting such information to foreign countries or
:: providing access to foreign nationals.
::
:: $Id$

:: Batch file that allows easy execution of the Catalog Tool
:: without the need to set the CLASSPATH or having to type in that long java
:: command (java gov.nasa.pds.citool.CITool ...)

@echo off

:: Expects the Catalog jar file to be located in the ../lib directory.

:: Set the JAVA_HOME environment variable here in the script if it will
:: not be defined in the environment.
if not defined JAVA_HOME (
  set JAVA_HOME=\path\to\java\home
)

:: Setup environment variables.
set SCRIPT_DIR=%~dp0
set PARENT_DIR=%SCRIPT_DIR%..
set LIB_DIR=%PARENT_DIR%\lib

:: Check for dependencies.
if exist "%LIB_DIR%\catalog-*.jar" (
set CATALOG_JAR=%LIB_DIR%\catalog-*.jar
) else (
echo Cannot find Catalog jar file in %LIB_DIR%
goto END
)

:: Finds the jar file in LIB_DIR and sets it to CATALOG_JAR
for %%i in ("%LIB_DIR%"\catalog-*.jar) do set CATALOG_JAR=%%i

:: Executes Catalog via the executable jar file
:: The special variable '%*' allows the arguments
:: to be passed into the executable.
"%JAVA_HOME%"\bin\java -jar "%CATALOG_JAR%" %*

:END
