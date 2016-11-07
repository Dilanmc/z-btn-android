package spalmalo.z_btn.models;


import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private String status;
    private TaskSession lastSession;

    public Task() {
        id = UUID.randomUUID().toString();
    }

    public TaskSession getLastSession() {
        return lastSession;
    }

    public void setLastSession(TaskSession lastSession) {
        this.lastSession = lastSession;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
