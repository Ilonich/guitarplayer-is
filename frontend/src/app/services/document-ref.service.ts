import {Injectable, Inject} from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';

@Injectable()
export class DocumentRef {
  constructor(@Inject(DOCUMENT) private document: Document) {
    console.log('DocumentRef CREATED');
  }

  public originHref(): string {
    return this.document.location.origin;
  }

  public getDocument(): Document {
    return this.document;
  }
}
