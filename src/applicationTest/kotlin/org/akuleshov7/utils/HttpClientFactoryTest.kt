package org.akuleshov7.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class HttpClientFactoryTest {
    @Test
    fun `pagination parser test`() {
        val headerLink =
            "<https://api.github.com/repositories/33565912/stargazers?per_page=100&page=2>; rel=\"next\"," +
                " <https://api.github.com/repositories/33565912/stargazers?per_page=100&page=13>; rel=\"last\"\n"

        assertEquals(13, headerLink.findPaginationLastPageNumber())
    }
}
