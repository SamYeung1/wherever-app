package com.samyeung.wherever.util.helper

import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.widget.Button
import com.samyeung.wherever.R
import com.samyeung.wherever.api.RequestServiceAdapter
import com.samyeung.wherever.model.UserProfile

object ButtonUtil {
    fun setUpFriendRelationshipButton(
        context: Context,
        button: Button,
        profile: UserProfile,
        requestServiceAdapter: RequestServiceAdapter
    ) {
        when (profile.friendStatus) {
            UserProfile.STATUS_FRIEND -> {
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary))
                button.setBackgroundResource(R.drawable.bg_button_primary_border)
                button.compoundDrawableTintList = colorStateList
                button.setTextColor(colorStateList)
                button.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp),
                    null,
                    null,
                    null
                )
                button.setText(R.string.friend)
                button.setOnClickListener {
                    button.isEnabled = false
                }
            }
            UserProfile.STATUS_ACCEPT_REQUEST -> {
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                button.setBackgroundResource(R.drawable.bg_button_accent_border)
                button.compoundDrawableTintList = colorStateList
                button.setTextColor(colorStateList)
                button.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp),
                    null,
                    null,
                    null
                )
                button.setText(R.string.accept_request)
                button.setOnClickListener {
                    requestServiceAdapter.acceptFriendRequest("",profile.id)
                    profile.friendStatus = UserProfile.STATUS_FRIEND
                    setUpFriendRelationshipButton(context, button, profile, requestServiceAdapter)
                    button.isEnabled = false
                }
            }
            UserProfile.STATUS_REQUEST_SENT -> {
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                button.setBackgroundResource(R.drawable.bg_button_accent_border)
                button.compoundDrawableTintList = colorStateList
                button.setTextColor(colorStateList)
                button.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp),
                    null,
                    null,
                    null
                )
                button.setText(R.string.request_sent)
                button.setOnClickListener {
                    requestServiceAdapter.cancelFriendRequest(profile.id)
                    profile.friendStatus = null
                    setUpFriendRelationshipButton(context, button, profile, requestServiceAdapter)
                    button.isEnabled = false
                }
            }
            else -> {
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white))
                button.setBackgroundResource(R.drawable.bg_button_accent)
                button.compoundDrawableTintList = colorStateList
                button.setTextColor(colorStateList)
                button.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_person_add_white_24dp),
                    null,
                    null,
                    null
                )
                button.setText(R.string.add_friend)
                button.setOnClickListener {
                    requestServiceAdapter.sendFriendRequest(profile.id)
                    profile.friendStatus = UserProfile.STATUS_REQUEST_SENT
                    setUpFriendRelationshipButton(context, button, profile, requestServiceAdapter)
                    button.isEnabled = false
                }
            }
        }
    }
}