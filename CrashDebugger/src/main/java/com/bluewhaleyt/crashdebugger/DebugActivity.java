package com.bluewhaleyt.crashdebugger;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.bluewhaleyt.crashdebugger.databinding.ActivityDebugBinding;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.snackbar.Snackbar;

public class DebugActivity extends AppCompatActivity {

    private ActivityDebugBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDebugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize() {

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.toolbar_title));
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));

        var strBuilderMessage = new StringBuilder();
        strBuilderMessage.append(getResources().getString(R.string.manufacturer) + ": " + DeviceUtils.getManufacturer() + "\n");
        strBuilderMessage.append(getResources().getString(R.string.device) + ": " + DeviceUtils.getModel() + "\n");
        strBuilderMessage.append(getIntent().getStringExtra("software"));
        strBuilderMessage.append(getResources().getString(R.string.app_version) + ": " + getAppVersion() + "\n");
        strBuilderMessage.append(getResources().getString(R.string.crash_time) + ": ");
        strBuilderMessage.append(getIntent().getStringExtra("date"));
        strBuilderMessage.append("\n");
        strBuilderMessage.append(getResources().getString(R.string.crash_message) + ": " + "\n");
        strBuilderMessage.append(getIntent().getStringExtra("error"));

        binding.tvResult.setText(strBuilderMessage.toString());

        binding.fabCopy.setOnClickListener(v -> {
            ClipboardUtils.copyText(binding.tvResult.getText());
            Snackbar.make(binding.getRoot(), getResources().getString(R.string.snackbar_copy_to_clipboard), Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(binding.fabCopy)
                    .show();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private String getAppVersion() {
        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_close) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
