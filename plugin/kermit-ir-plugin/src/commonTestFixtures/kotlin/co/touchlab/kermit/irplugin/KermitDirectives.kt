/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.irplugin

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.directives.model.DirectiveApplicability
import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices

object KermitDirectives : SimpleDirectivesContainer() {
    val STRIP_BELOW by stringDirective(
        description = "Severity passed to the compiler plugin as `stripBelow`, e.g. Info",
        applicability = DirectiveApplicability.Global,
    )
}

/** Passes the `// STRIP_BELOW:` directive of a test data file to the plugin as its `stripBelow` option. */
class KermitStripBelowConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override val directiveContainers = listOf(KermitDirectives)

    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        module.directives[KermitDirectives.STRIP_BELOW].firstOrNull()?.let {
            configuration.put(KermitCommandLineProcessor.ARG_STRIP_BELOW, it)
        }
    }
}
