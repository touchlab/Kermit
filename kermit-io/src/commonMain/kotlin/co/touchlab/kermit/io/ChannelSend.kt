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

/**
 * Sends [element] to the channel, blocking the calling thread if the channel is full.
 *
 * This is [kotlinx.coroutines.channels.trySendBlocking] with one exception: a thread that has already been interrupted is never blocked.
 * `trySendBlocking` falls back to `runBlocking` when the channel has no room, and `runBlocking` throws `InterruptedException` as soon as it
 * sees the calling thread's interrupt flag, which crashes the caller instead of logging a message. An interrupted thread is being torn down
 * anyway, so we drop the message rather than crash it or hold it up on file I/O.
 *
 * The element is silently discarded if it cannot be delivered without blocking such a thread, or if the channel is closed.
 *
 * See [issue #480](https://github.com/touchlab/Kermit/issues/480).
 */
internal expect fun <E> SendChannel<E>.sendBlockingUnlessInterrupted(element: E)
