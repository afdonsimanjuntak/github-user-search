package io.afdon.githubusersearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import io.afdon.search.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = (application as GithubSearchApp)
            .appComponent
            .getActivitySubcomponentFactory()
            .create(this@MainActivity)
            .getFragmentFactory()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace(android.R.id.content, SearchFragment::class.java, null)
            addToBackStack(SearchFragment::class.simpleName)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }
}