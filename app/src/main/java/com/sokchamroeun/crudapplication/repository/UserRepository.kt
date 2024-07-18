package com.sokchamroeun.crudapplication.repository

import com.sokchamroeun.crudapplication.data.User
import com.sokchamroeun.crudapplication.data.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insert(user)
        }
    }

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun getUserById(userId: Int): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }

    suspend fun update(user: User) {
        withContext(Dispatchers.IO) {
            userDao.update(user)
        }
    }

    suspend fun delete(user: User) {
        withContext(Dispatchers.IO) {
            userDao.delete(user)
        }
    }

    suspend fun deleteById(userId: Int) {
        withContext(Dispatchers.IO) {
            userDao.deleteById(userId)
        }
    }
}
