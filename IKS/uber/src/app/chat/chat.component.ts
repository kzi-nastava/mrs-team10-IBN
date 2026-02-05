import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { WebSocketService } from '../service/websocket.service';
import { ChatMessage } from '../model/chat-message.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ChatRoom } from '../model/chat-message.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-chat',
  imports: [FormsModule, CommonModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit, OnDestroy {
  chatRoom: ChatRoom = { id: 0, currentUserEmail: '', receiver: 0, messages: [] };
  messageContent: string = '';
  currentUserEmail: string = '';

  constructor(
    private webSocketService: WebSocketService,
    private http: HttpClient,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadChatRoom();

    this.webSocketService.chatMessage$.subscribe((msg) => {
      this.chatRoom.messages = [...this.chatRoom.messages, msg];
      console.log(msg);
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }

  sendMessage(): void {
    if (this.messageContent.trim()) {
      const chatMessage: ChatMessage = {
        content: this.messageContent,
        chatRoom: this.chatRoom.id,
        email: this.currentUserEmail,
      };

      this.webSocketService.sendMessage(chatMessage);
      this.chatRoom.messages.push(chatMessage);
      this.messageContent = '';
    }
  }

  loadChatRoom() {
    console.log(this.authService.role());
    if (this.authService.role() == 'administrator') {
      this.http.get<ChatRoom>(`${environment.apiHost}/chat/admin`).subscribe({
        next: (res) => {
          this.chatRoom = res;
          console.log(res);
          this.currentUserEmail = res.currentUserEmail;
          this.cdr.detectChanges();
        },
      });
    } else {
      this.http.get<ChatRoom>(`${environment.apiHost}/chat/other`).subscribe({
        next: (res) => {
          this.chatRoom = res;
          console.log(res);
          this.currentUserEmail = res.currentUserEmail;
          this.cdr.detectChanges();
        },
      });
    }
  }
}
