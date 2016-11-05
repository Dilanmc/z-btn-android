package spalmalo.z_btn;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import spalmalo.z_btn.adapters.ListTasksAdapter;
import spalmalo.z_btn.models.Task;

public class MainActivity extends AppCompatActivity implements TaskClickListener {

    private ListTasksAdapter tasksAdapter;
    private ListTasksAdapter finishedTasksAdapter;
    private List<Task> tasks;
    private List<Task> tasksFinished;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagerFinished;
    private MaterialDialog dialog, dialogDeleteTask;
    private EditText taskText;
    private Realm realm;

    @BindView(R.id.list_task)
    RecyclerView tasksList;
    @BindView(R.id.list_finish_task)
    RecyclerView listFinishedTask;
    @BindView(R.id.add_task)
    FloatingActionButton addTask;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.finished_task_text)
    TextView finishedTaskText;
    @BindView(R.id.empty_list)
    TextView emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        realm = Realm.getDefaultInstance();
        initRealmListener();

        tasks = realm.where(Task.class)
                .equalTo("status", Constants.TASK_STATUS_STARTED)
                .or()
                .equalTo("status", Constants.TASK_STATUS_STOPPED)
                .findAll();
        if (tasks.size() == 0) emptyList.setVisibility(View.VISIBLE);

        tasksFinished = realm.where(Task.class)
                .equalTo("status", Constants.TASK_STATUS_FINISHED)
                .findAll();
        setupRecyclerView();

        finishedTaskText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tasksFinished.size() == 0) {
                    Toast.makeText(MainActivity.this, R.string.show_finish_task, Toast.LENGTH_SHORT).show();
                }
                listFinishedTask.setVisibility(listFinishedTask.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialogTask();
            }
        });

    }

    private void initToolbar() {
        toolbar.setTitle(R.string.my_task);
        toolbar.setTitleTextColor(Color.WHITE);
    }

    private void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        tasksList.setLayoutManager(mLayoutManager);
        tasksAdapter = new ListTasksAdapter(tasks, this);
        tasksList.setAdapter(tasksAdapter);
        tasksList.setHasFixedSize(true);

        mLayoutManagerFinished = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        listFinishedTask.setLayoutManager(mLayoutManagerFinished);
        finishedTasksAdapter = new ListTasksAdapter(tasksFinished, this);
        listFinishedTask.setAdapter(finishedTasksAdapter);
        listFinishedTask.setHasFixedSize(true);
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
                        addNewTask(taskText.getText().toString());
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

    private void initRealmListener() {
        realm.where(Task.class)
                .equalTo("status", Constants.TASK_STATUS_STARTED)
                .or()
                .equalTo("status", Constants.TASK_STATUS_STOPPED)
                .findAll()
                .addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
                    @Override
                    public void onChange(RealmResults<Task> element) {
                        emptyList.setVisibility(element.size() > 0 ? View.GONE : View.VISIBLE);
                        tasks = element;
                        tasksAdapter.notifyDataSetChanged();
                    }
                });
        realm.where(Task.class)
                .equalTo("status", Constants.TASK_STATUS_FINISHED)
                .findAll()
                .addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
                    @Override
                    public void onChange(RealmResults<Task> element) {
                        tasksFinished = element;
                        finishedTasksAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void addNewTask(final String title){
        realm.beginTransaction();
        final Task task = new Task();
        task.setId(generateTaskId());
        task.setTitle(title);
        task.setStatus(Constants.TASK_STATUS_STOPPED);
        realm.commitTransaction();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(task);
            }
        });
    }

    private int generateTaskId(){
        RealmResults<Task> results = realm.where(Task.class).findAll();
        if(results.size()>0){
            int max = 0;
            for(Task task : results){
                if(task.getId()>max) max = task.getId();
            }
            Log.e("log","max id = "+ max);
            return ++max;
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void started(final int taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setStatus(Constants.TASK_STATUS_STARTED);
            }
        });
    }

    @Override
    public void stopped(final int taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setStatus(Constants.TASK_STATUS_STOPPED);
            }
        });
    }

    @Override
    public void finished(final int taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setStatus(task.getStatus().equals(Constants.TASK_STATUS_FINISHED) ? Constants.TASK_STATUS_STOPPED : Constants.TASK_STATUS_FINISHED);
            }
        });

    }

    @Override
    public void longClick(final int taskId) {
        dialogDeleteTask = new MaterialDialog.Builder(this)
                .title("Delete task?")
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Task task = realm.where(Task.class)
                                        .equalTo("id", taskId)
                                        .findFirst();
                                task.deleteFromRealm();
                            }
                        });
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                })
                .build();
        dialogDeleteTask.show();
    }
}

