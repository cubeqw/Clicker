package ya.cube.clicker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

public class Send extends AppCompatActivity {
    TextView textView;
    String short_url="";
    String api="https://clck.ru/--?url=";
    boolean connect;
    String getUrl;
    String url;
    int time=3;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        textView=findViewById(R.id.short_url);
        short_url=getResources().getString(R.string.wait);
        Intent intent = getIntent();
    String action = intent.getAction();
    String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
        if ("text/plain".equals(type)) {
            handleSendText(intent);
        }
    }

}

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            getUrl=sharedText;
            try {
                generate();
                startTimer();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qr_scan:
                Intent intent = new Intent(this, QR_Scan.class);
                startActivity(intent);
                return true;
            case R.id.about:
                Intent intent2 = new Intent(this, About.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onClickURL(View v){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, short_url);
        startActivity(Intent.createChooser(intent, "Поделиться "));

    }
    public void generate() throws UnsupportedEncodingException {
        if (getUrl != null) {
            url =URLEncoder.encode(getUrl, "UTF-8");
        }else{

        }

        setShort_url(url);

        if(connect){
            if(short_url.toCharArray().length>50){
                String s = getResources().getString(R.string.inavid_url);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();}
            else{
                textView.setText(short_url);}}
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(short_url, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.img_result_qr)).setImageBitmap(bmp);} catch (WriterException ex) {
            ex.printStackTrace();
        }
    }
    public String setShort_url(final String url){
        if (!url.isEmpty()) {
            new Thread(new Runnable() {
                public void run() {
                    DefaultHttpClient hc = new DefaultHttpClient();
                    ResponseHandler response = new BasicResponseHandler();
                    HttpGet http = new HttpGet(api + url);
                    try {
                        String responsec = (String) hc.execute(http, response);
                        short_url = responsec;
                        while(short_url.equals("")){
                            connect=false;
                            break;
                        }
                        while(!short_url.equals("")){
                            connect=true;
                            runThread();
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("",short_url);
                            clipboard.setPrimaryClip(clip);
                            break;}}
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            String s = getResources().getString(R.string.clip);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            return url;

        }else{
            String s = getResources().getString(R.string.inavid_url);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            return "";
        }}
    private void runThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(short_url);
            }
        });
    }
    public void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(time>0){
                            time--;
                        }
                        if(time==0){
                            if(short_url.equals(getResources().getString(R.string.wait))){
                                String s=getResources().getString(R.string.no_net);
                                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
                                close();
                                time--;
                            }
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }
    public void close(){
        this.finish();
    }
}

