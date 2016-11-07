package spalmalo.z_btn.adapters;


import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import spalmalo.z_btn.Constants;
import spalmalo.z_btn.R;
import spalmalo.z_btn.TaskClickListener;
import spalmalo.z_btn.models.Task;

public class ListTasksAdapter extends RecyclerView.Adapter<ListTasksViewHolder> {
    private List<Task> tasksList;
    private TaskClickListener clickListener;
    private CountDownTimer mTimer;


    public ListTasksAdapter(List<Task> tasks, TaskClickListener clickListener) {
        this.tasksList = tasks;
        this.clickListener = clickListener;
    }


    @Override
    public ListTasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ListTasksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListTasksViewHolder holder, final int position) {
        holder.textTask.setText(tasksList.get(position).getTitle());

        holder.checkBoxFinish.setChecked(tasksList.get(position).getStatus().equals(Constants.TASK_STATUS_FINISHED));
        holder.checkBoxFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.finished(tasksList.get(position).getId());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.longClick(tasksList.get(position).getId());
                return true;
            }
        });

        holder.btnPlayAndPause.setVisibility(tasksList.get(position).getStatus()
                .equals(Constants.TASK_STATUS_FINISHED) ? View.GONE : View.VISIBLE);
        holder.btnPlayAndPause.setImageResource(tasksList.get(position).getStatus()
                .equals(Constants.TASK_STATUS_STOPPED) ? R.drawable.ic_play_arrow : R.drawable.ic_pause);

        if (tasksList.get(position).getStatus().equals(Constants.TASK_STATUS_STOPPED)) {
            holder.taskCointainer.setBackgroundColor(Color.parseColor("#00000000"));
        } else {
            holder.taskCointainer.setBackgroundColor(Color.parseColor("#80E6BB00"));
        }

        if (tasksList.get(position).getStatus().equals(Constants.TASK_STATUS_STARTED)) {
            holder.taskCointainer.setBackgroundColor(Color.parseColor("#80E6BB00"));
        } else {
            holder.taskCointainer.setBackgroundColor(Color.parseColor("#00000000"));
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        long timer = Long.parseLong("100");

        timer = 1000 * timer;

        mTimer = new CountDownTimer(timer, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                holder.time.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                holder.time.setText("00:00");

            }
        }.start();

        holder.btnPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tasksList.get(position).getStatus().equals(Constants.TASK_STATUS_STOPPED)) {
                    holder.btnPlayAndPause.setImageResource(R.drawable.ic_play_arrow);
                    holder.taskCointainer.setBackgroundColor(Color.parseColor("#00000000"));
                    clickListener.started(tasksList.get(position).getId());
                } else if (tasksList.get(position).getStatus().equals(Constants.TASK_STATUS_STARTED)) {
                    holder.btnPlayAndPause.setImageResource(R.drawable.ic_pause);
                    holder.taskCointainer.setBackgroundColor(Color.parseColor("#80E6BB00"));
                    clickListener.stopped(tasksList.get(position).getId());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

}
