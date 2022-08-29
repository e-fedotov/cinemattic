package ru.evgenyfedotov.cinemattic.di

import dagger.Component
import ru.evgenyfedotov.cinemattic.FavoritesFragment
import ru.evgenyfedotov.cinemattic.MainListFragment
import javax.inject.Scope

@FavoritesFragmentScope
@Component(modules = [
    FavoritesFragmentModule::class
],
    dependencies = [
        ApplicationComponent::class
    ])
interface FavoritesFragmentComponent {
    fun inject(favoritesFragment: FavoritesFragment)
}

@Scope
annotation class FavoritesFragmentScope