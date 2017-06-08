import { Directive, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, Validator, ValidatorFn, Validators } from '@angular/forms';

export function regexValidator(stringRe: RegExp): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} => {
    const value = control.value;
    const no = stringRe.test(value);
    return no ? {'forbiddenString': {value}} : null;
  };
}

@Directive({
  selector: '[forbiddenString]',
  providers: [{provide: NG_VALIDATORS, useExisting: RegexValidationDirective, multi: true}]
})
export class RegexValidationDirective implements Validator, OnChanges {
  @Input() forbiddenString: string;
  private valFn = Validators.nullValidator;

  ngOnChanges(changes: SimpleChanges): void {
    const change = changes['forbiddenString'];
    if (change) {
      const val: string | RegExp = change.currentValue;
      const re = val instanceof RegExp ? val : new RegExp(val, 'i');
      this.valFn = regexValidator(re);
    } else {
      this.valFn = Validators.nullValidator;
    }
  }

  validate(control: AbstractControl): {[key: string]: any} {
    return this.valFn(control);
  }
}
