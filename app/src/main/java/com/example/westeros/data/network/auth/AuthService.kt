package com.example.westeros.data.network.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.westeros.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/*
 * AuthServise es una clase que se encarga de manejar la autenticación de los usuarios usando Firebase.
 * Esta clase es un Singleton porque solo necesitamos una instancia de ella en toda la aplicación.
 * La clase tiene como dependencia una instancia de FirebaseClient que es la clase que se encarga de manejar
 * la conexión con Firebase.
 */
@Singleton
class AuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebase: FirebaseClient
){

    /*
     * Para verificar si el usuario está autenticado usamos un Flow<Boolean> que se encarga de emitir un booleano
     * cada segundo indicando si el usuario está autenticado o no.
     */
    val verifiedAccount: Flow<Boolean> = flow {
        while (true) {
            val verified = verifyEmailIsVerified() // -> Se extrae la lógica de verificación de la autenticación del usuario a un método privado
            emit(verified)
            delay(1000)
        }
    }

    /*
     * Esta función suspend se encarga de manejar el proceso de autenticación de los usuarios con correo y contraseña.
     * Regresamos un LoginResult que es un sealed class que nos permite manejar los diferentes estados de la autenticación.
     * Esto se hace así porque el método signInWithEmailAndPassword de Firebase puede regresar diferentes tipos de errores
     * y queremos manejarlos de manera más eficiente.
     */
    suspend fun loginWithEmailAndPassword(email: String, password: String): LoginResult = runCatching {
        firebase.auth.signInWithEmailAndPassword(email, password).await()
    }.toLoginResult()

    /*
     * Así mismo creamos una función suspend para cerrar la sesión de un usuario.
     */
    suspend fun logout() {
        firebase.auth.signOut()
    }

    /*
     * Función suspend para crear una cuenta de usuario con correo y contraseña.
     * Esta función regresa un AuthResult? porque el método createUserWithEmailAndPassword de Firebase puede regresar null.
     */
    suspend fun createAccount(email: String, password: String): AuthResult? {
        return firebase.auth.createUserWithEmailAndPassword(email,password).await()
    }

    fun getCurrentUser() = firebase.auth.currentUser

    private suspend fun verifyEmailIsVerified(): Boolean {
        firebase.auth.currentUser?.reload()?.await() // -> Se recarga la información del usuario para obtener el estado de la autenticación
        return firebase.auth.currentUser?.isEmailVerified ?: false // -> Se regresa el estado de la autenticación del usuario
    }

    // También definimos una función suspend para mandar un correo de verificación al usuario
    suspend fun sendVerificationEmail(): Boolean {
        return runCatching {
            firebase.auth.currentUser?.sendEmailVerification()?.await()
            true
        }.getOrElse { false }
    }

    suspend fun loginWithGoogle(): IntentSender? {
        val result = try {
            Identity.getSignInClient(context).beginSignIn(beginSignInRequest()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun loginFromGoogleIntent(intent: Intent?): LoginResult = runCatching {
        val credential = Identity.getSignInClient(context).getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        firebase.auth.signInWithCredential(googleCredentials).await()
     }.toLoginResult()

    private fun beginSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    /*
     * Dado que se usa un bloque runCatching, la función login puede regresar un Result<AuthResult> que puede ser un Success o un Failure.
     * En esta función de extensión, convertimos el Result<AuthResult> en un LoginResult.
     * Si el Result<AuthResult> es un Success, entonces regresamos un LoginResult.Success con el valor de la propiedad isEmailVerified del usuario.
     * Si el Result<AuthResult> es un Failure, entonces regresamos un LoginResult.Error.
     */
    private fun Result<AuthResult>.toLoginResult() = when (val result = getOrNull()) {
        null -> LoginResult.Error
        else -> {
            val userId = result.user
            checkNotNull(userId) // -> This is a Kotlin function that throws an exception if the value is null
            LoginResult.Success(result.user?.isEmailVerified ?: false) // -> We return a LoginResult.Success object with the isEmailVerified property of the user
        }
    }
}

/*
 * Ya que tenemos la lógica de autenticación en una clase separada, podemos usarla en cualquier parte de la aplicación.
 * Por ejemplo, definir algunos casos de uso para manejar la autenticación de los usuarios.
 */