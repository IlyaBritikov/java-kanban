package test;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TaskManagerTest {
    private final InMemoryTaskManager manager = new InMemoryTaskManager();
    private final HistoryManager historyManager = manager.getHistoryManager();
    @Test
    void shouldAddTasksToHistory() {
        Task task = manager.addTask(new Task("Task", "Description"));
        EpicTask epic = manager.addEpicTask(new EpicTask("Epic", "Description"));
        SubTask subTask = manager.addSubTask(new SubTask("Sub", "Desc", epic.getId()));

        // Вызываем методы, которые должны добавлять в историю
        manager.getTaskById(task.getId());
        manager.getEpicTaskById(epic.getId());
        manager.getSubTaskById(subTask.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subTask, history.get(2));

    }

    @Test
    void historyShouldNotExceedMaxSize() {
        for (int i = 0; i < 15; i++) {
            Task task = manager.addTask(new Task("Task " + i, "Description"));
            manager.getTaskById(task.getId());
        }

        assertEquals(10, historyManager.getHistory().size());
    }
    @Test
    void shouldNotAddNullTaskToHistory() {
        manager.getTaskById(999); // Несуществующий ID
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldUpdateEpicStatusAutomatically() {
        EpicTask epic = manager.addEpicTask(new EpicTask("Epic", ""));
        SubTask subTask = manager.addSubTask(new SubTask("Sub", "", epic.getId()));

        // Начальный статус
        assertEquals(Status.NEW, epic.getStatus());

        // Изменяем статус подзадачи
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        // Завершаем подзадачу
        subTask.setStatus(Status.DONE);
        manager.updateSubTask(subTask);
        assertEquals(Status.DONE, epic.getStatus());
    }
}
