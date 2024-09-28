package com.scriptsquad.unitalk.Notices_Page

import android.widget.Filter
import com.scriptsquad.unitalk.Notices_Page.adapter.AdapterNotice
import com.scriptsquad.unitalk.Notices_Page.models.modelNotice

class FilterNoticeBoard: Filter {

    //Array list in Which we want to Search
    val filterList:List<modelNotice>

    val adapterNoticeBoard: AdapterNotice

    constructor(filterList: List<modelNotice>, adapterNoticeBoard: AdapterNotice) {
        this.filterList = filterList
        this.adapterNoticeBoard = adapterNoticeBoard
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence? = constraint // value to search
        val result = FilterResults()

        //value should not be null and not empty

        if (constraint != null && constraint.isNotEmpty()) {
            // change to Upper case or lowercase to avoid case senstivity

            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<modelNotice>()

            for (i in filterList.indices) {
                //validate if match

                if (filterList[i].title.lowercase().contains(constraint)) {
                    // searched value is smaller to value in list, add to filtered list
                    filteredModels.add(filterList[i])
                }

            }

            result.count = filteredModels.size
            result.values = filteredModels

        } else {

            // searched value is either null or empty return all data

            result.count = filterList.size
            result.values = filterList

        }

        return result // don't miss
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {

        //apply filter changes
        adapterNoticeBoard.noticeArrayList = results.values as List<modelNotice>

        //notify changes
       adapterNoticeBoard.notifyDataSetChanged()


    }


}