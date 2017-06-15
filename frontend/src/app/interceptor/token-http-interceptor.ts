import { Injectable } from '@angular/core';
import { ConnectionBackend, RequestOptions, Request, RequestOptionsArgs, Response, Http, Headers} from '@angular/http';
import { DocumentRef } from '../services/document-ref.service';
import { Observable } from 'rxjs/Rx';
import { LoginingResolverService } from '../services/logining-resolver.service';
import { Authentication } from '../classes/authentication';
import { HmacSHA256 } from 'crypto-js';
import { HmacSHA1 } from 'crypto-js';
import { HmacMD5 } from 'crypto-js';


@Injectable()
export class TokenHttpInterceptor extends Http {
  constructor(backend: ConnectionBackend,
              defaultOptions: RequestOptions,
              private logining: LoginingResolverService,
              private documentRef: DocumentRef) {
    super(backend, defaultOptions);
  }

  request(url: string | Request, options?: RequestOptionsArgs): Observable<Response> {
    return super.request(url, options);
  }

  get(url: string, options?: RequestOptionsArgs): Observable<Response> {
    return super.get(url, this.getRequestOptionArgs(url, 'GET', options));
  }

  post(url: string, body: string, options?: RequestOptionsArgs): Observable<Response> {
    return super.post(url, body, this.getRequestOptionArgs(url, 'POST', options, body));
  }

  put(url: string, body: string, options?: RequestOptionsArgs): Observable<Response> {
    return super.put(url, body, this.getRequestOptionArgs(url, 'PUT', options, body));
  }

  delete(url: string, options?: RequestOptionsArgs): Observable<Response> {
    return super.delete(url, this.getRequestOptionArgs(url, 'DELETE', options));
  }

  public switchState(auth?: Authentication): void {
    console.log('switch state ' + JSON.stringify(auth));
    console.log(auth);
    if (auth == null) {
      this.logining.erase();
    } else {
      this.logining.login(auth);
    }
  }

  private getRequestOptionArgs(url: string, method: string, options?: RequestOptionsArgs, body?: any): RequestOptionsArgs {
    if (options == null) {
      options = new RequestOptions();
    }
    if (options.headers == null) {
      options.headers = new Headers();
    }
    if (this.canVerify(url)) {
      const auth: Authentication = this.logining.getAuthentication();
      console.log(auth);
      if (auth !== null) {
        const date = new Date().toISOString();
        const fullUrl = this.documentRef.originHref().concat(url);
        let message = '';
        if (method === 'PUT' || method === 'POST') {
          message = method + body + fullUrl + date;
        } else {
          message = method + fullUrl + date;
        }
        console.log(auth.publicKey);
        console.log('MESSAGE='+ HmacSHA256(message, auth.publicKey));
        const encodingLvl = auth.encodingLvl;
        if (encodingLvl === 'HmacSHA256') {
          options.headers.append('X-Digest', HmacSHA256(message, auth.publicKey));
        } else if (encodingLvl === 'HmacSHA1') {
          options.headers.append('X-Digest', HmacSHA1(message, auth.publicKey));
        } else if (encodingLvl === 'HmacMD5') {
          options.headers.append('X-Digest', HmacMD5(message, auth.publicKey));
        }
        options.headers.append('X-HMAC-CSRF', auth.csrf);
        options.headers.append('X-Once', date);
      }
    }
    options.headers.append('Content-Type', 'application/json');

    return options;
  }

  private canVerify(url: string): boolean {
    console.log(url);
    console.log(url.includes('/api'));
    return url.includes('/api') && !url.includes('/api/authenticate');
  }
}
