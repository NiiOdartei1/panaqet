package com.example.panaqet.core.util

import java.util.Locale

object CurrencyUtils {
    fun formatCedi(amount: Double): String {
        return "GH₵ ${String.format(Locale.getDefault(), "%.2f", amount)}"
    }
}
