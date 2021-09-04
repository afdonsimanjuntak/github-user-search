package io.afdon.githubusersearch.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import io.afdon.favourite.ui.FavouriteFragment
import io.afdon.search.navigation.SearchNavigation
import io.afdon.search.ui.detail.DetailFragment
import javax.inject.Inject

class SearchNavigationImpl @Inject constructor(
    private val appCompatActivity: AppCompatActivity
): SearchNavigation {

    override fun showFavourites() {
        appCompatActivity.supportFragmentManager.commit {
            replace(android.R.id.content, FavouriteFragment::class.java, null)
            addToBackStack(FavouriteFragment::class.simpleName)
        }
    }

    override fun openDetail(login: String) {
        val bundle = Bundle().apply { putString("login", login) }
        appCompatActivity.supportFragmentManager.commit {
            replace(android.R.id.content, DetailFragment::class.java, bundle)
            addToBackStack(DetailFragment::class.simpleName)
        }
    }
}