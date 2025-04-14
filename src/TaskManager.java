import java.util.ArrayList;
import java.util.*;



public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int nextId = 1;

    // Task methods
    public Task addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    // Эпик методы
    public EpicTask addEpicTask(EpicTask epicTask) {
        epicTask.setId(nextId++);
        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask;
    }

    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public EpicTask getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public void updateEpicTask(EpicTask epicTask) {
        EpicTask savedEpic = epicTasks.get(epicTask.getId());
        if (savedEpic != null) {
            savedEpic.setName(epicTask.getName());
            savedEpic.setDescription(epicTask.getDescription());
        }
    }

    public void deleteEpicTaskById(int id) {
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
        }
    }

    public void deleteAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
    }

    // Сабтаск методы
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

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

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

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            epicTasks.get(subTask.getEpicId()).removeSubTaskId(id);
            updateEpicStatus(subTask.getEpicId());
        }
    }

    public void deleteAllSubTasks() {
        for (EpicTask epic : epicTasks.values()) {
            epic.getSubTaskIds().clear();
            epic.setStatus(Status.NEW);
        }
        subTasks.clear();
    }

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

    private void updateEpicStatus(int epicId) {
        EpicTask epic = epicTasks.get(epicId);
        if (epic == null || epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
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
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
