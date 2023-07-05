package com.example.miniproject

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.miniproject.databinding.FragmentAddPostBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap


class AddPostFragment : Fragment() {
    private val REQUEST_PICK_IMAGE = 1

    private lateinit var image:ImageView

    lateinit var imageUri:Uri
    lateinit var title:String
    lateinit var description:String
    lateinit var category:String

    lateinit var animationView: LottieAnimationView
    lateinit var layout:LinearLayout
    private  var db:FirebaseDatabase= FirebaseDatabase.getInstance()
    private  lateinit var root: DatabaseReference
    private val reference: StorageReference = FirebaseStorage.getInstance().getReference()
    var flag = false
    private lateinit var phno:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAddPostBinding>(inflater, R.layout.fragment_add_post, container, false)

        phno = arguments?.getString("phno").toString()
        root = db.getReference().child("Users").child(phno)
        image = binding.addimage

        image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }

        binding.createpostbtn.setOnClickListener {
            title = binding.addtitle.text.toString()
            description = binding.adddescription.text.toString()
            category = binding.categorytxt.text.toString()
            if(title.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty() && flag==true) {
                uploaddata()
            }
            else
                Toast.makeText(requireContext(),"Enter all the credentials",Toast.LENGTH_SHORT).show()
        }
        animationView=binding.loadingAnim
        layout=binding.linearlayoutpost

        return binding.root
    }


    private fun uploaddata() {
        layout.visibility = View.GONE
        animationView.visibility = View.VISIBLE
        animationView.playAnimation()
        val usermap: HashMap<String, Any> = HashMap()
        usermap.put("title", title)
        usermap.put("description", description)
        usermap.put("category",category)
        usermap.put("timestamp", ServerValue.TIMESTAMP)
        usermap.put("contactnumber",phno)
        val originalFileName = imageUri.lastPathSegment
        var fileExtension: String? = null
        if (!originalFileName.isNullOrEmpty()) {
            val lastDotIndex = originalFileName.lastIndexOf(".")
            if (lastDotIndex != -1) {
                fileExtension = originalFileName.substring(lastDotIndex)
            }
        }
        val uniqueFileName = UUID.randomUUID().toString() + fileExtension

        // Upload image to Storage
        val fileRef: StorageReference = reference.child("post_images/$uniqueFileName")
        fileRef.putFile(imageUri).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get the download URL of the uploaded image
                fileRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                    if (downloadUrlTask.isSuccessful) {
                        val downloadUrl = downloadUrlTask.result.toString()
                        // Set image download URL and user data in Realtime Database
                        val postId = root.push().key!!
                        val contactRef = root.child(postId) // Create child reference with contact as key
                        contactRef.setValue(usermap).addOnCompleteListener { setUserDataTask ->
                            if (setUserDataTask.isSuccessful) {
                                contactRef.child("image").setValue(downloadUrl).addOnCompleteListener { setImageUrlTask ->
                                    if (setImageUrlTask.isSuccessful) {
                                        animationView.cancelAnimation()
                                        animationView.visibility = View.GONE
                                        layout.visibility = View.VISIBLE
                                        Toast.makeText(requireContext(), "Data Saved and Image Uploaded Successfully", Toast.LENGTH_LONG).show()
                                        image.setImageURI(null)
                                    } else {
                                        animationView.cancelAnimation()
                                        animationView.visibility = View.GONE
                                        layout.visibility = View.VISIBLE
                                        // Handle error in setting image URL in Realtime Database
                                    }
                                }
                            } else {
                                animationView.cancelAnimation()
                                animationView.visibility = View.GONE
                                layout.visibility = View.VISIBLE
                                // Handle error in setting user data in Realtime Database
                            }
                        }
                    } else {
                        animationView.cancelAnimation()
                        animationView.visibility = View.GONE
                        layout.visibility = View.VISIBLE
                        // Handle error in getting download URL
                    }
                }
            } else {
                animationView.cancelAnimation()
                animationView.visibility = View.GONE
                layout.visibility = View.VISIBLE
                // Handle error in uploading image to Storage
            }
        }
    }


    private fun getFileExtension(uri: Uri): String {
        val cr: ContentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))!!
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            // Get the URI of the selected image
            imageUri = data.data!!
            image.setImageURI(imageUri)
            flag=true
            // Pass the URI to the next activity or use it as needed
            // For example, start the next activity with an Intent
//            val intent = Intent(this, DisplayImageActivity::class.java)
//            intent.putExtra("imageUri", imageUri.toString())
//            startActivity(intent)
        }
        else
            Toast.makeText(requireContext(),"Add the image of your donation",Toast.LENGTH_SHORT).show()

    }
}