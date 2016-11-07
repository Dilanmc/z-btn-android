package spalmalo.z_btn;

/**
 * Created by dilan on 11/5/16.
 */

public interface TaskClickListener {
    void started(String taskId);

    void stopped(String taskId);

    void finished(String  taskId);

    void longClick(String taskId);
}
