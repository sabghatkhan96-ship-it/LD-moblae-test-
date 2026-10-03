package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VaultItem
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {

    @Query("SELECT * FROM vault_items ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllItems(): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE category = :category ORDER BY isFavorite DESC, updatedAt DESC")
    fun getItemsByCategory(category: String): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE title LIKE '%' || :query || '%' OR domainOrPackage LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun searchVault(query: String): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE domainOrPackage LIKE '%' || :query || '%' OR :query LIKE '%' || domainOrPackage || '%'")
    suspend fun findMatchingAutofillItems(query: String): List<VaultItem>

    @Query("SELECT * FROM vault_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): VaultItem?

    @Query("SELECT COUNT(*) FROM vault_items")
    suspend fun getItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VaultItem): Long

    @Update
    suspend fun updateItem(item: VaultItem)

    @Delete
    suspend fun deleteItem(item: VaultItem)

    @Query("UPDATE vault_items SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)
}
