package com.shaon2016.firebaserealtimechat

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.shaon2016.firebaserealtimechat.adapter.RvMsgAdapter
import com.shaon2016.firebaserealtimechat.databinding.ActivityMainBinding
import com.shaon2016.firebaserealtimechat.model.MyMessage
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy {
        RvMsgAdapter(this, ArrayList())
    }
    private lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.rvMsg.layoutManager = LinearLayoutManager(this)
        binding.rvMsg.adapter = adapter

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadImage(uri)
            }
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.btnChooseImage.setOnClickListener {
            getContent.launch("image/*")
        }

        getMessages()
    }

    private fun getMessages() {
        binding.pb.visibility = View.VISIBLE
        Firebase.database.getReference("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(mainSnapshot: DataSnapshot) {
                    val msgs = arrayListOf<MyMessage>()

                    var senderName = ""

                    Firebase.database.getReference("users")
                        .child(Firebase.auth.currentUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                senderName = snapshot.child("name").value.toString()

                                mainSnapshot.children.forEach {
                                    val createdAt = it.child("createdAt").value.toString()
                                    val id = it.child("id").value.toString()
                                    val m = it.child("message").value.toString()
                                    val senderId = it.child("senderId").value.toString()
                                    val imageUrl = it.child("imageUrl").value.toString()
                                    val message =
                                        MyMessage(id, senderId, senderName, m, imageUrl, createdAt)

                                    msgs.add(message)
                                }

                                adapter.addUniquely(msgs)
                                binding.rvMsg.scrollToPosition(adapter.itemCount - 1)
                                binding.pb.visibility = View.GONE
                            }

                            override fun onCancelled(error: DatabaseError) {
                                binding.pb.visibility = View.GONE
                            }
                        })


                }

                override fun onCancelled(error: DatabaseError) {
                    binding.pb.visibility = View.GONE

                }
            })
    }

    private fun uploadImage(uri: Uri) {
        val storage = Firebase.storage("gs://real-time-chat-11c4d.appspot.com/")
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")
        val savedImagesRef = imageRef.child("images/${UUID.randomUUID()}.jpg")
        val stream = contentResolver.openInputStream(uri)
        stream?.let {
            binding.pb.visibility = View.VISIBLE
            savedImagesRef.putStream(stream).addOnCompleteListener { task ->
                binding.pb.visibility = View.GONE

                val downloadUri = task.result

                sendMessage(downloadUri)
            }
        }

    }

    private fun sendMessage(taskSnapShot: UploadTask.TaskSnapshot? = null) {
        val id = Firebase.database.reference.push().key.toString()
        var msgModel: MyMessage
        val senderName =
            Firebase.database.getReference("users").child(Firebase.auth.currentUser!!.uid)
                .child("name").toString()


        if (taskSnapShot != null) {
            taskSnapShot.storage.downloadUrl.addOnSuccessListener {

                msgModel = MyMessage(
                    id,
                    Firebase.auth.currentUser?.uid!!,
                    senderName,
                    "",
                    it.toString(),
                    Date().time.toString()
                )

                Firebase.database.getReference("messages").child(id).setValue(msgModel)
            }

        } else {
            val msg = binding.evMsg.text.toString()
            msgModel =
                MyMessage(id, Firebase.auth.uid!!, senderName, msg, null, Date().time.toString())


            Firebase.database.getReference("messages").child(id).setValue(msgModel)
            binding.evMsg.setText("")
        }


    }
}