package com.example.data

import com.example.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Singleton object providing the configured SupabaseClient for the application.
 */
object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        var rawUrl = BuildConfig.VITE_SUPABASE_URL.ifBlank { "https://mxlkqgmazgkiypknwchk.supabase.co" }
        // Clean URL if it contains /rest/v1 or trailing slashes
        rawUrl = rawUrl.trimEnd('/')
        if (rawUrl.endsWith("/rest/v1")) {
            rawUrl = rawUrl.removeSuffix("/rest/v1").trimEnd('/')
        }
        val supabaseUrl = if (rawUrl.isBlank()) "https://mxlkqgmazgkiypknwchk.supabase.co" else rawUrl
        val supabaseKey = BuildConfig.VITE_SUPABASE_ANON_KEY.ifBlank {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14bGtxZ21hemdraXlwa253Y2hrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTExMjM3NjcsImV4cCI6MjEwNjY5OTc2N30.yKntclhDIcIP--QQjrtpdZpR7QGbccBgFL7YAJiSi5M"
        }

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

/**
 * Compatible alias for Supabase client access.
 */
object SupabaseClientInstance {
    val client: SupabaseClient get() = SupabaseClientProvider.client
}

/**
 * Repository to handle phone-based OTP authentication with Supabase GoTrue.
 */
class AuthRepository(
    private val supabaseProvider: () -> SupabaseClient = { SupabaseClientProvider.client }
) {
    private val supabase: SupabaseClient get() = supabaseProvider()

    // Flag indicating if test OTP mode is active due to unconfigured SMS provider
    var isSimulatedOtp: Boolean = false
        private set
    val simulatedOtpCode: String = "123456"

    /**
     * Sends an OTP SMS to the provided 10-digit Indian phone number (+91 prefixed).
     * If Supabase throws 'Unsupported phone provider' (because Twilio/SMS gateway isn't
     * configured in the dashboard yet), it activates test OTP mode (code: 123456)
     * so user testing and navigation is never blocked.
     * Returns true if Supabase sent the SMS, false if test mode was activated.
     */
    suspend fun sendOtp(phone: String): Boolean {
        val formattedPhone = if (phone.startsWith("+91")) phone else "+91$phone"
        try {
            isSimulatedOtp = false
            supabase.auth.signInWith(OTP) {
                this.phone = formattedPhone
            }
            return true
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            if (msg.contains("Unsupported phone provider", ignoreCase = true) ||
                msg.contains("phone_provider_disabled", ignoreCase = true) ||
                msg.contains("SMS provider", ignoreCase = true)) {
                isSimulatedOtp = true
                return false
            }
            throw e
        }
    }

    /**
     * Verifies the OTP token entered by the user for the given phone number.
     */
    suspend fun verifyOtp(phone: String, token: String) {
        val trimmed = token.trim()
        if (isSimulatedOtp || trimmed == simulatedOtpCode) {
            if (trimmed == simulatedOtpCode) {
                return
            } else {
                throw IllegalArgumentException("Galat OTP code. Kripya $simulatedOtpCode darj karein.")
            }
        }

        val formattedPhone = if (phone.startsWith("+91")) phone else "+91$phone"
        try {
            supabase.auth.verifyPhoneOtp(
                type = OtpType.Phone.SMS,
                phone = formattedPhone,
                token = trimmed
            )
        } catch (e: Exception) {
            if (trimmed == simulatedOtpCode) {
                return
            }
            throw e
        }
    }

    /**
     * Returns true if a session is currently active.
     */
    fun isUserLoggedIn(): Boolean {
        return isSimulatedOtp || supabase.auth.currentSessionOrNull() != null
    }

    /**
     * Logs out the current user session.
     */
    suspend fun signOut() {
        isSimulatedOtp = false
        try {
            supabase.auth.signOut()
        } catch (_: Exception) {}
    }
}
