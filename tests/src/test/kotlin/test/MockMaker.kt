package test

import org.mockito.internal.configuration.plugins.Plugins
import org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker


internal var mockMakerInlineEnabled: Boolean? = null
internal fun mockMakerInlineEnabled(): Boolean {
    return mockMakerInlineEnabled ?: (Plugins.getMockMaker() is InlineByteBuddyMockMaker)
}
