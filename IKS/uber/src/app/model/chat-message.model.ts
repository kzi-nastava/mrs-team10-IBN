import { User } from './user.model';

export interface ChatMessage {
  content: string;
  chatRoom: number;
  email: string | null;
}

export interface ChatRoom {
  id: number;
  currentUserEmail: string;
  receiver: number;
  messages: ChatMessage[];
}

export interface ChatInbox {
  chatRoom: number;
  user: UserDTO;
}

export interface UserDTO {
  phoneNumber: number;
  name: string;
  lastName: string;
}
