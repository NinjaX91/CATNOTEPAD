package ninja.cfg.catnotepad.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import ninja.cfg.catnotepad.CatApp;

/**
 * Created by MohMah on 8/18/2016. modified 6/15/2020
 */
public class ViewUtils{
	public static View getEmptyView(Context context, int width, int height, @ColorRes int backgroundColor){
		View view = new View(context);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
		view.setLayoutParams(params);
		view.setBackgroundResource(backgroundColor);
		return view;
	}

	public static void tintMenu(Menu menu, @IdRes int menuId, @ColorRes int colorRes){
		Drawable drawable = menu.findItem(menuId).getIcon();
		drawable = DrawableCompat.wrap(drawable);
		DrawableCompat.setTint(drawable, ContextCompat.getColor(CatApp.CONTEXT, colorRes));
		menu.findItem(menuId).setIcon(drawable);
	}

	public static Drawable tintDrawable(@DrawableRes int drawableRes, @ColorRes int colorRes){
		final Drawable drawable = CatApp.CONTEXT.getResources().getDrawable(drawableRes);
		drawable.setColorFilter(CatApp.CONTEXT.getResources().getColor(colorRes), PorterDuff.Mode.SRC_ATOP);
		return drawable;
	}
}
