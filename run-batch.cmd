@echo off

ECHO 数组定义项目下的所有模块

SET Arr_Length=7
SET Arr[0].Name=server1
SET Arr[1].Name=server2
SET Arr[2].Name=server3
SET Arr[3].Name=server4
SET Arr[4].Name=server5
SET Arr[5].Name=server6
SET Arr[6].Name=server7

ECHO 定义包名
SET PackageName=com.loamen.javadoctest


SET Arr_Index=0
 
:LoopStart
IF %Arr_Index% EQU %Arr_Length% GOTO :Pause
 
SET Arr_Current.Name=0
 
FOR /F "usebackq delims==. tokens=1-3" %%I IN (`SET Arr[%Arr_Index%]`) DO (
  SET Arr_Current.%%J=%%K
)
 
ECHO Name = %Arr_Current.Name%
ECHO.

java -jar "javadoc-generator.jar" com.huima.App -source %PackageName% -sourcepath "..\%Arr_Current.Name%\src\main\java" -subpackages %PackageName% -classpath "..\%Arr_Current.Name%\target\classes" -fieldmodifier PUBLIC -methodmodifier PUBLIC -docpath "d:\%Arr_Current.Name%.docx"
 
SET /A Arr_Index=%Arr_Index% + 1
 
GOTO LoopStart

:Pause
pause