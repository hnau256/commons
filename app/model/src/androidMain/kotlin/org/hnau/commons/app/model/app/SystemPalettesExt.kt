package org.hnau.commons.app.model.app

import android.R
import android.content.Context
import org.hnau.commons.app.model.theme.PaletteTypeValues
import org.hnau.commons.app.model.theme.SystemPalettes
import org.hnau.commons.app.model.theme.Tone
import org.hnau.commons.kotlin.KeyValue

fun SystemPalettes.Companion.getForAndroid(
    context: Context,
): SystemPalettes {

}

private fun a(
    context: Context,
) {
    val a = PaletteTypeValues(
        primary = listOf(
            R.color.system_accent1_0 to 0,
            R.color.system_accent1_10 to 10,
            R.color.system_accent1_50 to 50,
            R.color.system_accent1_100 to 100,
            R.color.system_accent1_200 to 200,
            R.color.system_accent1_300 to 300,
            R.color.system_accent1_400 to 400,
            R.color.system_accent1_500 to 500,
            R.color.system_accent1_600 to 600,
            R.color.system_accent1_700 to 700,
            R.color.system_accent1_800 to 800,
            R.color.system_accent1_900 to 900,
            R.color.system_accent1_1000 to 1000,
        ),
        secondary = listOf(
            R.color.system_accent2_0 to 0,
            R.color.system_accent2_10 to 10,
            R.color.system_accent2_50 to 50,
            R.color.system_accent2_100 to 100,
            R.color.system_accent2_200 to 200,
            R.color.system_accent2_300 to 300,
            R.color.system_accent2_400 to 400,
            R.color.system_accent2_500 to 500,
            R.color.system_accent2_600 to 600,
            R.color.system_accent2_700 to 700,
            R.color.system_accent2_800 to 800,
            R.color.system_accent2_900 to 900,
            R.color.system_accent2_1000 to 1000,
        ),
        tertiary = listOf(
            R.color.system_accent3_0 to 0,
            R.color.system_accent3_10 to 10,
            R.color.system_accent3_50 to 50,
            R.color.system_accent3_100 to 100,
            R.color.system_accent3_200 to 200,
            R.color.system_accent3_300 to 300,
            R.color.system_accent3_400 to 400,
            R.color.system_accent3_500 to 500,
            R.color.system_accent3_600 to 600,
            R.color.system_accent3_700 to 700,
            R.color.system_accent3_800 to 800,
            R.color.system_accent3_900 to 900,
            R.color.system_accent3_1000 to 1000,
        ),
        neutral = listOf(
            R.color.system_neutral1_0 to 0,
            R.color.system_neutral1_10 to 10,
            R.color.system_neutral1_50 to 50,
            R.color.system_neutral1_100 to 100,
            R.color.system_neutral1_200 to 200,
            R.color.system_neutral1_300 to 300,
            R.color.system_neutral1_400 to 400,
            R.color.system_neutral1_500 to 500,
            R.color.system_neutral1_600 to 600,
            R.color.system_neutral1_700 to 700,
            R.color.system_neutral1_800 to 800,
            R.color.system_neutral1_900 to 900,
            R.color.system_neutral1_1000 to 1000,
        ),
        neutralVariant = listOf(
            R.color.system_neutral2_0 to 0,
            R.color.system_neutral2_10 to 10,
            R.color.system_neutral2_50 to 50,
            R.color.system_neutral2_100 to 100,
            R.color.system_neutral2_200 to 200,
            R.color.system_neutral2_300 to 300,
            R.color.system_neutral2_400 to 400,
            R.color.system_neutral2_500 to 500,
            R.color.system_neutral2_600 to 600,
            R.color.system_neutral2_700 to 700,
            R.color.system_neutral2_800 to 800,
            R.color.system_neutral2_900 to 900,
            R.color.system_neutral2_1000 to 1000,
        ),
        error = null,
    ).map { resourcesWithTone1000 ->
        resourcesWithTone1000?.map { (res, tone1000) ->
            val color = context.getColor(res)
            val tone = Tone.create(tone1000 / 10.0)
            KeyValue(tone, color)
        }
    }

}