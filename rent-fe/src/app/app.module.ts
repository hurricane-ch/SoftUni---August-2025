import {HttpClient,} from "@angular/common/http";
import {registerLocaleData} from "@angular/common";
import localeBg from "@angular/common/locales/bg";
import {MatTooltipDefaultOptions,} from "@angular/material/tooltip";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);

registerLocaleData(localeBg);

export const OtherOptions: MatTooltipDefaultOptions = {
    showDelay: 0,
    hideDelay: 0,
    touchGestures: "auto",
    position: "below",
    touchendHideDelay: 0,
    disableTooltipInteractivity: true,
};

export function httpTranslateLoader(http: HttpClient) {
    return new TranslateHttpLoader(http, "./assets/i18n/");
}
