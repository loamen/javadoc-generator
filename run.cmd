@echo off
java -jar "target\javadoc-generator-1.0-SNAPSHOT.jar" com.loamen.App -source "com.loamen.javadoctest" -subpackages "com.loamen.javadoctest" -sourcepath "..\..\javadoc-test\src\main\java" -classpath "..\..\javadoc-test\target\classes" -fieldmodifier PUBLIC -methodmodifier PUBLIC -docpath "d:\doc.docx"
pause
