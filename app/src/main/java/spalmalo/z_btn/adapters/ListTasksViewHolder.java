package spalmalo.z_btn.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import spalmalo.z_btn.R;

public class ListTasksViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_task)
    TextView textTask;
    @BindView(R.id.btn_play_and_pause)
    ImageButton btnPlayAndPause;
    @BindView(R.id.checkboxFinish)
    CheckBox checkBoxFinish;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.task_container)
    LinearLayout taskCointainer;

    public ListTasksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
