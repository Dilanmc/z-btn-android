package spalmalo.z_btn;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import spalmalo.z_btn.adapters.ListTasksAdapter;

public class MainActivity extends AppCompatActivity {

    private ListTasksAdapter tasksAdapter;
    private ArrayList<String> tasks = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private MaterialDialog dialog;
    private EditText taskText;

    @BindView(R.id.list_task)
    RecyclerView tasksList;
    @BindView(R.id.add_task)
    FloatingActionButton addTask;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle(" My Tasks");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        tasks.add("Android create project");
        setupRecyclerView();

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialogTask();
            }
        });

    }

    private void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        tasksList.setLayoutManager(mLayoutManager);
        tasksAdapter = new ListTasksAdapter(tasks);
        tasksList.setAdapter(tasksAdapter);
        tasksList.setHasFixedSize(true);
    }

    private void setupDialogTask() {
        dialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Create new task")
                .customView(R.layout.item_task_text, Boolean.parseBoolean(null))
                .positiveText("Create")
                .negativeText("Cancel")
                .positiveColor(Color.YELLOW)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        tasks.add(taskText.getText().toString());
                        tasksAdapter.notifyDataSetChanged();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                })
                .build();
        taskText = (EditText) dialog.findViewById(R.id.edit_task_text);
        dialog.show();
    }
}
