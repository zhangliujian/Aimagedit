package com.example.imagedit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imagedit.file.IMGAssetFileDecoder;
import com.example.imagedit.file.IMGDecoder;
import com.example.imagedit.file.IMGFileDecoder;
import com.example.imagedit.util.IMGUtils;
import com.example.imagedit.widget.EditImageView;

public class Main2Activity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URI = "IMAGE_URI";

    private static final int MAX_WIDTH = 1024;

    private static final int MAX_HEIGHT = 1024;

    private EditImageView mEditImageView;

    private SeekBar mBrightnessBar;

    private SeekBar mContrastBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        initEditView();
        initSeekBar();
    }

    private void initSeekBar() {
        mBrightnessBar = (SeekBar) findViewById(R.id.activity_main_brightness_seek_bar);
        mBrightnessBar.setOnSeekBarChangeListener(mOnBrightnessSeekBarChangeListener);

        mContrastBar = (SeekBar) findViewById(R.id.activity_main_contrast_seek_bar);
        mContrastBar.setOnSeekBarChangeListener(mOnContrastSeekBarChangeListener);
    }

    private void initEditView() {
        mEditImageView = (EditImageView) findViewById(R.id.activity_main_edit_image);
        Bitmap bitmap = getBitmap();
        mEditImageView.setImage(bitmap);
    }

    public Bitmap getBitmap() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        Uri uri = intent.getParcelableExtra(EXTRA_IMAGE_URI);
        if (uri == null) {
            return null;
        }

        IMGDecoder decoder = null;

        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            switch (uri.getScheme()) {
                case "asset":
                    decoder = new IMGAssetFileDecoder(this, uri);
                    break;
                case "file":
                    decoder = new IMGFileDecoder(uri);
                    break;
            }
        }

        if (decoder == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;

        decoder.decode(options);

        if (options.outWidth > MAX_WIDTH) {
            options.inSampleSize = IMGUtils.inSampleSize(Math.round(1f * options.outWidth / MAX_WIDTH));
        }

        if (options.outHeight > MAX_HEIGHT) {
            options.inSampleSize = Math.max(options.inSampleSize,
                    IMGUtils.inSampleSize(Math.round(1f * options.outHeight / MAX_HEIGHT)));
        }

        options.inJustDecodeBounds = false;

        Bitmap bitmap = decoder.decode(options);
        if (bitmap == null) {
            return null;
        }

        return bitmap;
    }


    public void withdrawClick(View view) {
        mEditImageView.withDraw();
        dismissSeekBar();
    }

    public void penClick(View view) {
        mEditImageView.drawLine();
        dismissSeekBar();
    }

    public void rotateClick(View view) {
        mEditImageView.rotate();
        dismissSeekBar();
    }

    public void reverseXClick(View view) {
        mEditImageView.reverseX();
        dismissSeekBar();
    }

    public void reverseYClick(View view) {
        mEditImageView.reverseY();
        dismissSeekBar();
    }

    public void brightnessClick(View view) {
        showBrightnessBar();
        dismissContrastBar();
    }

    public void contrastClick(View view) {
        showContrastBar();
        dismissBrightnessBar();
    }

    private void dismissSeekBar() {
        dismissContrastBar();
        dismissBrightnessBar();
    }

    private void showBrightnessBar() {
        mBrightnessBar.setVisibility(View.VISIBLE);
    }

    private void dismissBrightnessBar() {
        mBrightnessBar.setVisibility(View.GONE);
    }

    private void showContrastBar() {
        mContrastBar.setVisibility(View.VISIBLE);
    }

    private void dismissContrastBar() {
        mContrastBar.setVisibility(View.GONE);
    }

    private SeekBar.OnSeekBarChangeListener mOnBrightnessSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mEditImageView.brightness(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mEditImageView.brightnessDone(seekBar.getProgress());
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnContrastSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mEditImageView.contrast(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mEditImageView.contrastDone(seekBar.getProgress());
        }
    };

}
