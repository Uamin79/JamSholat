package com.example.prayerschedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private EditText etNamaMasjid, etAlamatMasjid;
    private Button btnSimpanMasjid, btnTambahPengumuman, btnKembali;
    private ListView listViewPengumuman;

    private PrayerDbHelper dbHelper;
    private ArrayList<String> pengumumanList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        etNamaMasjid = findViewById(R.id.etNamaMasjid);
        etAlamatMasjid = findViewById(R.id.etAlamatMasjid);
        btnSimpanMasjid = findViewById(R.id.btnSimpanMasjid);
        btnTambahPengumuman = findViewById(R.id.btnTambahPengumuman);
        btnKembali = findViewById(R.id.btnKembali);
        listViewPengumuman = findViewById(R.id.listViewPengumuman);

        dbHelper = new PrayerDbHelper(this);

        loadMasjidInfo();
        loadPengumuman();

        btnSimpanMasjid.setOnClickListener(v -> {
            String nama = etNamaMasjid.getText().toString();
            String alamat = etAlamatMasjid.getText().toString();
            dbHelper.updateMasjid(nama, alamat);
            Toast.makeText(this, "Masjid disimpan", Toast.LENGTH_SHORT).show();
        });

        btnTambahPengumuman.setOnClickListener(v -> showPengumumanDialog(null, -1));

        listViewPengumuman.setOnItemClickListener((parent, view, position, id) -> {
            String text = pengumumanList.get(position);
            showPengumumanDialog(text, position);
        });

        listViewPengumuman.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Pengumuman")
                    .setMessage("Apakah yakin ingin menghapus pengumuman ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        dbHelper.deletePengumuman(pengumumanList.get(position));
                        loadPengumuman();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
            return true;
        });

        btnKembali.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void loadMasjidInfo() {
        Cursor cursor = dbHelper.getMasjid();
        if (cursor != null && cursor.moveToFirst()) {
            etNamaMasjid.setText(cursor.getString(1)); // kolom nama
            etAlamatMasjid.setText(cursor.getString(2)); // kolom alamat
        }
    }

    private void loadPengumuman() {
        pengumumanList = dbHelper.getAllPengumuman();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pengumumanList);
        listViewPengumuman.setAdapter(adapter);
    }

    private void showPengumumanDialog(String text, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(text == null ? "Tambah Pengumuman" : "Edit Pengumuman");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (text != null) input.setText(text);

        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newText = input.getText().toString().trim();
            if (!newText.isEmpty()) {
                if (text == null) {
                    // Tambah baru
                    dbHelper.insertPengumuman(newText);
                } else {
                    // Update
                    dbHelper.updatePengumuman(text, newText);
                }
                loadPengumuman();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
