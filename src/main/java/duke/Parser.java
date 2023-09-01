package duke;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The Parser class represents a parser that takes inputs and produces an output.
 */
public class Parser {

    /**
     * Processes and responds to the inputs received.
     *
     * @param input Input typed by user.
     * @param tasks List of tasks.
     * @param storage Stores the file and handles file methods.
     */
    public static void parseInput(String input, TaskList tasks, Storage storage) {
        try {
            if (input.equals("list")) {
                Ui.listOfTasks(tasks.showList());
            } else if (input.startsWith("todo ") || (input.startsWith("todo") && input.length() == 4)) {
                try {
                    if (input.length() <= 5 || input.substring(5).isBlank()) {
                        throw new EmptyDescriptionException();
                    }
                    Task task = new ToDo(input.substring(5));
                    tasks.add(task);
                    storage.rewrite(tasks.fileList());
                    Ui.addTask(task.toString(), tasks.size());
                } catch (EmptyDescriptionException e) {
                    Ui.emptyDesc("todo");
                }
            } else if (input.startsWith("deadline ") || (input.startsWith("deadline") && input.length() == 8)) {
                try {
                    if (input.length() <= 9 || input.substring(9).isBlank()) {
                        throw new EmptyDescriptionException();
                    }
                    String[] details = input.substring(9).split(" /by ");
                    if (details.length != 2) {
                        throw new DeadlineUnclearException();
                    }
                    LocalDateTime dateTime = LocalDateTime.parse(details[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
                    Task task = new Deadline(details[0], dateTime);
                    tasks.add(task);
                    storage.rewrite(tasks.fileList());
                    Ui.addTask(task.toString(), tasks.size());
                } catch (EmptyDescriptionException e) {
                    Ui.emptyDesc("deadline");
                } catch (DeadlineUnclearException e) {
                    Ui.unclear("deadline");
                } catch (DateTimeParseException e) {
                    Ui.wrongDateTimeFormat();
                }
            } else if (input.startsWith("event ") || (input.startsWith("event") && input.length() == 5)) {
                try {
                    if (input.length() <= 6 || input.substring(6).isBlank()) {
                        throw new EmptyDescriptionException();
                    }
                    String[] details = input.substring(6).split(" /from | /to ");
                    if (details.length != 3 || !input.contains(" /from ") || !input.contains(" /to ")) {
                        throw new DurationUnclearException();
                    }
                    LocalDateTime fromDateTime = LocalDateTime.parse(details[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
                    LocalDateTime toDateTime = LocalDateTime.parse(details[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
                    Task task = new Event(details[0], fromDateTime, toDateTime);
                    tasks.add(task);
                    storage.rewrite(tasks.fileList());
                    Ui.addTask(task.toString(), tasks.size());
                } catch (EmptyDescriptionException e) {
                    Ui.emptyDesc("event");
                } catch (DurationUnclearException e) {
                    Ui.unclear("duration");
                } catch (DateTimeParseException e) {
                    Ui.wrongDateTimeFormat();
                }
            } else if (input.startsWith("mark ") && input.length() > 5 && input.substring(5).matches("\\d+")) {
                try {
                    int number = Integer.parseInt(input.substring(5));
                    if (number > tasks.size() || number <= 0) {
                        throw new InvalidTaskException();
                    }
                    Task task = tasks.get(number - 1);
                    task.setMark(true);
                    storage.rewrite(tasks.fileList());
                    Ui.mark(task);
                } catch (InvalidTaskException e) {
                    Ui.invalidTask();
                }
            } else if (input.startsWith("unmark ") && input.length() > 7 && input.substring(7).matches("\\d+")) {
                try {
                    int number = Integer.parseInt(input.substring(7));
                    if (number > tasks.size() || number <= 0) {
                        throw new InvalidTaskException();
                    }
                    Task task = tasks.get(number - 1);
                    task.setMark(false);
                    storage.rewrite(tasks.fileList());
                    Ui.unmark(task);
                } catch (InvalidTaskException e) {
                    Ui.invalidTask();
                }
            } else if (input.startsWith("delete ") && input.length() > 7 && input.substring(7).matches("\\d+")) {
                try {
                    int number = Integer.parseInt(input.substring(7));
                    if (number > tasks.size() || number <= 0) {
                        throw new InvalidTaskException();
                    }
                    Task task = tasks.delete(number - 1);
                    storage.rewrite(tasks.fileList());
                    Ui.delete(task, tasks.size());
                } catch (InvalidTaskException e) {
                    Ui.invalidTask();
                }
            } else {
                throw new InvalidTextException();
            }
        } catch (InvalidTextException e) {
            Ui.invalidText();
        }
    }
}
