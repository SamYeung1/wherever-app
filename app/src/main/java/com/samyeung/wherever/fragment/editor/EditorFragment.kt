package com.samyeung.wherever.fragment.editor


import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*

import com.samyeung.wherever.R
import com.samyeung.wherever.api.ImageTraceService
import com.samyeung.wherever.view.adapter.NoScrollViewPager
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.AlertUtils
import com.samyeung.wherever.util.helper.StorageUtil
import com.samyeung.wherever.fragment.editor.page.base.BaseEditorFragment
import com.samyeung.wherever.fragment.editor.page.base.BaseEditorPagerAdapter
import com.samyeung.wherever.fragment.editor.page.base.DescriptionEditorFragment
import com.samyeung.wherever.fragment.editor.page.base.LocationEditorFragment
import com.samyeung.wherever.fragment.editor.page.file.FileDescriptionEditorFragment
import com.samyeung.wherever.fragment.editor.page.file.FileLocationEditorFragment
import kotlinx.android.synthetic.main.fragment_editor.*


/**
 * A simple [Fragment] subclass.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
open class EditorFragment : BaseFragment() {
    private lateinit var type: String
    protected lateinit var descriptionData: DescriptionEditorFragment.Data
    protected lateinit var locationData: LocationEditorFragment.Data
    private var currentPosition = 0
    private val actionBarControllers =
        arrayOf(FirstActionBarController(), OnLastActionBarController())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            this.type = it.getString(READ_TYPE)!!
            this.descriptionData = it.getParcelable(DESCRIPTION_DATA)!!
            this.locationData = it.getParcelable(LOCATION_DATA)!!
        }
        this.title = context!!.resources.getString(R.string.upload_image)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpToolbar(true)
        this.bindView(savedInstanceState)
        (activity!! as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_gray_24dp)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        pager_editor.setPageTransformer(false, NoScrollViewPager.FadeTransition())
        pager_editor.adapter = PagerAdapterForFile(childFragmentManager, this.descriptionData, this.locationData)
        this.currentPosition = pager_editor.currentItem
        pager_editor.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (positionOffset == 0.0f) {
                    this@EditorFragment.currentPosition = position
                    activity!!.invalidateOptionsMenu()
                }
            }

            override fun onPageSelected(position: Int) {
                activity!!.invalidateOptionsMenu()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                if (this@EditorFragment.currentPosition == 0) {
                    showAlertOnClose()
                } else {
                    this.backPage()
                }
                true
            }
            R.id.forward -> {
                val editorFragment = ((pager_editor.adapter as? BaseEditorPagerAdapter)!!.getItem(pager_editor.currentItem) as BaseEditorFragment)
                if(editorFragment.canUpload()){
                    this.forwardPage()
                    true
                }else{
                    editorFragment.showError()
                    false
                }


            }
            R.id.done -> {
                item.isEnabled = false
                this.done()
                true
            }
            else -> {
                false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_editor_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        this@EditorFragment.actionBarControllers[currentPosition].render(activity!!, menu!!)
    }

    override fun onBackPressed(): Boolean {
        this.backPage()
        if (currentPosition == 0) {
            this.showAlertOnClose()
        }
        return true
    }

    private fun showAlertOnClose() {
        AlertUtils.create(context!!, "", context!!.resources.getString(R.string.warm_discard_trace), {

        }, {
            if (this.descriptionData.readType == READ_TYPE_FILE) {
                StorageUtil.deleteImage(context!!, this.descriptionData.imagePath)
            }

            activity!!.finish()
        }).show()
    }

    private fun backPage() {
        pager_editor.currentItem =
            if (pager_editor.currentItem < pager_editor.adapter!!.count) pager_editor.currentItem - 1 else 0
    }

    private fun forwardPage() {
        pager_editor.currentItem =
            if (pager_editor.currentItem < pager_editor.adapter!!.count) pager_editor.currentItem + 1 else 0
    }

    open fun done() {
        if (pager_editor.adapter is PagerAdapterForFile) {
            val pagerAdapter = pager_editor.adapter!! as PagerAdapterForFile
            val imageE =
                (pagerAdapter.getItem(0) as DescriptionEditorFragment).getDataForPrepareUpload() as DescriptionEditorFragment.Data
            val locationE =
                (pagerAdapter.getItem(1) as LocationEditorFragment).getDataForPrepareUpload() as LocationEditorFragment.Data
            UploadFragment.start(
                activity!!, ImageTraceService.TraceBody(
                    Uri.parse(imageE.imagePath),
                    imageE.title,
                    imageE.description,
                    imageE.tags,
                    locationE.location.lng,
                    locationE.location.lat
                )
            )
            activity!!.finish()
        }

    }

    companion object {
        const val READ_TYPE = "READ_TYPE"
        const val DESCRIPTION_DATA = "DESCRIPTION_DATA"
        const val LOCATION_DATA = "DATA"
        const val READ_TYPE_FILE = "FILE"
        const val READ_TYPE_HTTP = "HTTP"
        fun start(
            activity: Activity,
            descriptionData: DescriptionEditorFragment.Data,
            locationData: LocationEditorFragment.Data
        ) {
            val bundle = Bundle()
            descriptionData.readType = READ_TYPE_FILE
            locationData.readType = READ_TYPE_FILE
            bundle.putParcelable(DESCRIPTION_DATA, descriptionData)
            bundle.putParcelable(LOCATION_DATA, locationData)
            bundle.putString(READ_TYPE, READ_TYPE_FILE)
            PortraitBaseActivity.start(activity, EditorFragment(), bundle)
        }
    }

    private class PagerAdapterForFile(
        fragmentManager: FragmentManager,
        private val descriptionData: DescriptionEditorFragment.Data,
        private val locationData: LocationEditorFragment.Data
    ) :
        BaseEditorPagerAdapter(fragmentManager) {
        private val fragments = arrayOf(
            FileDescriptionEditorFragment.newInstance(descriptionData),
            FileLocationEditorFragment.newInstance(locationData)
        )

        override fun getItem(p0: Int): Fragment = fragments[p0]

        override fun getCount(): Int = fragments.size

    }

    //Action Bar Controller
    abstract class ActionBarController {
        open fun render(activity: Activity, menu: Menu) {
        }
    }

    private class FirstActionBarController : ActionBarController() {
        override fun render(activity: Activity, menu: Menu) {
            super.render(activity, menu)
            menu.findItem(R.id.done).isVisible = false
            menu.findItem(R.id.forward).isVisible = true
            (activity as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_gray_24dp)

        }

    }

    private class OnProcessingActionBarController : ActionBarController() {
        override fun render(activity: Activity, menu: Menu) {
            super.render(activity, menu)
            menu.findItem(R.id.done).isVisible = false
            menu.findItem(R.id.forward).isVisible = true
            (activity as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_gray_24dp)

        }

    }

    private class OnLastActionBarController : ActionBarController() {
        override fun render(activity: Activity, menu: Menu) {
            super.render(activity, menu)
            menu.findItem(R.id.done).isVisible = true
            menu.findItem(R.id.forward).isVisible = false
            (activity as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_gray_24dp)

        }

    }
}
