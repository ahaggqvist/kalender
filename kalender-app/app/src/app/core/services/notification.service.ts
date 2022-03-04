import {Injectable} from '@angular/core';
import {MessageService} from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private messageService: MessageService) {
  }

  error(message: string): void {
    this.messageService.add({severity: 'error', detail: message, closable: false});
  }
}
