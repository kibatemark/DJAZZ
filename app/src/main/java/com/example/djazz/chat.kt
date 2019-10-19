package com.example.djazz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
//import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.Toast
//import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
//import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
//import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.cometchat.pro.models.User
import com.google.android.material.card.MaterialCardView

class chat : AppCompatActivity()
{
    var user: UserModel? = null
    lateinit var txtMessageBox: AppCompatEditText
    lateinit var btnSend: AppCompatImageView
    lateinit var recyclerMessages: RecyclerView
    lateinit var progressLoading: ProgressBar

    val messagesList = arrayListOf<MessageModel>()
    var messagesAdapter: ChatMessagesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        if (intent.extras != null)
        {
            user = UserModel (
                uid = intent.getStringExtra("uid"),
                name = intent.getStringExtra("name"),
                status = intent.getStringExtra("status"),
                photoUrl = intent.getStringExtra("photo")
            )

            setupViews()
            loadConversationMessages()
           // loadDummyMessages()
        }
        else
        {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        user?.let {
            CometChat.addMessageListener(getUniqueListenerId(it.uid), object : CometChat.MessageListener() {
                override fun onTextMessageReceived(message: TextMessage?) {
                    message?.let {
                        messagesList.add(MessageModel(message = it.text, isMine = false))
                        messagesAdapter?.notifyItemInserted(messagesList.size)
                        recyclerMessages.scrollToPosition(messagesList.size-1)
                    }
                }
            })

            // Add Online/Offline Listener
            CometChat.addUserListener(getUniqueListenerId(it.uid), object : CometChat.UserListener() {
                override fun onUserOffline(offlineUser: User?) {
                    super.onUserOffline(offlineUser)
                    setUserStatus(false)
                }

                override fun onUserOnline(user: User?) {
                    super.onUserOnline(user)
                    setUserStatus(true)
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()

        user?.let {
            CometChat.removeMessageListener(getUniqueListenerId(it.uid))
            CometChat.removeUserListener(getUniqueListenerId(it.uid))
        }
    }

    fun setupViews()
    {
        // Get Views
        txtMessageBox = findViewById(R.id.txtMessageBox)
        btnSend = findViewById(R.id.imgSend)
        recyclerMessages = findViewById(R.id.recyclerMessages)
        progressLoading = findViewById(R.id.progressLoading)

        // Toolbar
        supportActionBar?.apply {
            title = user?.name
            subtitle = user?.status
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        setUserStatus(user?.status == "online")

        // Recycler View
        messagesAdapter = ChatMessagesAdapter()
        recyclerMessages.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerMessages.adapter = messagesAdapter

        // Message Box
        txtMessageBox.setOnEditorActionListener { _, actionId, _ ->
            when(actionId)
            {
                EditorInfo.IME_ACTION_GO -> {
                    sendMessage(txtMessageBox.text.toString())
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }

        // Send Button
        btnSend.setOnClickListener {
            sendMessage(txtMessageBox.text.toString())
        }
    }

    private fun setUserStatus(isOnline: Boolean)
    {
        if (isOnline)
        {
            //supportActionBar?.subtitle = Html.fromHtml("<font color='#149214'>online</font>")
            HtmlCompat.fromHtml("<font color='#149214'>online</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        else
        {
            //supportActionBar?.subtitle = Html.fromHtml("<font color='#575757'>offline</font>")
            HtmlCompat.fromHtml("<font color='#575757'>offline</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        }
    }

    private fun sendMessage(message: String) {
        if (!message.isEmpty())
        {
            user?.let {
                val receiverID: String = it.uid
                val messageText = message
                val messageType = CometChatConstants.MESSAGE_TYPE_TEXT
                val receiverType = CometChatConstants.RECEIVER_TYPE_USER

                val textMessage = TextMessage(receiverID, messageText, messageType,receiverType)

                CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
                    override fun onSuccess(p0: TextMessage?) {

                    }

                    override fun onError(p0: CometChatException?) {

                    }
                })

                val messageModel = MessageModel(message, true)
                messagesList.add(messageModel)
                messagesAdapter?.notifyItemInserted(messagesList.size-1)
                recyclerMessages.scrollToPosition(messagesList.size-1)

                // Clear the message box
                txtMessageBox.setText("")
            }
        }
    }


    fun loadConversationMessages()
    {
        user?.let {

            // Show Progress Bar
            progressLoading.visibility = View.VISIBLE
            recyclerMessages.visibility = View.GONE

            val messagesRequest = MessagesRequest.MessagesRequestBuilder()
                .setUID(it.uid)
                .build()

            messagesRequest.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
                override fun onSuccess(msgList: List<BaseMessage>?) {
                    // Hide Progress bar
                    progressLoading.visibility = View.GONE
                    recyclerMessages.visibility = View.VISIBLE

                    if (msgList != null)
                    {
                        for (msg in msgList)
                        {
                            if (msg is TextMessage)
                            {
                                messagesList.add(msg.convertToMessageModel())
                            }
                        }

                        // Update RecyclerView
                        messagesAdapter?.notifyDataSetChanged()
                    }
                    else
                    {
                        Toast.makeText(this@chat, "Couldn't fetch messages!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(exception: CometChatException?) {
                    // Hide Progress bar
                    progressLoading.visibility = View.GONE
                    recyclerMessages.visibility = View.VISIBLE

                    Toast.makeText(this@chat, exception?.localizedMessage ?: "Unknown error occurred!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId)
        {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


/*    fun loadDummyMessages()
    {
        messagesList.add(MessageModel("Hi", true))
        messagesList.add(MessageModel("How are you?", true))
        messagesList.add(MessageModel("I'm fine", false))
        messagesList.add(MessageModel("How about ya?", false))
        messagesList.add(MessageModel("Fine", true))
        messagesList.add(MessageModel("What ya upto these days?", true))
        messagesList.add(MessageModel("Same ol' job dude!", false))
        messagesList.add(MessageModel("Same ol' job dude! asfa sfasf asfd asfd asf asdf asfd asf asfasf asf asf asf sf safasfdasdf asf asfd asf asdfasdf", false))
        messagesList.add(MessageModel("Same ol' job dude! asfa sfasf asfd asfd asf asdf asfd asf asfasf asf asf asf sf safasfdasdf asf asfd asf asdfasdf", true))
        messagesAdapter?.notifyDataSetChanged()
    }

 */
    // Recycler Adapter
    inner class ChatMessagesAdapter : RecyclerView.Adapter<ChatMessagesAdapter.MessageViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view = LayoutInflater.from(this@chat).inflate(R.layout.chat_message_item, parent, false)
            return MessageViewHolder(view)
        }

        override fun getItemCount(): Int = messagesList.size

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val messageModel = messagesList[position]
            holder.bindItem(messageModel)
        }

        // View Holder
        inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            var txtMyMessage: AppCompatTextView
            var txtOtherMessage: AppCompatTextView
            var cardMyMessage: MaterialCardView
            var cardOtherMessage: MaterialCardView

            init {
                txtMyMessage = itemView.findViewById(R.id.txtMyMessage)
                txtOtherMessage = itemView.findViewById(R.id.txtOtherMessage)
                cardMyMessage = itemView.findViewById(R.id.cardChatMyMessage)
                cardOtherMessage = itemView.findViewById(R.id.cardChatOtherMessage)
            }


            fun bindItem(messageModel: MessageModel)
            {
                if (messageModel.isMine)
                {
                    cardMyMessage.visibility = View.VISIBLE
                    cardOtherMessage.visibility = View.GONE
                    txtMyMessage.text = messageModel.message
                }
                else
                {
                    cardMyMessage.visibility = View.GONE
                    cardOtherMessage.visibility = View.VISIBLE
                    txtOtherMessage.text = messageModel.message
                }
            }
        }
    }
}