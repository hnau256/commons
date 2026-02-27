package hnau.common.app.projector.utils.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import hnau.common.app.model.theme.ThemeBrightness

actual fun provideDynamicColorsGenerator(): DynamicColorsGenerator? =
    AndroidDynamicColorsGenerator.createIfSupported()

private class AndroidDynamicColorsGenerator private constructor() : DynamicColorsGenerator {

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    override fun generateDynamicColors(
        brightness: ThemeBrightness,
    ): ColorScheme {
        val context = LocalContext.current
        return when (brightness) {
            ThemeBrightness.Dark -> dynamicDarkColorScheme(context)
            ThemeBrightness.Light -> dynamicLightColorScheme(context)
        }
    }

    companion object {

        fun createIfSupported(): AndroidDynamicColorsGenerator? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AndroidDynamicColorsGenerator()
            } else {
                null
            }
    }
}