export interface Message {
  id: string;
  content: string;
  senderId: string;
  senderName: string;
  senderAvatar: string;
  timestamp: Date;
  isCurrentUser: boolean;
}

export interface Conversation {
  id: string;
  name: string;
  avatar: string;
  lastMessage: string;
  timestamp: Date;
  unreadCount: number;
  isOnline: boolean;
}

// Dữ liệu giả lập
const conversations: Conversation[] = [
  {
    id: '1',
    name: 'Nguyen Van A',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
    lastMessage: 'See you later!',
    timestamp: new Date(Date.now() - 5 * 60000),
    unreadCount: 2,
    isOnline: true,
  },
  {
    id: '2',
    name: 'Tran Thi B',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
    lastMessage: 'Thank you so much!',
    timestamp: new Date(Date.now() - 30 * 60000),
    unreadCount: 0,
    isOnline: true,
  },
  {
    id: '3',
    name: 'Le Van C',
    avatar: 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=100&h=100&fit=crop',
    lastMessage: 'OK, I will check again',
    timestamp: new Date(Date.now() - 2 * 60 * 60000),
    unreadCount: 0,
    isOnline: false,
  },
  {
    id: '4',
    name: 'Pham Thi D',
    avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop',
    lastMessage: 'See you soon!',
    timestamp: new Date(Date.now() - 24 * 60 * 60000),
    unreadCount: 5,
    isOnline: false,
  },
  {
    id: '5',
    name: 'Hoang Van E',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop',
    lastMessage: 'Awesome!',
    timestamp: new Date(Date.now() - 3 * 24 * 60 * 60000),
    unreadCount: 0,
    isOnline: true,
  },
  {
    id: '6',
    name: 'Nguyen Van A',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
    lastMessage: 'See you later!',
    timestamp: new Date(Date.now() - 5 * 60000),
    unreadCount: 2,
    isOnline: true,
  },
  {
    id: '7',
    name: 'Tran Thi B',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
    lastMessage: 'Thank you so much!',
    timestamp: new Date(Date.now() - 30 * 60000),
    unreadCount: 0,
    isOnline: true,
  },
  {
    id: '8',
    name: 'Le Van C',
    avatar: 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=100&h=100&fit=crop',
    lastMessage: 'OK, I will check again',
    timestamp: new Date(Date.now() - 2 * 60 * 60000),
    unreadCount: 0,
    isOnline: false,
  },
  {
    id: '9',
    name: 'Pham Thi D',
    avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop',
    lastMessage: 'See you soon!',
    timestamp: new Date(Date.now() - 24 * 60 * 60000),
    unreadCount: 5,
    isOnline: false,
  },
  {
    id: '10',
    name: 'Hoang Van E',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop',
    lastMessage: 'Awesome!',
    timestamp: new Date(Date.now() - 3 * 24 * 60 * 60000),
    unreadCount: 0,
    isOnline: true,
  },
];

const messagesMap: Record<string, Message[]> = {
  '1': [
    {
      id: 'm1',
      content: 'Hi! How are you?',
      senderId: '1',
      senderName: 'Nguyen Van A',
      senderAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 60 * 60000),
      isCurrentUser: false,
    },
    {
      id: 'm2',
      content: 'Hi! I am fine, how about you?',
      senderId: 'current',
      senderName: 'You',
      senderAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 55 * 60000),
      isCurrentUser: true,
    },
    {
      id: 'm3',
      content: 'I am good too! Thanks for asking.',
      senderId: '1',
      senderName: 'Nguyen Van A',
      senderAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 50 * 60000),
      isCurrentUser: false,
    },
    {
      id: 'm4',
      content: 'Are you free this weekend? I would like to meet up.',
      senderId: 'current',
      senderName: 'You',
      senderAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 45 * 60000),
      isCurrentUser: true,
    },
    {
      id: 'm5',
      content: 'See you later!',
      senderId: '1',
      senderName: 'Nguyen Van A',
      senderAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 5 * 60000),
      isCurrentUser: false,
    },
    {
      id: 'm6',
      content: 'Hi! How are you?',
      senderId: '1',
      senderName: 'Nguyen Van A',
      senderAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 60 * 60000),
      isCurrentUser: false,
    },
    {
      id: 'm7',
      content: 'Hi! I am fine, how about you?',
      senderId: 'current',
      senderName: 'You',
      senderAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 55 * 60000),
      isCurrentUser: true,
    },
    {
      id: 'm8',
      content: 'I am good too! Thanks for asking.',
      senderId: '1',
      senderName: 'Nguyen Van A',
      senderAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 50 * 60000),
      isCurrentUser: false,
    },
    {
      id: 'm9',
      content: 'Are you free this weekend? I would like to meet up.',
      senderId: 'current',
      senderName: 'You',
      senderAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 45 * 60000),
      isCurrentUser: true,
    },
    {
      id: 'm10',
      content: 'See you later!',
      senderId: '1',
      senderName: 'Nguyen Van A',
      senderAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 5 * 60000),
      isCurrentUser: false,
    },
  ],
  '2': [
    {
      id: 'm6',
      content: 'File sent to you!',
      senderId: 'current',
      senderName: 'You',
      senderAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 40 * 60000),
      isCurrentUser: true,
    },
    {
      id: 'm7',
      content: 'Thank you so much!',
      senderId: '2',
      senderName: 'Tran Thi B',
      senderAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
      timestamp: new Date(Date.now() - 30 * 60000),
      isCurrentUser: false,
    },
  ],
};

export const getConversations = async (): Promise<Conversation[]> => {
  console.log('Fetching conversations...');
  await new Promise(resolve => setTimeout(resolve, 300));
  return conversations;
};

export const getMessages = async (conversationId: string): Promise<Message[]> => {
  console.log('Fetching messages for conversation:', conversationId);
  await new Promise(resolve => setTimeout(resolve, 300));
  return messagesMap[conversationId] || [];
};

export const sendMessage = async (
  conversationId: string,
  content: string
): Promise<Message> => {
  console.log('Sending message to conversation:', conversationId, content);
  await new Promise(resolve => setTimeout(resolve, 500));
  
  const newMessage: Message = {
    id: `m${Date.now()}`,
    content,
    senderId: 'current',
    senderName: 'You',
    senderAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
    timestamp: new Date(),
    isCurrentUser: true,
  };
  
  if (!messagesMap[conversationId]) {
    messagesMap[conversationId] = [];
  }
  messagesMap[conversationId].push(newMessage);
  
  const conversation = conversations.find(c => c.id === conversationId);
  if (conversation) {
    conversation.lastMessage = content;
    conversation.timestamp = new Date();
  }
  
  return newMessage;
};
