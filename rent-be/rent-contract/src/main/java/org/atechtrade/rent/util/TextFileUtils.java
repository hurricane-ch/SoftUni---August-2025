package org.atechtrade.rent.util;

import org.atechtrade.rent.exception.ErrorObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextFileUtils {

    private TextFileUtils() {
        // empty constructor, because this is "utility" class with only static methods
    }

    private static final String TAB_DELIMITER = "\\t";

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final Consumer<List<String>> lineConsumer,
            final List<ErrorObject> objectErrors
    ) {
        processFileByLine(
                filename,
                in,
                delimiter,
                0,
                null,
                lineConsumer,
                objectErrors
        );
    }

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final List<String> headerNames,
            final Consumer<List<String>> lineConsumer,
            final List<ErrorObject> objectErrors
    ) {
        processFileByLine(
                filename,
                in,
                delimiter,
                0,
                headerNames,
                lineConsumer,
                objectErrors
        );
    }

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final int limit,
            final Consumer<List<String>> lineConsumer,
            final List<ErrorObject> objectErrors
    ) {
        processFileByLine(
                filename,
                in,
                delimiter,
                limit,
                null,
                lineConsumer,
                objectErrors
        );
    }

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final int limit,
            final List<String> headerNames,
            final Consumer<List<String>> lineConsumer,
            final List<ErrorObject> objectErrors
    ) {
        if (null == in || null == lineConsumer) {
            return;
        }
        try (final Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            while (scanner.hasNext()) {
                final String originalLine = scanner.nextLine();
                if (originalLine != null && !originalLine.startsWith("#")) {
                    final List<String> line = Stream.of(originalLine.split(delimiter, limit)).map(s -> {
                        if (TAB_DELIMITER.equals(delimiter)) {
                            return s;
                        }
                        return s.replaceAll("\"", "");
                    }).collect(Collectors.toList());

                    final long notEmptyItemsCount = line.stream()
                            .filter(item -> !item.trim().isEmpty())
                            .distinct()
                            .count();

                    if (originalLine.isEmpty()
                            || null != headerNames && headerNames.equals(line)
                            || notEmptyItemsCount < 1) {
                        continue;
                    }

                    try {
                        lineConsumer.accept(line);
                    } catch (IllegalArgumentException ex) {
                        objectErrors.add(new ErrorObject(filename, ex.getClass().getName(),
                                ex.getMessage() + " | " + originalLine));
                        throw new IllegalArgumentException(
                                ex.getMessage() + "\n"
                                + " filename=" + filename + "\n"
                                + " delimiter=" + delimiter + "\n"
                                + " original line=" + originalLine,
                                ex
                        );
                    }
                }
            }
        }
    }

    public static String valueAtIndex(final List<String> line, int index) {
        return (null != line && line.size() > index) ? line.get(index).trim() : "";
    }
}
