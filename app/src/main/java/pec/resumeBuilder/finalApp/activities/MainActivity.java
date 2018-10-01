package pec.resumeBuilder.finalApp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.activeandroid.query.Select;
import com.sultanofcardio.resumebuildersultanofcardio.R;
import pec.resumeBuilder.finalApp.models.Resume;
import pec.resumeBuilder.finalApp.util.ResumeUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.activeandroid.Cache.getContext;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private Resume resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);
        long id = getIntent().getLongExtra("resume_id", 0);
        resume = new Select().from(Resume.class).where("id = ?", id).executeSingle();
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
              //  Intent i = new Intent(getApplicationContext(),PDFActivity.class);
                // startActivity(i);
                String jobName = "Resume";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Context c = webView.getContext();
                    PrintDocumentAdapter printAdapter;
                    PrintManager printManager = (PrintManager) c.getSystemService(Context.PRINT_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        printAdapter = webView.createPrintDocumentAdapter(jobName);
                    } else {
                        printAdapter = webView.createPrintDocumentAdapter();
                    }
                    if (printManager != null) {
                        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                    }
                } else {
                    Log.e(getClass().getName(), "ERROR: Method called on too low Android API version");
                }
            }
        });
        Button button1=(Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               Intent i = new Intent(getApplicationContext(),SavedResumesActivity.class);
                startActivity(i);

            }
        });
        new Task().execute();

    }

    class Task extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            return ResumeUtils.createResume(resume, MainActivity.this);
        }

        @Override
        protected void onPostExecute(String html) {
            super.onPostExecute(html);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setInitialScale(50);
            /*
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            webView.buildDrawingCache();
                            Bitmap bitmap = webView.getDrawingCache();
                            ResumeUtils.takeSnapshot(bitmap, MainActivity.this, resume);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ResumeUtils.printResume(webView, MainActivity.this);
                                }
                            });
                        }
                    }, 500);
                    super.onPageFinished(view, url);
                }
            });*/
            webView.loadDataWithBaseURL("file:///android_asset/.", html, "text/html", "UTF-8", null);
        }
    }
}
