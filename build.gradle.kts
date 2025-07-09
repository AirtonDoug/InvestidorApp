// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // AQUI ESTÁ A MUDANÇA PRINCIPAL
    id("com.android.application") version "8.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}