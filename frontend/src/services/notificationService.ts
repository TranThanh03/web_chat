export interface Notification {
  id: string;
  type: 'friend_request' | 'message' | 'general';
  senderId: string;
  senderName: string;
  senderAvatar: string;
  content: string;
  timestamp: Date;
  isRead: boolean;
}

const notifications: Notification[] = [
  {
    id: '1',
    type: 'friend_request',
    senderId: 'user1',
    senderName: 'Vo Minh Tuan',
    senderAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&h=200&fit=crop',
    content: 'sent you a friend request',
    timestamp: new Date(Date.now() - 10 * 60000),
    isRead: false,
  },
  {
    id: '2',
    type: 'friend_request',
    senderId: 'user2',
    senderName: 'Nguyen Thi Huong',
    senderAvatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&h=200&fit=crop',
    content: 'sent you a friend request',
    timestamp: new Date(Date.now() - 2 * 60 * 60000),
    isRead: false,
  },
  {
    id: '3',
    type: 'friend_request',
    senderId: 'user3',
    senderName: 'Tran Quoc Bao',
    senderAvatar: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=200&h=200&fit=crop',
    content: 'sent you a friend request',
    timestamp: new Date(Date.now() - 5 * 60 * 60000),
    isRead: true,
  },
  {
    id: '4',
    type: 'friend_request',
    senderId: 'user4',
    senderName: 'Le Thi Mai',
    senderAvatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&h=200&fit=crop',
    content: 'sent you a friend request',
    timestamp: new Date(Date.now() - 24 * 60 * 60000),
    isRead: true,
  },
];

export const getNotifications = async (): Promise<Notification[]> => {
  console.log('Fetching notifications...');
  await new Promise(resolve => setTimeout(resolve, 300));
  return notifications;
};

export const acceptFriendRequest = async (notificationId: string): Promise<void> => {
  console.log('Accepting friend request:', notificationId);
  await new Promise(resolve => setTimeout(resolve, 500));
  const index = notifications.findIndex(n => n.id === notificationId);
  if (index !== -1) {
    notifications.splice(index, 1);
  }
};

export const rejectFriendRequest = async (notificationId: string): Promise<void> => {
  console.log('Rejecting friend request:', notificationId);
  await new Promise(resolve => setTimeout(resolve, 500));
  const index = notifications.findIndex(n => n.id === notificationId);
  if (index !== -1) {
    notifications.splice(index, 1);
  }
};

export const blockFriendRequest = async (notificationId: string): Promise<void> => {
  console.log('Blocking user from friend request:', notificationId);
  await new Promise(resolve => setTimeout(resolve, 500));
  const index = notifications.findIndex(n => n.id === notificationId);
  if (index !== -1) {
    notifications.splice(index, 1);
  }
};

export const markAsRead = async (notificationId: string): Promise<void> => {
  console.log('Marking notification as read:', notificationId);
  await new Promise(resolve => setTimeout(resolve, 300));
  const notification = notifications.find(n => n.id === notificationId);
  if (notification) {
    notification.isRead = true;
  }
};
