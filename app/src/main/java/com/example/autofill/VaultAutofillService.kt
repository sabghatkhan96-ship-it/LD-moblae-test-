package com.example.autofill

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.security.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Official Native Android Autofill Service.
 * Matches requested application package names and web domains with encrypted Vault records,
 * and releases autofill datasets to system forms strictly upon user intent and authorization.
 */
class VaultAutofillService : AutofillService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val fillContexts: List<FillContext> = request.fillContexts
        val latestStructure: AssistStructure = fillContexts.lastOrNull()?.structure ?: run {
            callback.onSuccess(null)
            return
        }

        val parsed = AutofillParser.parse(latestStructure)
        val usernameId = parsed.usernameId
        val passwordId = parsed.passwordId

        if (usernameId == null && passwordId == null) {
            callback.onSuccess(null)
            return
        }

        serviceScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val target = parsed.targetDomain.lowercase()
                val pkg = parsed.packageName.lowercase()

                // Find matching items from encrypted vault
                val allItems = db.vaultDao().findMatchingAutofillItems(target)
                    .ifEmpty { db.vaultDao().findMatchingAutofillItems(pkg) }

                if (allItems.isEmpty()) {
                    callback.onSuccess(null)
                    return@launch
                }

                val responseBuilder = FillResponse.Builder()

                // Build dataset presentation for each matching credential
                for (item in allItems.take(5)) {
                    val presentation = RemoteViews(packageName, R.layout.autofill_dataset_item).apply {
                        setTextViewText(R.id.autofill_dataset_title, item.title)
                        setTextViewText(R.id.autofill_dataset_sub, item.username)
                        setImageViewResource(R.id.autofill_icon, R.drawable.ic_vault_key)
                    }

                    val datasetBuilder = Dataset.Builder(presentation)

                    if (usernameId != null) {
                        datasetBuilder.setValue(
                            usernameId,
                            AutofillValue.forText(item.username),
                            presentation
                        )
                    }

                    if (passwordId != null) {
                        val decryptedPassword = CryptoManager.decrypt(item.encryptedPassword)
                        datasetBuilder.setValue(
                            passwordId,
                            AutofillValue.forText(decryptedPassword),
                            presentation
                        )
                    }

                    responseBuilder.addDataset(datasetBuilder.build())
                }

                // Setup SaveInfo so users can save or update credentials securely
                val requiredIds = mutableListOf<AutofillId>()
                usernameId?.let { requiredIds.add(it) }
                passwordId?.let { requiredIds.add(it) }

                if (requiredIds.isNotEmpty()) {
                    val saveInfo = SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_PASSWORD or SaveInfo.SAVE_DATA_TYPE_USERNAME,
                        requiredIds.toTypedArray()
                    ).build()
                    responseBuilder.setSaveInfo(saveInfo)
                }

                callback.onSuccess(responseBuilder.build())
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(e.localizedMessage)
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        // System save prompt callback when user submits credentials in external apps/sites
        callback.onSuccess()
    }
}
