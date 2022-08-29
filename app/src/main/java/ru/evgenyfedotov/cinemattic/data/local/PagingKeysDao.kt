package ru.evgenyfedotov.cinemattic.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.evgenyfedotov.cinemattic.model.PagingKeys

@Dao
interface PagingKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pagingKeys: List<PagingKeys>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(pagingKey: PagingKeys)

    @Query("SELECT * FROM pagingkeys WHERE keyId LIKE :keyId")
    suspend fun getPagingKeyId(keyId: Int): PagingKeys?

    @Query("DELETE FROM pagingkeys")
    suspend fun clearAllPagingKeys()

}
