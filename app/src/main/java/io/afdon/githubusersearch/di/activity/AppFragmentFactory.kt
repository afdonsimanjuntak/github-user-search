package io.afdon.githubusersearch.di.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject
import javax.inject.Provider

class AppFragmentFactory @Inject constructor(
    private val fragmentClassMap: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragmentClass = loadFragmentClass(classLoader, className)
        return fragmentClassMap[fragmentClass]?.get() ?: super.instantiate(classLoader, className)
    }
}