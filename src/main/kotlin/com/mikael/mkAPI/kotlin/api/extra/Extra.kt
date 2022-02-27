package com.mikael.mkAPI.kotlin.api.extra

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object Extra {

    var MONEY_BR = DecimalFormat(
        "###,###.##",
        DecimalFormatSymbols.getInstance(Locale.forLanguageTag("PT-BR"))
    )
    var MONEY_EN = DecimalFormat(
        "###,###.##",
        DecimalFormatSymbols.getInstance(Locale.forLanguageTag("EN-US"))
    )

    var MONEY_OP_CLASSES = listOf(
        "",
        "K",
        "M",
        "B",
        "T",
        "Q",
        "QQ",
        "S",
        "SS",
        "OC",
        "N",
        "D",
        "UN",
        "DD",
        "TR",
        "QT",
        "QN",
        "SD",
        "SPD",
        "OD",
        "ND",
        "VG",
        "UVG",
        "DVG",
        "TVG",
        "QTV",
        "QNV",
        "SEV",
        "SPV",
        "OVG",
        "NVG",
        "TD"
    )
    var MONEY_OP_FORMATER_SYMBOLS_BR = DecimalFormatSymbols(Locale.forLanguageTag("PT-BR"))
    var MONEY_OP_FORMATER_SYMBOLS_EN = DecimalFormatSymbols(Locale.forLanguageTag("EN-US"))
    var MONEY_OP_FORMATER_BR = DecimalFormat("#,###.###", MONEY_OP_FORMATER_SYMBOLS_BR)
    var MONEY_OP_FORMATER_EN = DecimalFormat("#,###.###", MONEY_OP_FORMATER_SYMBOLS_EN)
    var FORMAT_DATE_BR = SimpleDateFormat("dd/MM/yyyy")
    var FORMAT_DATE_EN = SimpleDateFormat("MM/dd/yyyy")
    var FORMAT_TIME = SimpleDateFormat("HH:mm:ss")
    var FORMAT_DATETIME_BR = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    var FORMAT_DATETIME_EN = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

    fun formatMoney(number: Double, locale: String = "PT-BR"): String {
        val formattedNumber = MONEY_OP_FORMATER_BR.format(number)
        var separador = "" + MONEY_OP_FORMATER_SYMBOLS_BR.groupingSeparator
        if (separador.contains(".")) {
            separador = "\\."
        }
        val setOfThreeHouses = formattedNumber.split(separador).toTypedArray()
        val size = setOfThreeHouses.size
        if (size <= 1) {
            return MONEY_BR.format(number)
        }
        var acronym = MONEY_OP_CLASSES[MONEY_OP_CLASSES.size - 1]
        if (size <= MONEY_OP_CLASSES.size) {
            acronym = MONEY_OP_CLASSES[size - 1]
        }
        return try {
            MONEY_OP_FORMATER_BR.format(number / 10.0.pow(((size - 1) * 3).toDouble())) + acronym
        } catch (ex: Exception) {
            "-1.0"
        }
    }

    fun cutText(text: String, lenght: Int): String {
        return if (text.length > lenght) text.substring(0, lenght) else text
    }

    fun getChance(chance: Double): Boolean {
        return Math.random() <= chance
    }

    fun formatColors(text: String): String {
        return text.replace("&", "§")
    }

    fun getColumn(index: Int): Int {
        return if (index < 9) {
            index + 1
        } else index % 9 + 1
    }

    fun isColumn(index: Int, colunm: Int): Boolean {
        return getColumn(index) == colunm
    }

    fun getRandomInt(minValue: Int, maxValue: Int): Int {
        val min = minValue.coerceAtMost(maxValue)
        val max = minValue.coerceAtLeast(maxValue)
        return min + Random().nextInt(max - min + 1)
    }

    fun formatTime(time: Long): String {
        if (time <= 0L) {
            return "-1"
        }
        val space = " "
        val day = TimeUnit.MILLISECONDS.toDays(time)
        val hours = TimeUnit.MILLISECONDS.toHours(time) - day * 24L
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.MILLISECONDS.toHours(time) * 60L
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MILLISECONDS.toMinutes(time) * 60L
        val stringBuilder = StringBuilder()
        if (day > 0L) {
            stringBuilder.append(day).append("d")
            if (minutes > 0 || seconds > 0 || hours > 0) {
                stringBuilder.append(space)
            }
        }
        if (hours > 0L) {
            stringBuilder.append(hours).append("h")
            if (minutes > 0 || seconds > 0) {
                stringBuilder.append(space)
            }
        }
        if (minutes > 0L) {
            stringBuilder.append(minutes).append("m")
            if (seconds > 0) {
                stringBuilder.append(space)
            }
        }
        if (seconds > 0L) {
            stringBuilder.append(seconds).append("s")
        }
        return stringBuilder.toString()
    }

}