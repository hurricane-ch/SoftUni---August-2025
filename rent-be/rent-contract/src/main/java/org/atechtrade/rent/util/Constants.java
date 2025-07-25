package org.atechtrade.rent.util;

import org.atechtrade.rent.enums.SettlementType;
import org.atechtrade.rent.model.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Constants {

    public static final String DEFAULT_DATE_PATTERN = "dd.MM.yyyy";
    public static final String DEFAULT_DATETIME_PATTERN = "dd.MM.yyyy HH:mm:ss";

    public static final String APPLICATION_JSON_METADATA_KEY = "application.json_json";
    public static final String APPLICATION_XML_METADATA_KEY = "application.json_xml";

    public static final String TOKEN_INVALID = "INVALID";
    public static final String TOKEN_EXPIRED = "EXPIRED";
    public static final String TOKEN_VALID = "VALID";
    public final static String SUCCESS = "success";

    public static final String SUCCESSFULLY_CREATED = "The CREATE has been completed successfully";
    public static final String SUCCESSFULLY_UPDATED = "The UPDATE has been completed successfully";
    public static final String SUCCESSFULLY_DELETED = "The DELETE has been completed successfully";
    public static final String SUCCESS_MSG = "The {0} request has been completed successfully";


    // --- CLASSIFIERS ---

    public static final String ADDRESS_TYPE_CODE = "0001000";
    public static final String ADDRESS_TYPE_HEAD_OFFICE_CODE = "0001001";
    public static final String ADDRESS_TYPE_PERMANENT_ADDRESS_CODE = "0001002";
    public static final String ADDRESS_TYPE_CORRESPONDENCE_CODE = "0001003";


    public static final String FILE_TYPE_CODE = "0002000";


    public static final String DOCUMENT_TYPE_CODE = "0003000";
    public static final String DOCUMENT_TYPE_ORDINANCE_CODE = "0003001";
    public static final String DOCUMENT_TYPE_PROTOCOL_CODE = "0003002";
    public static final String DOCUMENT_TYPE_CERTIFICATE_CODE = "0003003";
    public static final String DOCUMENT_TYPE_AUTHORIZATION_CODE = "0003004";
    public static final String DOCUMENT_TYPE_DIPLOMA_CODE = "0003005";
    public static final String DOCUMENT_TYPE_APPLICATION_CODE = "0003006";
    public static final String DOCUMENT_TYPE_ATTACHMENT_CODE = "0003007";
    public static final String DOCUMENT_TYPE_EXTERNAL_ATTACHMENT_CODE = "0003008";


    public static final String PERSONAL_DOCUMENT_TYPE_CODE = "0004000";


    public static final String SETTLEMENT_TYPE_ABBR_CODE = "0005000";
    public static final String SETTLEMENT_TYPE_ABBR_CITY_CODE = "0005001";
    public static final String SETTLEMENT_TYPE_ABBR_VILLAGE_CODE = "0005002";
    public static final String SETTLEMENT_TYPE_ABBR_RESORT_CODE = "0005003";
    public static final String SETTLEMENT_TYPE_ABBR_MONASTERY_CODE = "0005004";
    public static final String SETTLEMENT_TYPE_ABBR_AREA_CODE = "0005005";
    public static final String SETTLEMENT_TYPE_ABBR_REGION_CODE = "0005006";
    public static final String SETTLEMENT_TYPE_ABBR_MUNICIPALITIES_CODE = "0005007";


    // Видове проверки на лице за статус на регистрация
    public static final String REGISTRATION_STATUS_CHECK_TYPE = "0013000";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_OTHER_COUNTRY_NRA_CONCLUSION_CHANGE_EMPLOYER_CODE = "0013002";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_OTHER_COUNTRY_NRA_INSURED_UNDER_H13_CODE = "0013003";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_OTHER_COUNTRY_AB_INDIVIDUAL_PARTICIPATION_COMPANIES_CODE = "0013004";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_OTHER_COUNTRY_AB_STATUS_BULSTAT_CODE = "0013005";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_OTHER_COUNTRY_MZH_AGRICULTURAL_CODE = "0013006";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_QUALIFICATION_DEGREE_NACID_CODE = "0013007";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_EDUCATIONAL_DEGREE_MES_CODE = "0013008";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_PENSION_68A_CODE = "0013009";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_OTHER_DIRECTORATE_CODE = "0013011";
    public static final String REGISTRATION_STATUS_CHECK_TYPE_JOB_SERVICE_INTEND_CODE = "0013012";


    // Причина за регистрацията
    public static final String REGISTRATION_REASON_CODE = "0014000";
    public static final String REGISTRATION_REASON_MASS_DISMISSAL_CODE = "0014001";



    // Вид тема на групово събитие
    public static final String GROUP_EVENT_THEME_TYPE_CODE = "0015000";


    public static final String STATUTORY_REGULATION_CODE = "0024000";
    public static final String STATUTORY_REGULATION_MINIMUM_WAGE_CODE = "0024001";

    private static final Map<SettlementType, Map<String, String>> settlementAbbreviations = new HashMap<>();

    static {
        Map<String, String> cityAbbreviations = new HashMap<>();
        cityAbbreviations.put(Language.BG, "гр.");
        cityAbbreviations.put(Language.EN, "c.");
        settlementAbbreviations.put(SettlementType.CITY, cityAbbreviations);

        Map<String, String> villageAbbreviations = new HashMap<>();
        villageAbbreviations.put(Language.BG, "с.");
        villageAbbreviations.put(Language.EN, "vlg.");
        settlementAbbreviations.put(SettlementType.VILLAGE, villageAbbreviations);

        Map<String, String> resortAbbreviations = new HashMap<>();
        resortAbbreviations.put(Language.BG, "к.с.");
        resortAbbreviations.put(Language.EN, "res.");
        settlementAbbreviations.put(SettlementType.RESORT, resortAbbreviations);

        Map<String, String> monasteryAbbreviations = new HashMap<>();
        monasteryAbbreviations.put(Language.BG, "ман.");
        monasteryAbbreviations.put(Language.EN, "mon.");
        settlementAbbreviations.put(SettlementType.MONASTERY, monasteryAbbreviations);

        Map<String, String> areaAbbreviations = new HashMap<>();
        areaAbbreviations.put(Language.BG, "район");
        areaAbbreviations.put(Language.EN, "area");
        settlementAbbreviations.put(SettlementType.AREA, areaAbbreviations);

        Map<String, String> regionAbbreviations = new HashMap<>();
        regionAbbreviations.put(Language.BG, "обл.");
        regionAbbreviations.put(Language.EN, "reg.");
        settlementAbbreviations.put(SettlementType.REGION, regionAbbreviations);

        Map<String, String> municipalityAbbreviations = new HashMap<>();
        municipalityAbbreviations.put(Language.BG, "общ.");
        municipalityAbbreviations.put(Language.EN, "mun.");
        settlementAbbreviations.put(SettlementType.MUNICIPALITY, municipalityAbbreviations);
    }

    public static String getSettlementAbbreviation(SettlementType settlementType, String languageId) {
        Map<String, String> translationMap = settlementAbbreviations.get(settlementType);
        return translationMap != null
                ? Objects.requireNonNullElse(translationMap.get(languageId), "Translation not available.")
                : "Invalid settlement code.";
    }

}
