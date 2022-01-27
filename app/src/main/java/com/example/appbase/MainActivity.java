package com.example.appbase;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String URLSistem = "https://gpetest.simbiosis-dg-apps.com";
    WebView webview;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar=findViewById(R.id.progresbar);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Cargando por favor espere...");




        webview = findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webview.getSettings().setLoadsImagesAutomatically(true);

        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //webview.setWebChromeClient(new WebChromeClient());
       /* webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView web,int newProgress){
                super.onProgressChanged(web, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                progressDialog.show();
                if (newProgress==100){
                    progressBar.setVisibility(View.GONE);
                    setTitle(getTitle());
                    progressDialog.dismiss();
                }

            }
        });*/
        webview.requestFocus();
       //webview.loadUrl("file:///android_asset/risktest.html");
        //webview.loadUrl(jcrs_sub.get(position).addr);
        webview.loadUrl(URLSistem);
        webview.setWebViewClient(new MyWebViewClient());
        webview.setDownloadListener(new MyWebViewDownLoadListener());
        // Establecer el cliente de vista web



    }
    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        //DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.trim()));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.replace("blob:","")));
        // Permitir el escaneo de medios y agregarse a bibliotecas de medios, como álbumes y música, según el tipo de archivos descargados
       // request.allowScanningByMediaScanner();
        // Establece el tipo de visualización de la notificación, muestra la notificación cuando la descarga está en curso y después de la finalización
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        // Establecer el título de la barra de notificaciones, si no se establece, el nombre del archivo se utilizará por defecto
        request.setTitle("Descargando gpexprt");
        // Establecer la descripción de la barra de notificaciones
        request.setDescription("This is description");
        // Permitir la descarga con tráfico de facturación
      //  request.setAllowedOverMetered(true);
        // Permitir que el registro sea visible en la interfaz de administración de descargas
        request.setVisibleInDownloadsUi(true);
        // Permitir la descarga en itinerancia
        request.setAllowedOverRoaming(true);
        // Permitir descargar el tipo de red
       // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // Establece la ruta y el nombre del archivo descargado
        String fileName  = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Log.e("fileName:{}", fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // Otra opción es personalizar la ruta de descarga
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // Agregar una tarea de descarga
        long downloadId = downloadManager.enqueue(request);
       /* MDToast toast = MDToast.makeText(MainActivity.this,"Descarga COMPLETADA..." + String.valueOf(downloadId) ,
                Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
        toast.show();*/
        Log.e("downloadId:{}", String.valueOf(downloadId));
    }
    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
        // clase interna
    public class MyWebViewClient extends WebViewClient {
        // Si hay un enlace en la página, si desea hacer clic en el enlace para continuar respondiendo en el navegador actual,
        // En lugar de responder al enlace en el navegador del sistema Android recién abierto, debe anular el objeto WebViewClient de la vista web.
        public boolean shouldOverviewUrlLoading(WebView view, String url) {
            Log.i("", "shouldOverviewUrlLoading");
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("", "onPageStarted");
            //showProgress();
        }

        public void onPageFinished(WebView view, String url) {
            Log.i("", "onPageFinished");
           // closeProgress();
        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.i("", "onReceivedError");
           // closeProgress();
        }
    }

    // Si no hace nada, navegue por la web, haga clic en el botón "Atrás" del sistema, todo el navegador llamará a terminar () para finalizar,
    // Si desea volver a navegar por la página web en lugar de iniciar el navegador, debe procesar y consumir el evento Atrás en la Actividad actual.
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if((keyCode==KeyEvent.KEYCODE_BACK)&&webview.canGoBack()){
        // webview.goBack();
        // return true;
        // }
        return false;
    }


    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType,
                                    long contentLength) {

            Log.e("mimetye", mimeType);
            Toast a = Toast.makeText(getApplicationContext(), "mimetype:" + mimeType, Toast.LENGTH_LONG);
            a.show();
          /*  MDToast toast = MDToast.makeText(MainActivity.this,"Iniciando Descarga...",
                    Toast.LENGTH_LONG, MDToast.TYPE_INFO);
            toast.show();*/


           final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(
                        url.replace("blob:","")));

            request.setMimeType(mimeType);
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading File...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                            url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();


            new Thread("Browser download") {
                    public void run() {
                        dm.enqueue(request);
                    }
                }.start();
                //dm.enqueue(request);

                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));




            new Thread("Browser download"){
                public void run() {
                    dm.enqueue(request);
                }
            }.start();

            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            //especificar el directorio destino de la descarga

          /*  Toast a = Toast.makeText(getApplicationContext(), "descarga en curso", Toast.LENGTH_LONG);
            a.show();
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast t = Toast.makeText(getApplicationContext(), "Se requiere tarjeta SD", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }
            MyWebViewDownLoadListener.DownloaderTask task = new MyWebViewDownLoadListener.DownloaderTask();
            task.execute(url);*/
        }
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "Downloading Complete", Toast.LENGTH_SHORT).show();
            }
        };

//        private class DownloaderTask extends AsyncTask<String, Void, String> {
//
//            public DownloaderTask() {
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                // TODO Auto-generated method stub
//                String url = params[0];
//
////			Log.i("tag", "url="+url);
//                String fileName = url.substring(url.lastIndexOf("/") + 1);
//                fileName = URLDecoder.decode(fileName);
//                Log.i("tag", "fileName=" + fileName);
//
//                File directory = Environment.getExternalStorageDirectory();
//                File file = new File(directory, fileName);
//                if (file.exists()) {
//                    Log.i("tag", "The file has already exists.");
//                    return fileName;
//                }
//                try {
//                    HttpClient client = new DefaultHttpClient();
//// client.getParams (). setIntParameter ("http.socket.timeout", 3000); // Establecer tiempo de espera
//                    HttpGet get = new HttpGet(url);
//                    HttpResponse response = client.execute(get);
//                    if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
//                        HttpEntity entity = response.getEntity();
//                        InputStream input = entity.getContent();
//
//                        writeToSDCard(fileName, input);
//
//                        input.close();
////					entity.consumeContent();
//                        return fileName;
//                    } else {
//                        return null;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//
//            }
//
//            @Override
//            protected void onCancelled() {
//                // TODO Auto-generated method stub
//                super.onCancelled();
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                // TODO Auto-generated method stub
//                super.onPostExecute(result);
//               // closeProgressDialog();
//                if (result == null) {
//                    Toast t = Toast.makeText(getApplicationContext(), "¡Error de conexión! ¡Inténtalo de nuevo más tarde!", Toast.LENGTH_LONG);
//                    t.setGravity(Gravity.CENTER, 0, 0);
//                    t.show();
//                    return;
//                }
//
//                Toast t = Toast.makeText(getApplicationContext(), "Guardado en la tarjeta SD", Toast.LENGTH_LONG);
//                t.setGravity(Gravity.CENTER, 0, 0);
//                t.show();
//                File directory = Environment.getExternalStorageDirectory();
//                File file = new File(directory, result);
//                Log.i("tag", "Path=" + file.getAbsolutePath());
//
//
//                Intent intent = getFileIntent(file);
//                startActivity(intent);
//
//            }
//
//            @Override
//            protected void onPreExecute() {
//                // TODO Auto-generated method stub
//                super.onPreExecute();
//                //showProgressDialog();
//            }
//
//            @Override
//            protected void onProgressUpdate(Void... values) {
//                // TODO Auto-generated method stub
//                super.onProgressUpdate(values);
//            }
//
//
//            private ProgressDialog mDialog;
//
//            private void showProgressDialog() {
//                if (mDialog == null) {
//                    mDialog = new ProgressDialog(getApplicationContext());
//                    mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Establece el estilo en una barra de progreso circular
//                    mDialog.setMessage("Cargando, por favor espere ...");
//                    mDialog.setIndeterminate(false); // Establecer si la barra de progreso es ambigua
//                    mDialog.setCancelable(true); // Establezca si la barra de progreso se puede cancelar presionando la tecla Atrás
//                    mDialog.setCanceledOnTouchOutside(false);
//                    mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            // TODO Auto-generated method stub
//                            mDialog = null;
//                        }
//                    });
//                    mDialog.show();
//
//                }
//            }
//
//            private void closeProgressDialog() {
//                if (mDialog != null) {
//                    mDialog.dismiss();
//                    mDialog = null;
//                }
//            }
//
//            public Intent getFileIntent(File file) {
////		 Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
//                Uri uri = Uri.fromFile(file);
//                String type = getMIMEType(file);
//                Log.i("tag", "type=" + type);
//                Intent intent = new Intent("android.intent.action.VIEW");
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setDataAndType(uri, type);
//                return intent;
//            }
//
//            public void writeToSDCard(String fileName, InputStream input) {
//
//                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    File directory = Environment.getExternalStorageDirectory();
//                    File file = new File(directory, fileName);
////			if(file.exists()){
////				Log.i("tag", "The file has already exists.");
////				return;
////			}
//                    try {
//                        FileOutputStream fos = new FileOutputStream(file);
//                        byte[] b = new byte[2048];
//                        int j = 0;
//                        while ((j = input.read(b)) != -1) {
//                            fos.write(b, 0, j);
//                        }
//                        fos.flush();
//                        fos.close();
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.i("tag", "NO SDCard.");
//                }
//            }
//
//            private String getMIMEType(File f) {
//                String type = "";
//                String fName = f.getName();
//                // Obtener extensión
//                String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
//
//                //Determinar MimeType según el tipo de extensión *
//                if (end.equals("pdf")) {
//                    type = "application/pdf";//
//                } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
//                        end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
//                    type = "audio/*";
//                } else if (end.equals("3gp") || end.equals("mp4")) {
//                    type = "video/*";
//                } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
//                        end.equals("jpeg") || end.equals("bmp")) {
//                    type = "image/*";
//                } else if (end.equals("apk")) {
//                    /* android.permission.INSTALL_PACKAGES */
//                    type = "application/vnd.android.package-archive";
//                }
////      else if(end.equals("pptx")||end.equals("ppt")){
////    	  type = "application/vnd.ms-powerpoint";
////      }else if(end.equals("docx")||end.equals("doc")){
////    	  type = "application/vnd.ms-word";
////      }else if(end.equals("xlsx")||end.equals("xls")){
////    	  type = "application/vnd.ms-excel";
////      }
//                else {
//// / * Si no se puede abrir directamente, salte de la lista de software para que el usuario elija * /
//                    type = "*/*";
//                }
//                return type;
//            }
//
//
//        }



    }

}

