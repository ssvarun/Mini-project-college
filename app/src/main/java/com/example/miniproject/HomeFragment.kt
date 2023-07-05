package com.example.miniproject

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*


class HomeFragment : Fragment() {
    private lateinit var bottomnav:BottomNavigationView
    private lateinit var postAdapter: HomeAdapter
    var phno:String?=null
    private lateinit var postlist:ArrayList<Post>
    private var flag=false
    private  var db:FirebaseDatabase= FirebaseDatabase.getInstance()
    private  lateinit var root: DatabaseReference
    private lateinit var searchbtn:ImageView
    private lateinit var searchtext:EditText
    private var fcmToken: String = "hii"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater,R.layout.fragment_home,container,false)

        bottomnav=binding.bottomNavigation
        setUserLoggedin()

//        phno = arguments?.getString("phno").toString()

          val name = arguments?.getString("name")
          val occupation = arguments?.getString("occupation")
          phno = arguments?.getString("getphno")
          root = db.getReference().child("Profiles")


        root.child(phno!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                } else {
                        flag = true
                        val profileref = root.child(phno!!)
                        val usermap: HashMap<String, Any> = HashMap()
                        usermap.put("Name",name!!)
                        usermap.put("occupation", occupation!!)
                        usermap.put("fcmtoken",fcmToken.toString())
                        profileref.setValue(usermap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error querying user information: ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })

        binding.apply {
            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.homw_nav -> {
                        true
                    }
                    R.id.imagepost -> {
                        val bundle = Bundle()
                        bundle.putString("phno",phno)
                        view?.findNavController()
                            ?.navigate(R.id.action_homeFragment_to_addPostFragment,bundle)
                        true
                    }
                    R.id.set_nav -> {
                        view?.findNavController()
                            ?.navigate(R.id.action_homeFragment_to_settingsFragment)
                        true
                    }

                    R.id.person_nav -> {
                        val bundle = Bundle()
                        bundle.putString("phno",phno)
                        view?.findNavController()
                            ?.navigate(R.id.action_homeFragment_to_showProfileFragment,bundle)
                        true
                    }

                    else -> false

                }
            }
            searchtext = binding.searchbar.searchtext
            searchbtn = binding.searchbar.searchbtn
            searchbtn.setOnClickListener {
                getuserdata()
            }
           val recyclerView = binding.recyclerView
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            postlist = ArrayList<Post>()
            postAdapter = HomeAdapter(requireContext(), postlist,phno.toString())
            recyclerView.adapter = postAdapter
            getuserdata()
        }
        return binding.root
        }

    private fun setUserLoggedin() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPrefs.edit()
        editor.putBoolean("hasSignedUp", true)
        editor.apply()

    }

    private fun getuserdata() {
        var db: FirebaseDatabase = FirebaseDatabase.getInstance()
        var root: DatabaseReference =db.getReference().child("Users")

        root.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredPostList = ArrayList<Post>()
                if(snapshot.exists())
                {
                    for (phoneNumberSnapshot in snapshot.children) {
                        // Iterate over all the posts for this phone number
                        for (postSnapshot in phoneNumberSnapshot.children) {
                            // Get the post data
                            val post = postSnapshot.getValue(Post::class.java)
                            if (post != null) {
                                // Add the post to the list
                                filteredPostList.add(post)
                            }
                        }
                    }
                    filteredPostList.reverse()
                    if (searchtext.text.toString().isNotEmpty()) {
                        val searchQuery = searchtext.text.toString().toLowerCase()
                        postlist.clear()
                        for (post in filteredPostList) {
                            if (post.category?.toLowerCase()?.contains(searchQuery) == true) {
                                postlist.add(post)
                            }
                        }
                    } else {
                        postlist.clear()
                        postlist.addAll(filteredPostList)
                    }
                    postAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchtext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getuserdata()
            }
        })
    }
    override fun onResume() {
        super.onResume()
        bottomnav.selectedItemId=R.id.homw_nav
        phno= arguments?.getString("getphno")
    }


}