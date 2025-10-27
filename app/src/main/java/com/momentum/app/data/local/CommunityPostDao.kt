package com.momentum.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityPostDao {
    @Insert
    suspend fun insert(post: CommunityPostEntity): Long

    @Query("SELECT * FROM community_posts ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CommunityPostEntity>>

    @Query("SELECT * FROM community_posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeByUser(userId: Long): Flow<List<CommunityPostEntity>>

    @Query("UPDATE community_posts SET likes = likes + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Long)

    @Query("UPDATE community_posts SET comments = comments + 1 WHERE id = :postId")
    suspend fun incrementComments(postId: Long)
}