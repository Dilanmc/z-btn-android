package spalmalo.z_btn.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import spalmalo.z_btn.R;
import spalmalo.z_btn.models.Task;

public class ListTasksAdapter extends RecyclerView.Adapter<ListTasksViewHolder> {
    List<String> tasksList;
    private boolean isPlay;

    private boolean isPlay() {
        return isPlay;
    }

    public ListTasksAdapter(List<String> tasksList) {
        this.tasksList = tasksList;
    }


    @Override
    public ListTasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ListTasksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListTasksViewHolder holder, int position) {
        String task = tasksList.get(position);
        holder.textTask.setText(task);

        holder.btnPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay() == false) {
                    holder.btnPlayAndPause.setImageResource(R.drawable.ic_pause);
                } else {
                    holder.btnPlayAndPause.setImageResource(R.drawable.ic_play_arrow);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }
}
