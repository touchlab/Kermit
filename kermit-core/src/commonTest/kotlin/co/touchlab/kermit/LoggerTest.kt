/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package co.touchlab.kermit

import co.touchlab.testhelp.concurrency.ThreadOperations
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggerTest {

    private fun getTestConfig(logWriterList: List<LogWriter>): TestConfig {
        return TestConfig(
            minSeverity = Severity.Verbose,
            logWriterList = logWriterList,
        )
    }

    private fun getTestLogWriter(): TestLogWriter {
        return TestLogWriter(loggable = Severity.Verbose)
    }

    @Test
    fun testGlobal() {
        val testLogWriter = getTestLogWriter()
        Logger.apply {
            setMinSeverity(Severity.Verbose)
            setLogWriters(testLogWriter)
        }
        Logger.i { "Does global log?" }
        assertEquals(testLogWriter.logs.first().message, "Does global log?")
    }

    @Test
    fun testGlobal_Severity() {
        val testLogWriter = getTestLogWriter()
        Logger.apply {
            setMinSeverity(Severity.Assert)
            setLogWriters(testLogWriter)
        }
        Logger.e { "Does global log?" }
        assertTrue(testLogWriter.logs.isEmpty())
    }

    @Test
    fun testGlobal_MultipleLoggers_SetVararg() {
        val firstLogWriter = getTestLogWriter()
        val secondLogWriter = TestLogWriter(loggable = Severity.Verbose)
        Logger.apply {
            setMinSeverity(Severity.Verbose)
            setLogWriters(firstLogWriter, secondLogWriter)
        }
        Logger.e { "Message" }
        assertTrue(firstLogWriter.logs.size == 1)
        assertTrue(secondLogWriter.logs.size == 1)
        firstLogWriter.assertLast { message == "Message" }
        secondLogWriter.assertLast { message == "Message" }
    }

    @Test
    fun testGlobal_MultipleLoggers_SetList() {
        val firstLogWriter = getTestLogWriter()
        val secondLogWriter = TestLogWriter(loggable = Severity.Verbose)
        Logger.apply {
            setMinSeverity(Severity.Verbose)
            setLogWriters(listOf(firstLogWriter, secondLogWriter))
        }
        Logger.e { "Message" }
        assertTrue(firstLogWriter.logs.size == 1)
        assertTrue(secondLogWriter.logs.size == 1)
        firstLogWriter.assertLast { message == "Message" }
        secondLogWriter.assertLast { message == "Message" }
    }

    @Test
    fun testGlobal_MultipleLoggers_AddLogWriter() {
        val firstLogWriter = getTestLogWriter()
        Logger.apply {
            setMinSeverity(Severity.Verbose)
            setLogWriters(firstLogWriter)
        }
        Logger.e { "Message" }
        assertTrue(firstLogWriter.logs.size == 1)
        firstLogWriter.assertLast { message == "Message" }

        val secondLogWriter = TestLogWriter(loggable = Severity.Verbose)
        Logger.addLogWriter(secondLogWriter)
        Logger.e { "Message again" }
        assertTrue(firstLogWriter.logs.size == 2)
        firstLogWriter.assertLast { message == "Message again" }

        assertTrue(secondLogWriter.logs.size == 1)
        secondLogWriter.assertLast { message == "Message again" }
    }

    @Test
    fun testGlobal_DefaultTag() {
        val testLogWriter = getTestLogWriter()
        Logger.setMinSeverity(Severity.Verbose)
        Logger.addLogWriter(testLogWriter)

        Logger.d { "Log Without Tag (Original Kermit)" }
        testLogWriter.assertLast { tag == "Kermit" }

        Logger.setTag("My Custom Tag")

        Logger.d { "Log Without Tag (Kermit With Tag)" }
        testLogWriter.assertLast { tag == "My Custom Tag" }
    }

    @Test
    fun defaultConfigTest() {
        val testLogWriter = getTestLogWriter()
        val logger = Logger(LoggerConfig.default.copy(logWriterList = listOf(testLogWriter)))
        logger.v { "Message" }
        testLogWriter.assertCount(1)
    }

    @Test
    fun configSeverityCheckFailedTest() {
        val testLogWriter = getTestLogWriter()
        val testConfig = getTestConfig(listOf(testLogWriter))
        val logger = Logger(testConfig.copy(minSeverity = Severity.Error))
        logger.v { "Message" }
        testLogWriter.assertCount(0)
    }

    @Test
    fun simpleLogTest() {
        val testLogWriter = getTestLogWriter()
        val testConfig = getTestConfig(listOf(testLogWriter))
        val logger = Logger(testConfig)
        logger.e { "Message" }
        testLogWriter.assertCount(1)
    }

    @Test
    fun directLogTest() {
        val testLogWriter = getTestLogWriter()
        val testConfig = getTestConfig(listOf(testLogWriter))
        val logger = Logger(testConfig)
        logger.v("Message")
        logger.d("Message")
        logger.i("Message")
        logger.w("Message")
        logger.e("Message")
        logger.a("Message")
        testLogWriter.assertCount(6)
    }

    @Test
    fun testIsLoggable() {
        val testConfig = getTestConfig(listOf(getTestLogWriter()))
        val errorLogWriter = TestLogWriter(Severity.Error)
        val logger = Logger(testConfig.copy(logWriterList = listOf(errorLogWriter)))

        logger.v { "verbose" }
        logger.d { "debug" }
        logger.i { "info" }
        logger.w { "warn" }

        errorLogWriter.assertCount(0)
        logger.e { "error" }
        errorLogWriter.assertLast { message == "error" && severity == Severity.Error }

        logger.a { "assert" }
        errorLogWriter.assertCount(2)
        errorLogWriter.assertLast { message == "assert" && severity == Severity.Assert }
    }

    @Test
    fun testMultipleLoggers() {
        val firstLogWriter = getTestLogWriter()
        val testConfig = getTestConfig(listOf(firstLogWriter))
        val secondLogWriter = TestLogWriter(loggable = Severity.Verbose)
        val logWriterList = listOf(firstLogWriter, secondLogWriter)
        val logger = Logger(testConfig.copy(logWriterList = logWriterList))
        logger.e { "Message" }

        firstLogWriter.assertLast { message == "Message" }
        secondLogWriter.assertLast { message == "Message" }
    }

    @Test
    fun testSingleLogger() {
        val firstLogWriter = getTestLogWriter()
        val testConfig = getTestConfig(listOf(firstLogWriter))
        val secondLogWriter = TestLogWriter(loggable = Severity.Verbose)
        val logger = Logger(testConfig)
        logger.e { "Message" }

        firstLogWriter.assertCount(1)
        secondLogWriter.assertCount(0)
    }

    @Test
    fun testDefaultTag() {
        val testLogWriter = getTestLogWriter()
        val testConfig = getTestConfig(listOf(testLogWriter))
        val logger = Logger(testConfig)
        val loggerWithTag = logger.withTag("My Custom Tag")

        logger.d { "Log Without Tag (Original Kermit)" }
        testLogWriter.assertLast { tag == "Kermit" }

        loggerWithTag.d { "Log Without Tag (Kermit With Tag)" }
        testLogWriter.assertLast { tag == "My Custom Tag" }

        logger.d { "Log Without Tag (Original Kermit)" }  // Ensuring first Kermit isn't affected by withTag
        testLogWriter.assertLast { tag == "Kermit" }
    }

    @Test
    fun testMutableLoggerConfig() {
        val testConfig = object : MutableLoggerConfig {
            override var minSeverity: Severity = Severity.Verbose
            override var logWriterList: List<LogWriter> = listOf()
        }
        val logger = Logger(testConfig)
        logger.d { "Message" }

        val testLogWriter = getTestLogWriter()
        testConfig.logWriterList = listOf(testLogWriter)
        logger.d { "Message2" }

        assertEquals(1, testLogWriter.logs.size)
        testLogWriter.assertLast { message == "Message2" }
        testLogWriter.assertLast { tag == "Kermit" }

        testConfig.minSeverity = Severity.Info

        val newLogger = logger.withTag("New Tag")
        newLogger.d { "Message3" }
        assertEquals(1, testLogWriter.logs.size)
        testLogWriter.assertLast { message == "Message2" }
        testLogWriter.assertLast { tag == "Kermit" }

        newLogger.a { "Message4" }
        assertEquals(2, testLogWriter.logs.size)
        testLogWriter.assertLast { message == "Message4" }
        testLogWriter.assertLast { tag == "New Tag" }
    }

    @Test
    fun testMutableLoggerConfig_MultiThreading() {
        val testLogWriter = getTestLogWriter()
        val config = object : MutableLoggerConfig {
            override var minSeverity: Severity = Severity.Verbose
            override var logWriterList: List<LogWriter> = listOf(testLogWriter)
        }
        val logger = Logger(config)
        val operations = 100
        val ops = ThreadOperations {}
        val threads = 10
        repeat(operations) {
            ops.exe {
                logger.d { "message" }
            }
        }
        ops.run(threads)
        assertEquals(operations, testLogWriter.logs.size)
    }

    @Ignore
    fun testMutableLoggerConfig_MultiThreading_Severity() {
        val testLogWriter = TestLogWriter(loggable = Severity.Verbose)
        val config = mutableKermitConfigInit(listOf(testLogWriter))
        val logger = Logger(config)
        val operations = 200
        val ops = ThreadOperations {}
        val threads = 10
        repeat(operations / 2) {
            ops.exe {
                config.minSeverity = Severity.Info
                logger.d { "message" }
            }
        }
        repeat(operations / 2) {
            ops.exe {
                config.minSeverity = Severity.Debug
                logger.d { "message" }
            }
        }
        ops.run(threads)
        assertEquals(operations / 2, testLogWriter.logs.size)
    }
}