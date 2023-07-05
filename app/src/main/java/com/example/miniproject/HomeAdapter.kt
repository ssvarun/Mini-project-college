package com.example.miniproject

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*

class HomeAdapter(val context: Context, val postlist: List<Post>,val currentphno:String) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = postlist[position]
        CoroutineScope(Dispatchers.Main).launch {
            coroutineScope {
                val finish = async {
                    Picasso.get().load(currentitem.image).into(holder.postImage)
                }
                finish.await()
            }
        }
        holder.contact.setOnClickListener { view: View ->
            if(currentphno==currentitem.contactnumber)
                Toast.makeText(context,"You cannot contact yourself",Toast.LENGTH_SHORT).show()
            if (isCallPermissionGranted() && currentphno!=currentitem.contactnumber) {
                dialPhoneNumber(currentitem.contactnumber.toString())
            }
            else {
                requestCallPermission()
            }
        }
        holder.description.text = currentitem.description
        holder.title.text = currentitem.title
    }

    private fun isCallPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCallPermission() {
        ActivityCompat.requestPermissions(
            context as AppCompatActivity,
            arrayOf(Manifest.permission.CALL_PHONE),
            REQUEST_CALL_PERMISSION
        )
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Cannot make a call", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return postlist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.imageThumbnail)
        val title: TextView = itemView.findViewById(R.id.titletxt)
        val description: TextView = itemView.findViewById(R.id.descriptiontxt)
        val contact: LinearLayout = itemView.findViewById(R.id.contactbtn)
    }

    companion object {
        private const val REQUEST_CALL_PERMISSION = 1
    }
}
