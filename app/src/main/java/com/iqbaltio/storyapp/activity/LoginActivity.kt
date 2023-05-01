package com.iqbaltio.storyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.iqbaltio.storyapp.R
import com.iqbaltio.storyapp.Result
import com.iqbaltio.storyapp.data.LoginRequest
import com.iqbaltio.storyapp.data.UserModels
import com.iqbaltio.storyapp.data.ViewModelFactory
import com.iqbaltio.storyapp.databinding.ActivityLoginBinding
import com.iqbaltio.storyapp.viewmodel.MainViewModel


class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var loginVM : MainViewModel
//    private lateinit var loginSession : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginVM = ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]
//        loginSession = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)

        supportActionBar?.hide()
        setButton()
        setupAnimation()
        showLoading(false)

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
                setButton()
            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnSubmit.setOnClickListener{
            clicked()
        }
    }

    private fun clicked() {
        val respone = LoginRequest(binding.edLoginEmail.text.toString(), binding.edLoginPassword.text.toString())
        showLoading(true)
        loginVM.login(respone).observe(this) {
            when (it) {
                is Result.Success -> {
                    showLoading(false)
                    val response = it.data
                    saveUserData(
                        UserModels(
                        response.loginResult?.name.toString(),
                        response.loginResult?.token.toString(),
                        true)
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun setButton() {
        binding.btnSubmit.isEnabled = binding.edLoginPassword.text.toString().length >= 8
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun saveUserData(user: UserModels) {
        loginVM.saveUser(user)
    }

    private fun setupAnimation() {
        ObjectAnimator.ofFloat(binding.ivLoginLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val label = ObjectAnimator.ofFloat(binding.tvLoginLabel, View.ALPHA, 1f).setDuration(400)
        val email = ObjectAnimator.ofFloat(binding.emailTextInputLayout, View.ALPHA, 1f).setDuration(400)
        val password = ObjectAnimator.ofFloat(binding.passwordTextInputLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnSubmit, View.ALPHA, 1f).setDuration(600)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(700)

        AnimatorSet().apply {
            playSequentially(label,email,password,login,register)
            start()
        }
    }
}