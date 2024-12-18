package com.tms.dicodingstory.utils

fun capitalizeWords(input: String): String {
    return input.split(" ") // Split the string by spaces into a list of words
        .joinToString(" ") { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        }
}