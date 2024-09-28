package com.scriptsquad.unitalk.Notices_Page.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.scriptsquad.unitalk.Notices_Page.FilterNoticeAdmin
import com.scriptsquad.unitalk.Notices_Page.activities.Notice_Details_Activity
import com.scriptsquad.unitalk.Notices_Page.models.modelNotice
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.Notes.MyApplication
import com.scriptsquad.unitalk.databinding.RowNoticeAdminBinding


class AdapterAdminNotice : RecyclerView.Adapter<AdapterAdminNotice.HolderAdminNotice>, Filterable {

    private var context: Context
    var noticeAdminArrayList: List<modelNotice> = emptyList()
    private var filteredNoticeList: List<modelNotice> = emptyList()
    private var filter: FilterNoticeAdmin? = null

    constructor(context: Context, noticeArrayList: List<modelNotice>) : super() {
        this.context = context
        this.noticeAdminArrayList = noticeArrayList
        this.filteredNoticeList = noticeArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAdminNotice {
        val binding = RowNoticeAdminBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAdminNotice(binding)
    }

    override fun onBindViewHolder(holder: HolderAdminNotice, position: Int) {
        val model = filteredNoticeList[position]

        val id = model.id

        holder.titleTv.text = model.title
        holder.descriptionTv.text = model.description
        holder.dateTv.text = MyApplication.formatTimestampDate(model.timestamp)


        MyApplication.loadBookImage(
            model.imageUrl,
            holder.progressBar,
            context,
            holder.noticeImageIv
        )

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Notice_Details_Activity::class.java).apply {
                putExtra("noticeId", model.id)
            }
            context.startActivity(intent)
        }

        holder.deleteBtn.setOnClickListener {
            deleteVideoFromStorage(model.imageUrl)
            deleteFromDb(id)
        }
    }

    private fun deleteVideoFromStorage(noticeUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(noticeUrl)

        // Delete the video file
        storageRef.delete()
            .addOnSuccessListener {
                // Video successfully deleted
                Log.d("TAG", "Notice deleted successfully")
                Utils.toast(context, "Deleted Successfully")

            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the deletion
                Log.e("TAG", "Error deleting video: ${exception.message}")
                Utils.toast(context, "Failed to delete due to $exception")
            }
    }

    private fun deleteFromDb(id: String) {

        val ref = FirebaseDatabase.getInstance().getReference("Notices")
        ref.child(id).removeValue()
            .addOnSuccessListener {

            }.addOnFailureListener {

            }

    }

    override fun getItemCount() = filteredNoticeList.size

    override fun getFilter(): Filter {
        if (filter == null) {
filter = FilterNoticeAdmin(noticeAdminArrayList,this)
        }
        return filter as FilterNoticeAdmin
    }

    inner class HolderAdminNotice(private val binding: RowNoticeAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val dateTv = binding.dateTv
        val noticeImageIv = binding.noticeImageIv
        val deleteBtn = binding.deleteBtn
    }
}