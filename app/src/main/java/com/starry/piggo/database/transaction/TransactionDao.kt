/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
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


package com.starry.piggo.database.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class DashboardDepositSummary(
    val transactionId: Long,
    val ownerGoalId: Long,
    val goalTitle: String,
    val goalIconId: String?,
    val timeStamp: Long,
    val amount: Double,
    val notes: String
)

@Dao
interface TransactionDao {

    /**
     * Insert transaction.
     * @param transaction Transaction to insert.
     */
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query(
        "SELECT tx.transactionId AS transactionId, tx.ownerGoalId AS ownerGoalId, " +
                "goal.title AS goalTitle, goal.goalIconId AS goalIconId, " +
                "tx.timeStamp AS timeStamp, tx.amount AS amount, tx.notes AS notes " +
                "FROM `transaction` AS tx " +
                "INNER JOIN saving_goal AS goal ON goal.goalId = tx.ownerGoalId " +
                "WHERE tx.type = 0 " +
                "ORDER BY tx.timeStamp DESC"
    )
    fun getDashboardDeposits(): Flow<List<DashboardDepositSummary>>

    /**
     * Delete transaction.
     * @param transaction Transaction to delete.
     */
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    /**
     * Update transaction.
     * @param transaction Transaction to update.
     */
    @Update
    suspend fun updateTransaction(transaction: Transaction)
}
