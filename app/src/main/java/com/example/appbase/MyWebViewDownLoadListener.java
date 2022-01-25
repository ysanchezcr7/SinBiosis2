///*package com.example.appbase;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.util.Log;
//import android.view.Gravity;
//import android.webkit.DownloadListener;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URLDecoder;
//
//// clase interna
//private class MyWebViewDownLoadListener implements DownloadListener {
//
//    Context mContext;
//
//    @Override
//    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
//                                long contentLength) {
//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            Toast t = Toast.makeText(mContext, "Se requiere tarjeta SD", Toast.LENGTH_LONG);
//            t.setGravity(Gravity.CENTER, 0, 0);
//            t.show();
//            return;
//        }
//        DownloaderTask task = new DownloaderTask();
//        task.execute(url);
//    }
//
//    private class DownloaderTask extends AsyncTask<String, Void, String> {
//
//        public DownloaderTask() {
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            // TODO Auto-generated method stub
//            String url = params[0];
////			Log.i("tag", "url="+url);
//            String fileName = url.substring(url.lastIndexOf("/") + 1);
//            fileName = URLDecoder.decode(fileName);
//            Log.i("tag", "fileName=" + fileName);
//
//            File directory = Environment.getExternalStorageDirectory();
//            File file = new File(directory, fileName);
//            if (file.exists()) {
//                Log.i("tag", "The file has already exists.");
//                return fileName;
//            }
//            try {
//                HttpClient client = new DefaultHttpClient();
//// client.getParams (). setIntParameter ("http.socket.timeout", 3000); // Establecer tiempo de espera
//                HttpGet get = new HttpGet(url);
//                HttpResponse response = client.execute(get);
//                if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
//                    HttpEntity entity = response.getEntity();
//                    InputStream input = entity.getContent();
//
//                    writeToSDCard(fileName, input);
//
//                    input.close();
////					entity.consumeContent();
//                    return fileName;
//                } else {
//                    return null;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            // TODO Auto-generated method stub
//            super.onCancelled();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // TODO Auto-generated method stub
//            super.onPostExecute(result);
//            closeProgressDialog();
//            if (result == null) {
//                Toast t = Toast.makeText(mContext, "¡Error de conexión! ¡Inténtalo de nuevo más tarde!", Toast.LENGTH_LONG);
//                t.setGravity(Gravity.CENTER, 0, 0);
//                t.show();
//                return;
//            }
//
//            Toast t = Toast.makeText(mContext, "Guardado en la tarjeta SD", Toast.LENGTH_LONG);
//            t.setGravity(Gravity.CENTER, 0, 0);
//            t.show();
//            File directory = Environment.getExternalStorageDirectory();
//            File file = new File(directory, result);
//            Log.i("tag", "Path=" + file.getAbsolutePath());
//
//            Intent intent = getFileIntent(file);
//            startActivity(intent);
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            super.onPreExecute();
//            showProgressDialog();
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            // TODO Auto-generated method stub
//            super.onProgressUpdate(values);
//        }
//
//
//        private ProgressDialog mDialog;
//
//        private void showProgressDialog() {
//            if (mDialog == null) {
//                mDialog = new ProgressDialog(mContext);
//                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Establece el estilo en una barra de progreso circular
//                mDialog.setMessage("Cargando, por favor espere ...");
//                mDialog.setIndeterminate(false); // Establecer si la barra de progreso es ambigua
//                mDialog.setCancelable(true); // Establezca si la barra de progreso se puede cancelar presionando la tecla Atrás
//                mDialog.setCanceledOnTouchOutside(false);
//                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        // TODO Auto-generated method stub
//                        mDialog = null;
//                    }
//                });
//                mDialog.show();
//
//            }
//        }
//
//        private void closeProgressDialog() {
//            if (mDialog != null) {
//                mDialog.dismiss();
//                mDialog = null;
//            }
//        }
//
//        public Intent getFileIntent(File file) {
////		 Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
//            Uri uri = Uri.fromFile(file);
//            String type = getMIMEType(file);
//            Log.i("tag", "type=" + type);
//            Intent intent = new Intent("android.intent.action.VIEW");
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(uri, type);
//            return intent;
//        }
//
//        public void writeToSDCard(String fileName, InputStream input) {
//
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                File directory = Environment.getExternalStorageDirectory();
//                File file = new File(directory, fileName);
////			if(file.exists()){
////				Log.i("tag", "The file has already exists.");
////				return;
////			}
//                try {
//                    FileOutputStream fos = new FileOutputStream(file);
//                    byte[] b = new byte[2048];
//                    int j = 0;
//                    while ((j = input.read(b)) != -1) {
//                        fos.write(b, 0, j);
//                    }
//                    fos.flush();
//                    fos.close();
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            } else {
//                Log.i("tag", "NO SDCard.");
//            }
//        }
//
//        private String getMIMEType(File f) {
//            String type = "";
//            String fName = f.getName();
//            // Obtener extensión
//            String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
//
//            //Determinar MimeType según el tipo de extensión *
//            if (end.equals("pdf")) {
//                type = "application/pdf";//
//            } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
//                    end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
//                type = "audio/*";
//            } else if (end.equals("3gp") || end.equals("mp4")) {
//                type = "video/*";
//            } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
//                    end.equals("jpeg") || end.equals("bmp")) {
//                type = "image/*";
//            } else if (end.equals("apk")) {
//                /* android.permission.INSTALL_PACKAGES */
//                type = "application/vnd.android.package-archive";
//            }
////      else if(end.equals("pptx")||end.equals("ppt")){
////    	  type = "application/vnd.ms-powerpoint";
////      }else if(end.equals("docx")||end.equals("doc")){
////    	  type = "application/vnd.ms-word";
////      }else if(end.equals("xlsx")||end.equals("xls")){
////    	  type = "application/vnd.ms-excel";
////      }
//            else {
//// / * Si no se puede abrir directamente, salte de la lista de software para que el usuario elija * /
//                type = "*/*";
//            }
//            return type;
//        }
//
//
//    }
//
//}
//
//// clase interna
