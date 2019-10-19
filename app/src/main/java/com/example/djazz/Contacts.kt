package com.example.djazz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
//import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.cometchat.pro.core.CometChat
//import com.cometchat.pro.core.UsersRequest
//import com.cometchat.pro.exceptions.CometChatException
//import com.cometchat.pro.models.User
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.core.UsersRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Contacts : AppCompatActivity() {

    lateinit var recyclerContacts: RecyclerView
    lateinit var progressLoading: ProgressBar
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: ContactsRecyclerAdapter
    var contactsList = arrayListOf<UserModel>()

    private var mDatabase = FirebaseDatabase.getInstance()
    private var mDatabaseReference = mDatabase.reference.child("Users")
    private var mAuth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        setupViews()
        //loadAllUsers()
        loadDummyData()
    }


    override fun onPause() {
        super.onPause()
        for (user in contactsList)
        {
            CometChat.removeUserListener(getUniqueListenerId(user.uid))
        }
    }

    fun setupViews()
    {
        // progress bar
        progressLoading = findViewById(R.id.progressLoading)

        // RecyclerView
        recyclerContacts = findViewById(R.id.recyclerContacts)
        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerContacts.layoutManager = layoutManager
        recyclerAdapter = ContactsRecyclerAdapter(this)
        recyclerContacts.adapter = recyclerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_contacts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.menuProfile -> {
                val intent = Intent(this, profile::class.java)
                startActivity(intent)
            }
        }
        return true
    }

  /*  private fun loadAllUsers()
    {
        // Show Progress Bar
        progressLoading.visibility = View.VISIBLE
        recyclerContacts.visibility = View.GONE

        // Load All Users from Comet Chat
        val usersRequest = UsersRequest.UsersRequestBuilder().setLimit(50).build()
        usersRequest.fetchNext(object : CometChat.CallbackListener<List<User>>() {
            override fun onSuccess(usersList: List<User>?) {
                if (usersList != null)
                {
                    val loggedInUser = CometChat.getLoggedInUser()
                    for (user in usersList)
                    {
                        // Don't add yourself (logged in user) in the list
                        if (loggedInUser.uid != user.uid)
                        {
                            contactsList.add(user.convertToUserModel())

                            

                            // Add Online/Offline Listener
                            CometChat.addUserListener(getUniqueListenerId(user.uid), object : CometChat.UserListener() {
                                override fun onUserOffline(offlineUser: User?) {
                                    super.onUserOffline(offlineUser)
                                    user.let {
                                        searchUserWithId(contactsList, it.uid)?.let {
                                            contactsList[it].status = "offline"
                                            recyclerAdapter.notifyItemChanged(it)

                                        }
                                    }

                                }

                                override fun onUserOnline(user: User?) {
                                    super.onUserOnline(user)
                                    user?.let {
                                        searchUserWithId(contactsList, it.uid)?.let {
                                            contactsList[it].status = "online"
                                            recyclerAdapter.notifyItemChanged(it)

                                        }
                                    }
                                }
                            })
                        }
                    }

                    // Update the Recycler Adapter
                    recyclerAdapter.notifyDataSetChanged()
                }
                else
                {
                    Toast.makeText(this@Contacts, "Couldn't load the users!", Toast.LENGTH_SHORT).show()
                }

                // Hide Progress
                progressLoading.visibility = View.GONE
                recyclerContacts.visibility = View.VISIBLE
            }

            override fun onError(exception: CometChatException?) {

                // Hide Progress
                progressLoading.visibility = View.GONE
                recyclerContacts.visibility = View.VISIBLE

                Toast.makeText(this@Contacts, exception?.localizedMessage ?: "Unknown error occurred!", Toast.LENGTH_SHORT).show()
            }
        })
    }


*/
 fun loadDummyData()
    {
    val contactslistener: ValueEventListener = object :ValueEventListener{
        //val datasnapshot: DataSnapshot? = null
        override fun onCancelled(p0: DatabaseError) {
                Log.w("Contacts","load contacts failed", p0.toException())
            }
        override fun onDataChange(p0: DataSnapshot) {
            for (datasnapshot in p0.children){
                var usermodel = UserModel()
                usermodel = datasnapshot.getValue(UserModel::class.java)!!
                contactsList.add(usermodel)
                recyclerAdapter.notifyDataSetChanged()
            }

            }

        }


        mDatabaseReference.addValueEventListener(contactslistener)

        /*contactsList.add(UserModel("John Doe", "John Doe","Online", "http://cdn.pixabay.com/photo/2013/07/13/10/07/man-156584_960_720.png"))
        contactsList.add(UserModel("Alexa Johnson", "Alexa Johnson", "Offline", "https://www.google.com/imgres?imgurl=https%3A%2F%2Fcdn.pixabay.com%2Fphoto%2F2018%2F05%2F17%2F11%2F14%2Fletter-3408291__340.png&imgrefurl=https%3A%2F%2Fpixabay.com%2Fimages%2Fsearch%2Fcapital%2520letter%2F&docid=bJtx_2_ui-P4aM&tbnid=CT9G-jIOvsHCCM%3A&vet=10ahUKEwjT1cm80s3kAhUnz6YKHfXlDUwQMwh1KAAwAA..i&w=340&h=340&client=opera&bih=601&biw=1263&q=letter%20images&ved=0ahUKEwjT1cm80s3kAhUnz6YKHfXlDUwQMwh1KAAwAA&iact=mrc&uact=8"))
        contactsList.add(UserModel("Robert Smith", "Robert Smith","Online", "https://www.google.com/imgres?imgurl=https%3A%2F%2Fcdn.pixabay.com%2Fphoto%2F2018%2F05%2F17%2F11%2F14%2Fletter-3408291__340.png&imgrefurl=https%3A%2F%2Fpixabay.com%2Fimages%2Fsearch%2Fcapital%2520letter%2F&docid=bJtx_2_ui-P4aM&tbnid=CT9G-jIOvsHCCM%3A&vet=10ahUKEwjT1cm80s3kAhUnz6YKHfXlDUwQMwh1KAAwAA..i&w=340&h=340&client=opera&bih=601&biw=1263&q=letter%20images&ved=0ahUKEwjT1cm80s3kAhUnz6YKHfXlDUwQMwh1KAAwAA&iact=mrc&uact=8"))
        contactsList.add(UserModel("Steve Boam", "Steve Boam","Online",  "https://www.google.com/imgres?imgurl=https%3A%2F%2Fcdn.pixabay.com%2Fphoto%2F2018%2F05%2F17%2F11%2F14%2Fletter-3408291__340.png&imgrefurl=https%3A%2F%2Fpixabay.com%2Fimages%2Fsearch%2Fcapital%2520letter%2F&docid=bJtx_2_ui-P4aM&tbnid=CT9G-jIOvsHCCM%3A&vet=10ahUKEwjT1cm80s3kAhUnz6YKHfXlDUwQMwh1KAAwAA..i&w=340&h=340&client=opera&bih=601&biw=1263&q=letter%20images&ved=0ahUKEwjT1cm80s3kAhUnz6YKHfXlDUwQMwh1KAAwAA&iact=mrc&uact=8"))
        recyclerAdapter.notifyDataSetChanged()*/
    }




    inner class ContactsRecyclerAdapter(val context: Context) : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            return ContactViewHolder(LayoutInflater.from(this@Contacts).inflate(R.layout.contact_item_layout, parent, false))
        }

        override fun getItemCount(): Int = contactsList.size

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val user = contactsList[position]
            holder.bindItem(user) {
                val intent = Intent(context, chat::class.java)
                intent.putExtra("uid", user.uid)
                intent.putExtra("name", user.name)
                intent.putExtra("status", user.status)
                intent.putExtra("photo", user.photoUrl)
                context.startActivity(intent)
            }
        }

        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            var txtUsername: AppCompatTextView
            var txtStatus: AppCompatTextView
            var imgContactPhoto: AppCompatImageView
            var userItemClickCallback: (() -> Unit)? = null

            init {
                txtUsername = itemView.findViewById(R.id.txtUsername)
                txtStatus = itemView.findViewById(R.id.txtStatus)
                imgContactPhoto = itemView.findViewById(R.id.imgContactPhoto)
                val cardRootLayout = itemView.findViewById<ConstraintLayout>(R.id.cardRootLayout)
                cardRootLayout.setOnClickListener {
                    userItemClickCallback?.invoke()
                }
            }

            fun bindItem(userModel: UserModel, callback: () -> Unit)
            {
                userItemClickCallback = callback
                txtUsername.text = userModel.name

                if (userModel.photoUrl != null && !userModel.photoUrl.isEmpty())
                {
                    // Load Avatar Image if any
                    Glide.with(context)
                        .asBitmap()
                        .load(userModel.photoUrl)
                        .into(imgContactPhoto)
                }
                else
                {
                    // Generate Letter Avatar
                    setAvatarImage(userModel.name[0].toString())
                }

                txtStatus.text = userModel.status
                if (userModel.status.equals("online"))
                   // txtStatus.setTextColor(context.resources.getColor(R.color.colorOnline))
                txtStatus.setTextColor(ContextCompat.getColor(applicationContext,
                    R.color.colorOnline))
                else
                   // txtStatus.setTextColor(context.resources.getColor(R.color.colorOffline))
                txtStatus.setTextColor(ContextCompat.getColor(applicationContext,
                    R.color.colorOffline))
            }

            fun setAvatarImage(letter: String)
            {
                val generator = ColorGenerator.MATERIAL
                val color = generator.randomColor

                val drawable = TextDrawable.builder().buildRect(letter, color)
                imgContactPhoto.setImageDrawable(drawable)
            }
        }
    }

}
