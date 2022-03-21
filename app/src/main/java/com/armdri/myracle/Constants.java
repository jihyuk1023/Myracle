package com.armdri.myracle;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEFT_POSITION, RIGHT_POSITION})
    public @interface types {}

    public static final int LEFT_POSITION = 0;
    public static final int RIGHT_POSITION = 1;
}
