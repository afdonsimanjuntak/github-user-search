package io.afdon.githubusersearch.di.activity

import androidx.fragment.app.Fragment
import dagger.MapKey
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FragmentKey(val clazz: KClass<out Fragment>)