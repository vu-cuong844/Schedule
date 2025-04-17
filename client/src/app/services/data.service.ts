import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor() { }

  private indexFileRenderSubject = new BehaviorSubject<number>(0);
  indexFileRender$ = this.indexFileRenderSubject.asObservable();

  private filesSubject = new BehaviorSubject<File[]>([]);
  files$ = this.filesSubject.asObservable();
  setFiles(files: File[]) {
    this.filesSubject.next(files);
  }

  setIndexFileRender(index: number) {
    this.indexFileRenderSubject.next(index);
  }

  getCurrentFiles(): File[] {
    return this.filesSubject.getValue();
  }

  getCurrentIndexFileRender(): number {
    return this.indexFileRenderSubject.getValue();
  }

}
