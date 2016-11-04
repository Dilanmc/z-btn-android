package spalmalo.z_btn.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import spalmalo.z_btn.R;

public class ListTasksViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_task)
    TextView textTask;
    @BindView(R.id.btn_play_and_pause)
    ImageButton btnPlayAndPause;

    public ListTasksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
