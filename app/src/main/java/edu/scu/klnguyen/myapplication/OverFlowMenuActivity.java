package edu.scu.klnguyen.myapplication;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by kim long on 5/3/2016.
 */
public class OverFlowMenuActivity extends AppCompatActivity {

    protected void createActionBar(String tittle) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF000000));
        actionBar.setIcon(R.mipmap.wheel88);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        String str = "<small>" + tittle + "</small>";
        actionBar.setTitle(Html.fromHtml(str));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.uninstall: {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:edu.scu.klnguyen.myapplication"));
                startActivity(intent);
            }
        }

        return true;
    }
}
