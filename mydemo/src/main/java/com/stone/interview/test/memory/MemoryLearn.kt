package com.stone.interview.test.memory

import android.util.Log
import com.stone.interview.common.beans.Animal

class MemoryLearn {
    val TAG = "MemoryLearn"

    companion object {
        val  animals = ArrayList<Animal>()
    }


    fun start() {
        for(i in 1..1000000000) {
            val animal = Animal(i, "animal--$i")

            animals.add(animal)

        }
        Log.d(TAG, "animals size : $${animals.size}")
    }
}