package com.orestepmr.projet_pmr.ui.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orestepmr.projet_pmr.R
import com.orestepmr.projet_pmr.models.Game

class GamesAdapter(private val mContext: Context) :
    RecyclerView.Adapter<GamesAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.nomPartie)
        val textViewDate: TextView = view.findViewById(R.id.date)

    }

    private var gameList = ArrayList<Game>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.partie_view, parent, false) as View
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = gameList[position].name
        holder.textViewDate.text = gameList[position].date

    }

    override fun getItemCount() = gameList.size

    fun replaceData(newData: List<Game>){
        gameList = ArrayList(newData)
        this.notifyDataSetChanged()
    }
}
