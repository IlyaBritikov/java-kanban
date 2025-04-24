import java.util.ArrayList;
import java.util.*;



public class InMemoryTaskManager  implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;
    private int nextId = 1;


    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }


    // Task methods
    @Override
    public Task addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Эпик методы
    @Override
    public EpicTask addEpicTask(EpicTask epicTask) {
        epicTask.setId(nextId++);
        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask;
    }

    @Override
    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        EpicTask savedEpic = epicTasks.get(epicTask.getId());
        if (savedEpic != null) {
            savedEpic.setName(epicTask.getName());
            savedEpic.setDescription(epicTask.getDescription());
        }
    }

    @Override
    public void deleteEpicTaskById(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
        }
    }

    @Override
    public void deleteAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
    }

    // Сабтаск методы
    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (epicTasks.containsKey(subTask.getEpicId())) {
            subTask.setId(nextId++);
            subTasks.put(subTask.getId(), subTask);
            epicTasks.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
            updateEpicStatus(subTask.getEpicId());
            return subTask;
        }
        return null;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            SubTask savedSubTask = subTasks.get(subTask.getId());
            if (savedSubTask.getEpicId() != subTask.getEpicId()) {
                epicTasks.get(savedSubTask.getEpicId()).removeSubTaskId(savedSubTask.getId());
                epicTasks.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
            }
            savedSubTask.setName(subTask.getName());
            savedSubTask.setDescription(subTask.getDescription());
            savedSubTask.setStatus(subTask.getStatus());
            updateEpicStatus(subTask.getEpicId());
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            epicTasks.get(subTask.getEpicId()).removeSubTaskId(id);
            updateEpicStatus(subTask.getEpicId());
        }
    }

    @Override
    public void deleteAllSubTasks() {
        for (EpicTask epic : epicTasks.values()) {
            epic.getSubTaskIds().clear();
            epic.setStatus(Status.NEW);
        }
        subTasks.clear();
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(int epicId) {
        List<SubTask> result = new ArrayList<>();
        EpicTask epic = epicTasks.get(epicId);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                result.add(subTasks.get(subTaskId));
            }
        }
        return result;
    }

    @Override
    public void updateEpicStatus(int epicId) {
        EpicTask epic = epicTasks.get(epicId);
        if (epic == null) {
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask == null) continue;

            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.forceUpdateStatus(Status.DONE);
        } else if (allNew) {
            epic.forceUpdateStatus(Status.NEW);
        } else {
            epic.forceUpdateStatus(Status.IN_PROGRESS);
        }
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
