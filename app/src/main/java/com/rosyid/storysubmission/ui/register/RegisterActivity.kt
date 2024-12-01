package com.rosyid.storysubmission.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.rosyid.storysubmission.R
import com.rosyid.storysubmission.data.remote.Result
import com.rosyid.storysubmission.databinding.ActivityLoginBinding
import com.rosyid.storysubmission.databinding.ActivityRegisterBinding
import com.rosyid.storysubmission.ui.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.etPassword.addTextChangedListener { text ->
            val password = text.toString()
            if (password.length < 8) {
                binding.etInputPassword.error = ""
                binding.passwordErrorMessage.visibility = View.VISIBLE
            } else {
                binding.etInputPassword.error = null
                binding.passwordErrorMessage.visibility = View.GONE
            }
        }

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (password.length >= 8) {
                viewModel.register(name, email, password).observe(this) {  result ->
                    when(result) {
                        is Result.Error -> {
                            binding.linear.visibility = View.GONE
                            Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            binding.linear.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.linear.visibility = View.GONE
                            if (result.data.error == true) {
                                Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, R.string.minimal_password, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAnimation() {
        val welcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(100)
        val prolog = ObjectAnimator.ofFloat(binding.tvProlog2, View.ALPHA, 1f).setDuration(100)
        val username = ObjectAnimator.ofFloat(binding.etInputUsername, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.etLayoutEmail, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etInputPassword, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                welcome,
                prolog,
                username,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
            )
            startDelay = 100
        }.start()
    }
}