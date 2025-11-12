package com.example.instagram.fragments.chattoFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.instagram.R
import com.example.instagram.chattoActivity.GroupChatActivity
import com.google.firebase.database.*

class GroupsFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val listOfGroups = ArrayList<String>()
    private lateinit var groupRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        groupRef = FirebaseDatabase.getInstance().reference.child("Groups")

        listView = view.findViewById(R.id.list_view)
        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listOfGroups)
        listView.adapter = arrayAdapter

        retrieveAndDisplayGroups()

        listView.setOnItemClickListener { _, _, position, _ ->
            val currentGroupName = listOfGroups[position]
            val groupChatIntent = Intent(context, GroupChatActivity::class.java)
            groupChatIntent.putExtra("groupName", currentGroupName)
            startActivity(groupChatIntent)
        }

        return view
    }

    private fun retrieveAndDisplayGroups() {
        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val set = HashSet<String>()
                for (snapshot in dataSnapshot.children) {
                    snapshot.key?.let { set.add(it) }
                }
                listOfGroups.clear()
                listOfGroups.addAll(set)
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
