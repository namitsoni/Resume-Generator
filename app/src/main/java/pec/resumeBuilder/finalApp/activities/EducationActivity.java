package pec.resumeBuilder.finalApp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.sultanofcardio.resumebuildersultanofcardio.R;
import pec.resumeBuilder.finalApp.adapters.EducationAdapter;
import pec.resumeBuilder.finalApp.models.Resume;

public class EducationActivity extends AppCompatActivity {
    private Resume resume;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EducationAdapter adapter;
    private ImageView add, remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        long id = getIntent().getLongExtra("resume_id", 0);
        resume = new Select().from(Resume.class).where("id = ?", id).executeSingle();

        initViews();

        setUpViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_education, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.next:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new EducationAdapter(resume.getEducation(), this);
        add = (ImageView) findViewById(R.id.add);
        remove = (ImageView) findViewById(R.id.remove);
    }

    private void setUpViews(){
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addEducation();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeEducation();
            }
        });
    }

    private void save(){
        resume.setEducation(adapter.getEducationList());
        resume.save();
        Intent intent = new Intent(this, SkillsActivity.class);
        intent.putExtra("resume_id", resume.getId());
        startActivity(intent);
    }
}
