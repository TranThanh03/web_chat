import { MessageSquare, Users, Bell } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { getConversations } from "@/services/chatService";
import { getFriends } from "@/services/friendService";
import { getNotifications } from "@/services/notificationService";
import { Link, useLocation } from "react-router-dom";
import MobileAccountMenu from "./MobileAccountMenu";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { memo } from "react";

const MobileBottomNav = () => {
    const location = useLocation();

    const { data: conversations = [] } = useQuery({
        queryKey: ['conversations'],
        queryFn: getConversations,
    });

    const { data: friends = [] } = useQuery({
        queryKey: ['friends'],
        queryFn: getFriends,
    });

    const { data: notifications = [] } = useQuery({
        queryKey: ['notifications'],
        queryFn: getNotifications,
    });

    const unreadMessagesCount = conversations.reduce((sum, conv) => sum + conv.unreadCount, 0);
    const friendsCount = friends.length;
    const unreadNotificationsCount = notifications.filter(n => !n.isRead).length;

    const navItems = [
        { icon: MessageSquare, label: "Messages", path: "/", badge: unreadMessagesCount },
        { icon: Users, label: "Friends", path: "/friends", badge: friendsCount },
        { icon: Bell, label: "Notifications", path: "/notifications", badge: unreadNotificationsCount },
    ];

    return (
        <div className="md:hidden fixed bottom-0 left-0 right-0 h-16 bg-card border-t border-border z-50">
            <div className="h-full flex items-center justify-around px-2">
                {navItems.map((item) => {
                    const Icon = item.icon;
                    const isActive = location.pathname === item.path;

                    return (
                        <Link key={item.path} to={item.path}>
                            <Button
                                variant="ghost"
                                size="icon"
                                className={cn(
                                    "flex flex-col gap-1 h-auto py-2 px-4 rounded-xl relative",
                                    isActive && "bg-primary/10 text-primary hover:bg-primary/20 hover:text-primary"
                                )}
                            >
                                <div className="relative">
                                    <Icon className="w-5 h-5" />
                                    
                                    {item.badge > 0 && (
                                        <div className="absolute -top-2 -right-2 min-w-4 h-4 px-1 bg-red-500 text-white text-[9px] font-bold rounded-full flex items-center justify-center">
                                            {item.badge > 99 ? '99+' : item.badge}
                                        </div>
                                    )}
                                </div>

                                <span className="text-[10px] font-medium">{item.label}</span>
                                {isActive && (
                                    <div className="absolute top-0 left-1/2 -translate-x-1/2 w-8 h-1 bg-primary rounded-b-full" />
                                )}
                            </Button>
                        </Link>
                    );
                })}

                <MobileAccountMenu isActive={location.pathname === '/account'} />
            </div>
        </div>
    );
};

export default memo(MobileBottomNav);