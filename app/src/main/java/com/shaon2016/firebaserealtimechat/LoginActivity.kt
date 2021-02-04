package com.shaon2016.firebaserealtimechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shaon2016.firebaserealtimechat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { Firebase.auth }
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnGoSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

        binding.btnSignIn.setOnClickListener {
            doSignIn()
        }
    }

    private fun doSignIn() {
        val e = binding.evEmail.text.toString()
        val p = binding.evPass.text.toString()

        auth.signInWithEmailAndPassword(e, p).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
//                updateUI(null)
                // ...
            }

            // ...
        }
    }
}