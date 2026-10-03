package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SecureVault USA", appName)
  }

  @Test
  fun `password generator produces high entropy password`() {
    val pwd = com.example.data.security.PasswordGenerator.generate(length = 24)
    assertEquals(24, pwd.length)
    val strength = com.example.data.security.PasswordGenerator.calculateStrength(pwd)
    org.junit.Assert.assertTrue(strength.entropyBits > 60)
  }
}
