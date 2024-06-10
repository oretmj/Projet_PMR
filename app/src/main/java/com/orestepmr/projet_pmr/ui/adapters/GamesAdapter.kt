package com.orestepmr.projet_pmr.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orestepmr.projet_pmr.R
import com.orestepmr.projet_pmr.models.Game

class GamesAdapter(private val partie_list: List<Game>) :
    RecyclerView.Adapter<GamesAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.nomPartie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.partie_view, parent, false) as View
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = partie_list[position].name
    }

    override fun getItemCount() = partie_list.size
}
