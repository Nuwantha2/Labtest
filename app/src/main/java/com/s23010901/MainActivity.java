package com.s23010901;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button goButton;
    EditText usernameInput, passwordInput;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        goButton = findViewById(R.id.goButton);
        db = new DBHelper(this);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = usernameInput.getText().toString().trim();
                String pass = passwordInput.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.checkUser(user, pass)) {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, Map.class));
                } else if (!db.userExists(user)) {
                    boolean created = db.registerUser(user, pass);
                    if (created) {
                        Toast.makeText(MainActivity.this, "User registered and logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Map.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //  SQLite DB Helper
    public static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "Userdata.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }

        public boolean userExists(String username) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
            return cursor.getCount() > 0;
        }

        public boolean checkUser(String username, String password) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
            return cursor.getCount() > 0;
        }

        public boolean registerUser(String username, String password) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            long result = db.insert("users", null, values);
            return result != -1;
        }
    }
}
