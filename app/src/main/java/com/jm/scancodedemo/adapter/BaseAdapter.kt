package com.jm.scancodedemo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

abstract class BaseAdapter<T, VH : BaseViewHolder>(
    @LayoutRes private val layoutResId: Int,
    private var dataList: MutableList<T> = arrayListOf()
) : RecyclerView.Adapter<VH>() {

    private var onItemClickListener: ((View, Int, T) -> Unit)? = null
    private var onItemLongClickListener: ((View, Int, T) -> Unit)? = null
    private var onItemChildClickListener: ((View, Int, T) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val baseViewHolder: VH
        val viewHolder = onCreateDefViewHolder(parent, viewType)
        bindViewClickListener(viewHolder, viewType)
        baseViewHolder = viewHolder
        return baseViewHolder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        convert(holder, getItemViewType(position), getItem(position), position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    open fun getItem(@IntRange(from = 0) position: Int): T {
        return dataList[position]
    }

    protected open fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createBaseViewHolder(parent, layoutResId)
    }

    protected open fun createBaseViewHolder(parent: ViewGroup, @LayoutRes layoutResId: Int): VH {
        return createBaseViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        )
    }

    private val childClickViewIds = LinkedHashSet<Int>()

    fun getChildClickViewIds(): LinkedHashSet<Int> {
        return childClickViewIds
    }

    fun addChildClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childClickViewIds.add(viewId)
        }
    }

    protected open fun bindViewClickListener(viewHolder: VH, viewType: Int) {
        val itemView: View = viewHolder.itemView
        onItemClickListener?.let {
            itemView.setOnClickListener { v ->
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                it(
                    v,
                    position,
                    dataList[position]
                )
            }
        }
        onItemLongClickListener?.let {
            itemView.setOnLongClickListener { v ->
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnLongClickListener false
                }
                it(
                    v,
                    position,
                    dataList[position]
                )
                true
            }
        }
        onItemChildClickListener?.let {
            for (id in getChildClickViewIds()) {
                viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                    if (!childView.isClickable) {
                        childView.isClickable = true
                    }
                    childView.setOnClickListener { v ->
                        val position = viewHolder.bindingAdapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnClickListener
                        }
                        it(v, position, dataList[position])
                    }
                }
            }
        }

    }

    @Suppress("UNCHECKED_CAST")
    protected open fun createBaseViewHolder(view: View): VH {
        var temp: Class<*>? = javaClass
        var z: Class<*>? = null
        while (z == null && null != temp) {
            z = getInstancedGenericKClass(temp)
            temp = temp.superclass
        }

        val vh: VH? = if (z == null) {
            BaseViewHolder(view) as VH
        } else {
            createBaseGenericKInstance(z, view)
        }
        return vh ?: BaseViewHolder(view) as VH
    }

    private fun getInstancedGenericKClass(z: Class<*>): Class<*>? {
        try {
            val type = z.genericSuperclass
            if (type is ParameterizedType) {
                val types = type.actualTypeArguments
                for (temp in types) {
                    if (temp is Class<*>) {
                        if (BaseViewHolder::class.java.isAssignableFrom(temp)) {
                            return temp
                        }
                    } else if (temp is ParameterizedType) {
                        val rawType = temp.rawType
                        if (rawType is Class<*> && BaseViewHolder::class.java.isAssignableFrom(
                                rawType
                            )
                        ) {
                            return rawType
                        }
                    }
                }
            }
        } catch (e: java.lang.reflect.GenericSignatureFormatError) {
            e.printStackTrace()
        } catch (e: TypeNotPresentException) {
            e.printStackTrace()
        } catch (e: java.lang.reflect.MalformedParameterizedTypeException) {
            e.printStackTrace()
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun createBaseGenericKInstance(z: Class<*>, view: View): VH? {
        try {
            val constructor: Constructor<*>

            return if (z.isMemberClass && !Modifier.isStatic(z.modifiers)) {
                constructor = z.getDeclaredConstructor(javaClass, View::class.java)
                constructor.isAccessible = true
                constructor.newInstance(this, view) as VH
            } else {
                constructor = z.getDeclaredConstructor(View::class.java)
                constructor.isAccessible = true
                constructor.newInstance(view) as VH
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return null
    }

    fun clear() {
        dataList.clear()
        refreshData()
    }


    fun setData(@IntRange(from = 0) index: Int, data: T) {
        if (index >= this.dataList.size) {
            return
        }
        this.dataList[index] = data
        notifyItemInserted(index)
    }

    fun setData(list: Collection<T>?) {
        if (list !== this.dataList) {
            this.dataList.clear()
            if (!list.isNullOrEmpty()) {
                this.dataList.addAll(list)
            }
        } else {
            if (!list.isNullOrEmpty()) {
                val newList = ArrayList(list)
                this.dataList.clear()
                this.dataList.addAll(newList)
            } else {
                this.dataList.clear()
            }
        }
        refreshData()
    }

    fun addData(data: T) {
        dataList.add(data)
        notifyItemInserted(dataList.size - 1)
    }

    fun addData(@IntRange(from = 0) position: Int, data: T) {
        if (position >= dataList.size) {
            addData(data)
        } else {
            dataList.add(position, data)
            notifyItemInserted(position)
        }
    }

    fun addData(data: MutableList<T>) {
        dataList.addAll(data)
        notifyItemRangeInserted(dataList.size - 1, data.size)
    }

    fun addData(@IntRange(from = 0) position: Int, data: MutableList<T>) {
        if (position >= dataList.size) {
            addData(data)
        } else {
            dataList.addAll(position, data)
            notifyItemRangeInserted(position, data.size)
        }
    }

    private fun removeAt(@IntRange(from = 0) position: Int) {
        if (position >= dataList.size) {
            return
        }
        this.dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeData(data: T) {
        val index = this.dataList.indexOf(data)
        if (index == -1) {
            return
        }
        removeAt(index)
    }

    fun removeData(@IntRange(from = 0) index: Int) {
        removeAt(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData() {
        notifyDataSetChanged()
    }

    fun setItemOnClickListener(onItemClickListener: (view: View, position: Int, data: T) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }
    fun setItemChildOnClickListener(onItemChildClickListener: (view: View, position: Int, data: T) -> Unit) {
        this.onItemChildClickListener = onItemChildClickListener
    }


    abstract fun convert(holder: VH, type: Int, item: T, position: Int)
}

abstract class BaseDBAdapter<T, DB : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    BaseAdapter<T, BaseDBViewHolder<DB>>(layoutResId, mutableListOf()) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseDBViewHolder<DB> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutResId,
            parent,
            false
        ) as DB
        return BaseDBViewHolder(binding)
    }
}