import { Component } from '@angular/core';
import { ChatComponent } from '../../chat.component';

@Component({
  selector: 'app-admin-chat',
  imports: [ChatComponent],
  templateUrl: './admin-chat.component.html',
  styleUrl: './admin-chat.component.css',
})
export class AdminChatComponent {}
