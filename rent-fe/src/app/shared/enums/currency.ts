export enum Currency {
  BGN = "BGN",
  EUR = "EUR",
}

type CurrencyType = keyof typeof Currency;
type CurrencyLabelMap = {
  [key in CurrencyType]: string;
};

export const currencyLabels: CurrencyLabelMap = {
  BGN: "лв.",
  EUR: "€",
};
