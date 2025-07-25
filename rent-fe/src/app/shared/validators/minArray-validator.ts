import { AbstractControl, FormArray, ValidationErrors, ValidatorFn } from '@angular/forms';

export function minLengthArray(min: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control instanceof FormArray) {
      return control.length >= min ? null : { minLengthArray: { valid: false, min } };
    }
    return null;
  };
}