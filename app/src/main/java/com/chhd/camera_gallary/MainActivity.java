package com.chhd.camera_gallary;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener {

	private Button btn;
	private ImageView iv;

	private final int REQUEST_FROM_CAMERA = 0;
	private final int REQUEST_FROM_GALLARY = 1;
	private final int REQUEST_FROM_CROP = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();

		setListeners();
	}

	private void initViews() {
		btn = (Button) findViewById(R.id.btn);
		iv = (ImageView) findViewById(R.id.iv);
	}

	private void setListeners() {
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("图库", this);
		builder.setNegativeButton("相机", this);
		builder.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				startGallary();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				startCamera();
				break;
		}
	}

	private void startGallary() {
		Intent intent = new Intent("android.intent.action.PICK");
		intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_FROM_GALLARY);
	}

	private void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_FROM_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent = null;
		Bitmap bitmap = null;
		if (data != null) {
			switch (requestCode) {
				case REQUEST_FROM_GALLARY:
					Uri uri = data.getData();
					intent = new Intent("com.android.camera.action.CROP");
					intent.setDataAndType(uri, "image/*");
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", 256);
					intent.putExtra("outputY", 256);
					intent.putExtra("return-data", true);
					startActivityForResult(intent, REQUEST_FROM_CROP);
					break;
				case REQUEST_FROM_CAMERA:
					bitmap = data.getParcelableExtra("data");
					intent = new Intent("com.android.camera.action.CROP");
					intent.setType("image/*");
					intent.putExtra("data", bitmap);
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", 256);
					intent.putExtra("outputY", 256);
					intent.putExtra("return-data", true);
					startActivityForResult(intent, REQUEST_FROM_CROP);
					break;
				case REQUEST_FROM_CROP:
					try {
						bitmap = data.getParcelableExtra("data");

						File file = new File(getFilesDir(), "icon");
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);

						iv.setImageBitmap(bitmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

			}
		}
	}
}
