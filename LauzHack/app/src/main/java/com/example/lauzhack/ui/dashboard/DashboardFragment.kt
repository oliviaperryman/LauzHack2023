package com.example.lauzhack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lauzhack.R
import com.example.lauzhack.databinding.FragmentDashboardBinding
import com.example.lauzhack.ui.home.HomeFragment.Companion.globalData
import com.example.lauzhack.ui.home.Item

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recycler.adapter = CustomAdapter(globalData)
        binding.recycler.layoutManager = LinearLayoutManager(this.requireContext())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class CustomAdapter(private val dataSet: MutableList<Item>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val itemImg: ImageView
        val urlView: TextView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.item_description)
            itemImg = view.findViewById(R.id.item_img)
            urlView = view.findViewById(R.id.item_url)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
//        val full_description =
        val i: Item = dataSet[position]
        viewHolder.textView.text = i.name
        viewHolder.itemImg.setImageURI(i.uri)
        viewHolder.urlView.text = i.location
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
