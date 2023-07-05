package com.example.miniproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject.databinding.FragmentShowProfileBinding

import com.google.firebase.database.*


class ShowProfileFragment : Fragment() {
    private  var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private  lateinit var root: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var filteredPostList:ArrayList<Post>
    private lateinit var postAdapter: ProfileAdapter
    private lateinit var contact:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentShowProfileBinding>(inflater, R.layout.fragment_show_profile, container, false)
        filteredPostList = ArrayList()
        contact = arguments?.getString("phno").toString()
        getuserdata()
        binding.profilename.text= contact
        val nametext = binding.profilename
        val occupationtxt = binding.occupation
        root = db.getReference().child("Profiles")
        root.child(contact).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve the value of the "name" key
                val name = dataSnapshot.child("Name").getValue(String::class.java)
                val occupation = dataSnapshot.child("occupation").getValue(String::class.java)
                if (name != null) {
                   nametext.text=name
                }
                if(occupation!=null)
                {
                    occupationtxt.text=occupation
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException())
            }
        })

        val recyclerView = binding.recyclerviewprofile
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getuserdata()
        postAdapter = ProfileAdapter(requireContext(),filteredPostList,contact)
        recyclerView.adapter = postAdapter
        return binding.root
    }
    private fun getuserdata() {
        val db: FirebaseDatabase = FirebaseDatabase.getInstance()
        val root: DatabaseReference = db.getReference().child("Users").child(contact)

        root.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                filteredPostList.clear() // Clear the list before adding new posts

                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        filteredPostList.add(post)
                    }
                }

                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })
    }

}