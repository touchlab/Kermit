/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.gradle

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.gradle.testfixtures.ProjectBuilder

class KermitGradlePluginTest {
    @Test
    fun `plugin registers kermit extension with default stripBelow None`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("co.touchlab.kermit")

        val extension = project.extensions.findByType(KermitGradleExtension::class.java)
        assertNotNull(extension)
        assertEquals(StripSeverity.None, extension.stripBelow)
    }

    @Test
    fun `plugin allows configuring stripBelow`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("co.touchlab.kermit")

        val extension = project.extensions.getByType(KermitGradleExtension::class.java)
        extension.stripBelow = StripSeverity.Error
        assertEquals(StripSeverity.Error, extension.stripBelow)
    }

    @Test
    fun `compiler plugin id and artifact coordinates match build config`() {
        val plugin = KermitGradlePlugin()
        assertEquals(BuildConfig.KOTLIN_PLUGIN_ID, plugin.getCompilerPluginId())
        val artifact = plugin.getPluginArtifact()
        assertEquals(BuildConfig.KOTLIN_PLUGIN_GROUP, artifact.groupId)
        assertEquals(BuildConfig.KOTLIN_PLUGIN_NAME, artifact.artifactId)
        assertEquals(BuildConfig.KOTLIN_PLUGIN_VERSION, artifact.version)
    }
}
