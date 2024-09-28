package com.scriptsquad.unitalk.Notices_Page

import android.widget.Filter
import com.scriptsquad.unitalk.Notices_Page.adapter.AdapterAdminNotice
import com.scriptsquad.unitalk.Notices_Page.models.modelNotice

class FilterNoticeAdmin: Filter {

    //Array list in Which we want to Search
    val filterAdminList:List<modelNotice>

    val adapterAdminNotice: AdapterAdminNotice

    constructor(filterList: List<modelNotice>, adapterAdminNoticeBoard: AdapterAdminNotice) {
        this.filterAdminList = filterList
        this.adapterAdminNotice = adapterAdminNoticeBoard
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {

        var constraint: CharSequence? = constraint // value to search
        val result = FilterResults()

        //value should not be null and not empty

        if (constraint != null && constraint.isNotEmpty()) {
            // change to Upper case or lowercase to avoid case senstivity

            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<modelNotice>()

            for (i in filterAdminList.indices) {
                //validate if match

                if (filterAdminList[i].title.lowercase().contains(constraint)) {
                    // searched value is smaller to value in list, add to filtered list
                    filteredModels.add(filterAdminList[i])
                }

            }

            result.count = filteredModels.size
            result.values = filteredModels

        } else {

            // searched value is either null or empty return all data

            result.count = filterAdminList.size
            result.values = filterAdminList

        }

        return result // don't miss


    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {

        //apply filter changes
       adapterAdminNotice.noticeAdminArrayList = results.values as List<modelNotice>

        //notify changes
        adapterAdminNotice.notifyDataSetChanged()


    }

}