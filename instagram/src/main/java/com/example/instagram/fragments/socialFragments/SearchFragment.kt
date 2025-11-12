package com.example.instagram.fragments.socialFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagram.adapter.socialAdapter.UserAdapter
import com.example.instagram.model.socialModel.User
import com.example.instagram.databinding.FragmentSearchBinding
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        // RecyclerView setup
        binding.recyclerViewSearch.setHasFixedSize(true)
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>, true) }
        binding.recyclerViewSearch.adapter = userAdapter

        // Search EditText listener
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = binding.searchEditText.text.toString()
                if (input.isEmpty()) {
                    binding.recyclerViewSearch.visibility = View.GONE
                } else {
                    binding.recyclerViewSearch.visibility = View.VISIBLE
                    retrieveUsers()
                    searchUser(input.lowercase())
                }
            }
        })

        return view
    }

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) mUser?.add(user)
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun retrieveUsers() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val input = binding.searchEditText.text.toString()
                if (input.isEmpty()) {
                    mUser?.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) mUser?.add(user)
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
