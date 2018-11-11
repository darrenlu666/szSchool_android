// Generated code from Butter Knife. Do not modify!
package com.dt5000.ischool.activity.media.image;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MMPreviewImageActivity$$ViewBinder<T extends com.dt5000.ischool.activity.media.image.MMPreviewImageActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131559032, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131559032, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131559062, "field 'mTitle'");
    target.mTitle = finder.castView(view, 2131559062, "field 'mTitle'");
    view = finder.findRequiredView(source, 2131559031, "field 'mHackyViewPager'");
    target.mHackyViewPager = finder.castView(view, 2131559031, "field 'mHackyViewPager'");
    view = finder.findRequiredView(source, 2131558651, "field 'mConfirmButton'");
    target.mConfirmButton = finder.castView(view, 2131558651, "field 'mConfirmButton'");
  }

  @Override public void unbind(T target) {
    target.mToolbar = null;
    target.mTitle = null;
    target.mHackyViewPager = null;
    target.mConfirmButton = null;
  }
}
