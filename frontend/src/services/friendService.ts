export interface Friend {
  id: string;
  name: string;
  avatar: string;
  email: string;
  phone: string;
  bio: string;
  mutualFriends: number;
  isOnline: boolean;
  lastActive: Date;
}

const friends: Friend[] = [
  {
    id: '1',
    name: 'Nguyen Van A',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop',
    email: 'nguyenvana@email.com',
    phone: '0123456789',
    bio: 'Passionate about programming and technology',
    mutualFriends: 15,
    isOnline: true,
    lastActive: new Date(),
  },
  {
    id: '2',
    name: 'Tran Thi B',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&h=200&fit=crop',
    email: 'tranthib@email.com',
    phone: '0987654321',
    bio: 'Graphic Design & UI/UX',
    mutualFriends: 23,
    isOnline: true,
    lastActive: new Date(),
  },
  {
    id: '3',
    name: 'Le Van C',
    avatar: 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=200&h=200&fit=crop',
    email: 'levanc@email.com',
    phone: '0369852147',
    bio: 'Marketing & Social Media',
    mutualFriends: 8,
    isOnline: false,
    lastActive: new Date(Date.now() - 2 * 60 * 60000),
  },
  {
    id: '4',
    name: 'Pham Thi D',
    avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=200&h=200&fit=crop',
    email: 'phamthid@email.com',
    phone: '0741852963',
    bio: 'Photography & Travel',
    mutualFriends: 31,
    isOnline: false,
    lastActive: new Date(Date.now() - 24 * 60 * 60000),
  },
  {
    id: '5',
    name: 'Hoang Van E',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop',
    email: 'hoangvane@email.com',
    phone: '0258963147',
    bio: 'Business & Entrepreneurship',
    mutualFriends: 12,
    isOnline: true,
    lastActive: new Date(),
  },
  {
    id: '6',
    name: 'Dang Thi F',
    avatar: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&h=200&fit=crop',
    email: 'dangthif@email.com',
    phone: '0147258369',
    bio: 'Education & Training',
    mutualFriends: 19,
    isOnline: false,
    lastActive: new Date(Date.now() - 5 * 60 * 60000),
  },
];

export const getFriends = async (): Promise<Friend[]> => {
  console.log('Fetching friends...');
  await new Promise(resolve => setTimeout(resolve, 300));
  return friends;
};

export const getFriendById = async (id: string): Promise<Friend | null> => {
  console.log('Fetching friend by id:', id);
  await new Promise(resolve => setTimeout(resolve, 300));
  return friends.find(f => f.id === id) || null;
};

export const removeFriend = async (id: string): Promise<void> => {
  console.log('Removing friend:', id);
  await new Promise(resolve => setTimeout(resolve, 500));
  const index = friends.findIndex(f => f.id === id);
  if (index !== -1) {
    friends.splice(index, 1);
  }
};

export const blockFriend = async (id: string): Promise<void> => {
  console.log('Blocking friend:', id);
  await new Promise(resolve => setTimeout(resolve, 500));
};
