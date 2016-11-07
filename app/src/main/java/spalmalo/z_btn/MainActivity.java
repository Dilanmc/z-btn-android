package spalmalo.z_btn;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import spalmalo.z_btn.adapters.ListTasksAdapter;
import spalmalo.z_btn.models.Task;
import spalmalo.z_btn.models.TaskSession;

public class MainActivity extends AppCompatActivity implements TaskClickListener {

    private ListTasksAdapter tasksAdapter;
    private ListTasksAdapter finishedTasksAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagerFinished;
    private MaterialDialog dialog, dialogDeleteTask;
    private InputMethodManager inputMgr;
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
        setupRecyclerView();

        finishedTaskText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finishedTasksAdapter.getItemCount() == 0) {
                    Toast.makeText(MainActivity.this, R.string.show_finish_task, Toast.LENGTH_SHORT).show();
                }
                listFinishedTask.setVisibility(listFinishedTask.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (listFinishedTask.getVisibility() == View.VISIBLE) {
                    finishedTaskText.setText(R.string.closeFinishedText);
                } else {
                    finishedTaskText.setText(R.string.show_finish_task);
                }
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
        tasksAdapter = new ListTasksAdapter(realm.where(Task.class)
                .equalTo("status", Constants.TASK_STATUS_STARTED)
                .or()
                .equalTo("status", Constants.TASK_STATUS_STOPPED)
                .findAllAsync(), this);
        tasksList.setAdapter(tasksAdapter);
        tasksList.setHasFixedSize(true);

        mLayoutManagerFinished = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        listFinishedTask.setLayoutManager(mLayoutManagerFinished);
        finishedTasksAdapter = new ListTasksAdapter(realm.where(Task.class)
                .equalTo("status", Constants.TASK_STATUS_FINISHED)
                .findAll(), this);
        listFinishedTask.setAdapter(finishedTasksAdapter);
        listFinishedTask.setHasFixedSize(true);
    }

    private void setupDialogTask() {
        dialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Create new task")
                .customView(R.layout.item_task_text, Boolean.parseBoolean(null))
                .positiveText("Create")
                .negativeText("Cancel")
                .positiveColor(Color.rgb(230, 187, 0))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addNewTask(taskText.getText().toString());
                        tasksAdapter.notifyDataSetChanged();
                        inputMgr = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMgr.hideSoftInputFromWindow(taskText.getWindowToken(), 0);
                    }

                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        inputMgr = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMgr.hideSoftInputFromWindow(taskText.getWindowToken(), 0);
                        dialog.cancel();
                    }
                })
                .build();
        taskText = (EditText) dialog.findViewById(R.id.edit_task_text);

        taskText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputMgr = (InputMethodManager) getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        taskText.requestFocus();
        dialog.show();
    }

    private void addNewTask(final String title) {
        realm.beginTransaction();
        final Task task = new Task();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void started(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task startedTask = realm.where(Task.class).equalTo("status", Constants.TASK_STATUS_STARTED).findFirst();
                if (startedTask != null) {
                    startedTask.setStatus(Constants.TASK_STATUS_STOPPED);
                    startedTask.getLastSession().setEndDate(new Date());
                    startedTask.setLastSession(null);
                }
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setStatus(Constants.TASK_STATUS_STARTED);
                TaskSession taskSession = realm.copyToRealm(new TaskSession());
                taskSession.setTaskId(taskId);
                taskSession.setStartDate(new Date());
                task.setLastSession(taskSession);
            }
        });
    }

    @Override
    public void stopped(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setStatus(Constants.TASK_STATUS_STOPPED);
                task.getLastSession().setEndDate(new Date());
                task.setLastSession(null);
            }
        });
    }

    @Override
    public void finished(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                if (task.getLastSession() != null) {
                    task.getLastSession().setEndDate(new Date());
                    task.setLastSession(null);
                }
                task.setStatus(task.getStatus().equals(Constants.TASK_STATUS_FINISHED) ? Constants.TASK_STATUS_STOPPED : Constants.TASK_STATUS_FINISHED);
            }
        });

    }

    @Override
    public void longClick(final String taskId) {
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

