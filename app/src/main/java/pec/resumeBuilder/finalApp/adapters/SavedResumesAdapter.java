package pec.resumeBuilder.finalApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sultanofcardio.resumebuildersultanofcardio.R;
import pec.resumeBuilder.finalApp.activities.ResumeDetailActivity;
import pec.resumeBuilder.finalApp.models.Resume;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * @author namit
 */

public class SavedResumesAdapter extends RecyclerView.Adapter<SavedResumesAdapter.SavedResumeHolder> {
    private List<Resume> resumes;
    private Context context;

    public SavedResumesAdapter(List<Resume> resumes, Context context) {
        this.resumes = resumes;
        this.context = context;
    }

    @Override
    public SavedResumeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SavedResumeHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_resume_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SavedResumeHolder holder, int position) {
        final Resume resume = resumes.get(holder.getAdapterPosition());
        File snapshot = new File(new File(context.getFilesDir(), "Photos"), String.format(Locale.US,
                "%d_snapshot.png", resume.getId()));
        Picasso.with(context).load(snapshot).into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResumeDetailActivity.class);
                intent.putExtra("resume_id", resume.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resumes.size();
    }

    class SavedResumeHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;

        SavedResumeHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}
