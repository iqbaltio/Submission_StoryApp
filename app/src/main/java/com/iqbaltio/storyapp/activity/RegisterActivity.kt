package com.iqbaltio.storyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.iqbaltio.storyapp.databinding.ActivityRegisterBinding
import com.iqbaltio.storyapp.viewmodel.MainViewModel
import com.iqbaltio.storyapp.Result
import com.iqbaltio.storyapp.data.ViewModelFactory
import androidx.core.widget.addTextChangedListener as adt

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val mainViewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setButtonEnabler()
        setButton()
        setupAnimation()
        showLoading(false)

        binding.btnSubmit.setOnClickListener{
            sendDataUser()
        }
    }

    private fun sendDataUser() {
        showLoading(true)
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        if (password.length < 8) {
            showLoading(false)
            Toast.makeText(this, "Password harus memiliki panjang minimal 8 karakter", Toast.LENGTH_SHORT).show()
            return
        }

        mainViewModel.register(name,email, password).observe(this) {
            when (it) {
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun setButton(){
        binding.apply {
            edRegisterName.adt{ setButtonEnabler()}
            edRegisterEmail.adt{ setButtonEnabler() }
            edRegisterPassword.adt{ setButtonEnabler() }
        }
    }

    private fun setButtonEnabler() {
        val rName = binding.edRegisterName.text
        val rEmail = binding.edRegisterEmail.text
        val rPassword = binding.edRegisterPassword.text.toString()
        binding.btnSubmit.isEnabled = !(rName.isNullOrEmpty() && rEmail.isNullOrEmpty() && rPassword.length < 8)
        if (rPassword.length < 8) {
            binding.edRegisterPassword.error = "Password harus memiliki minimal 8 karakter"
        } else {
            binding.edRegisterPassword.error = null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupAnimation() {
        val logo = binding.ivRegisterLogo
        val label = binding.tvRegisLabel
        val name = binding.nameTextInputLayout
        val email = binding.emailTextInputLayout
        val password = binding.passwordTextInputLayout
        val login = binding.btnSubmit

        ObjectAnimator.ofFloat(logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(label, View.ALPHA, 1f),
                ObjectAnimator.ofFloat(name, View.ALPHA, 1f),
                ObjectAnimator.ofFloat(email, View.ALPHA, 1f),
                ObjectAnimator.ofFloat(password, View.ALPHA, 1f),
                ObjectAnimator.ofFloat(login, View.ALPHA, 1f)
            )
            duration = 700
            startDelay = 400
            start()
        }
    }
}