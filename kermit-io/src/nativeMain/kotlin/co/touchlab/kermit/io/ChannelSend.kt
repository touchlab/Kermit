/*
 * Copyright (c) 2026 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.io

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking

internal actual fun <E> SendChannel<E>.sendBlockingUnlessInterrupted(element: E) {
    // Kotlin/Native has no thread interruption, so the runBlocking inside trySendBlocking cannot throw InterruptedException here.
    trySendBlocking(element)
}
