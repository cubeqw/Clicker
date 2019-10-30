package ya.cube.clicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Send extends AppCompatActivity {
    TextView textView, title_view;
    String short_url="";
    String url;
    Button share;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        textView=findViewById(R.id.short_url);
        short_url=getResources().getString(R.string.wait);
        share=findViewById(R.id.button_share);
        share.setVisibility(View.INVISIBLE);
        title_view=findViewById(R.id.title_url);
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
            url=sharedText;
            setShort_url();
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
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
    }
    public void generate() {
        if(!short_url.equals(getResources().getString(R.string.wait))){
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
        }}

    public void setShort_url() {
        if (!url.isEmpty()) {
            App.getApi().url(url).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    short_url=(response.body());
                    if(short_url.charAt(0)=='<'){
                        String s = getResources().getString(R.string.inavid_url);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        textView.setText("");
                    }else{
                        textView.setText(short_url);
                        generate();
                        share.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    String s = getResources().getString(R.string.no_net);
                    Toast.makeText(Send.this,s , Toast.LENGTH_SHORT).show();
                    close();
                }
            });
        }
        else {
            String s = getResources().getString(R.string.inavid_url);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }}

    public void close(){
        this.finish();
    }
    class MyTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            if(url.charAt(0)=='h'&&url.charAt(1)=='t'&&url.charAt(3)=='t'&&url.charAt(4)=='p'&&url.charAt(5)=='s'&&url.charAt(6)==':'&&url.charAt(7)=='/'&&url.charAt(8)=='/'){}
            else{url="https://"+url;}
            Document doc = null;
            try {

                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (doc!=null)
                title = doc.title();

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            title_view.setText(title);
        }
    }

}

