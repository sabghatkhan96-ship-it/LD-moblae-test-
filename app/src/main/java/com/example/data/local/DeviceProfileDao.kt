package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DeviceProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceProfileDao {

    @Query("SELECT * FROM device_profiles ORDER BY isActive DESC, id ASC")
    fun getAllProfiles(): Flow<List<DeviceProfile>>

    @Query("SELECT * FROM device_profiles WHERE isActive = 1 LIMIT 1")
    fun getActiveProfile(): Flow<DeviceProfile?>

    @Query("SELECT * FROM device_profiles WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveProfileSync(): DeviceProfile?

    @Query("SELECT * FROM device_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): DeviceProfile?

    @Query("SELECT COUNT(*) FROM device_profiles")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: DeviceProfile): Long

    @Update
    suspend fun updateProfile(profile: DeviceProfile)

    @Delete
    suspend fun deleteProfile(profile: DeviceProfile)

    @Query("UPDATE device_profiles SET isActive = CASE WHEN id = :selectedId THEN 1 ELSE 0 END")
    suspend fun setActiveProfile(selectedId: Long)

    @Query("UPDATE device_profiles SET lastTestStatus = :status, lastTestedAt = :time WHERE id = :id")
    suspend fun updateProxyStatus(id: Long, status: String, time: Long)
}
