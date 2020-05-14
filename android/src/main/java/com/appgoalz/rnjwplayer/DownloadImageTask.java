package main;

import java.io.InputStream;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

	ImageView bmImage;
	static Hashtable<String, Bitmap> images;

	public DownloadImageTask(ImageView bmImage) {
		this.bmImage = bmImage;
		if(images == null){
			images = new Hashtable<String, Bitmap>();
		}
	}

	protected Bitmap doInBackground(String... urls) {
		Bitmap image = images.get(urls[0]);
		if(image!=null){
			return image;
		}

		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(mIcon11!=null){
			images.put(urldisplay, mIcon11);
		}
		return mIcon11;
	}

	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}

	public static void showImage(ImageView iv, String url){
		if(url!=null && !url.equals(""))
			new DownloadImageTask(iv).execute(url);
	}

}