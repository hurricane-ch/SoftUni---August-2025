import { Component, Input } from "@angular/core";
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from "@angular/forms";

type CheckboxOption = { key: string; label: string; checked: boolean };

@Component({
    selector: "app-checkbox-group",
    templateUrl: "./checkbox-group.component.html",
    styleUrls: ["./checkbox-group.component.scss"],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: CheckboxGroupComponent,
            multi: true,
        },
    ]
})
export class CheckboxGroupComponent implements ControlValueAccessor {
  @Input() options: Omit<CheckboxOption, "checked">[] = [];

  items: CheckboxOption[] = [];
  disabled = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  onCheckboxChange(option: CheckboxOption, checked: boolean): void {
    this.items = this.items.map((item) =>
      item.key === option.key ? { ...item, checked: checked } : item
    );
    this.onChange(
      this.items.filter(({ checked }) => checked).map(({ key }) => key)
    );
  }

  writeValue(obj: any[]): void {
    this.items = this.options.map((option) => ({
      ...option,
      checked: obj?.includes(option.key),
    }));
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
}
