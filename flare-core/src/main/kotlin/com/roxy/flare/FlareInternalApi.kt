package com.roxy.flare

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal Flare API and should not be used outside of the library modules."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class FlareInternalApi
