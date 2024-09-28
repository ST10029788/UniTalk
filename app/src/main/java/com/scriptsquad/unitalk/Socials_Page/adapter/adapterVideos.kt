package com.scriptsquad.unitalk.Socials_Page.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.Notes.MyApplication
import com.scriptsquad.unitalk.databinding.RowVideosBinding
import com.scriptsquad.unitalk.Socials_Page.activities.Download_Video_Activity
import com.scriptsquad.unitalk.Socials_Page.model.modelVideo

class adapterVideos : RecyclerView.Adapter<adapterVideos.viewHolderVideo> {

    private var context: Context

    private lateinit var binding: RowVideosBinding

    private var uid: String = ""

    private var name: String = ""

    private var userMode: String = ""

    private var profileImageURl: String = ""

    private var galleryVideoArrayList: ArrayList<modelVideo>

    private lateinit var firebaseAuth: FirebaseAuth


    constructor(context: Context, galleryVideoArrayList: ArrayList<modelVideo>) : super() {
        this.context = context
        this.galleryVideoArrayList = galleryVideoArrayList
    }

    init {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderVideo {

        binding = RowVideosBinding.inflate(LayoutInflater.from(context), parent, false)

        return viewHolderVideo(binding.root)

    }


    override fun onBindViewHolder(holder: viewHolderVideo, position: Int) {

        //get data
        val model = galleryVideoArrayList[position]

        val id = model.id
        val videoUrl = model.videoUrl
        val timestamp = model.timestamp
        val title = model.title
        uid = model.uid

        val formattedDate = MyApplication.formatTimestampDate(timestamp)

        //set data
        holder.date.text = formattedDate
        holder.titleTv.text = title

        holder.userNameTv.text = name

        loadVideo(videoUrl)

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    name = "${snapshot.child("name").value}"
                    profileImageURl = "${snapshot.child("profileImageURl").value}"
                    userMode = "${snapshot.child("userMode").value}"

                    holder.userNameTv.text = name
                    loadPersonImage(profileImageURl, context, binding.personIv)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        val ref1 = FirebaseDatabase.getInstance().getReference("Users")
        ref1.child(firebaseAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    userMode = "${snapshot.child("userMode").value}"

                    holder.moreBtn.setOnClickListener {
                        moreOptionsDialog(model, holder, userMode)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



    }

    override fun getItemCount(): Int {
        return galleryVideoArrayList.size
    }

    fun loadPersonImage(
        imageUrl: String,
        context: Context,
        imageView: ShapeableImageView
    ) {

        val tag = "BOOK_IMAGE_TAG"

        //using url we can get image
        try {

            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_gray)
                .into(imageView)

        } catch (e: Exception) {
            Log.e(tag, "onDataChanged", e)
        }

    }

    private fun moreOptionsDialog(
        model: modelVideo,
        holder: viewHolderVideo,
        userMode:String
    ) {

        //get id,url ,title of the book
        val videoUrl = model.videoUrl
        val id = model.id
        // options to show in Dialog

        if (firebaseAuth.uid == model.uid) {
            val options = arrayOf("Download", "Delete")

            // alert dialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose Option")
                .setItems(options) { _, position ->

                    if (position == 0) {
                        val intent = Intent(context, Download_Video_Activity::class.java)
                        intent.putExtra("videoUrl", model.videoUrl)
                        context.startActivity(intent)

                    } else if (position == 1) {
                        //delete is  is clicked
                        //show confirmation dialog
                        deleteVideoFromStorage(videoUrl)
                        deleteFromDb(id)
                    }

                }
                .show()
        } else if (userMode == "Admin") {
            val options = arrayOf("Download", "Delete")

            // alert dialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose Option")
                .setItems(options) { _, position ->

                    if (position == 0) {
                        val intent = Intent(context, Download_Video_Activity::class.java)
                        intent.putExtra("videoUrl", model.videoUrl)
                        context.startActivity(intent)

                    } else if (position == 1) {
                        //delete is  is clicked
                        //show confirmation dialog
                        deleteVideoFromStorage(videoUrl)
                        deleteFromDb(id)
                    }

                }
                .show()
        } else {
            val options = arrayOf("Download")

            // alert dialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose Option")
                .setItems(options) { _, _ ->

                    val intent = Intent(context, Download_Video_Activity::class.java)
                    intent.putExtra("videoUrl", model.videoUrl)
                    context.startActivity(intent)

                }
                .show()
        }


    }

    private fun loadVideo(videoUrl: String) {
        // Initialize the player
        val player = SimpleExoPlayer.Builder(context).build()
        binding.playerView.player = player

        // Set the media item
        player.setMediaItem(MediaItem.fromUri(videoUrl))

        // Prepare the player
        player.prepare()

//        // Autoplay the video
//        player.play()
    }

    private fun deleteVideoFromStorage(videoUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("$videoUrl")

        // Delete the video file
        storageRef.delete()
            .addOnSuccessListener {
                // Video successfully deleted
                Log.d("TAG", "Video deleted successfully")
                Utils.toast(context, "Deleted Successfully")

            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the deletion
                Log.e("TAG", "Error deleting video: ${exception.message}")
                Utils.toast(context, "Failed to delete due to $exception")
            }
    }

    private fun deleteFromDb(id: String) {

        val ref = FirebaseDatabase.getInstance().getReference("GalleryVideos")
        ref.child(id).removeValue()
            .addOnSuccessListener {

            }.addOnFailureListener {

            }

    }


    inner class viewHolderVideo(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTv = binding.titleTv
        val userNameTv = binding.userNameTv
        val date = binding.publishDateTv
        val moreBtn = binding.moreBtn

    }

}