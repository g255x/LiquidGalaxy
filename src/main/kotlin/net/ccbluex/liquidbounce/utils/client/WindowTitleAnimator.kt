/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2026 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */

package net.ccbluex.liquidbounce.utils.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.events.GameTickEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.utils.kotlin.Minecraft
import net.minecraft.SharedConstants

/**
 * 窗口标题动画器
 * 负责在加载时显示窗口标题动画
 */
object WindowTitleAnimator {
    private var animationJob: Job? = null
    private var isAnimating = false

    @Volatile
    private var displayTitle: String = "LiquidGalaxy - Loading..."

    val currentTitle: String
        get() = displayTitle

    private val expandFrames = listOf(
        "L",
        "Li",
        "Liq",
        "Liqu",
        "Liqui",
        "Liquid",
        "LiquidG",
        "LiquidGa",
        "LiquidGal",
        "LiquidGala",
        "LiquidGalax",
        "LiquidGalaxy",
        "LiquidGalaxy 1",
        "LiquidGalaxy 1.",
        "LiquidGalaxy 1.2",
        "LiquidGalaxy 1.21",
        "LiquidGalaxy 1.21.",
        "LiquidGalaxy 1.21.1",
        "LiquidGalaxy 1.21.11"
    )

    // 收回动画：从 "LiquidGalaxy 1.21.11" 逐步收回到 "L"
    private val collapseFrames = listOf(
        "LiquidGalaxy 1.21.11",
        "LiquidGalaxy 1.21.1",
        "LiquidGalaxy 1.21.",
        "LiquidGalaxy 1.21",
        "LiquidGalaxy 1.2",
        "LiquidGalaxy 1.",
        "LiquidGalaxy 1",
        "LiquidGalaxy",
        "LiquidGalax",
        "LiquidGala",
        "LiquidGal",
        "LiquidGa",
        "LiquidG",
        "Liquid",
        "Liqui",
        "Liqu",
        "Liq",
        "Li",
        "L"
    )

    private const val ANIMATION_SPEED = 50L // 动画间隔（毫秒），快速动画
    private const val PAUSE_AFTER_EXPAND = 3000L // 展开动画完成后暂停时间（毫秒）
    private const val PAUSE_AFTER_COLLAPSE = 500L // 收回动画完成后暂停时间（毫秒）

    /**
     * 初始化标题（在客户端启动时调用）
     */
    fun initialize() {
        displayTitle = "LiquidGalaxy - Loading..."
        mc.window.setTitle(displayTitle)
    }

    /**
     * 开始播放动画（在资源加载完成后调用）
     */
    fun startAnimation() {
        if (isAnimating) return

        isAnimating = true

        animationJob = CoroutineScope(Dispatchers.Default).launch {
            // 等待一段时间后开始动画
            delay(500)

            // 循环播放动画
            playAnimationLoop()
        }
    }

    /**
     * 播放动画循环
     */
    private suspend fun playAnimationLoop() {
        while (isAnimating) {
            playExpandAnimation()
            if (!isAnimating) break
            
            delay(PAUSE_AFTER_EXPAND)
            
            playCollapseAnimation()
            if (!isAnimating) break
            
            delay(PAUSE_AFTER_COLLAPSE)
        }
    }

    /**
     * 播放展开动画
     */
    private suspend fun playExpandAnimation() {
        for (frame in expandFrames) {
            if (!isAnimating) break
            displayTitle = frame
            mc.window.setTitle(frame)
            delay(ANIMATION_SPEED)
        }
    }

    /**
     * 播放收回动画
     */
    private suspend fun playCollapseAnimation() {
        for (frame in collapseFrames) {
            if (!isAnimating) break
            displayTitle = frame
            mc.window.setTitle(frame)
            delay(ANIMATION_SPEED)
        }
    }

    /**
     * 停止标题动画
     */
    fun stopAnimation() {
        if (!isAnimating) return

        isAnimating = false
        animationJob?.cancel()
        animationJob = null

        // 设置最终标题
        displayTitle = "LiquidGalaxy"
    }

    /**
     * 是否正在播放动画
     */
    fun isPlaying(): Boolean = isAnimating
}
