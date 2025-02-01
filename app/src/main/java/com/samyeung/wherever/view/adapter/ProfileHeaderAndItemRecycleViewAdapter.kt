package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.samyeung.wherever.R
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.util.helper.LoadingUtil
import com.samyeung.wherever.view.custom.ItemView2
import com.samyeung.wherever.model.UserProfile
import kotlinx.android.synthetic.main.layout_profile_card.view.*

abstract class ProfileHeaderAndItemRecycleViewAdapter(private val context: Context, recyclerView: RecyclerView) :
    HeaderAndItemViewRecycleViewAdapter(
        context, recyclerView,
        R.layout.layout_profile_card
    ) {
    private lateinit var headerItem: ProfileHeaderItem

    init {
        recyclerView.adapter = this
    }

    override fun createHeaderItem(): Item {
        this.headerItem = ProfileHeaderItem()
        return this.headerItem
    }

    override fun onCreateHeaderViewHolder(view: View): HeaderViewHolder = ProfileHeaderViewHolder(view)

    class ProfileHeaderItem : Item {
        var profile: UserProfile? = null
    }

    fun setProfile(value: UserProfile) {
        this@ProfileHeaderAndItemRecycleViewAdapter.headerItem.profile = value
        this.refreshHeader()
    }

    abstract fun onHeaderProfileIconClick(view: View, profile: UserProfile)
    abstract fun onHeaderIconClick(view: View, profile: UserProfile)
    abstract fun onDisplayNameClick(view: View, profile: UserProfile)
    abstract fun onAccountIDClick(view: View, profile: UserProfile)
    abstract fun onAboutMeClick(view: View, profile: UserProfile)
    inner class ProfileHeaderViewHolder(private val view: View) : HeaderViewHolder(view) {
        init {
            LoadingUtil.show(itemView)
        }
        override fun bind(item: Item) {
            val profile = (item as? ProfileHeaderItem)?.profile
            profile?.let {
                LoadingUtil.hide(itemView)
                itemView.findViewById<ItemView2>(R.id.iv_display_name).value = it.displayName
                itemView.findViewById<ItemView2>(R.id.iv_display_name).setOnClickListener {
                    onDisplayNameClick(itemView, profile)
                }
                itemView.findViewById<ItemView2>(R.id.iv_account_id).value =  "@" + it.accountID.toString()
                itemView.findViewById<ItemView2>(R.id.iv_account_id).setOnClickListener {
                    onAccountIDClick(itemView, profile)
                }
                itemView.findViewById<ItemView2>(R.id.iv_aboutme).value =  if(it.aboutMe !=null) it.aboutMe else ""
                itemView.findViewById<ItemView2>(R.id.iv_aboutme).setOnClickListener {
                    onAboutMeClick(itemView, profile)
                }
                it.profileIcon.let { profileIcon ->
                    ImageUtil.loadImage(
                        context,
                        itemView.img_profile_image,
                        profileIcon,
                        ImageUtil.createCenterCropRequestOption().fallback(R.drawable.bg_profile_icon_default)
                    )

                }
                it.icon.let { icon ->
                    ImageUtil.loadImage(
                        context,
                        itemView.img_icon_image,
                        icon,
                        ImageUtil.createCircleCropRequestOption().fallback(R.drawable.ic_person_icon_default)
                    )


                }
                itemView.img_profile_image.setOnClickListener { icon_view ->
                    onHeaderProfileIconClick(itemView, profile)

                }
                itemView.img_icon_image.setOnClickListener { icon_view ->
                    onHeaderIconClick(itemView, profile)

                }
            }

        }

    }
}