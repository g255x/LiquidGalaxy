@echo off
del D:\Users\wodes\Desktop\LiquidGalaxy\LiquidGalaxy\build\libs\*.jar /f /q
start /wait cmd.exe /c "gradlew build -x generateGitProperties"
del D:\Users\wodes\Desktop\Games\.minecraft\versions\LiquidBounceG255-1.21.11\mods\liquidgalaxy-0.36.0.jar /f /q
copy D:\Users\wodes\Desktop\LiquidGalaxy\LiquidGalaxy\build\libs\liquidgalaxy-0.36.0.jar D:\Users\wodes\Desktop\Games\.minecraft\versions\LiquidBounceG255-1.21.11\mods\
powershell -File start.ps1
pause