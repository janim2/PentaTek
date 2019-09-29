package com.uenr.pentatek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Accessories
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    Context context;
    private static final String SP_NAME = "appStore";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Accessories(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(SP_NAME, 0);
    }

    //Method to send and retrieve String from server
    public void serverRequest(String task, String urlLocation, String[] keys, String[] values,
                              Callback callback) throws UnsupportedEncodingException {
        new ServerRequest(task, urlLocation, keys, values, callback).execute();

    }

    //Method to return to Home Screen
    public void gotoHome(){
        Intent finali = new Intent(Intent.ACTION_MAIN);
        finali.addCategory(Intent.CATEGORY_HOME);
        finali.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(finali);
    }

    //Method to increaseListHeight Dynamically
    public static void setListHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    //Method to validate input fields
    public boolean validateFields(int[] ints, EditText[] editTexts, String[] strings){
        for(int i = 0; i < ints.length; i++){
            int type = ints[i];
            EditText editText = editTexts[i];
            String string = strings[i];
            if(type == 1){
                //Email
                if(string.equals("")){
                    return setError("E-mail Field Required!", editText);
                }else if(!string.contains("@")){
                    return setError("@ Sign Required in E-mail addresses", editText);
                }else if(!string.contains(".com")){
                    return setError("All E-mails should end in '.com'", editText);
                }
            }else if(type == 2){
                //Phone
                if(string.equals("")){
                    return setError("Phone Field Required!", editText);
                }else if(string.length() < 10){
                    return setError("Not a valid phone number", editText);
                }else if(!string.contains("0") && !string.contains("1") && !string.contains("2") &&
                        !string.contains("3") && !string.contains("4") && !string.contains("5") &&
                        !string.contains("6") && !string.contains("7") && !string.contains("8") &&
                        !string.contains("9")){
                    return setError("No number found in field", editText);
                }
            }else if(type == 3){
                //Name
                if(string.equals("")){
                    return setError("Name Field Required!", editText);
                }else if(string.contains("0") && string.contains("1") && string.contains("2") &&
                        string.contains("3") && string.contains("4") && string.contains("5") &&
                        string.contains("6") && string.contains("7") && string.contains("8") &&
                        string.contains("9")){
                    return setError("No number required in field!", editText);
                }
            }else if(type == 4){
                //Email or Phone
                if(string.equals("")){
                    return setError("E-mail or phone required in field!", editText);
                }else if(!string.contains("@") && !string.contains(".com")){
                    for(i = 0; i < string.length(); i++){
                        if(Character.isLetter(string.charAt(i))){
                            return setError("Input not e-mail or phone!", editText);
                        }
                    }
                }else if(string.length() < 10){
                    setError("Input too short!", editText);
                }
            }else if(type == 5){
                //Password
                if(string.equals("")){
                    return setError("Password Field Required!", editText);
                }else if(string.length() < 6){
                    return setError("Password Entered Too Short", editText);
                }
            }
        }
        return true;
    }

    //Method to change ImageBitmap to String
    public String encodeImage(Bitmap imageBitmap) {
        final String[] toreturn = {""};
        new BitmapConverter(imageBitmap, new Callback() {
            @Override
            public void done(String returned) {
               toreturn[0] = returned;
            }
        }).execute();
        return toreturn[0];
    }

    //Method to call DatePicker
    EditText[] editText;
    TextView[] textView;
    public void showDatePicker(EditText[] editText, TextView[] textView){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        this.editText = editText;
        this.textView = textView;

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, this, year, month, day);
        datePickerDialog.show();
    }

    public void showTimePicker(EditText[] editText, TextView[] textView){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int second = calendar.get(Calendar.SECOND);
        int minute = calendar.get(Calendar.MINUTE);
        this.editText = editText;
        this.textView = textView;

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, this, hour, minute, false);
        timePickerDialog.show();
    }

    int yr, mt, dy, hr, min;
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yr = year;
        mt = month + 1;
        dy = dayOfMonth;
        if(editText != null){
            for (int i = 0; i < editText.length; i++){
                editText[i].setText(yr + "-" + mt + "-" + dy);
            }
        }
        if(textView != null){
            for (int i = 0; i < textView.length; i++){
                textView[i].setText(yr + "-" + mt + "-" + dy);
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hr = hourOfDay;
        min = minute;
        if(editText != null){
            for (int i = 0; i < editText.length; i++){
                editText[i].setText(hr+":"+min);
            }
        }
        if(textView != null){
            for (int i = 0; i < textView.length; i++){
                textView[i].setText(hr+":"+min);
            }
        }
    }

    //Method to get date results
    public int[] getDatePickerResult(){
        int[] toret = {yr, mt, dy};
        return toret;
    }

    public int[] getTodayDate(){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dArr[] = date.split("-");
        int[] toret = null;
        try {
            toret = new int[]{Integer.parseInt(dArr[0]), Integer.parseInt(dArr[1]), Integer.parseInt(dArr[2])};
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return toret;
    }

    public static class BitmapConverter extends AsyncTask<Void, Void, String> {

        Bitmap bitmap;
        Callback callback;

        public BitmapConverter(Bitmap bitmap, Callback callback) {
            super();
            this.bitmap = bitmap;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            return encodedImage;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            callback.done(s);
        }
    }

    private boolean setError(String error, EditText editText){
        editText.setError(error);
        editText.requestFocus();
        return false;
    }

    public void stopKeyboard(){
        if(context instanceof Activity){
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else{
            Toast.makeText(context, "Error: Context should be an instance of activity", Toast.LENGTH_LONG).show();
        }
    }

    //Method to call gallery action
    //RESULT_LOAD_IMAGE: eg. 123
    //Afterwards, Override onActivityResult
    //if(resultCode == RESULT_OK)TODO: Do Action Here
    //eg. Uri uri = data.getData(); imageView.setImageURI(uri); (:- To set the picture to an imageview -:)
    public void galleryAction(final int RESULT_LOAD_IMAGE){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(context instanceof Activity){
            ((Activity) context).startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }else{
            Toast.makeText(context, "Error: Context should be an instance of activity", Toast.LENGTH_LONG).show();
        }
    }

    //Method to check for any permission
    //permission: eg. android.Manifest.permission.READ_EXTERNAL_STORAGE
    //msg: eg. "External Storage"
    //MY_PERMISSIONS_REQUEST: eg. 123
    //Afterwards, Override onRequestPermissionsResult
    //switch(requestCode){case MY_PERMISSIONS_REQUEST:TODO: Do Action if
    //grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
    //else Permission Denied-:)
    public boolean checkPermission(String permission, String msg, int MY_PERMISSIONS_REQUEST){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, permission)){
                    showDialog(msg, context, permission, MY_PERMISSIONS_REQUEST);
                }else{
                    ActivityCompat.requestPermissions((Activity)context, new String[]{permission},
                            MY_PERMISSIONS_REQUEST);
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }

    private void showDialog(final String msg, final Context context, final String permission,
                            final int MY_PERMISSIONS_REQUEST){
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions((Activity)context, new String[]{permission},
                        MY_PERMISSIONS_REQUEST);
            }
        });
        android.support.v7.app.AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public class ServerRequest extends AsyncTask<Void, Void, String> {

        String data = "";
        String task, urlLocation;
        ProgressDialog alertDialog;
        Callback callback;

        public ServerRequest(String task, String urlLocation, String[] keys, String[] values,
                             Callback callback) throws UnsupportedEncodingException {
            this.callback = callback;
            this.task = task;
            this.urlLocation = urlLocation;
            if(keys != null) {
                for (int o = 0; o < keys.length; o++) {
                    if (o < keys.length - 1) {
                        data = data + URLEncoder.encode(keys[o], "UTF-8") + "=" +
                                URLEncoder.encode(values[o], "UTF-8") + "&";
                    } else {
                        data = data + URLEncoder.encode(keys[o], "UTF-8") + "=" +
                                URLEncoder.encode(values[o], "UTF-8");
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = new ProgressDialog(context);
            alertDialog.setMessage(task);
            alertDialog.setCancelable(false);
            alertDialog.show();
            AlertDialog.Builder ad = new AlertDialog.Builder(context);
            ad.setMessage(data);
            AlertDialog adf = ad.create();
            adf.show();;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            try {
                URL url = new URL(urlLocation);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                if(!data.equals("")) {
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                }
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String fetch = "";
                while((fetch = bufferedReader.readLine()) != null){
                    stringBuffer.append(fetch);
                }
                String string = stringBuffer.toString();
                return string;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();
            callback.done(s);
        }
    }

    /*
    SHARED PREFERENCES START HERE:
     */

    //Clear User Data
    public boolean clearStore(){
        editor = preferences.edit();
        editor.clear();
        return editor.commit();
    }

    //Put String Values Into Store
    public void put(String key, String value){
        editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //Retrieve String Values From Store
    public String getString(String key){
        return preferences.getString(key, "");
    }

    //Put Boolean Values Into Store
    public void put(String key, boolean value){
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    //Retrieve Boolean Values From Store
    public boolean getBoolean(String key){
        return preferences.getBoolean(key, false);
    }
    /*
    SHARED PREFERENCES END HERE:
     */

    /*
    SQLITE DATABASE STARTS HERE:
     */
    private SQLiteDatabase database;
    private Database base;
    private String tbname;
    private String dbname;

    public static class Database extends SQLiteOpenHelper {

        String query;
        String tbname;

        public Database(Context context, String dbname, int dbversion, String query,
                        String tbname) {
            super(context, dbname, null, dbversion);
            this.query = query;
            this.tbname = tbname;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(query);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Database", "Upgrading Database");
            db.execSQL("DROP TABLE IF EXISTS "+tbname);
            onCreate(db);
        }
    }

    //@Required Creates the database
    public void create(String dbname, int dbversion, String query, String tbname){
        base = new Database(context, dbname, dbversion, query, tbname);
        this.tbname = tbname;
        this.dbname = dbname;
    }

    //Open Database
    public void openDatabase(){
        try {
            database = base.getWritableDatabase();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //Close Database
    public void closeDatabase(){
        base.close();
    }

    //Insert into table
    public long insertRow(String columns[], String[] values){
        try{
            ContentValues cv = new ContentValues();
            for(int i = 0; i < columns.length; i++){
                cv.put(columns[i], values[i]);
            }
            return database.insertOrThrow(tbname, null, cv);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    //Get Values
    public Cursor getValues(String[] columns, String where, String[] whereargs, String groupby,
                            String having, String orderby, String limit){
        return database.query(tbname, columns, where, whereargs, groupby, having, orderby,
                limit);
    }

    //Delete Row
    public int deleteRow(String column, String where, String[] whereargs){
        try{
            return database.delete(tbname, where, whereargs);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public int updateRow(String columns[], String[] values, String where, String[] whereargs){
        try{
            ContentValues cv = new ContentValues();
            for(int i = 0; i < columns.length; i++){
                cv.put(columns[i], values[i]);
            }
            database.update(tbname, cv, where, whereargs);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    /*
    SQLITE DATABASE ENDS HERE:
     */
}