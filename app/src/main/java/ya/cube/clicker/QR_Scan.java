package ya.cube.clicker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QR_Scan extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QR_Scan.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera(camId);
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
        mScannerView = null;
    }

    @Override
    public void handleResult(Result rawResult) {

        final String result = rawResult.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getResources().getString(R.string.copy), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("",result);
                clipboard.setPrimaryClip(clip);
                mScannerView.resumeCameraPreview(QR_Scan.this);
                String s = getResources().getString(R.string.clip);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.visit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                startActivity(browserIntent);}
                catch (ActivityNotFoundException e){
                    mScannerView.resumeCameraPreview(QR_Scan.this);
                    String s = getResources().getString(R.string.inavid_url);
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }}
