import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ChatComponent } from '../../chat.component';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { ChatInbox } from '../../../model/chat-message.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-chat',
  imports: [ChatComponent, CommonModule],
  templateUrl: './admin-chat.component.html',
  styleUrl: './admin-chat.component.css',
})
export class AdminChatComponent implements OnInit {
  chatInbox: ChatInbox[] = [];

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadAllChatRooms();
  }
  loadAllChatRooms() {
    this.http.get<ChatInbox[]>(`${environment.apiHost}/chat/all`).subscribe({
      next: (res) => {
        this.chatInbox = res;
        console.log(this.chatInbox);
        this.cdr.detectChanges();
      },
    });
  }

  selectedChatRoom!: number;

  loadChat(chatRoomId: number) {
    this.selectedChatRoom = chatRoomId;
  }
}
