package com.dev.cameronc.androidutilities

import android.support.v7.util.DiffUtil

class RecyclerViewDiffCalculator<T : Identifiable> : DiffUtil.Callback() {
    private lateinit var oldList: List<T>
    private lateinit var newList: List<T>

    fun calculateDiff(oldList: List<T>, newList: List<T>): DiffUtil.DiffResult {
        this.oldList = oldList
        this.newList = newList

        return DiffUtil.calculateDiff(this, true)
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean = oldList[oldPosition].id == newList[newPosition].id
    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean = oldList[oldPosition].id == newList[newPosition].id
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
}