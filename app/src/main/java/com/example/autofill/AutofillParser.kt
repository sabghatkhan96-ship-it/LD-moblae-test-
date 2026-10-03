package com.example.autofill

import android.app.assist.AssistStructure
import android.os.Build
import android.text.InputType
import android.view.View
import android.view.autofill.AutofillId

data class ParsedAutofillStructure(
    val targetDomain: String,
    val packageName: String,
    val usernameId: AutofillId?,
    val passwordId: AutofillId?,
    val newPasswordId: AutofillId?
)

object AutofillParser {

    fun parse(structure: AssistStructure): ParsedAutofillStructure {
        var usernameId: AutofillId? = null
        var passwordId: AutofillId? = null
        var newPasswordId: AutofillId? = null
        var detectedDomain = ""
        val targetPackage = structure.activityComponent?.packageName ?: ""

        val windowNodeCount = structure.windowNodeCount
        for (i in 0 until windowNodeCount) {
            val rootNode = structure.getWindowNodeAt(i).rootViewNode
            traverseNode(rootNode) { node ->
                // Check for Web Domain if in Chrome/WebView
                if (detectedDomain.isEmpty() && !node.webDomain.isNullOrBlank()) {
                    detectedDomain = node.webDomain ?: ""
                }

                val hints = node.autofillHints
                val inputType = node.inputType
                val idEntry = node.idEntry?.lowercase() ?: ""
                val hint = node.hint?.toString()?.lowercase() ?: ""

                // Password Detection
                val isPasswordType = (inputType and InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_TEXT &&
                        (inputType and InputType.TYPE_MASK_VARIATION) in listOf(
                    InputType.TYPE_TEXT_VARIATION_PASSWORD,
                    InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                )

                val hasPasswordHint = hints?.any {
                    it.equals(View.AUTOFILL_HINT_PASSWORD, ignoreCase = true) ||
                            it.contains("password", ignoreCase = true)
                } == true || idEntry.contains("password") || hint.contains("password")

                if (isPasswordType || hasPasswordHint) {
                    if (hints?.any { it.contains("new", ignoreCase = true) } == true ||
                        idEntry.contains("new") || hint.contains("confirm")) {
                        if (newPasswordId == null) newPasswordId = node.autofillId
                    } else if (passwordId == null) {
                        passwordId = node.autofillId
                    }
                }

                // Username / Email Detection
                val isEmailType = (inputType and InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_TEXT &&
                        (inputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                val hasUsernameHint = hints?.any {
                    it.equals(View.AUTOFILL_HINT_USERNAME, ignoreCase = true) ||
                            it.equals(View.AUTOFILL_HINT_EMAIL_ADDRESS, ignoreCase = true) ||
                            it.contains("username", ignoreCase = true) ||
                            it.contains("email", ignoreCase = true)
                } == true || idEntry.contains("user") || idEntry.contains("email") || idEntry.contains("login") ||
                        hint.contains("email") || hint.contains("username") || hint.contains("phone")

                if ((isEmailType || hasUsernameHint) && usernameId == null) {
                    usernameId = node.autofillId
                }
            }
        }

        val domain = if (detectedDomain.isNotBlank()) detectedDomain else targetPackage
        return ParsedAutofillStructure(
            targetDomain = domain,
            packageName = targetPackage,
            usernameId = usernameId,
            passwordId = passwordId,
            newPasswordId = newPasswordId
        )
    }

    private fun traverseNode(node: AssistStructure.ViewNode, visitor: (AssistStructure.ViewNode) -> Unit) {
        visitor(node)
        for (i in 0 until node.childCount) {
            traverseNode(node.getChildAt(i), visitor)
        }
    }
}
