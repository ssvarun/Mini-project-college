package com.example.miniproject

import android.content.Context
import android.database.DataSetObserver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
private  var db: FirebaseDatabase = FirebaseDatabase.getInstance()
private  lateinit var root: DatabaseReference

class ProfileAdapter(val context: Context, val postlist:List<Post>,val phno:String) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>(),
    Adapter {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val category: TextView
        val deletebtn:Button
        init {
            title=itemView.findViewById(R.id.profiletitletxt)
            category=itemView.findViewById(R.id.profilecategorytxt)
            deletebtn= itemView.findViewById(R.id.deletebtn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.profilestyle,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem=postlist[position]
        holder.category.text=currentitem.category
        holder.title.text=currentitem.title
        holder.deletebtn.setOnClickListener {
            root = db.getReference().child("Users").child(phno)

            root.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val childTitle = snapshot.child("title").getValue(String::class.java)
                        if (childTitle == currentitem.title) {
                            snapshot.ref.removeValue() // Remove the entire node
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors
                }
            })


        }

    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

}