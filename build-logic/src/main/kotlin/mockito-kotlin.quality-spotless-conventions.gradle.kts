import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless")
}

configure<SpotlessExtension> {
    // We run the check separately on CI, so don't run this by default
    isEnforceCheck = false

    kotlin {
        ktfmt().kotlinlangStyle()
    }
}
