import java.util.List;

public interface TaskManager {
    // Task methods
    Task addTask(Task task);

    List<Task> getAllTasks();

    Task getTaskById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    void deleteAllTasks();

    // Эпик методы
    EpicTask addEpicTask(EpicTask epicTask);

    List<EpicTask> getAllEpicTasks();

    EpicTask getEpicTaskById(int id);

    void updateEpicTask(EpicTask epicTask);

    void deleteEpicTaskById(int id);

    void deleteAllEpicTasks();

    // Сабтаск методы
    SubTask addSubTask(SubTask subTask);

    List<SubTask> getAllSubTasks();

    SubTask getSubTaskById(int id);

    void updateSubTask(SubTask subTask);

    void deleteSubTaskById(int id);

    void deleteAllSubTasks();

    List<SubTask> getSubTasksByEpicId(int epicId);

    List<Task> getHistory();
}










