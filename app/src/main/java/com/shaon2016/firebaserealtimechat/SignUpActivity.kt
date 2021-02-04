package com.shaon2016.firebaserealtimechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.shaon2016.firebaserealtimechat.databinding.ActivitySignupBinding
import com.shaon2016.firebaserealtimechat.model.User

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val auth by lazy { Firebase.auth }

    private val TAG = SignUpActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

        binding.btnSignUp.setOnClickListener {
            signUp()
        }
        binding.btnGoSignIn.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun signUp() {
        val email = binding.evEmail.text.toString()
        val name = binding.evName.text.toString()
        val pass = binding.evPass.text.toString()

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val user = User(Firebase.auth.currentUser?.uid, name, email, pass)

                val database = Firebase.database
                val userRef = database.getReference("users")
                userRef.child(user.id!!).setValue(user)

                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
                //updateUI(null)
            }
        }
    }

    private suspend fun saveUserToDb(name: String, email: String, pass: String) {

    }

}