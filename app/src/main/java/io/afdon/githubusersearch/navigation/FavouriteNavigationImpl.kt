package io.afdon.githubusersearch.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import io.afdon.favourite.navigation.FavouriteNavigation
import io.afdon.search.ui.detail.DetailFragment
import javax.inject.Inject

class FavouriteNavigationImpl @Inject constructor(
    private val appCompatActivity: AppCompatActivity
): FavouriteNavigation {

    override fun openDetail(login: String) {
        val bundle = Bundle().apply { putString("login", login) }
        appCompatActivity.supportFragmentManager.commit {
            replace(android.R.id.content, DetailFragment::class.java, bundle)
            addToBackStack(DetailFragment::class.simpleName)
        }
    }
}