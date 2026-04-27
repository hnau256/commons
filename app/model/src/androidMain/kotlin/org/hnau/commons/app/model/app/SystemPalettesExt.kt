package org.hnau.commons.app.model.app

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.Variant
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.theme.Hue
import org.hnau.commons.app.model.theme.Palette
import org.hnau.commons.app.model.theme.PaletteTypeValues
import org.hnau.commons.app.model.theme.SystemPalettes
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.Tone
import org.hnau.commons.app.model.theme.create
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.lerp

fun SystemPalettes.Companion.getForAndroid(
    context: Context,
): SystemPalettes {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return SystemPalettes.None
    }
    return SystemPalettes.Some(
        palettes = getSystemPalettes(
            context = context,
        )
    )
}


@RequiresApi(Build.VERSION_CODES.S)
private fun getSystemPalettes(
    context: Context,
): PaletteTypeValues<Palette> = getResourcesByTones().map { type, resourcesByTones ->
    resourcesByTones
        ?.map { toneWithResources ->
            toneWithResources.map { res ->
                context
                    .getColor(res)
                    .let(Hct::fromInt)
            }
        }
        ?.let(::SystemPalette)
        ?: Palette.create(
            hue = Hue(0),
            type = type,
            variant = Variant.TONAL_SPOT,
            brightness = ThemeBrightness.Light,
        )
}

@RequiresApi(Build.VERSION_CODES.S)
private fun getResourcesByTones(): PaletteTypeValues<List<KeyValue<Tone, Int>>?> =
    PaletteTypeValues(
        primary = listOf(
            android.R.color.system_accent1_0 to 0,
            android.R.color.system_accent1_10 to 10,
            android.R.color.system_accent1_50 to 50,
            android.R.color.system_accent1_100 to 100,
            android.R.color.system_accent1_200 to 200,
            android.R.color.system_accent1_300 to 300,
            android.R.color.system_accent1_400 to 400,
            android.R.color.system_accent1_500 to 500,
            android.R.color.system_accent1_600 to 600,
            android.R.color.system_accent1_700 to 700,
            android.R.color.system_accent1_800 to 800,
            android.R.color.system_accent1_900 to 900,
            android.R.color.system_accent1_1000 to 1000,
        ),
        secondary = listOf(
            android.R.color.system_accent2_0 to 0,
            android.R.color.system_accent2_10 to 10,
            android.R.color.system_accent2_50 to 50,
            android.R.color.system_accent2_100 to 100,
            android.R.color.system_accent2_200 to 200,
            android.R.color.system_accent2_300 to 300,
            android.R.color.system_accent2_400 to 400,
            android.R.color.system_accent2_500 to 500,
            android.R.color.system_accent2_600 to 600,
            android.R.color.system_accent2_700 to 700,
            android.R.color.system_accent2_800 to 800,
            android.R.color.system_accent2_900 to 900,
            android.R.color.system_accent2_1000 to 1000,
        ),
        tertiary = listOf(
            android.R.color.system_accent3_0 to 0,
            android.R.color.system_accent3_10 to 10,
            android.R.color.system_accent3_50 to 50,
            android.R.color.system_accent3_100 to 100,
            android.R.color.system_accent3_200 to 200,
            android.R.color.system_accent3_300 to 300,
            android.R.color.system_accent3_400 to 400,
            android.R.color.system_accent3_500 to 500,
            android.R.color.system_accent3_600 to 600,
            android.R.color.system_accent3_700 to 700,
            android.R.color.system_accent3_800 to 800,
            android.R.color.system_accent3_900 to 900,
            android.R.color.system_accent3_1000 to 1000,
        ),
        neutral = listOf(
            android.R.color.system_neutral1_0 to 0,
            android.R.color.system_neutral1_10 to 10,
            android.R.color.system_neutral1_50 to 50,
            android.R.color.system_neutral1_100 to 100,
            android.R.color.system_neutral1_200 to 200,
            android.R.color.system_neutral1_300 to 300,
            android.R.color.system_neutral1_400 to 400,
            android.R.color.system_neutral1_500 to 500,
            android.R.color.system_neutral1_600 to 600,
            android.R.color.system_neutral1_700 to 700,
            android.R.color.system_neutral1_800 to 800,
            android.R.color.system_neutral1_900 to 900,
            android.R.color.system_neutral1_1000 to 1000,
        ),
        neutralVariant = listOf(
            android.R.color.system_neutral2_0 to 0,
            android.R.color.system_neutral2_10 to 10,
            android.R.color.system_neutral2_50 to 50,
            android.R.color.system_neutral2_100 to 100,
            android.R.color.system_neutral2_200 to 200,
            android.R.color.system_neutral2_300 to 300,
            android.R.color.system_neutral2_400 to 400,
            android.R.color.system_neutral2_500 to 500,
            android.R.color.system_neutral2_600 to 600,
            android.R.color.system_neutral2_700 to 700,
            android.R.color.system_neutral2_800 to 800,
            android.R.color.system_neutral2_900 to 900,
            android.R.color.system_neutral2_1000 to 1000,
        ),
        error = null,
    ).map { resourcesWithTone1000 ->
        resourcesWithTone1000?.map { (resource, tone1000) ->
            val tone = Tone.create(tone1000 / 10)
            KeyValue(tone, resource)
        }
    }

private class SystemPalette(
    private val keyColors: List<KeyValue<Tone, Hct>>,
) : Palette {

    private val cache = HashMap<Tone, Hct>()

    override fun get(
        tone: Tone,
    ): Hct = cache.getOrPut(
        key = tone,
    ) {
        keyColors.getInterpolation(
            tone = tone,
        )
    }

    private fun List<KeyValue<Tone, Hct>>.getInterpolation(
        tone: Tone,
    ): Hct {
        val index = binarySearchBy(tone.raw) { it.key.raw }
        if (index >= 0) {
            return this[index].value
        }
        val insertionPoint = -(index + 1)
        if (insertionPoint == 0) {
            return this[0].value
        }
        if (insertionPoint == size) {
            return this[lastIndex].value
        }
        val (fromTone, fromColor) = this[insertionPoint - 1]
        val (toTone, toColor) = this[insertionPoint]
        val fraction = (tone.raw - fromTone.raw).toDouble() / (toTone.raw - fromTone.raw)
        return Hct.from(
            hue = lerp(fromColor.hue, toColor.hue, fraction),
            chroma = lerp(fromColor.chroma, toColor.chroma, fraction),
            tone = tone.raw.toDouble(),
        )
    }
}

