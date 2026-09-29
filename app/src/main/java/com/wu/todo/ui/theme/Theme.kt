package com.wu.todo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 取自设计稿的配色
val WuBackground = Color(0xFFF2F2F2)   // 页面浅灰背景
val WuCard = Color(0xFFFFFFFF)         // 卡片白
val WuAccent = Color(0xFFF2645A)       // 列标题前的红点
val WuTitle = Color(0xFF3A3A3A)        // 列标题文字
val WuTaskText = Color(0xFFA6A6A6)     // 任务文字（灰）
val WuCircleStroke = Color(0xFFBBBBBB) // 未勾选圆圈描边
val WuSubtle = Color(0xFF8A8A8A)       // 次要文字
val WuDoneGrey = Color(0xFFB4B4B4)     // 已完成任务的图标/文字（浅灰）
val WuDivider = Color(0xFFE2E2E2)      // 分隔线

private val LightScheme = lightColorScheme(
    background = WuBackground,
    surface = WuCard,
    surfaceVariant = WuCard,
    onBackground = WuTitle,
    onSurface = WuTitle,
    primary = WuAccent,
    outline = WuCircleStroke
)

@Composable
fun WuTodoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        content = content
    )
}
