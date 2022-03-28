package com.example.aarongram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aarongram.Post
import com.example.aarongram.R
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment : FeedFragment() {

    override fun queryPosts() {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        // Only return posts from currently signed in user
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())
        // Return newest posts first
        query.addDescendingOrder("createdAt")
        // Return only most recent 20 posts
        query.limit = 20

        adapter.clear()

        query.findInBackground { posts, e ->
            if (e != null) {
                Log.e(TAG, "Error fetching posts")
            } else {
                if (posts != null) {
                    swipeContainer.isRefreshing = false
                    for (post in posts) {
                        Log.i(
                            TAG,
                            "Post: ${post.getDescription()}, username: ${post.getUser()?.username}"
                        )
                    }
                    allPosts.addAll(posts)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}