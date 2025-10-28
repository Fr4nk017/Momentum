package com.momentum.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ClientEntity::class,
        UserEntity::class,
        FriendEntity::class,
        CommunityPostEntity::class,
        RecentPlaceEntity::class,
        HikingSessionEntity::class,
        LocationPointEntity::class,
        DiaryEntryEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun userDao(): UserDao
    abstract fun friendDao(): FriendDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun recentPlaceDao(): RecentPlaceDao
    abstract fun hikingSessionDao(): HikingSessionDao
    abstract fun locationPointDao(): LocationPointDao
    abstract fun diaryEntryDao(): DiaryEntryDao
}