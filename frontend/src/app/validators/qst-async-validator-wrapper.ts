import {AbstractControl} from "@angular/forms";
import {Observable, Subject} from "rxjs/Rx";
export class QstAsyncValidatorWrapper {
    public static debounce(asyncValidator: (c: AbstractControl) => Observable<any>,
                           time: number = 500): (c: AbstractControl) => Observable<any> {
        /*Starting a debouncing observable*/
        const subject: Subject<AbstractControl> = new Subject<AbstractControl>();

        const obs: Observable<any> = subject
            .debounceTime(time)
            .switchMap(abstractControl => asyncValidator(abstractControl))
            .share();

        /*Need to have at least 1 active subscriber, because otherwise
         * the first `subject.next(c)` event won't be registered*/
        obs.subscribe();

        return (c: AbstractControl) => {
            /*Every time this function is invoked by Angular I must inform a subject about it*/
            subject.next(c);

            /*Need to take only one for every function invocation,
             * because the subscription must complete.
             * Otherwise Angular form state would be "PENDING"*/
            return obs.first();
        };
    }
}