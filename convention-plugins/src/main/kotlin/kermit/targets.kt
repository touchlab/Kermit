/*
 * Copyright (c) 2026 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package kermit

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Action
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.allTargets(
    namespace: String? = null,
    supportsBrowser: Boolean = true,
    wasiEnabled: Boolean = false,
) {
    if (namespace != null) androidJvmTarget(namespace)
    jvmTarget()
    webTargets(supportsBrowser, wasiEnabled)
    nativeTargets()
}

/**
 * The Android target, via the `com.android.kotlin.multiplatform.library` plugin. Unlike the other
 * targets each module needs its own [namespace], so that is required rather than defaulted.
 *
 * Host test (unit test) and device test compilations are opt-in with this plugin, so modules that
 * have Android tests enable them through [configure], e.g. `androidJvmTarget("...") { withHostTest { } }`.
 */
fun KotlinMultiplatformExtension.androidJvmTarget(
    namespace: String,
    configure: Action<KotlinMultiplatformAndroidLibraryTarget> = Action { },
) {
    val compileSdkVersion = catalogVersion("compileSdk").toInt()
    val minSdkVersion = catalogVersion("minSdk").toInt()

    project.pluginManager.apply("com.android.kotlin.multiplatform.library")
    extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
        this.namespace = namespace
        this.compileSdk = compileSdkVersion
        this.minSdk = minSdkVersion

        withHostTest {}

        configure(this)
    }
}

fun KotlinMultiplatformExtension.jvmTarget() {
    jvm()
}

/**
 * The JS and Wasm targets, along with the `jsAndWasmJs` source set they share.
 *
 * `wasmWasi` has no JS interop, so it cannot share `jsAndWasmJs`. Modules whose web code lives in
 * that source set, or that depend on a module without a WASI variant, pass `wasiEnabled = false`.
 */
fun KotlinMultiplatformExtension.webTargets(
    supportsBrowser: Boolean = true,
    wasiEnabled: Boolean = false,
) {
    jsTarget(supportsBrowser)
    wasmTarget(supportsBrowser = supportsBrowser, wasiEnabled = wasiEnabled)
}

fun KotlinMultiplatformExtension.nativeTargets(
    supportsWatchosArm32: Boolean = true,
    supportsWatchosDeviceArm64: Boolean = true,
    supportsLinuxArm64: Boolean = true,
    supportsAndroidNative: Boolean = true,
) {
    appleTargets(
        supportsWatchosArm32 = supportsWatchosArm32,
        supportsWatchosDeviceArm64 = supportsWatchosDeviceArm64,
    )
    linuxTargets(supportsArm64 = supportsLinuxArm64)
    mingwTargets()
    if (supportsAndroidNative) androidNativeTargets()
}

fun KotlinMultiplatformExtension.jsTarget(
    supportsBrowser: Boolean = true,
) {
    js {
        nodejs()
        if (supportsBrowser) browser()
    }
}

fun KotlinMultiplatformExtension.wasmTarget(
    supportsBrowser: Boolean = true,
    wasiEnabled: Boolean = false,
) {
    wasmJsTarget(supportsBrowser)
    if (wasiEnabled) wasmWasiTarget()
}

@OptIn(ExperimentalWasmDsl::class)
fun KotlinMultiplatformExtension.wasmJsTarget(supportsBrowser: Boolean = true) {
    wasmJs {
        nodejs()
        if (supportsBrowser) browser()
    }
}

@OptIn(ExperimentalWasmDsl::class)
fun KotlinMultiplatformExtension.wasmWasiTarget() {
    wasmWasi {
        nodejs()
    }
}

fun KotlinMultiplatformExtension.appleTargets(
    supportsWatchosArm32: Boolean = true,
    supportsWatchosDeviceArm64: Boolean = true,
) {
    macosTargets()
    iosTargets()
    watchosTargets(
        supportsArm32 = supportsWatchosArm32,
        supportsDeviceArm64 = supportsWatchosDeviceArm64,
    )
    tvosTargets()
}

fun KotlinMultiplatformExtension.macosTargets() {
    macosX64()
    macosArm64()
}

fun KotlinMultiplatformExtension.iosTargets() {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}

fun KotlinMultiplatformExtension.watchosTargets(
    supportsArm32: Boolean = true,
    supportsDeviceArm64: Boolean = true,
) {
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()
    if (supportsArm32) watchosArm32()
    if (supportsDeviceArm64) watchosDeviceArm64()
}

fun KotlinMultiplatformExtension.tvosTargets() {
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
}

fun KotlinMultiplatformExtension.linuxTargets(
    supportsArm64: Boolean = true,
) {
    linuxX64()
    if (supportsArm64) linuxArm64()
}

fun KotlinMultiplatformExtension.mingwTargets() {
    mingwX64()
}

fun KotlinMultiplatformExtension.androidNativeTargets() {
    androidNativeX64()
    androidNativeX86()
    androidNativeArm64()
    androidNativeArm32()
}

private fun KotlinMultiplatformExtension.catalogVersion(name: String): String =
    project.extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findVersion(name)
        .orElseThrow { IllegalStateException("Version '$name' is missing from the 'libs' version catalog") }
        .requiredVersion
