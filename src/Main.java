import manager.Managers;
import manager.TaskManager;
import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;


public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        System.out.println("=== Тестирование методов для обычных задач ===");
        // Создание задач
        Task task1 = manager.addTask(new Task("Покупки", "Купить молоко и хлеб"));
        Task task2 = manager.addTask(new Task("Звонок", "Позвонить другу"));
        System.out.println("Создано 2 задачи");

        // Получение всех задач
        System.out.println("\nВсе задачи:");
        System.out.println(manager.getAllTasks());

        // Получение задачи по ID
        System.out.println("\nПолученная задача по ID " + task1.getId() + ": " +
                manager.getTaskById(task1.getId()));

        // Обновление задачи
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.println("\nПосле обновления статуса:");
        System.out.println(manager.getTaskById(task1.getId()));

        // Удаление задачи по ID
        manager.deleteTaskById(task2.getId());
        System.out.println("\nПосле удаления задачи ID " + task2.getId() +
                ", осталось задач: " + manager.getAllTasks().size());

        // Удаление всех задач
        manager.deleteAllTasks();
        System.out.println("После удаления всех задач, осталось: " +
                manager.getAllTasks().size());

        System.out.println("\n=== Тестирование методов для эпиков и подзадач ===");
        // Создание эпиков
        EpicTask epic1 = manager.addEpicTask(new EpicTask("Переезд", "Организация переезда"));
        EpicTask epic2 = manager.addEpicTask(new EpicTask("Отпуск", "Планирование отпуска"));
        System.out.println("Создано 2 эпика");

        // Создание подзадач
        SubTask subTask1 = manager.addSubTask(new SubTask("Упаковать вещи", "Коробки, скотч", epic1.getId()));
        SubTask subTask2 = manager.addSubTask(new SubTask("Найти грузчиков", "Договориться о дате", epic1.getId()));
        SubTask subTask3 = manager.addSubTask(new SubTask("Купить билеты", "Выбрать авиакомпанию", epic2.getId()));
        System.out.println("Создано 3 подзадачи");

        // Получение всех эпиков
        System.out.println("\nВсе эпики:");
        System.out.println(manager.getAllEpicTasks());

        // Получение эпика по ID
        System.out.println("\nПолученный эпик по ID " + epic1.getId() + ": " +
                manager.getEpicTaskById(epic1.getId()));

        // Обновление эпика
        epic1.setName("Большой переезд");
        epic1.setDescription("Полная организация переезда");
        manager.updateEpicTask(epic1);
        System.out.println("\nПосле обновления эпика:");
        System.out.println(manager.getEpicTaskById(epic1.getId()));

        // Получение всех подзадач
        System.out.println("\nВсе подзадачи:");
        System.out.println(manager.getAllSubTasks());

        // Получение подзадачи по ID
        System.out.println("\nПолученная подзадача по ID " + subTask1.getId() + ": " +
                manager.getSubTaskById(subTask1.getId()));

        // Обновление подзадачи
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);
        System.out.println("\nПосле обновления подзадачи:");
        System.out.println(manager.getSubTaskById(subTask1.getId()));
        System.out.println("Статус эпика после изменения подзадачи: " + epic1.getStatus());

        // Получение подзадач эпика
        System.out.println("\nПодзадачи эпика '" + epic1.getName() + "':");
        System.out.println(manager.getSubTasksByEpicId(epic1.getId()));

        // Проверка автоматического обновления статуса эпика
        System.out.println("\nПроверка обновления статуса эпика:");
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        System.out.println("Статус эпика после завершения всех подзадач: " + epic1.getStatus());

        // Попытка изменить статус эпика вручную
        System.out.println("\nПопытка изменить статус эпика вручную:");
        epic1.setStatus(Status.IN_PROGRESS);
        System.out.println("Статус эпика после попытки изменения: " + epic1.getStatus());

        System.out.println("\n=== Тестирование методов удаления ===");
        // Удаление подзадачи по ID
        manager.deleteSubTaskById(subTask3.getId());
        System.out.println("После удаления подзадачи ID " + subTask3.getId() +
                ", осталось подзадач: " + manager.getAllSubTasks().size());

        // Удаление эпика по ID (должны удалиться и его подзадачи)
        manager.deleteEpicTaskById(epic1.getId());
        System.out.println("После удаления эпика ID " + epic1.getId() + ":");
        System.out.println("Осталось эпиков: " + manager.getAllEpicTasks().size());
        System.out.println("Осталось подзадач: " + manager.getAllSubTasks().size());

        // Удаление всех подзадач
        manager.deleteAllSubTasks();
        System.out.println("\nПосле удаления всех подзадач, осталось: " +
                manager.getAllSubTasks().size());

        // Удаление всех эпиков
        manager.deleteAllEpicTasks();
        System.out.println("После удаления всех эпиков, осталось: " +
                manager.getAllEpicTasks().size());

        System.out.println("\n=== Проверка граничных случаев ===");
        // Попытка получить несуществующую задачу
        System.out.println("Попытка получить несуществующую задачу: " +
                manager.getTaskById(999));

        // Попытка обновить несуществующую задачу
        Task nonExistentTask = new Task("Несуществующая", "Нет описания");
        nonExistentTask.setId(999);
        manager.updateTask(nonExistentTask);
        System.out.println("Попытка обновить несуществующую задачу (без ошибок)");

        // Попытка добавить подзадачу к несуществующему эпику
        SubTask invalidSubTask = manager.addSubTask(new SubTask("Невалидная", "Нет эпика", 999));
        System.out.println("Попытка добавить подзадачу к несуществующему эпику: " + invalidSubTask);
    }
}