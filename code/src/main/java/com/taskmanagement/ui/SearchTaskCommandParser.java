package com.taskmanagement.ui;

import com.taskmanagement.command.Command;
import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.Status;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.search.DateRangeCriterion;
import com.taskmanagement.search.DayOfWeekCriterion;
import com.taskmanagement.search.KeywordCriterion;
import com.taskmanagement.search.PriorityCriterion;
import com.taskmanagement.search.SearchCommand;
import com.taskmanagement.search.SearchCriterion;
import com.taskmanagement.search.StatusCriterion;
import com.taskmanagement.search.TagCriterion;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses CLI arguments for search-task command.
 */
public class SearchTaskCommandParser {

    public Command parse(String args) {
        String normalizedArgs = (args == null || args.trim().isEmpty()) ? "status OPEN" : args;

        SearchCommand searchCommand = new SearchCommand(TaskCatalog.getInstance());
        for (SearchCriterion criterion : parseCriteria(normalizedArgs)) {
            searchCommand.addCriterion(criterion);
        }

        return new Command() {
            @Override
            public void execute() {
                searchCommand.execute();
                List<Task> results = searchCommand.getSearchResults();
                printResults(results);
            }
        };
    }

    private List<SearchCriterion> parseCriteria(String args) {
        List<SearchCriterion> criteria = new ArrayList<>();
        String[] criterionParts = args.split("\\|");

        for (String rawCriterion : criterionParts) {
            String criterionText = rawCriterion.trim();
            if (criterionText.isEmpty()) {
                continue;
            }

            String[] parts = criterionText.split("\\s+", 2);
            String mode = parts[0].trim().toLowerCase();
            String modeArgs = parts.length > 1 ? parts[1].trim() : "";
            criteria.add(parseCriterion(mode, modeArgs));
        }

        if (criteria.isEmpty()) {
            throw new IllegalArgumentException(getUsage());
        }

        return criteria;
    }

    private SearchCriterion parseCriterion(String mode, String modeArgs) {
        switch (mode) {
            case "keyword":
                requireArgs(modeArgs, "keyword");
                return new KeywordCriterion(modeArgs);

            case "tag":
                requireArgs(modeArgs, "tag");
                return new TagCriterion(modeArgs);

            case "status":
                requireArgs(modeArgs, "status");
                try {
                    return new StatusCriterion(Status.valueOf(modeArgs.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Invalid status. Valid values: OPEN, COMPLETED, CANCELLED");
                }

            case "priority":
                requireArgs(modeArgs, "priority");
                try {
                    return new PriorityCriterion(Priority.valueOf(modeArgs.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Invalid priority. Valid values: LOW, MEDIUM, HIGH");
                }

            case "date":
                requireArgs(modeArgs, "date");
                LocalDate exactDate = parseDate(modeArgs);
                return new DateRangeCriterion(exactDate, exactDate);

            case "date-range":
                String[] rangeParts = modeArgs.split("\\s+");
                if (rangeParts.length != 2) {
                    throw new IllegalArgumentException("Usage: search-task date-range <from-yyyy-MM-dd> <to-yyyy-MM-dd>");
                }
                LocalDate fromDate = parseDate(rangeParts[0]);
                LocalDate toDate = parseDate(rangeParts[1]);
                return new DateRangeCriterion(fromDate, toDate);

            case "weekday":
                requireArgs(modeArgs, "weekday");
                return new DayOfWeekCriterion(parseWeekday(modeArgs));

            default:
                throw new IllegalArgumentException(getUsage());
        }
    }

    private void printResults(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No matching tasks found.");
            return;
        }

        System.out.println("Found " + tasks.size() + " task(s):");
        int idWidth = 8;
        int titleWidth = 30;
        int statusWidth = 10;
        int dueDateWidth = 12;
        int priorityWidth = 10;

        String divider = "+" + repeat("-", idWidth + 2)
                + "+" + repeat("-", titleWidth + 2)
                + "+" + repeat("-", statusWidth + 2)
                + "+" + repeat("-", dueDateWidth + 2)
                + "+" + repeat("-", priorityWidth + 2)
                + "+";

        System.out.println(divider);
        System.out.println("| " + pad("ID", idWidth)
                + " | " + pad("Title", titleWidth)
                + " | " + pad("Status", statusWidth)
                + " | " + pad("Due Date", dueDateWidth)
                + " | " + pad("Priority", priorityWidth)
                + " |");
        System.out.println(divider);

        for (Task task : tasks) {
            System.out.println("| " + pad(safe(task.getId()), idWidth)
                    + " | " + pad(safe(task.getTitle()), titleWidth)
                    + " | " + pad(safe(task.getStatus() != null ? task.getStatus().name() : null), statusWidth)
                    + " | " + pad(safe(task.getDueDate() != null ? task.getDueDate().toString() : null), dueDateWidth)
                    + " | " + pad(safe(task.getPriority() != null ? task.getPriority().name() : null), priorityWidth)
                    + " |");
        }

        System.out.println(divider);
    }

    private void requireArgs(String modeArgs, String mode) {
        if (modeArgs == null || modeArgs.trim().isEmpty()) {
            throw new IllegalArgumentException("Usage: search-task " + mode + " <value>");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd (example: 2026-04-02)");
        }
    }

    private int parseWeekday(String value) {
        String normalized = value.trim().toLowerCase();
        switch (normalized) {
            case "1":
            case "sun":
            case "sunday":
                return 1;
            case "2":
            case "mon":
            case "monday":
                return 2;
            case "3":
            case "tue":
            case "tuesday":
                return 3;
            case "4":
            case "wed":
            case "wednesday":
                return 4;
            case "5":
            case "thu":
            case "thursday":
                return 5;
            case "6":
            case "fri":
            case "friday":
                return 6;
            case "7":
            case "sat":
            case "saturday":
                return 7;
            default:
                throw new IllegalArgumentException("Invalid weekday. Use 1-7 or day name (sun..sat)");
        }
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String pad(String value, int width) {
        String text = value == null ? "-" : value;
        if (text.length() > width) {
            if (width <= 1) {
                return text.substring(0, width);
            }
            return text.substring(0, width - 1) + "~";
        }
        return text + repeat(" ", width - text.length());
    }

    private String repeat(String text, int count) {
        if (count <= 0) {
            return "";
        }
        return String.valueOf(text).repeat(count);
    }

    private String getUsage() {
        return "Usage: search-task [<mode> <args> [| <mode> <args> ...]]\n" +
            "No args: defaults to status OPEN\n" +
            "Modes:\n" +
            "  keyword <text>\n" +
            "  tag <tag-name>\n" +
            "  status <OPEN|COMPLETED|CANCELLED>\n" +
            "  priority <LOW|MEDIUM|HIGH>\n" +
            "  date <yyyy-MM-dd>\n" +
            "  date-range <from-yyyy-MM-dd> <to-yyyy-MM-dd>\n" +
            "  weekday <1-7|sun|mon|...|sat>\n" +
            "Example:\n" +
            "  search-task keyword report | status OPEN | date-range 2026-04-01 2026-04-30";
    }
}
