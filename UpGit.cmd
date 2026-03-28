@echo off
git config --global user.email "g_255@qq.com"
git config --global user.name "g255x"
git init
git add .
git commit -m "first commit"
git remote add origin https://github.com/g255x/LiquidGalaxy.git
git branch -M main
git push -u origin main
pause