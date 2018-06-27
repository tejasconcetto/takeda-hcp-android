package com.takeda.android.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.skk.lib.BaseClasses.BaseActivity;
import com.skk.lib.utils.AppDelegate;
import com.takeda.android.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Created by amitkumar on 22/09/16.
 */

public class DownloadFileAsync extends AsyncTask<String, String, String> {

  private ProgressDialog progressDialog;
  private BaseActivity mContext;
  String fileURL, fileName, folderPath;
  public downloadFileResponse responseListener;
  File fileFinal;
  String folder_main = "", action;
  JSONObject jsonParams = new JSONObject();
  boolean showDialog;
  int i = 0;

  public DownloadFileAsync(BaseActivity mContext, String fileURL,
      downloadFileResponse responseListener,
      String fileName, JSONObject jsonParams, boolean showDialog) {
    this.showDialog = showDialog;
    this.mContext = mContext;
    this.folder_main = mContext.getString(R.string.app_name);
    this.fileURL = fileURL;
    this.responseListener = responseListener;
    this.fileName = fileName;
    this.jsonParams = jsonParams;
  }

  public interface downloadFileResponse {

    void onSuccess(File filePath);

    void onError(String error);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    mContext.showToast("Downloading...");

    if (showDialog) {

    }

    File f = new File(Environment.getExternalStorageDirectory(), folder_main);
    mContext.showLogs("FileFolderDir", "Exists : " + String.valueOf(f.exists()));
    if (!f.exists()) {
      mContext.showLogs("FileFolderDir", "Exists : " + String.valueOf(f.mkdir()));
    }
    folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folder_main;
    Log.d("DirFolder", "Folder Path: " + folderPath);
  }

  @Override
  protected String doInBackground(String... aurl) {
    int count;

    try {

      fileURL = fileURL.replaceAll("\\s+", "%20");

      URL url = new URL(fileURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(5000);
//            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      conn.setDoOutput(true);
      conn.setRequestMethod("GET");

      OutputStream os = conn.getOutputStream();
//            os.write(jsonParams.toString().getBytes("UTF-8"));
      os.close();

      int lenghtOfFile = conn.getContentLength();
      InputStream input = new BufferedInputStream(conn.getInputStream());

      if (input == null) {
        return null;
      } else if (input != null) {
        fileFinal = new File(folderPath, fileName);
        mContext.showLogs("FileExistence", "Status : " + fileFinal.exists());
        if (!fileFinal.exists()) {
          fileFinal.createNewFile();
//                fileFinal = new File(folderPath, "("+TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())+")"+fileName);
        }
        OutputStream fOut = new FileOutputStream(fileFinal);

        byte data[] = new byte[1024];
        long total = 0;

        while ((count = input.read(data)) != -1) {
          total += count;
          publishProgress("" + (int) ((total * 100) / lenghtOfFile));
          fOut.write(data, 0, count);
        }
        fOut.flush();
        fOut.close();

      }

      input.close();
      return "done";
    } catch (Exception e) {
      Log.d("Exception", e.toString());
      e.printStackTrace();
    }
    return null;
  }

  protected void onProgressUpdate(String... progress) {

    if (showDialog) {
      progressDialog.setProgress(Integer.parseInt(progress[0]));

      if (Integer.parseInt(progress[0]) >= 100) {
        responseListener.onSuccess(fileFinal);
        Toast.makeText(mContext, "File downloaded", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  protected void onPostExecute(final String result) {
    Log.d("AsyncTask", "onPostExecute called");
    Log.d("Result", "Result status : " + result);
    i = 1;

    if (showDialog) {
      AppDelegate.hideLoadingDialog(mContext);
    }

    if (result != null && !result.equalsIgnoreCase("") && !result.equalsIgnoreCase("[]")) {
      if (responseListener != null) {
        responseListener.onSuccess(fileFinal);
      }
    } else if (result == null) {
      mContext.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          mContext.showToast("Error occurred. Please try again");
          if (responseListener != null) {
            responseListener.onError(result);
          }
        }
      });
    }
  }

  @Override
  protected void onCancelled(String s) {
    super.onCancelled(s);
  }
}
