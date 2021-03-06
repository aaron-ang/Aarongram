package com.example.aarongram.fragments

import android.util.Log
import com.example.aarongram.Post
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment : FeedFragment() {

    // TODO add new adapter in onViewCreated, may need to remove super call

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        // GridLayoutManager vs StaggeredGridLayoutManager?
//        postsRecyclerView.layoutManager = StaggeredGridLayoutManager(3, resources.configuration.orientation)
//    }

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