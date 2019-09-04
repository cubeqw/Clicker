package ya.cube.clicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getResources().getString(R.string.about));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    public void onClick(View v){
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.github.com/cubeqw"));
    startActivity(browserIntent);}
}
