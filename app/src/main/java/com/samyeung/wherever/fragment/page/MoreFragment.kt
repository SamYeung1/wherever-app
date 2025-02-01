package com.samyeung.wherever.fragment.page

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.esafirm.imagepicker.features.ImagePicker
import com.samyeung.wherever.activity.PreferenceActivity

import com.samyeung.wherever.R
import com.samyeung.wherever.api.UserServiceAdapter
import com.samyeung.wherever.util.helper.CropImageUtil
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.fragment.TextEditorDialogFragment
import com.samyeung.wherever.fragment.TextEditorNoEmojiDialogFragment
import com.samyeung.wherever.util.helper.ImagePickerUtil
import com.samyeung.wherever.model.UserProfile
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.view.adapter.ProfileHeaderAndItemRecycleViewAdapter
import com.samyeung.wherever.view.custom.menu.MenuItem
import com.samyeung.wherever.view.custom.menu.MenuListDialogFragment
import com.samyeung.wherever.view.adapter.HeaderAndItemViewRecycleViewAdapter
import com.samyeung.wherever.fragment.main.FavouriteFragment
import com.samyeung.wherever.fragment.main.GalleryFragment
import com.samyeung.wherever.fragment.main.ProfileFragment
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_more.*
import java.io.File


const val FRG_TAG_MENU = "frg_tag_menu"
const val MENU_TAG_PROFILE_ICON = "menu_profile_icon"
const val MENU_TAG_ICON = "menu_icon"
const val TEXT_INPUT_TAG_DISPLAY_NAME = "text_input_display_name"
const val TEXT_INPUT_TAG_ABOUT_ME = "text_input_about_me"

const val REQUEST_CODE_ICON_PICK = 8000
const val REQUEST_CODE_ICON_CROP = 8001
const val REQUEST_CODE_PROFILE_ICON_PICK = 8010
const val REQUEST_CODE_PROFILE_ICON_CROP = 8011

class MoreFragment : BaseFragment(), MenuListDialogFragment.Listener,
    TextEditorDialogFragment.Listener {
    private lateinit var userService: UserServiceAdapter
    private lateinit var headerAndItemViewRecycleViewAdapter: ProfileHeaderAndItemRecycleViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = resources.getString(R.string.app_name)
        setHasOptionsMenu(true)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(false)
        setUpService()
        bindView(savedInstanceState)
        userService.getUserProfile("me")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_more_menu, menu)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.home -> {
                ProfileFragment.start(activity!!)
                true
            }
            R.id.setting -> {
                PreferenceActivity.start(activity!!)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setUpService() {
        this.userService = object : UserServiceAdapter(context!!) {
            override fun onGetProfile(userProfile: UserProfile) {
                this@MoreFragment.dismissLoadingDialog()
                this@MoreFragment.headerAndItemViewRecycleViewAdapter.setProfile(userProfile)
            }

            override fun onUpdateIcon(userProfile: UserProfile) {
                this@MoreFragment.dismissLoadingDialog()
                this@MoreFragment.headerAndItemViewRecycleViewAdapter.setProfile(userProfile)

            }

            override fun onUpdateProfileIcon(userProfile: UserProfile) {
                this@MoreFragment.dismissLoadingDialog()
                this@MoreFragment.headerAndItemViewRecycleViewAdapter.setProfile(userProfile)

            }

            override fun onUpdateAboutMe() {
                this@MoreFragment.userService.getUserProfile("me")

            }

            override fun onRemoveIcon(userProfile: UserProfile) {
                this@MoreFragment.dismissLoadingDialog()
                this@MoreFragment.headerAndItemViewRecycleViewAdapter.setProfile(userProfile)
            }

            override fun onRemoveProfileIcon(userProfile: UserProfile) {
                this@MoreFragment.dismissLoadingDialog()
                this@MoreFragment.headerAndItemViewRecycleViewAdapter.setProfile(userProfile)
            }

            override fun onUpdateDisplayName() {
                this@MoreFragment.userService.getUserProfile("me")
            }
        }
    }

    private fun bindView(savedInstanceState: Bundle?) {
        this.headerAndItemViewRecycleViewAdapter =
            object : ProfileHeaderAndItemRecycleViewAdapter(context!!, rc_view_item) {
                override fun onDisplayNameClick(view: View, profile: UserProfile) {
                    TextEditorNoEmojiDialogFragment.show(
                        childFragmentManager,
                        context!!.getString(R.string.display_name),
                        false,
                        profile.displayName,
                        true,
                        TEXT_INPUT_TAG_DISPLAY_NAME,
                        true
                        , 20
                    )
                }

                override fun onAccountIDClick(view: View, profile: UserProfile) {
                    //None yet
                }

                override fun onHeaderProfileIconClick(view: View, profile: UserProfile) {
                    val list = if(profile.profileIcon == null) arrayListOf(
                        MenuItem(
                            context!!.resources.getString(R.string.select_profile_icon),
                            "",
                            R.drawable.ic_photo_library_gray_24dp
                        )
                    ) else arrayListOf(
                        MenuItem(
                            context!!.resources.getString(R.string.select_profile_icon),
                            "",
                            R.drawable.ic_photo_library_gray_24dp
                        ),
                        MenuItem(
                            context!!.resources.getString(R.string.remove_profile_icon),
                            "",
                            R.drawable.ic_delete_gray_24dp,
                            R.color.colorDelete
                        )
                    )
                    MenuListDialogFragment.show(
                        childFragmentManager, list, MENU_TAG_PROFILE_ICON, FRG_TAG_MENU
                    )
                }

                override fun onHeaderIconClick(view: View, profile: UserProfile) {
                    val list = if(profile.icon == null) arrayListOf(
                        MenuItem(
                            context!!.resources.getString(R.string.select_icon),
                            "",
                            R.drawable.ic_photo_library_gray_24dp
                        )
                    ) else arrayListOf(
                        MenuItem(
                            context!!.resources.getString(R.string.select_icon),
                            "",
                            R.drawable.ic_photo_library_gray_24dp
                        ),
                        MenuItem(
                            context!!.resources.getString(R.string.remove_icon),
                            "",
                            R.drawable.ic_delete_gray_24dp,
                            R.color.colorDelete
                        )
                    )
                    MenuListDialogFragment.show(
                        childFragmentManager,
                        list,
                        MENU_TAG_ICON,
                        FRG_TAG_MENU
                    )
                }

                override fun onAboutMeClick(view: View, profile: UserProfile) {
                    TextEditorDialogFragment.show(
                        childFragmentManager,
                        context!!.getString(R.string.about_me),
                        true,
                        if (profile.aboutMe != null) profile.aboutMe else "",
                        false,
                        TEXT_INPUT_TAG_ABOUT_ME,
                        false
                        , 50
                    )
                }
            }
        this.headerAndItemViewRecycleViewAdapter.addItem(
            HeaderAndItemViewRecycleViewAdapter.BodyItem(
                R.drawable.ic_collections_gray_24dp,
                context!!.resources.getString(R.string.gallery),
                "",
                {
                    GalleryFragment.start(activity!!)
                }
            )
        )
        this.headerAndItemViewRecycleViewAdapter.addItem(
            HeaderAndItemViewRecycleViewAdapter.BodyItem(
                R.drawable.ic_star_white_24dp,
                context!!.resources.getString(R.string.favourite),
                "",
                {
                    FavouriteFragment.start(activity!!)
                }
            )
        )
        //        this.headerAndItemViewRecycleViewAdapter.addItem(
//            HeaderAndItemViewRecycleViewAdapter.BodyItem(
//                R.drawable.ic_love_white_24dp,
//                context!!.resources.getString(R.string.reaction_history),
//                ""
//            )
//        )
    }

    override fun onMenuClicked(mTag: String, position: Int, data: Bundle?) {
        if (mTag == MENU_TAG_ICON) {
            when (position) {
                0 -> {
                    ImagePickerUtil.openImagePicker(
                        activity!!, resources.getString(R.string.select_icon), true,
                        REQUEST_CODE_ICON_PICK
                    )
                }
                1 -> {
                    LoadingDialog.show(childFragmentManager)
                    this.userService.removeIcon()
                }
            }
        } else if (mTag == MENU_TAG_PROFILE_ICON) {
            when (position) {
                0 -> {
                    ImagePickerUtil.openImagePicker(
                        activity!!, resources.getString(R.string.select_profile_icon), true,
                        REQUEST_CODE_PROFILE_ICON_PICK
                    )
                }
                1 -> {
                    LoadingDialog.show(childFragmentManager)
                    this.userService.removeProfileIcon()
                }
            }
        }
    }

    override fun onDone(mTag: String, changedText: String) {
        when (mTag) {
            TEXT_INPUT_TAG_DISPLAY_NAME -> {
                LoadingDialog.show(childFragmentManager)
                this.userService.updateDisplayName(changedText)
            }
            TEXT_INPUT_TAG_ABOUT_ME -> {
                LoadingDialog.show(childFragmentManager)
                this.userService.updateAboutMe(changedText)
            }

        }

    }

    override fun onCancel(mTag: String, changedText: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ICON_PICK -> {
                if (resultCode == Activity.RESULT_OK) {
                    val image = ImagePicker.getFirstImageOrNull(data)
                    CropImageUtil.openCrop(
                        activity!!,
                        Uri.fromFile(File(image.path)),
                        REQUEST_CODE_ICON_CROP,
                        true
                    )
                }

            }
            REQUEST_CODE_PROFILE_ICON_PICK -> {
                if (resultCode == Activity.RESULT_OK) {
                    val image = ImagePicker.getFirstImageOrNull(data)
                    CropImageUtil.openCrop(
                        activity!!,
                        Uri.fromFile(File(image.path)),
                        REQUEST_CODE_PROFILE_ICON_CROP,
                        true
                    )
                }
            }
            REQUEST_CODE_ICON_CROP -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    this.userService.updateIcon(result.uri)
                    LoadingDialog.show(childFragmentManager)
                }
            }
            REQUEST_CODE_PROFILE_ICON_CROP -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    this.userService.updateProfileIcon(result.uri)
                    LoadingDialog.show(childFragmentManager)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onDestroy() {
        this.userService.onDestroy()
        super.onDestroy()
    }

    private fun dismissLoadingDialog() {
        (FragmentUtil.getFragmentFromChildFragmentManagerByTag(
            this@MoreFragment,
            LoadingDialog.FRA_TAG
        ) as? DialogFragment)?.dismissAllowingStateLoss()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MoreFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
