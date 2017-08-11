package cn.breaksky.rounds.publics.util.imageselector;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.widget.Toast;
import cn.breaksky.rounds.publics.R;
import cn.breaksky.rounds.publics.util.imageselector.utils.Utils;

/**
 * ImageSelector
 * Created by Yancy on 2015/12/6.
 */
public class ImageSelector {


    public static final int IMAGE_REQUEST_CODE = 1002;
    public static final int IMAGE_CROP_CODE = 1003;

    private static ImageConfig mImageConfig;

    public static ImageConfig getImageConfig() {
        return mImageConfig;
    }

    public static void open(Activity activity, ImageConfig config) {
        if (config == null) {
            return;
        }
        mImageConfig = config;

        if (config.getImageLoader() == null) {
            Toast.makeText(activity, R.string.open_camera_fail, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.existSDCard()) {
            Toast.makeText(activity, R.string.empty_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        activity.startActivityForResult(intent, mImageConfig.getRequestCode());
    }

    public static void open(Fragment fragment, ImageConfig config) {
        if (config == null) {
            return;
        }
        mImageConfig = config;

        if (config.getImageLoader() == null) {
            Toast.makeText(fragment.getActivity(), R.string.open_camera_fail, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.existSDCard()) {
            Toast.makeText(fragment.getActivity(), R.string.empty_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent(fragment.getActivity(), ImageSelectorActivity.class);
        fragment.startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

}
