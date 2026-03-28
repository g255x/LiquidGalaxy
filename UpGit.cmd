@echo off
git config --global user.email "g_255@qq.com"
git config --global user.name "g255x"

cd /d D:\Users\wodes\Desktop\LiquidGalaxy\LiquidGalaxy

:: 删除子模块的 .git 目录（如果存在）
if exist LiquidBounceG255\.git (
    rmdir /s /q LiquidBounceG255\.git
)

:: 初始化并提交所有文件
git init
git add .
git commit -m "first commit"

:: 设置远程仓库
git remote add origin https://github.com/g255x/LiquidGalaxy.git
git branch -M main

:: 强制推送，覆盖远程内容
git push -f -u origin main

pause