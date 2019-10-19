package com.example.djazz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class Createaccount : AppCompatActivity() {

    lateinit var emailTxt: TextInputEditText
    lateinit var passwordTxt: TextInputEditText
    lateinit var nameTxt: TextInputEditText
    lateinit var signin: MaterialButton


    //private var mDatabaseReference: DatabaseReference? = null
    //private var mDatabase: FirebaseDatabase? = null
    //private var mAuth: FirebaseAuth? = null
    val TAG = "Create Account"

    private var mDatabase = FirebaseDatabase.getInstance()
    private var mDatabaseReference = mDatabase.reference.child("Users")
    private var mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createaccount)

        val signin = findViewById<View>(R.id.signin) as MaterialButton

        signin.setOnClickListener {
            createaccount()
        }

    }

    private fun createaccount() {


        emailTxt = findViewById(R.id.txtemail)
        val email = emailTxt.text.toString()
        passwordTxt = findViewById(R.id.txtpassword)
        val password = passwordTxt.text.toString()
        nameTxt = findViewById(R.id.txtname)
        val name = nameTxt.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
        ) {
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }

        mAuth!!
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val userId = mAuth!!.currentUser!!.uid
                    //Verify Email
                    verifyEmail()
                    //update user profile information
                    val currentUserDb = mDatabaseReference.child(userId)
                    currentUserDb.child("email").setValue(email)
                    currentUserDb.child("password").setValue(password)
                    currentUserDb.child("name").setValue(name)
                    currentUserDb.child("image").setValue(name)

                    createUser(getString(R.string.comet_app_id), getString(R.string.comet_api_key), "eu", email, password)

                    updateUserInfoAndUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@Createaccount, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


    }


    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@Createaccount,
                        "Verification email sent to " + mUser.getEmail(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(
                        this@Createaccount,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this@Createaccount, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun createUser(appId:String, apiKey:String , region:String ,  UID:String, name:String ) {

            val createUserEndpoint:String = "https://api-eu.cometchat.io/v2.0/users";
            val url = URL(createUserEndpoint)
              //val url = URL(String.format(createUserEndpoint, region))

                val httpsURLConnection = url.openConnection() as  (HttpsURLConnection)
                httpsURLConnection.setReadTimeout(10000)
                httpsURLConnection.connectTimeout =15000
                httpsURLConnection.requestMethod = "POST"
                httpsURLConnection.setDoInput(true)
                httpsURLConnection.doOutput =true

                // Adding the necessary headers

                httpsURLConnection.setRequestProperty("appId", appId)
                httpsURLConnection.setRequestProperty("appKey", apiKey)
                httpsURLConnection.setRequestProperty("content-type", "application/json")
                httpsURLConnection.setRequestProperty("accept", "application/json")

                // Creating the JSON with post params
                emailTxt = findViewById(R.id.txtemail)
                val email = emailTxt.text.toString()
                passwordTxt = findViewById(R.id.txtpassword)
                val password = passwordTxt.text.toString()

                val userData = JSONObject()
                userData.put("uid", email)
                userData.put("name", password)
                userData.put("status", "offline")

        try {
                val outputStream = BufferedOutputStream(httpsURLConnection.getOutputStream())
                val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
                writer.write(userData.toString())
                writer.flush()
                writer.close()
                outputStream.close()
        }
        catch (exception:Exception){
        }

        if(httpsURLConnection.responseCode != HttpsURLConnection.HTTP_OK && httpsURLConnection.responseCode!= HttpsURLConnection.HTTP_CREATED){
            try {
                val inputStream: InputStream? = null
                val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                val inputLine = bufferedReader.readLine()
                var result:String =  String()
                while (inputLine != null) {
                    result += inputLine
                }

                Toast.makeText(this,"An error while connecting the chat $inputLine", Toast.LENGTH_SHORT).show()

            } catch (e:Exception){
                e.printStackTrace()
            }
        }
        /*
                val responseCode:Int = httpsURLConnection.getResponseCode()
                val responseMessage:String = httpsURLConnection.getResponseMessage()
                Log.d(TAG, "Response Code : " + responseCode)
                Log.d(TAG, "Response Message : " + responseMessage)
                var result:String =  String()
                var inputStream: InputStream? = null
                if (responseCode >= 400 && responseCode <= 499) {
                    inputStream = httpsURLConnection.getErrorStream()
                } else {
                    inputStream = httpsURLConnection.getInputStream()
                }
                val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
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
        try{
            } catch (e:Exception) {
                e.printStackTrace()
            }
        */
        }
}