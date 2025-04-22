package com.example.cashwire.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import com.example.cashwire.data.BudgetRepository
import com.example.cashwire.data.TransactionRepository
import com.example.cashwire.data.UserRepository
import com.example.cashwire.models.Transaction
import com.example.cashwire.models.Budget
import com.example.cashwire.models.CategoryBudget
import com.example.cashwire.models.TransactionType
import com.example.cashwire.models.User
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class BackupRestoreManager(private val context: Context) {

    private val transactionRepository = TransactionRepository.getInstance(context)
    private val budgetRepository = BudgetRepository.getInstance(context)
    private val userRepository = UserRepository.getInstance(context)
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val currentDateTime = "2025-04-22 07:25:04" // Updated timestamp
    private val currentUserLogin = "SakithLiyanageNow" // Updated user login

    /**
     * Show dialog to choose backup format
     */
    fun showBackupFormatDialog(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val options = arrayOf("JSON Format", "Text Format")

        AlertDialog.Builder(context)
            .setTitle("Choose Backup Format")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> createBackupJson(onSuccess, onError)
                    1 -> createBackupText(onSuccess, onError)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Creates a backup of all data in JSON format
     */
    private fun createBackupJson(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            // Create backup object with all data
            val backupData = JsonObject().apply {
                // Add metadata
                addProperty("timestamp", System.currentTimeMillis())
                addProperty("version", "1.0.0")
                addProperty("appName", "CashWire")
                addProperty("backupDate", currentDateTime)
                addProperty("userLogin", currentUserLogin)

                // Add user info
                add("user", gson.toJsonTree(userRepository.getCurrentUser()))

                // Add transactions
                add("transactions", gson.toJsonTree(transactionRepository.getAllTransactions()))

                // Add budget data
                add("mainBudget", gson.toJsonTree(budgetRepository.getMainBudget()))
                add("categoryBudgets", gson.toJsonTree(budgetRepository.getCategoryBudgets()))
            }

            // Convert to JSON string
            val jsonData = gson.toJson(backupData)

            // Generate file name based on date
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "CashWire_backup_${dateFormat.format(Date())}.json"

            // Write to file
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { stream ->
                stream.write(jsonData.toByteArray())
            }

            onSuccess(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Creates a backup of all data in text format
     */
    private fun createBackupText(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            val sb = StringBuilder()

            // Add header information
            sb.appendLine("CASHWIRE BACKUP FILE")
            sb.appendLine("Created on: $currentDateTime")
            sb.appendLine("User: $currentUserLogin")
            sb.appendLine("----------------------------------------")
            sb.appendLine()

            // Add user information
            val user = userRepository.getCurrentUser()
            sb.appendLine("## USER INFORMATION ##")
            sb.appendLine("Name: ${user.name}")
            sb.appendLine("Email: ${user.email}")
            sb.appendLine("Currency: ${user.currency}")
            sb.appendLine()

            // Add budget information
            val budget = budgetRepository.getMainBudget()
            sb.appendLine("## BUDGET INFORMATION ##")
            if (budget != null) {
                sb.appendLine("Monthly Budget: ${CurrencyFormatter.formatLKR(budget.amount)}")
                sb.appendLine("Monthly Spending: ${CurrencyFormatter.formatLKR(budgetRepository.getMonthlySpending())}")
                sb.appendLine("Budget Used: ${budgetRepository.getBudgetUsagePercentage()}%")
                sb.appendLine("Budget Remaining: ${CurrencyFormatter.formatLKR(budgetRepository.getRemainingBudget())}")
            } else {
                sb.appendLine("No budget has been set.")
            }
            sb.appendLine()

            // Add category budgets
            val categoryBudgets = budgetRepository.getCategoryBudgets()
            if (categoryBudgets.isNotEmpty()) {
                sb.appendLine("## CATEGORY BUDGETS ##")
                categoryBudgets.forEach { catBudget ->
                    sb.appendLine("${catBudget.categoryName}: ${CurrencyFormatter.formatLKR(catBudget.amount)}")
                }
                sb.appendLine()
            }

            // Add transactions
            val transactions = transactionRepository.getAllTransactions()
            if (transactions.isNotEmpty()) {
                sb.appendLine("## TRANSACTIONS ##")

                // Group by year and month
                val groupedByMonth = transactions.sortedByDescending { it.date }
                    .groupBy { transaction ->
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = transaction.date
                        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}"
                    }

                groupedByMonth.forEach { (yearMonth, monthTransactions) ->
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = monthTransactions.first().date
                    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)

                    sb.appendLine("=== $monthName ===")

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    monthTransactions.forEach { transaction ->
                        val date = dateFormat.format(Date(transaction.date))
                        val amount = CurrencyFormatter.formatLKR(transaction.amount)
                        val type = if (transaction.type == TransactionType.INCOME) "INCOME" else "EXPENSE"
                        val sign = if (transaction.type == TransactionType.INCOME) "+" else "-"

                        sb.appendLine("$date | $type | ${transaction.title} | $sign$amount")

                        if (transaction.notes != null) {
                            sb.appendLine("  Note: ${transaction.notes}")
                        }
                    }
                    sb.appendLine()
                }
            }

            // Write to file
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "CashWire_backup_${dateFormat.format(Date())}.txt"

            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { stream ->
                stream.write(sb.toString().toByteArray())
            }

            onSuccess(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Shows dialog to choose a backup file to restore
     */
    fun showRestoreDialog(onRestoreComplete: (Boolean) -> Unit) {
        val backupFiles = getAvailableBackupFiles()

        if (backupFiles.isEmpty()) {
            AlertDialog.Builder(context)
                .setTitle("No Backups Found")
                .setMessage("There are no backup files available to restore.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, backupFiles)

        AlertDialog.Builder(context)
            .setTitle("Select Backup to Restore")
            .setAdapter(adapter) { _, which ->
                val selectedFile = backupFiles[which]
                showRestoreConfirmationDialog(selectedFile, onRestoreComplete)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Shows confirmation dialog before restoring backup
     */
    private fun showRestoreConfirmationDialog(fileName: String, onRestoreComplete: (Boolean) -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Restore Backup")
            .setMessage("Are you sure you want to restore from this backup? This will overwrite your current data.")
            .setPositiveButton("Restore") { _, _ ->
                val success = restoreFromBackup(fileName)
                onRestoreComplete(success)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Restores data from a backup file
     */
    private fun restoreFromBackup(fileName: String): Boolean {
        return try {
            val file = File(context.filesDir, fileName)

            // Handle JSON restore
            if (fileName.endsWith(".json")) {
                restoreFromJson(file)
            }
            // Text files can't be automatically restored
            else if (fileName.endsWith(".txt")) {
                AlertDialog.Builder(context)
                    .setTitle("Text Backup")
                    .setMessage("Text backups can only be viewed but not automatically restored. Would you like to view this backup?")
                    .setPositiveButton("View") { _, _ ->
                        shareBackupFile(fileName)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                false
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Restore from JSON backup file
     */
    private fun restoreFromJson(file: File): Boolean {
        try {
            val jsonData = file.readText()

            // Parse JSON data
            val backupData = gson.fromJson(jsonData, JsonObject::class.java)

            // Restore user data if exists
            if (backupData.has("user")) {
                val user = gson.fromJson(backupData.get("user"), User::class.java)
                userRepository.saveUser(user)
            }

            // Restore transactions if exists
            if (backupData.has("transactions")) {
                val type = TypeToken.getParameterized(List::class.java, Transaction::class.java).type
                val transactions: List<Transaction> = gson.fromJson(backupData.get("transactions"), type)
                transactionRepository.saveTransactions(transactions)
            }

            // Restore main budget if exists
            if (backupData.has("mainBudget") && !backupData.get("mainBudget").isJsonNull) {
                val budget = gson.fromJson(
                    backupData.get("mainBudget"),
                    Budget::class.java
                )
                budgetRepository.setMainBudget(budget.amount)
            }

            // Restore category budgets if exists
            if (backupData.has("categoryBudgets")) {
                val type = TypeToken.getParameterized(List::class.java, CategoryBudget::class.java).type
                val categoryBudgets: List<CategoryBudget> = gson.fromJson(backupData.get("categoryBudgets"), type)
                categoryBudgets.forEach {
                    budgetRepository.setCategoryBudget(it)
                }
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Gets list of available backup files
     */
    private fun getAvailableBackupFiles(): List<String> {
        return context.filesDir.listFiles { file ->
            file.isFile && file.name.startsWith("CashWire_backup_") &&
                    (file.name.endsWith(".json") || file.name.endsWith(".txt"))
        }?.map { it.name } ?: emptyList()
    }

    /**
     * Shares a backup file
     */
    fun shareBackupFile(fileName: String) {
        try {
            val file = File(context.filesDir, fileName)
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".fileprovider",
                file
            )

            val mimeType = when {
                fileName.endsWith(".json") -> "application/json"
                fileName.endsWith(".txt") -> "text/plain"
                else -> "application/octet-stream"
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "CashWire Backup")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Backup"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}