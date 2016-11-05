package spalmalo.z_btn;

/**
 * Created by dilan on 11/5/16.
 */

public interface TaskClickListener {
    void started(int taskId);

    void stopped(int taskId);

    void finished(int taskId);

    void longClick(int taskId);
}
