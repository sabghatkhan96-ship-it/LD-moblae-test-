package com.example.data.security

import java.security.SecureRandom
import kotlin.math.log2
import kotlin.math.roundToInt

data class PasswordStrength(
    val entropyBits: Int,
    val score: Int, // 0 to 4
    val label: String,
    val description: String
)

object PasswordGenerator {

    private val secureRandom = SecureRandom()

    private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
    private const val NUMBERS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    private const val AMBIGUOUS = "Il1O0"

    fun generate(
        length: Int = 18,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeNumbers: Boolean = true,
        includeSymbols: Boolean = true,
        avoidAmbiguous: Boolean = true
    ): String {
        val pool = StringBuilder()
        if (includeUpper) pool.append(UPPER)
        if (includeLower) pool.append(LOWER)
        if (includeNumbers) pool.append(NUMBERS)
        if (includeSymbols) pool.append(SYMBOLS)

        if (pool.isEmpty()) return ""

        var allowedChars = pool.toString()
        if (avoidAmbiguous) {
            allowedChars = allowedChars.filter { it !in AMBIGUOUS }
        }

        if (allowedChars.isEmpty()) allowedChars = pool.toString()

        val passwordChars = CharArray(length)

        // Ensure at least one of each selected character set is present
        var index = 0
        if (includeUpper && index < length) {
            val upperFiltered = if (avoidAmbiguous) UPPER.filter { it !in AMBIGUOUS } else UPPER
            passwordChars[index++] = upperFiltered[secureRandom.nextInt(upperFiltered.length)]
        }
        if (includeLower && index < length) {
            val lowerFiltered = if (avoidAmbiguous) LOWER.filter { it !in AMBIGUOUS } else LOWER
            passwordChars[index++] = lowerFiltered[secureRandom.nextInt(lowerFiltered.length)]
        }
        if (includeNumbers && index < length) {
            val numbersFiltered = if (avoidAmbiguous) NUMBERS.filter { it !in AMBIGUOUS } else NUMBERS
            passwordChars[index++] = numbersFiltered[secureRandom.nextInt(numbersFiltered.length)]
        }
        if (includeSymbols && index < length) {
            passwordChars[index++] = SYMBOLS[secureRandom.nextInt(SYMBOLS.length)]
        }

        while (index < length) {
            passwordChars[index++] = allowedChars[secureRandom.nextInt(allowedChars.length)]
        }

        // Shuffle characters
        for (i in passwordChars.indices.reversed()) {
            val j = secureRandom.nextInt(i + 1)
            val temp = passwordChars[i]
            passwordChars[i] = passwordChars[j]
            passwordChars[j] = temp
        }

        return String(passwordChars)
    }

    fun calculateStrength(password: String): PasswordStrength {
        if (password.isEmpty()) {
            return PasswordStrength(0, 0, "Empty", "Enter a password")
        }

        var poolSize = 0
        if (password.any { it.isUpperCase() }) poolSize += UPPER.length
        if (password.any { it.isLowerCase() }) poolSize += LOWER.length
        if (password.any { it.isDigit() }) poolSize += NUMBERS.length
        if (password.any { it in SYMBOLS }) poolSize += SYMBOLS.length

        if (poolSize == 0) poolSize = 26

        val entropy = (password.length * log2(poolSize.toDouble())).roundToInt()

        return when {
            entropy < 36 -> PasswordStrength(
                entropyBits = entropy,
                score = 0,
                label = "Very Weak",
                description = "Vulnerable to instant brute-force"
            )
            entropy < 60 -> PasswordStrength(
                entropyBits = entropy,
                score = 1,
                label = "Weak",
                description = "Can be cracked within hours"
            )
            entropy < 80 -> PasswordStrength(
                entropyBits = entropy,
                score = 2,
                label = "Moderate",
                description = "Acceptable for non-sensitive accounts"
            )
            entropy < 100 -> PasswordStrength(
                entropyBits = entropy,
                score = 3,
                label = "Strong",
                description = "Resistant to modern offline dictionary attacks"
            )
            else -> PasswordStrength(
                entropyBits = entropy,
                score = 4,
                label = "Military Grade",
                description = "Impractical to crack with quantum-era compute"
            )
        }
    }
}
