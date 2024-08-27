package com.ujwal.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ujwal.roomdemo.databinding.ListItemBinding
import com.ujwal.roomdemo.db.Subscriber

class MyRecyclerViewAdapter(private val clickListener: (Subscriber) -> Unit) :
    RecyclerView.Adapter<MyViewHolder>() {

    private val subscriberList = ArrayList<Subscriber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return subscriberList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscriberList[position], clickListener)
    }

    fun setList(subscribers: List<Subscriber>) {
        subscriberList.clear()
        subscriberList.addAll(subscribers)
    }
}

class MyViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(subscriber: Subscriber, clickListener: (Subscriber) -> Unit) {
        binding.apply {
            nameTextView.text = subscriber.name
            emailTextView.text = subscriber.email
            listItemLayout.setOnClickListener {
                clickListener(subscriber)
            }
        }
    }
}