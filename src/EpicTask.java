import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subTaskIds;

    public EpicTask(String name, String description) {
        super(name, description);
        this.subTaskIds = new ArrayList<>();
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void addSubTaskId(int subTaskId) {
        if (!subTaskIds.contains(subTaskId)) {
            subTaskIds.add(subTaskId);
        }
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove((Integer) subTaskId);
    }

    @Override
    public void setStatus(Status status) {
        // Статус эпика нельзя изменить вручную
    }
    protected void forceUpdateStatus (Status status) {
        if (status == null) {
            super.setStatus(Status.NEW);
            return;
        }
        super.setStatus(status);
    }
}