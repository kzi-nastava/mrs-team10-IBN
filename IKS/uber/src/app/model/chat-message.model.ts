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
