package com.awesome.notes

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: DispatcherType)

enum class DispatcherType {
    Default,
    IO,
}