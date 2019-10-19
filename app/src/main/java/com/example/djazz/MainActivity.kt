package com.example.djazz


import android.content.Intent
import android.os.Bundle
import android.view.View

import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity() {

    lateinit var inputUsername: TextInputLayout
    lateinit var txtUsername: TextInputEditText
    //lateinit var btnLogin: MaterialButton
    lateinit var passwordTxt:TextInputEditText
    lateinit var progressLoading: ProgressBar

    val mAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // setupViews()

        val btnSignin = findViewById<View>(R.id.BtnSign) as MaterialButton
        btnSignin.setOnClickListener {
            val intent = Intent(this@MainActivity, Createaccount::class.java)
            startActivity(intent)
            finish()
        }


        val btnLogin = findViewById<View>(R.id.btnLogin) as MaterialButton
        btnLogin.setOnClickListener {

                inputUsername = findViewById(R.id.inputUsername)
                txtUsername = findViewById(R.id.txtUsername)
                val email = txtUsername.text.toString()
                passwordTxt = findViewById(R.id.password)
                val password = passwordTxt.text.toString()

                if (!email.isEmpty() && !password.isEmpty()) {
                    this.mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                            if (task.isSuccessful) {
                               /* startActivity(Intent(this, Contacts::class.java))
                                Toast.makeText(this, "Successfully Logged in :)", Toast.LENGTH_LONG)
                                    .show()*/
                                performLogin(email)
                            } else {
                                Toast.makeText(this, "Error Logging in :(", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })

                }
            else{
                    Toast.makeText(this, "Error Logging in :(", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


     /*       fun setupViews()
    {
        inputUsername = findViewById(R.id.inputUsername)
        txtUsername = findViewById(R.id.txtUsername)
        btnLogin = findViewById(R.id.btnLogin)
        progressLoading = findViewById(R.id.progressLoading)


        btnLogin.setOnClickListener {
            // Clear previous errors if any
            inputUsername.error = null

            // Username Validation
            if (txtUsername.text.toString().isEmpty())
            {
                inputUsername.error = "Email cannot be empty!"
                return@setOnClickListener
            }


            performLogin(txtUsername.text.toString())
        }


 /*   private fun createUser(appID:String, apiKey:String , region:String ,  UID:String, name:String ) {

        val createUserEndpoint:String = "https://api-eu.cometchat.io/v2.0/users";
        try {
            val url = URL(String.format(createUserEndpoint, region))
            val httpsURLConnection = url.openConnection() as  (HttpsURLConnection)
            httpsURLConnection.setReadTimeout(10000)
            httpsURLConnection.setConnectTimeout(15000)
            httpsURLConnection.setRequestMethod("POST")
            httpsURLConnection.setDoInput(true)
            httpsURLConnection.setDoOutput(true)

            // Adding the necessary headers

            httpsURLConnection.setRequestProperty(getString(R.string.comet_app_id), appID)
            httpsURLConnection.setRequestProperty(getString(R.string.comet_api_key), apiKey)
            httpsURLConnection.setRequestProperty("Content-Type", "application/json")
            httpsURLConnection.setRequestProperty("Accept", "application/json")

            // Creating the JSON with post params
            emailTxt = findViewById(R.id.txtemail)
            val email = emailTxt.text.toString()
            passwordTxt = findViewById(R.id.txtpassword)
            val password = passwordTxt.text.toString()

            val userData = JSONObject()
            userData.put("uid", email)
            userData.put("name", password)

            val outputStream = BufferedOutputStream(httpsURLConnection.getOutputStream())
            val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
            writer.write(userData.toString())
            writer.flush()
            writer.close()
            outputStream.close()

            val responseCode:Int = httpsURLConnection.getResponseCode()
            val responseMessage:String = httpsURLConnection.getResponseMessage()
            Log.d(TAG, "Response Code : " + responseCode)
            Log.d(TAG, "Response Message : " + responseMessage)
            var result:String =  String()
            var inputStream:InputStream? = null
            if (responseCode >= 400 && responseCode <= 499) {
                inputStream = httpsURLConnection.getErrorStream()
            } else {
                inputStream = httpsURLConnection.getInputStream()
            }
            val bufferedReader:BufferedReader = BufferedReader(InputStreamReader(inputStream))
            var inputLine:String? = null
            inputLine = bufferedReader.readLine()
            while (inputLine != null) {
                result += inputLine
            }
            if (responseCode == 200) {
                Log.d(TAG, "Create User Success Response : " + result)
                // The details of the user can be obtained from the result variable in JSON format


            } else {
                Log.e(TAG, "Create User Error Response : " + result)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

  */

    }
*/
    private fun performLogin(userId: String) {
        // Show Progress Bar
        progressLoading.visibility = View.VISIBLE
        btnLogin.visibility = View.GONE
        txtUsername.isEnabled = false

        CometChat.login(userId, getString(R.string.comet_api_key), object : CometChat.CallbackListener<User>() {
            override fun onSuccess(user: User?) {

                // Hide Progress Bar
                progressLoading.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE
                txtUsername.isEnabled = true

                if (user != null)
                {
                    // Go to Contacts screen
                    val intent = Intent(this@MainActivity, Contacts::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this@MainActivity, "Couldn't find the user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(exception: CometChatException?) {

                // Hide Progress Bar
                progressLoading.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE
                txtUsername.isEnabled = true

                Toast.makeText(this@MainActivity, exception?.localizedMessage ?: "Unknown Error Occurred!", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
