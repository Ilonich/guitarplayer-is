/* Как же я по началу заебался c асинхронной валидацией, учитывая мои скудные знания angular,
    хорошо спустя день догадался начать гуглить.
 Спасибо этой теме => https://github.com/angular/angular/issues/6895 */

import {
    AbstractControl, NG_ASYNC_VALIDATORS, AsyncValidatorFn, ValidationErrors,
    AsyncValidator
} from '@angular/forms';
import {Directive, forwardRef, Input, ReflectiveInjector, OnInit} from '@angular/core';
import {Observable, Subject, Subscription, Observer, ReplaySubject, Scheduler} from 'rxjs/Rx';
import {
    CookieXSRFStrategy, XSRFStrategy, XHRBackend, ConnectionBackend, BaseResponseOptions,
    ResponseOptions, BaseRequestOptions, RequestOptions, BrowserXhr, Http, Headers
} from '@angular/http';

const headers: Headers = new Headers({'Content-Type': 'application/json'});
const http$: Http = ReflectiveInjector.resolveAndCreate([
    Http,
    BrowserXhr,
    {provide: RequestOptions, useClass: BaseRequestOptions},
    {provide: ResponseOptions, useClass: BaseResponseOptions},
    {provide: ConnectionBackend, useClass: XHRBackend},
    {provide: XSRFStrategy, useFactory: () => new CookieXSRFStrategy()},
]).get(Http);

function httpValidation(property: string, value: string): Observable<ValidationErrors | null> {
    return http$.post('/api/validate', JSON.stringify({[property]: value}), {headers: headers})
        .map(
            response => {
                let json = response.json();
                if (json.valid) {
                    return null;
                } else {
                    return {'notUnique': true};
                }
            },
            error => {
                return null;
            }).catch(e => { return Observable.of(null)});
}

type AsyncValidatorFactory = (service: (value: any) => Observable<any | null>) => AsyncValidatorFn;

const asyncValidatorFactory: AsyncValidatorFactory = (service: (value: any) => Observable<any | null>): AsyncValidatorFn => {
    let subscription: Subscription = Subscription.EMPTY;
    return (input: AbstractControl) => {
        subscription.unsubscribe();
        return Observable.create((observer: Observer<any | null>) => {
            subscription = Observable.timer(600).flatMap(() => service(input.value)).subscribe(observer);
            return () => subscription.unsubscribe();
        });
    };
};

const cachingAsyncValidatorFactory: AsyncValidatorFactory = (service: (value: any) => Observable<any | null>): AsyncValidatorFn => {
    let subscription: Subscription = Subscription.EMPTY;
    const sampler = new Subject<any>();
    const validationCache = new ReplaySubject<any>(1, undefined, Scheduler.async);
    const samplerCache = new ReplaySubject<any>(1);
    sampler.bufferCount(2, 1).subscribe(samplerCache);
    sampler.next(null); // prime/invalidate 'samplerCache' with a dummy value
    return (input: AbstractControl) => {
        subscription.unsubscribe();
        return Observable.create((observer: Observer<any | null>) => {
            subscription = Observable.timer(600).flatMap(() => {
                sampler.next(input.value);
                return samplerCache.first().flatMap((sample: [any, any]) => {
                    if (sample[0] == sample[1]) {
                        return validationCache.first();
                    } else {
                        // introduce side effect via do() by piggybacking on service call result
                        return service(sample[1]).do((value) => {
                            validationCache.next(value); // cache successfull result into validationCache
                        }, () => {
                            sampler.next(null); // invalidate samplerCache due to service error
                        });
                    }
                })
            }).subscribe(observer);
            return () => subscription.unsubscribe();
        });
    };
};

export class CustomAsyncValidators {
    static usernameValidator: AsyncValidatorFn = asyncValidatorFactory((value) => httpValidation('username', value));
    static emailValidator: AsyncValidatorFn = asyncValidatorFactory((value) => httpValidation('email', value));
}


@Directive({
    selector: '[uniqueConstraintValue][ngModel],[uniqueConstraintValue][formControl]',
    providers: [
        { provide: NG_ASYNC_VALIDATORS, useExisting: forwardRef(() => UniqueConstraintValidator), multi: true }
    ]
})
export class UniqueConstraintValidator implements AsyncValidator, OnInit {
    @Input() uniqueConstraintValue: string;
    private validationFunction: AsyncValidatorFn;

    constructor() {}

    ngOnInit():void {
        //В конструкторе input = null, поэтому инициация тут
        this.validationFunction = cachingAsyncValidatorFactory(value => httpValidation(this.uniqueConstraintValue, value));
    }

    //noinspection JSAnnotator
    validate(control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
        return this.validationFunction(control);
    }
}