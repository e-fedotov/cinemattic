package ru.evgenyfedotov.cinemattic.di

import dagger.Component
import ru.evgenyfedotov.cinemattic.DetailsFragment
import ru.evgenyfedotov.cinemattic.FavoritesFragment
import javax.inject.Scope

@DetailsFragmentScope
@Component(
    modules = [
        DetailsFragmentModule::class
    ],
    dependencies = [
        ApplicationComponent::class
    ]
)

interface DetailsFragmentComponent {
    fun inject(detailsFragment: DetailsFragment)
}

@Scope
annotation class DetailsFragmentScope