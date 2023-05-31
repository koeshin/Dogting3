package com.example.testfire.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.testfire.model.Friend
import com.example.testfire.MessageActivity
import com.example.testfire.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private lateinit var database: DatabaseReference
    private var friend: ArrayList<Friend> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        database = Firebase.database.reference
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        init {
            val myUid = Firebase.auth.currentUser?.uid.toString()

            FirebaseDatabase.getInstance().reference.child("users").child(myUid).child("location")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val myLocation = snapshot.getValue(String::class.java)
                        retrieveFriendList(myUid, myLocation)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // 오류 처리
                    }
                })
        }

        private fun retrieveFriendList(myUid: String, myLocation: String?) {
            FirebaseDatabase.getInstance().reference.child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        // 오류 처리
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        friend.clear()

                        for (data in snapshot.children) {
                            val item = data.getValue<Friend>()
                            if (item?.uid.equals(myUid)) {
                                // 본인은 친구창에서 제외
                                // 본인 Friend 값 가져
                                continue
                            }

                            val location = item?.location
                            if (location == myLocation) {
                                friend.add(item!!)
                            }
                        }

                        notifyDataSetChanged()
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_home, parent, false)
            )
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.home_item_iv)
            val textView: TextView = itemView.findViewById(R.id.home_item_tv)
            val textViewcharacter: TextView = itemView.findViewById(R.id.home_item_character)
            val textViewclass: TextView = itemView.findViewById(R.id.home_item_class)
            val textViewsex: TextView = itemView.findViewById(R.id.home_item_sex)
            val textViewage: TextView = itemView.findViewById(R.id.home_item_age)

        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            Glide.with(holder.itemView.context)
                .load(friend[position].profileImageUrl)
                .transform(CircleCrop())
                .into(holder.imageView)
            holder.textView.text = friend[position].name
            holder.textViewcharacter.text = friend[position].dog?.dcharacter
            holder.textViewclass.text = friend[position].dog?.dclass
            holder.textViewage.text = friend[position].dog?.dage
            holder.textViewclass.text = friend[position].dog?.dsex

            holder.itemView.setOnClickListener {
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("destinationUid", friend[position].uid)
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return friend.size
        }
    }
}