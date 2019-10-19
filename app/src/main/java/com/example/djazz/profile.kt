package com.example.djazz

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException


class profile : AppCompatActivity() {

    lateinit var imgContactPhoto: AppCompatImageView
    lateinit var txtUsername: AppCompatTextView
    lateinit var txtEmail: AppCompatTextView
    lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupViews()
        setupProfile()
    }

    fun setupViews()
    {
        imgContactPhoto = findViewById(R.id.imgContactPhoto)
        txtUsername = findViewById(R.id.txtUsername)
        txtEmail = findViewById(R.id.txtEmail)
        btnLogout = findViewById(R.id.btnLogout)

        // Toolbar
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // Logout Button
        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId)
        {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupProfile()
    {
        val loggedInUser = CometChat.getLoggedInUser()
        loggedInUser?.let {user ->
            txtUsername.text = user.name
            txtEmail.text = user.uid
            if (user.avatar != null)
            {
                // Load Avatar Image if any
                Glide.with(this)
                    .asBitmap()
                    .load(user.avatar)
                    .into(imgContactPhoto)
            }
            else
            {
                // Generate Letter Avatar
                val generator = ColorGenerator.MATERIAL
                val color = generator.randomColor

                val drawable = TextDrawable.builder().buildRect(user.name[0].toString(), color)
                imgContactPhoto.setImageDrawable(drawable)
            }
        }
    }

    fun logoutUser()
    {
        CometChat.getLoggedInUser()?.let {
            CometChat.logout(object : CometChat.CallbackListener<String>() {
                override fun onSuccess(p0: String?) {
                    // Starting Login screen
                    val intent = Intent(this@profile, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

                override fun onError(exception: CometChatException?) {
                    Toast.makeText(this@profile, exception?.message ?: "Unknown Error Occured!", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}



