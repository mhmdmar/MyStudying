package com.example.user.mystudying;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    ImageButton btnViewAll, btnAdd, btnReset,btnDelete;
    private EditText txtSubject, txtHours;
    private TextView txtSum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Disable Rotation Landscape Mode
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Declare the Database
        myDb = new DatabaseHelper(this);
        // Declare the Buttons
        btnViewAll = findViewById(R.id.btnViewAll);
        btnReset = findViewById(R.id.btnReset);
        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        // Declare the Text Boxes
        txtSubject = findViewById(R.id.txtSubject);
        txtHours = findViewById(R.id.txtHours);
        // Declare Text View
        txtSum = findViewById(R.id.txtSum);
        updateSum();
        // On Button Reset Click empty all textboxes
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeTexts();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtSubject.getText().length() == 0) {
                    txtSubject.setError("Error Empty");
                    return;
                }
                if (txtHours.getText().length() == 0) {
                    txtHours.setError("Error Empty");
                    return;
                }
                boolean isSuccess = myDb.insertData(txtSubject.getText().toString(), Integer.valueOf(txtHours.getText().toString()));
                if (isSuccess)
                    Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                else {
                    isSuccess = UpdateData();
                    if (isSuccess)
                        Toast.makeText(MainActivity.this, "Data Updated", Toast.LENGTH_LONG).show();
                }
                updateSum();
                removeTexts();
            }
        });
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = myDb.getAllData();
                if(res.getCount() == 0) {
                    // show message
                    showMessage("Error","Nothing found");
                    return;
                }
                StringBuilder buffer = new StringBuilder();
                while (res.moveToNext()) {
                    buffer.append("SUBJECT :"+ res.getString(0)+"\n");
                    buffer.append("HOURS :"+ res.getString(1)+"\n\n");
                }

                // Show all data
                showMessage("Data",buffer.toString());
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtSubject.getText().length() == 0) {
                    txtSubject.setError("Error Empty");
                    return;
                }
              DeleteData();
            }
        });

    }

    public void updateSum() {
        txtSum.setText(String.valueOf(myDb.getAllData().getCount()));
    }
    // Remove Focus when clicking on the screen outside of the buttons
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    // Delete the DataBase
    public void DeleteData() {
        Integer deletedRows = myDb.deleteData(txtSubject.getText().toString());
        if (deletedRows > 0) {
            Toast.makeText(MainActivity.this, "Data is Deleted", Toast.LENGTH_LONG).show();
            updateSum();
        }
        else
            Toast.makeText(MainActivity.this, "Data Couldn't be Deleted", Toast.LENGTH_LONG).show();
    }
    // Remove texts from edit texts
    public void removeTexts()
    {
        txtSubject.setText("");
        txtHours.setText("");
        txtSubject.setError(null);
        txtHours.setError(null);
    }

    // Update the DataBase
    public boolean UpdateData() {
        int hours = 0;
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return false;
        }
        while (res.moveToNext()) {
            if (res.getString(0).equals(txtSubject.getText().toString()))
                hours = Integer.valueOf(res.getString(1)) + Integer.valueOf(txtHours.getText().toString());
        }
        return myDb.updateData(txtSubject.getText().toString(), hours + "");
    }
    // Show the Data Base Table
    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            {
                View current = getCurrentFocus();
                if (current != null) current.clearFocus();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
    /*
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final  int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize Value that is a power of 2 and eeps both height and width larger than the requested height and width
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public  static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight)
    {
        // Decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        //calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //Decode bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);

    }
        */
}