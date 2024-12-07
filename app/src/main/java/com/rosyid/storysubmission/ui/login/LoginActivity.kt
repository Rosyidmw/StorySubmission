package com.rosyid.storysubmission.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
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
import com.rosyid.storysubmission.data.pref.UserModel
import com.rosyid.storysubmission.data.remote.Result
import com.rosyid.storysubmission.databinding.ActivityLoginBinding
import com.rosyid.storysubmission.ui.ViewModelFactory
import com.rosyid.storysubmission.ui.home.MainActivity
import com.rosyid.storysubmission.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.getSession().observe(this) { session ->
            if (session.isLogin) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (binding.etEmail.error == null && binding.etPassword.error == null) {
                viewModel.login(email, password).observe(this) { result ->
                    when (result) {
                        is Result.Error -> {
                            binding.linear.visibility = View.GONE
                        }
                        is Result.Loading -> {
                            binding.linear.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.linear.visibility = View.GONE
                            if (result.data.error == true) {
                                Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                            } else {
                                result.data.loginResult?.let {
                                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                                    viewModel.saveSession(UserModel(email, it.token.toString(), true))
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, R.string.minimal_password, Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation() {
        val welcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(100)
        val back = ObjectAnimator.ofFloat(binding.tvBack, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.tvProlog, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etLayoutEmail, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etLayoutPassword, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val or = ObjectAnimator.ofFloat(binding.tvOr, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                welcome,
                back,
                message,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
                or,
                register
            )
            startDelay = 300
        }.start()
    }
}