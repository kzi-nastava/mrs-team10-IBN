import { Component, OnInit, OnDestroy, ChangeDetectorRef, OnChanges } from '@angular/core';
import { WebSocketService } from '../service/websocket.service';
import { ChatMessage } from '../model/chat-message.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ChatRoom } from '../model/chat-message.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';
import { Input } from '@angular/core';
import { SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit, OnDestroy, OnChanges {
  chatRoom: ChatRoom = { id: 0, currentUserEmail: '', receiver: 0, messages: [] };
  messageContent: string = '';
  currentUserEmail: string = '';
  @Input() chatRoomId!: number;

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

  ngOnChanges(changes: SimpleChanges) {
    if (changes['chatRoomId'] && this.chatRoomId) {
      this.loadChatRoom();
    }
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
    if (this.authService.role() == 'administrator') {
      this.http.get<ChatRoom>(`${environment.apiHost}/chat/room/${this.chatRoomId}`).subscribe({
        next: (res) => {
          this.chatRoom = res;
          this.currentUserEmail = res.currentUserEmail;
          this.cdr.detectChanges();
        },
      });
    } else {
      this.http.get<ChatRoom>(`${environment.apiHost}/chat/other`).subscribe({
        next: (res) => {
          this.chatRoom = res;
          this.currentUserEmail = res.currentUserEmail;
          this.cdr.detectChanges();
        },
      });
    }
  }
}
