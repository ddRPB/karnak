package org.karnak.backend.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateFormat {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateFormat.class);

    public static String formatDA(String date, String option) {
        LocalDate localDate = DateTimeUtils.parseDA(date);

        switch (option) {
            case "day" :
                localDate = localDate.minusDays(localDate.getDayOfMonth() - 1L);
                break;
            case "month_day" :
                localDate = localDate.minusDays(localDate.getDayOfMonth() - 1L);
                localDate = localDate.minusMonths(localDate.getMonthValue() - 1L);
        }

        String newLocalDate = localDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        return newLocalDate;
    }



    public static String formatDT(String dateTime, String option) {

        LocalDateTime localDateTime = LocalDateTime.from(DateTimeUtils.parseDT(dateTime));

        switch (option) {
            case "day" :
                localDateTime = localDateTime.minusDays(localDateTime.getDayOfMonth() - 1L);
                break;
            case "month_day" :
                localDateTime = localDateTime.minusDays(localDateTime.getDayOfMonth() - 1L);
                localDateTime = localDateTime.minusMonths(localDateTime.getMonthValue() - 1L);
        }

        String newLocalDate = DateTimeUtils.formatDT(localDateTime);
        return newLocalDate;
    }

    public static String format(DicomObject dcm, DicomElement dcmEl,
        List<ArgumentEntity> argumentEntities) {
        try {
            verifyPatternArguments(argumentEntities);
        } catch (IllegalArgumentException e) {
            throw e;
        }

        String dcmElValue = dcm.getString(dcmEl.tag()).orElse(null);
        String format = "";

        for (ArgumentEntity argumentEntity : argumentEntities) {
            final String key = argumentEntity.getKey();
            final String value = argumentEntity.getValue();

            try {
                if (key.equals("remove")) {
                    format = value;
                }
            } catch (Exception e) {
                LOGGER.error("args {} is not correct", value, e);
            }
        }
        if (dcmElValue != null) {
            return switch (dcmEl.vr()) {
                case DA -> formatDA(dcmElValue, format);
                case DT -> formatDT(dcmElValue, format);
                default -> null;
            };
        } else {
            return null;
        }
    }

    public static void verifyPatternArguments(List<ArgumentEntity> argumentEntities)
        throws IllegalArgumentException {
        List<String> listValue = new ArrayList<>();
        listValue.add("day");
        listValue.add("month_day");

        if (!argumentEntities.stream().anyMatch(
            argument -> argument.getKey().equals("remove") && listValue
                .contains(argument.getValue()))) {
            IllegalArgumentException missingParameters = new IllegalArgumentException(
                "Cannot build the option date_format, arguments are not correct"
            );
            LOGGER
                .error("Missing argument, the class need pattern as parameters", missingParameters);
            throw missingParameters;
        }
    }
}