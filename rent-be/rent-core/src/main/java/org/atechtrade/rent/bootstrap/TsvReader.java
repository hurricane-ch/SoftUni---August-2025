package org.atechtrade.rent.bootstrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.exception.ErrorObject;
import org.atechtrade.rent.model.MessageResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.atechtrade.rent.util.TextFileUtils.processFileByLine;
import static org.atechtrade.rent.util.TextFileUtils.valueAtIndex;

@Slf4j
public class TsvReader {

    public static final String TAB_DELIMITER = "\\t";

    private TsvReader() {
        // empty constructor, this is "utility" class with only static methods
    }

    public static List<TsvMessageResource> tsvMessageResourceList(
            final Class clazz, final String filename, final List<ErrorObject> objectErrors) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvMessageResourceList(is, filename, objectErrors);
    }

    public static List<TsvMessageResource> tsvMessageResourceList(final InputStream inputStream, final String filename, final List<ErrorObject> objectErrors) {
        final List<TsvMessageResource> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Code",
                        "Language ID",
                        "Message"
                ),
                line -> {
                    final String code = valueAtIndex(line, 0);
                    final String languageId = valueAtIndex(line, 1);
                    final String message = valueAtIndex(line, 2);

                    list.add(TsvMessageResource.builder()
                            .code(code)
                            .languageId(languageId)
                            .message(message).build());
                }, objectErrors);

        return list;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvMessageResource {
        private final String code, languageId, message;

        public static MessageResource to(TsvMessageResource tsv) {
            return MessageResource.builder()
                    .messageResourceIdentity(new MessageResource.MessageResourceIdentity(tsv.getCode(), tsv.getLanguageId()))
                    .message(tsv.getMessage()).build();
        }

        public static List<MessageResource> to(final List<TsvMessageResource> list) {
            return list.stream().map(TsvMessageResource::to).collect(Collectors.toList());
        }
    }
}