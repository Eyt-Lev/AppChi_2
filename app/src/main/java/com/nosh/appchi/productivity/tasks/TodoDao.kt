package com.nosh.appchi.productivity.tasks

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTask(todoModel: TaskActivity.TodoModel):Long

    @Query("Select * from TodoModel where isFinished == 0 AND isDeleted == 0")
    fun getTask(): LiveData<List<TaskActivity.TodoModel>>

    @Query("Select * from TodoModel")
    fun getAll(): LiveData<List<TaskActivity.TodoModel>>

    @Query("UPDATE TodoModel SET title = :title, description = :description, category = :category, date = :date, time = :time, isFinished = :isFinished WHERE id = :uid")
    fun updateTask(uid: Long, title: String, description: String, category: String, date: Long, time: Long, isFinished: Int)

    @Query("Update TodoModel Set isFinished = 1 where id=:uid")
    fun finishTask(uid:Long)

    @Query("Update TodoModel Set isDeleted = 1 where id=:uid")
    suspend fun deleteTask(uid:Long)

}

