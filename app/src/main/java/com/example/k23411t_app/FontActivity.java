package com.example.k23411t_app;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FontActivity extends AppCompatActivity {

    private TextView txtPreview;
    private Button btnPlayAudio1;
    private Button btnPlayAudio2;
    private ListView lvFonts;
    private MediaPlayer mediaPlayer;

    // Names of font typefaces to display
    private final String[] fontNames = {
            "Default",
            "Serif",
            "Monospace",
            "Sans-Serif",
            "Casual",
            "Cursive",
            "Sans-Serif Condensed",
            "Sans-Serif Medium",
            "Sans-Serif Light",
            "Sans-Serif Thin",
            "Sans-Serif Black",
            "Sans-Serif Smallcaps"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_font);

        // Configure system windows inset padding (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up Action Bar title and back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.str_font_demo_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
    }

    private void initViews() {
        txtPreview = findViewById(R.id.txt_preview);
        btnPlayAudio1 = findViewById(R.id.btn_play_audio_1);
        btnPlayAudio2 = findViewById(R.id.btn_play_audio_2);
        lvFonts = findViewById(R.id.lv_fonts);

        // Predefine typefaces mapping to the list names
        final Typeface[] typefaces = {
                Typeface.DEFAULT,
                Typeface.SERIF,
                Typeface.MONOSPACE,
                Typeface.SANS_SERIF,
                Typeface.create("casual", Typeface.NORMAL),
                Typeface.create("cursive", Typeface.NORMAL),
                Typeface.create("sans-serif-condensed", Typeface.NORMAL),
                Typeface.create("sans-serif-medium", Typeface.NORMAL),
                Typeface.create("sans-serif-light", Typeface.NORMAL),
                Typeface.create("sans-serif-thin", Typeface.NORMAL),
                Typeface.create("sans-serif-black", Typeface.NORMAL),
                Typeface.create("sans-serif-smallcaps", Typeface.NORMAL)
        };

        // Custom Adapter to display font styles directly in the list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fontNames) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                if (textView != null) {
                    textView.setTypeface(typefaces[position]);
                    textView.setTextSize(18);
                }
                return view;
            }
        };
        lvFonts.setAdapter(adapter);

        // Font click listener
        lvFonts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtPreview.setTypeface(typefaces[position]);
                Toast.makeText(FontActivity.this, "Applied: " + fontNames[position], Toast.LENGTH_SHORT).show();
            }
        });

        // Play Audio 1 listener (local raw/audio1)
        btnPlayAudio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRawSound(R.raw.audio1);
            }
        });

        // Play Audio 2 listener (local raw/audio2)
        btnPlayAudio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRawSound(R.raw.audio2);
            }
        });
    }

    private void playRawSound(int resId) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, resId);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                Toast.makeText(this, "Playing Audio...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not load audio resource", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error playing audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
