package com.example.learningsupport_argame.Course;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class PopupMenuActionItem {

	public Drawable mDrawable;
	public CharSequence mTitle;
	
	public PopupMenuActionItem(Drawable drawable, CharSequence title){
		this.mDrawable = drawable;
		this.mTitle = title;
	}
	
	public PopupMenuActionItem(Context context, int titleId, int drawableId){
		this.mTitle = context.getResources().getText(titleId);
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}
	
	public PopupMenuActionItem(Context context, CharSequence title, int drawableId) {
		this.mTitle = title;
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}
}
