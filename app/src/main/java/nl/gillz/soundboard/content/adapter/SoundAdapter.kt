package nl.gillz.soundboard.content.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import nl.gillz.soundboard.R
import nl.gillz.soundboard.model.SoundItem
import android.widget.Filter
import android.widget.Filterable
import java.util.*

class SoundAdapter(context: Context,
                   var dataSource: ArrayList<SoundItem>) : BaseAdapter(), Filterable {

    var filterList: ArrayList<SoundItem> = dataSource
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val soundItem = getItem(position) as SoundItem
        if (soundItem.isSection) {
            // if section header
            view = inflater.inflate(R.layout.list_section_sound, parent, false)
            val titleTextView = view.findViewById(R.id.text_view_title) as TextView
            titleTextView.text = soundItem.title
        } else {
            // if item
            view = inflater.inflate(R.layout.list_item_sound, parent, false)
            val titleTextView = view.findViewById(R.id.text_view_title) as TextView
            val lengthTextView = view.findViewById(R.id.text_view_length) as TextView
            titleTextView.text = soundItem.title
            lengthTextView.text = "${soundItem.length} seconds"
        }

        return view
    }

    override fun getFilter(): Filter {
        val filter = object : Filter() {

            override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                var constraint = constraint

                val results = Filter.FilterResults()

                if (constraint != null && constraint.length > 0) {
                    //CONSTARINT TO UPPER
                    constraint = constraint.toString().toUpperCase()

                    val filters = ArrayList<SoundItem>()

                    //get specific items
                    for (i in 0 until filterList!!.size) {
                        if(filterList!!.get(i).isSection){
                            filters.add(filterList!!.get(i))
                        } else if (filterList!!.get(i).title.toUpperCase().contains(constraint)) {
                            filters.add(filterList!!.get(i))
                        }
                    }

                    results.count = filters.size
                    results.values = filters

                } else {
                    results.count = filterList!!.size
                    results.values = filterList

                }

                return results
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                // TODO Auto-generated method stub

                dataSource = results.values as ArrayList<SoundItem>
                notifyDataSetChanged()
            }
        }

        return filter
    }
}
