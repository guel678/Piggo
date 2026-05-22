/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] StÉ‘rry ShivÉ‘m
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.piggo.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.starry.piggo.database.goal.GoalDao
import com.starry.piggo.database.goal.DashboardGoalSummary
import com.starry.piggo.database.transaction.Transaction
import com.starry.piggo.database.transaction.TransactionDao
import com.starry.piggo.database.transaction.TransactionType
import com.starry.piggo.ui.screens.settings.DateStyle
import com.starry.piggo.utils.NumberUtils
import com.starry.piggo.utils.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    goalDao: GoalDao,
    private val transactionDao: TransactionDao,
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    val goalsList = goalDao.getDashboardGoalSummaries().asLiveData()
    val depositsList = transactionDao.getDashboardDeposits().asLiveData()

    fun transfer(
        fromGoal: DashboardGoalSummary,
        toGoal: DashboardGoalSummary,
        amountText: String,
        onComplete: (Boolean) -> Unit
    ) {
        val amount = amountText.toDoubleOrNull()?.let { NumberUtils.roundDecimal(it) }
        if (amount == null || amount <= 0 || amount > fromGoal.savedAmount || fromGoal.goalId == toGoal.goalId) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            transactionDao.insertTransaction(
                Transaction(
                    ownerGoalId = fromGoal.goalId,
                    type = TransactionType.Withdraw,
                    timeStamp = now,
                    amount = amount,
                    notes = "Transfer to ${toGoal.title}"
                )
            )
            transactionDao.insertTransaction(
                Transaction(
                    ownerGoalId = toGoal.goalId,
                    type = TransactionType.Deposit,
                    timeStamp = now,
                    amount = amount,
                    notes = "Transfer from ${fromGoal.title}"
                )
            )
            onComplete(true)
        }
    }

    fun getDefaultCurrency(): String {
        return preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")!!
    }

    fun getDateStyle(): DateStyle {
        return preferenceUtil.getInt(PreferenceUtil.DATE_STYLE_INT, DateStyle.DD_MM_YYYY.ordinal)
            .let { DateStyle.entries[it] }
    }
}
