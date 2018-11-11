// Generated code from Butter Knife. Do not modify!
package com.dt5000.ischool.activity.media.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MMSelectorActivity$$ViewBinder<T extends com.dt5000.ischool.activity.media.activity.MMSelectorActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131559032, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131559032, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131559062, "field 'mTitle'");
    target.mTitle = finder.castView(view, 2131559062, "field 'mTitle'");
  }

  @Override public void unbind(T target) {
    target.mToolbar = null;
    target.mTitle = null;
  }
}
