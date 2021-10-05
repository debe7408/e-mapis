// Generated by view binder compiler. Do not edit!
package com.vu.emapis.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.vu.emapis.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityTripSettingsBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView TEST;

  @NonNull
  public final ConstraintLayout progressBar;

  @NonNull
  public final SeekBar seekBar;

  @NonNull
  public final TextView seekBarText;

  @NonNull
  public final Spinner vehicleMenu;

  @NonNull
  public final TextView vehicleSelectionText;

  private ActivityTripSettingsBinding(@NonNull ConstraintLayout rootView, @NonNull TextView TEST,
      @NonNull ConstraintLayout progressBar, @NonNull SeekBar seekBar,
      @NonNull TextView seekBarText, @NonNull Spinner vehicleMenu,
      @NonNull TextView vehicleSelectionText) {
    this.rootView = rootView;
    this.TEST = TEST;
    this.progressBar = progressBar;
    this.seekBar = seekBar;
    this.seekBarText = seekBarText;
    this.vehicleMenu = vehicleMenu;
    this.vehicleSelectionText = vehicleSelectionText;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityTripSettingsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityTripSettingsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_trip_settings, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityTripSettingsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.TEST;
      TextView TEST = ViewBindings.findChildViewById(rootView, id);
      if (TEST == null) {
        break missingId;
      }

      ConstraintLayout progressBar = (ConstraintLayout) rootView;

      id = R.id.seekBar;
      SeekBar seekBar = ViewBindings.findChildViewById(rootView, id);
      if (seekBar == null) {
        break missingId;
      }

      id = R.id.seekBarText;
      TextView seekBarText = ViewBindings.findChildViewById(rootView, id);
      if (seekBarText == null) {
        break missingId;
      }

      id = R.id.vehicleMenu;
      Spinner vehicleMenu = ViewBindings.findChildViewById(rootView, id);
      if (vehicleMenu == null) {
        break missingId;
      }

      id = R.id.vehicleSelectionText;
      TextView vehicleSelectionText = ViewBindings.findChildViewById(rootView, id);
      if (vehicleSelectionText == null) {
        break missingId;
      }

      return new ActivityTripSettingsBinding((ConstraintLayout) rootView, TEST, progressBar,
          seekBar, seekBarText, vehicleMenu, vehicleSelectionText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
