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

import org.jetbrains.kotlin.compiler.plugin.devkit.runners.DevKitJvmBoxTest
import org.jetbrains.kotlin.compiler.plugin.devkit.services.configurePlugin
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives

/** Base class of the generated box tests: each test data file compiles with the plugin and `box()` must return "OK". */
open class AbstractKermitBoxTest :
    DevKitJvmBoxTest(
        {
            defaultDirectives {
                -CodegenTestDirectives.DUMP_IR
                -FirDiagnosticsDirectives.FIR_DUMP
            }
            useConfigurators(::KermitStripBelowConfigurator)
            configurePlugin(KermitCompilerPluginRegistrar())
        },
    )
