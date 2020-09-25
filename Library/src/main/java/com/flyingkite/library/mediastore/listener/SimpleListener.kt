package com.flyingkite.library.mediastore.listener

import android.database.Cursor

open class SimpleListener<T> : DataListener<T> {
    var listener : DataListener<T>?
    constructor(li: DataListener<T>?) {
        listener = li
    }

    override fun onPreExecute() {
        listener?.onPreExecute()
    }

    override fun onQueried(count: Int, cursor: Cursor?) {
        listener?.onQueried(count, cursor)
    }

    override fun onProgress(position: Int, count: Int, data: T) {
        listener?.onProgress(position, count, data)
    }

    override fun onComplete(all: MutableList<T>?) {
        listener?.onComplete(all)
    }

    override fun onError(error: Exception?) {
        listener?.onError(error)
    }
}