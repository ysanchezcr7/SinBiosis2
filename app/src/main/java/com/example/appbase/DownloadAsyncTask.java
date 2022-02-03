package com.example.appbase;

import android.os.AsyncTask;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadAsyncTask extends AsyncTask<String, Void, String>
{
    @Override
    protected String doInBackground(String... arg0)
    {
        String result = null;
        String url = arg0[0];
        String filname = arg0[1];

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            URL urlObject = null;
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            try
            {
                urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                inputStream = urlConnection.getInputStream();

                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
                File directory = new File(fileName);
               // File file = new File(directory, url.substring(url.lastIndexOf("/")));
                File file = new File(directory, filname);

                directory.mkdirs();


                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;

                while (inputStream.available() > 0	&& (len = inputStream.read(buffer)) != -1)
                {
                    byteArrayOutputStream.write(buffer, 0, len);
                }

                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.flush();
                result = "guardado en : " + file.getAbsolutePath();
            }
            catch (Exception ex)
            {
                result = ex.getClass().getSimpleName() + " " + ex.getMessage();
            }
            finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException ex)
                    {
                        result = ex.getClass().getSimpleName() + " " + ex.getMessage();
                    }
                }
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
            }
        }
        else
        {
            result = "Almacenamiento no disponible";
        }

        return result;
    }


//    @Override
//    protected void onPostExecute(String result)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(GuajirosWeb.class);
//        builder.setMessage(result).setPositiveButton("Aceptar", null).setTitle("Descarga");
//        builder.show();
//    }


    @Override
    protected void onPostExecute(String file_url) {
        System.out.println("Descargado con exito");
    }






}
