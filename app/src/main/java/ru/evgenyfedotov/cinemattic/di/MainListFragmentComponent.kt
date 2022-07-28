package ru.evgenyfedotov.cinemattic.di

import dagger.Component
import ru.evgenyfedotov.cinemattic.MainListFragment
import javax.inject.Scope

@MainListFragmentScope
@Component(modules = [
    MainListFragmentModule::class
],
dependencies = [
    ApplicationComponent::class
])
interface MainListFragmentComponent {

        fun inject(mainListFragment: MainListFragment)

}

@Scope
annotation class MainListFragmentScope