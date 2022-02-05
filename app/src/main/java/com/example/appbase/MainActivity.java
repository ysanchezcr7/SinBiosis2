package com.example.appbase;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private String URLSistem = "https://gpetest.simbiosis-dg-apps.com";
    WebView webview;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 23) {
            initPermission();
        }
        progressBar = findViewById(R.id.progresbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando Archivo por favor espere...");


        webview = findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setDefaultTextEncodingName("utf-8");


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
        // webview.loadDataWithBaseURL(null, URLSistem, "text/html", "UTF-8", null);
        // webview.loadData(URLSistem, "text/html", "UTF-8");
        webview.setWebViewClient(new MyWebViewClient());


        // webview.setDownloadListener(new MyWebViewDownLoadListener());
        // Establecer el cliente de vista web


    }

    @Override
    public void onBackPressed() {
        if (webview.copyBackForwardList().getCurrentIndex() > 0) {
            webview.goBack();
        } else {
            // Your exit alert code, or alternatively line below to finish
            super.onBackPressed(); // finishes activity
        }
    }

    // clase interna
    class MyWebViewClient extends WebViewClient {
        // Si hay un enlace en la página, si desea hacer clic en el enlace para continuar respondiendo en el navegador actual,
        // En lugar de responder al enlace en el navegador del sistema Android recién abierto, debe anular el objeto WebViewClient de la vista web.

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String FileName = "";
            if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
                if (url.startsWith("https://gpetest.simbiosis-dg-apps.com/app/export/exporterallptt.html")) {
                    FileName = "todos-mis-pacientes.pdf";
                    DownloaderTask task = new DownloaderTask();
                    task.execute("https://www.granma.cu/file/pdf/2022/02/03/G_2022020301.pdf", FileName);
                    //downloadBySystem(url,"",FileName);
                    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    //view.getContext().startActivity(intent);
                    return true;

                } else if (url.startsWith("https://gpetest.simbiosis-dg-apps.com/app/export/exporterslctdptt.html")) {

                    FileName = "mis-pacientes-seleccionados.gpexprt";
                    // downloadBySystem(url,"",FileName);

                    //new DownloadTask().execute(String.valueOf(Uri.parse(url)));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                } else if (url.startsWith("https://gpetest.simbiosis-dg-apps.com/app/export/exporteralldata.html")) {
                    FileName = "todos-mis-datos.gpexprt";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    //downloadBySystem(url,"",FileName);
                    return true;

                } else {
                    return false;
                }

            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            } catch (Exception e) {

                Toast t = Toast.makeText(getApplicationContext(), "shouldOverrideUrlLoading Exception:" + e,
                        Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                Log.i("TAG", "shouldOverrideUrlLoading Exception:" + e);
                return true;
            }
        }


        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("", "onPageStarted");
            super.onPageStarted(view, url, favicon);

            //showProgress();
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

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
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return false;
    }


    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType,
                                    long contentLength) {


            Log.e("mimetye", mimeType);
            // Toast a = Toast.makeText(getApplicationContext(), "mimetype:" + mimeType, Toast.LENGTH_LONG);
            //a.show();
          /*  MDToast toast = MDToast.makeText(MainActivity.this,"Iniciando Descarga...",
                    Toast.LENGTH_LONG, MDToast.TYPE_INFO);
            toast.show();*/


            Uri urlDowload = Uri.parse(url.replace("blob:", ""));


            //String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
            //String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            //        .getAbsolutePath() + File.separator + fileName;
            // new DownloadTask().execute(String.valueOf(urlDowload), destPath);

            DownloaderTask task = new DownloaderTask();
            task.execute(String.valueOf(urlDowload));
            // new DownloadAsyncTask().execute(String.valueOf(urlDowload));
        }

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "Downloading Complete", Toast.LENGTH_SHORT).show();
            }


        };


    }

    private class DownloaderTask extends AsyncTask<String, Void, String> {

        public DownloaderTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url = params[0];
            String fileName = params[1];

//			Log.i("tag", "url="+url);
            // String fileName = url.substring(url.lastIndexOf("/") + 1);
            //fileName = URLDecoder.decode(fileName);
            //String fileName = "mis-pacientes.gpexprt";
//                String fileName = getFileNameFromURL(url);
//                Log.i("tag", "fileName=" + fileName);
            new File(Environment.getExternalStorageDirectory() + "/Download/pdf").mkdirs();

            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/pdf");

            //File directory = Environment.getExternalStorageDirectory() ;
            File file = new File(directory, fileName);
            if (file.exists()) {
                Log.i("tag", "The file has already exists.");
                return fileName;
            }
            try {
                HttpClient client = new DefaultHttpClient();
// client.getParams (). setIntParameter ("http.socket.timeout", 3000); // Establecer tiempo de espera
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);
                if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                    HttpEntity entity = response.getEntity();
                    InputStream input = entity.getContent();

                    writeToSDCard(fileName, input);

                    input.close();
//					entity.consumeContent();
                    return fileName;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // closeProgressDialog();
            progressDialog.dismiss();
            if (result == null) {
                Toast t = Toast.makeText(getApplicationContext(), "¡Error de conexión! ¡Inténtalo de nuevo más tarde!", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }

            Toast t = Toast.makeText(getApplicationContext(), "Guardado archivo", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            // File directory = Environment.getExternalStorageDirectory();
            // File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/Read.pdf");
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/gpe");

            File file = new File(directory, result);
            Log.i("tag", "Path=" + file.getAbsolutePath());

            try {
                Intent intent = getFileIntent(file);
                startActivity(intent);
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_LONG);
                toast.show();
            }


        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog.show();
            //showProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }


        private ProgressDialog mDialog;

       /* private void showProgressDialog() {
            if (mDialog == null) {
                mDialog = new ProgressDialog(getApplicationContext());
                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Establece el estilo en una barra de progreso circular
                mDialog.setMessage("Cargando, por favor espere ...");
                mDialog.setIndeterminate(false); // Establecer si la barra de progreso es ambigua
                mDialog.setCancelable(true); // Establezca si la barra de progreso se puede cancelar presionando la tecla Atrás
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        mDialog = null;
                    }
                });
                mDialog.show();

            }
        }

        private void closeProgressDialog() {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        }*/

        public Intent getFileIntent(File file) {
            Uri uri = null;
//		 Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = Uri.parse(String.valueOf(file));
            } else {
                uri = Uri.fromFile(file);
                // imageUri = Uri.fromFile(new File(filepath));
            }

            String type = getMIMEType(file);
            //Log.i("tag", "type=" + type);
            Intent intent = new Intent("android.intent.action.VIEW");
            //intent.addCategory("android.intent.category.DEFAULT");
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setDataAndType(uri, type);
            return intent;


        }

        public void writeToSDCard(String fileName, InputStream input) {

            //if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //File directory = Environment.getExternalStorageDirectory();
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/gpe");

            File file = new File(directory, fileName);
//			if(file.exists()){
//				Log.i("tag", "The file has already exists.");
//				return;
//			}
            try {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int j = 0;
                while ((j = input.read(b)) != -1) {
                    fos.write(b, 0, j);
                }
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //} else {
            //  Log.i("tag", "NO SDCard.");
            // }
        }

        private String getMIMEType(File f) {
            String type = "";
            String fName = f.getName();
            // Obtener extensión
            String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

            //Determinar MimeType según el tipo de extensión *
            if (end.equals("pdf")) {
                type = "application/pdf";//
            } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                    end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
                type = "audio/*";
            } else if (end.equals("3gp") || end.equals("mp4")) {
                type = "video/*";
            } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                    end.equals("jpeg") || end.equals("bmp")) {
                type = "image/*";
            } else if (end.equals("apk")) {
                /* android.permission.INSTALL_PACKAGES */
                type = "application/vnd.android.package-archive";
            } else if (end.equals("text/plain")) {
                /* android.permission.INSTALL_PACKAGES */
                type = "gpexprt/*";
            }
//      else if(end.equals("pptx")||end.equals("ppt")){
//    	  type = "application/vnd.ms-powerpoint";
//      }else if(end.equals("docx")||end.equals("doc")){
//    	  type = "application/vnd.ms-word";
//      }else if(end.equals("xlsx")||end.equals("xls")){
//    	  type = "application/vnd.ms-excel";
//      }
            else {
// / * Si no se puede abrir directamente, salte de la lista de software para que el usuario elija * /
                type = "*/*";
            }
            return type;
        }

        public String getFileNameFromURL(String url) {
            if (url == null) {
                return "";
            }
            try {
                URL resource = new URL(url);
                String host = resource.getHost();
                if (host.length() > 0 && url.endsWith(host)) {
                    // handle ...example.com
                    return "";
                }
            } catch (MalformedURLException e) {
                return "";
            }

            int startIndex = url.lastIndexOf('/') + 1;
            int length = url.length();

            // find end index for ?
            int lastQMPos = url.lastIndexOf('?');
            if (lastQMPos == -1) {
                lastQMPos = length;
            }

            // find end index for #
            int lastHashPos = url.lastIndexOf('#');
            if (lastHashPos == -1) {
                lastHashPos = length;
            }

            // calculate the end index
            int endIndex = Math.min(lastQMPos, lastHashPos);
            return url.substring(startIndex, endIndex);
        }


    }



    private class DownloadTask extends AsyncTask<String, Void, Void> {
        // Pasar dos parámetros: URL y ruta de destino
        private String url;
        private String Filname;


        @Override
        protected void onPreExecute() {
            Toast a = Toast.makeText(getApplicationContext(), "Iniciar descarga", Toast.LENGTH_LONG);
            a.show();
            //log.info ("Iniciar descarga");
        }

        @Override
        protected Void doInBackground(String... params) {
            //log.debug("doInBackground. url:{}, dest:{}", params[0], params[1]);
            url = params[0];
            Filname = params[1];
            try {
                //String s = url.replaceAll(" " , "%20");
                //Uri link = Uri.parse(s);
                // DownloadManager.Request request = new DownloadManager.Request(link);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                // DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.replace("blob:","")));
                //  request.setMimeType(mime);
                //request.setMimeType("application/json");
                //String mime = "application/json";
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                //request.addRequestHeader("mimetype", mimeType);

                //request.setTitle("Descargando archivo");

                request.setAllowedOverMetered(true);
                // Permitir que el registro sea visible en la interfaz de administración de descargas
                request.setVisibleInDownloadsUi(true);
                // Permitir la descarga en itinerancia
                request.setAllowedOverRoaming(true);
                //  String filename = URLUtil.guessFileName(url, params[1],mime);
                // String filename = "mis-pacientes-seleccionados.gpexprt";
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                // new File(Environment.getExternalStorageDirectory() + "/Download/gpexprt").mkdirs();
                //File file = new File(Environment.DIRECTORY_DOWNLOADS, filename);
                //request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS + "/gpexprt", Filname);
                // request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/gpexprt", Filname);
                request.setDestinationInExternalPublicDir(String.valueOf(directory), Filname);

                // if (file.exists()) file.delete();
                // request.setDestinationUri(Uri.fromFile(file));

                //File file = new File( "/Download/gpexprt/");
               /* request.setDestinationInExternalPublicDir(String.valueOf(file),
                        filename +".gpexprt");*/

                //String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();//Downloads folder path

                // request.setDestinationInExternalPublicDir(root,filename);
                //request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                //dm.enqueue(request);
                dm.enqueue(request);
                // Log.d("downloadId:{}", "" +downloadId);

                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();


            } catch (Exception e) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                110);
                    } else {
                        Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                110);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }


    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        /*public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                // get the DownloadManager instance
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                DownloadManager.Query q = new DownloadManager.Query();
                Cursor c = manager.query(q);

                if(c.moveToFirst()) {
                    do {
                        String name = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        Toast.makeText(getApplicationContext(), "Downloading LISTENER: " + "file name: " + name, Toast.LENGTH_SHORT).show();

                        //Log.i("DOWNLOAD LISTENER", "file name: " + name);
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "Downloading LISTENER: " + "empty cursor :(", Toast.LENGTH_SHORT).show();

                    Log.i("DOWNLOAD LISTENER", "empty cursor :(");
                }

                c.close();
            }
        }*/
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Downloading Complete",
                    Toast.LENGTH_SHORT).show();
        }
    };

    String[] permissions = new String[]{Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,

    };
    // 2. Cree una mPermissionList y determine qué permisos no se otorgan uno por uno, y los permisos no autorizados se almacenan en mPerrrmissionList
    List<String> mPermissionList = new ArrayList<>();

    // Sentencia y solicitud de permiso
    private void initPermission() {

        mPermissionList.clear();// Borrar el permiso que no ha pasado
        // Uno por uno, juzgue si se ha aprobado el permiso que desea
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);// Agregar permisos que no se han otorgado
            }
        }
        // solicitud de acceso
        if (mPermissionList.size() > 0) {// No se pudo aprobar el permiso, es necesario solicitarlo
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        } else {
           /* MDToast.makeText(MainActivity.this,
                    "Permisos otorgados a la aplicación.",
                    Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();*/

            // Se ha aprobado el permiso, puedes hacer lo que quieras hacer
        }
    }

    private final int mRequestCode = 100;// Código de solicitud de permiso


    // Método de devolución de llamada después de solicitar permiso
    // Parámetro: requestCode es nuestro propio código de solicitud de permiso
    // Parámetros: permisos es una matriz de nombres de permisos que solicitamos
    // Parámetros: grantResults es una matriz de identificación de si permitimos el permiso después de que aparezca la página, la longitud de la matriz corresponde a la longitud de la matriz de nombres de permisos, los datos de la matriz 0 significan permiso y -1 significa que hicimos clic en el permiso de prohibición
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;// Permiso fallido
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            // Si no se permite el permiso
            if (hasPermissionDismiss) {
                showPermissionDialog();// Vaya a la página de permisos de configuración del sistema o cierre directamente la página para evitar que continúe visitando
            } else {
                // Se pasan todos los permisos, puede continuar con el siguiente paso. . .

            }
        }

    }

    AlertDialog mPermissionDialog;
    String mPackName = "com.huawei.liwenzhi.weixinasr";

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("Permiso deshabilitado, otorgue manualmente")
                    .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cierra la página o realiza otras operaciones
                            cancelPermissionDialog();

                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    // Cerrar el diálogo
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }


}

