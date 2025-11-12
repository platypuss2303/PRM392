package com.example.instagram.adapter.chattoAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.instagram.fragments.chattoFragments.*

class TabsAccessorAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> GroupsFragment()
            2 -> ContactsFragment()
            3 -> RequestsFragment()
            else -> ChatsFragment()
        }
    }

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Chats"
            1 -> "Groups"
            2 -> "Contacts"
            3 -> "Requests"
            else -> ""
        }
    }
}
