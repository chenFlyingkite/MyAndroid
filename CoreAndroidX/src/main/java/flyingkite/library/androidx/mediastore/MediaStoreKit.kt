package flyingkite.library.androidx.mediastore;

import android.content.Context
import android.database.Cursor
import flyingkite.library.android.log.Loggable
import flyingkite.library.androidx.mediastore.request.MediaGroupRequest
import flyingkite.library.androidx.mediastore.request.MediaRequest
import flyingkite.library.java.functional.Projector
import flyingkite.library.java.util.StringParseUtil

open class MediaStoreKit : Loggable, StringParseUtil {

    /**
     * Context for obtaining android.content.ContentResolver
     */
    val context : Context?

    constructor(c : Context?) {
        context = c
    }

    /**
     * Core method for querying content resolver for media / files
     */
    fun queryRequest(request : MediaRequest) {
        if (context == null) {
            return
        }

        //logE("queryRequest $request")
        val r = request
        var c : Cursor? = null
        try {
            r.listener?.onPreExecute()
            c = context.contentResolver.query(r.uri, r.projection, r.selection, r.selectionArgs, r.sortOrder, r.cancelSignal)
            if (c == null) {
                val e = Exception("null == ContentResolver#query()")
                r.listener?.onError(e)
                return
            }
            // rowN records fetched
            val rowN = c.count

            r.listener?.onQueried(rowN, c)
            val all = arrayListOf<Map<String, String>>()
            if (c.moveToFirst()) {
                // Each row has colN fields
                val colN = c.columnCount
                var now = 0
                // Create each record as Map & add to all
                do {
                    val data = HashMap<String, String>()
                    for (i in 0 until colN) {
                        val t = c.getType(i)
                        val pass = t == Cursor.FIELD_TYPE_NULL || t == Cursor.FIELD_TYPE_BLOB
                        if (pass) continue

                        val k = c.getColumnName(i)
                        val v = c.getString(i) ?: "null"
                        if (data[k] != null) {
                            logE("$k exists in map (${data.size} items)")
                            logE("data = $data")
                        }
                        data[k] = v
                    }
                    r.listener?.onProgress(now, rowN, data)
                    now++
                    all.add(data)
                    //logE("now = #%4d : %s", now, data);
                } while (c.moveToNext())
            }
            // Fetch complete
            r.listener?.onComplete(all)
        } catch (e : Exception) {
            e.printStackTrace()
            r.listener?.onError(e)
        } finally {
            flyingkite.library.android.util.IOUtil.closeIt(c)
        }
    }

    /**
     * Easy method for queryRequest(request, projector)
     * GetterS takes key from column named <groupBy>, use <nullKey> if column is null
     */
    fun queryRequest(request : MediaGroupRequest, groupBy : String, nullKey : String) {
        val projector = Projector<Cursor, String> { c: Cursor? ->
            val ki = c?.getColumnIndex(groupBy) ?: -1
            val k = c?.getString(ki) ?: nullKey
            return@Projector k
        }
        queryRequest(request, projector)
    }

    /**
     * Core method for querying contents resolver for media with grouped by generic key provider, projector.
     */
    fun queryRequest(request : MediaGroupRequest, projector: Projector<Cursor, String>) {
        if (context == null) {
            return
        }

        //logE("queryRequest & proj $request")
        val r = request
        var c : Cursor? = null
        try {
            r.listener?.onPreExecute()
            c = context.contentResolver.query(r.uri, r.projection, r.selection, r.selectionArgs, r.sortOrder, r.cancelSignal)
            if (c == null) {
                val e = Exception("null == ContentResolver#query()")
                r.listener?.onError(e)
                return
            }

            if (c.moveToFirst()) {
                // Each row has colN fields
                val colN = c.columnCount
                val all = HashMap<String, MutableList<Map<String, String>>>()
                // <all> = { ( <group> : {(<data>)*} )* }
                do {
                    // Peeking group key
                    val key = projector.get(c)
                    if (key == null) {
                        val e = NullPointerException("projector.get(cursor) = null")
                        r.listener?.onError(e)
                        return
                    }

                    val data = HashMap<String, String>()
                    for (i in 0 until colN) {
                        val k = c.getColumnName(i)
                        val v = c.getString(i) ?: "null"
                        if (data[k] != null) {
                            logE("$k exists in map (${data.size} items)")
                            logE("data = $data")
                        }
                        data[k] = v
                    }

                    // Add data to key and use _null if key = null
                    val groupKey = projector.get(c)//c.getString(groupIndex) ?: nullKey
                    val list = all[groupKey] ?: arrayListOf()
                    list.add(data)
                    all[groupKey] = list
                    //logE("#%4d : %s", list.size, data);
                } while (c.moveToNext())

                // Report progress
                val rowN = all.size
                val allList = all.values.toList()
                r.listener?.onQueried(rowN, c)
                for ((i, list) in allList.withIndex()) {
                    r.listener?.onProgress(i, rowN, list)
                }

                // Fetch complete
                r.listener?.onComplete(allList)
            }
        } catch (e : Exception) {
            e.printStackTrace()
            r.listener?.onError(e)
        } finally {
            flyingkite.library.android.util.IOUtil.closeIt(c)
        }
    }
}
