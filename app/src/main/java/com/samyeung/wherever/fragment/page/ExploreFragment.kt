package com.samyeung.wherever.fragment.page


import android.os.Bundle
import com.samyeung.wherever.api.ImageTraceServiceAdapter
import com.samyeung.wherever.model.Location
import com.samyeung.wherever.model.Trace
import java.util.*




class ExploreFragment : BaseExploreFragment() {
    private var imageTraceService: ImageTraceServiceAdapter? = null
    override fun doFindTrace(location: Location, filterData: Data?) {
        this.imageTraceService!!.getTraces(
            location.lng,
            location.lat,
            filterData!!.distance,
            Date(filterData.startDate),
            Date(filterData.endDate),
            tags = filterData.tags
        )
    }



    override fun setUpService() {
        this.imageTraceService = object : ImageTraceServiceAdapter(context!!) {
            override fun onGetTraces(traces: Array<Trace>) {
                this@ExploreFragment.handleMarker(context, traces)
            }
        }
    }
    override fun onDestroy() {
        this.imageTraceService!!.onDestroy()
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ExploreFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
