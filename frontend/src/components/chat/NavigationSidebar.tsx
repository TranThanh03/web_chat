import { MessageSquare, Users, User, LogOut, Bell, MessageCircle } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { getConversations } from "@/services/chatService";
import { getFriends } from "@/services/friendService";
import { getNotifications } from "@/services/notificationService";
import { Link, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { toast } from "sonner";
import { memo } from "react";

const NavigationSidebar = () => {
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

    const handleLogout = () => {
        console.log('Logout clicked');
        toast.success('Logged out successfully!');
    };

    const navItems = [
        { icon: MessageSquare, label: "Messages", path: "/", badge: unreadMessagesCount },
        { icon: Users, label: "Friends", path: "/friends", badge: friendsCount },
        { icon: Bell, label: "Notifications", path: "/notifications", badge: unreadNotificationsCount },
    ];

    return (
        <div className="hidden md:flex md:flex-col md:w-20 bg-card border-r border-border h-full">
            <div className="h-16 flex items-center justify-center border-b border-border">
                <div className="bg-primary rounded-xl p-2 sm:p-2.5">
                    <MessageCircle className="w-5 h-5 sm:w-6 sm:h-6 text-primary-foreground" />
                </div>
            </div>

            <div className="flex-1 flex flex-col items-center mt-1 py-4 gap-2">
                {navItems.map((item) => {
                    const Icon = item.icon;
                    const isActive = location.pathname === item.path;
                    
                    return (
                        <Link key={item.path} to={item.path}>
                            <Button
                                variant="ghost"
                                size="icon"
                                className={
                                    cn(
                                        "w-12 h-12 rounded-xl relative transition-colors",
                                        isActive && "bg-primary/10 text-primary hover:bg-primary/20 hover:text-primary"
                                    )
                                }
                                    title={item.label}
                            >
                                <Icon className="w-5 h-5" />
                                
                                {item.badge > 0 && (
                                    <div className="absolute -top-1 -right-1 min-w-4.5 h-4.5 px-1 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                                        {item.badge > 99 ? '99+' : item.badge}
                                    </div>
                                )}
                                {isActive && (
                                    <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-primary rounded-r-full" />
                                )}
                            </Button>
                        </Link>
                    );
                })}
            </div>

            <div className="flex flex-col items-center py-4 gap-2 border-t border-border">
                <Link to="/account">
                    <Button
                        variant="ghost"
                        size="icon"
                        className={
                            cn(
                                "w-12 h-12 rounded-xl relative transition-colors",
                                location.pathname === "/account" && "bg-primary/10 text-primary hover:bg-primary/20 hover:text-primary"
                            )
                        }
                            title="Account"
                    >
                        <User className="w-5 h-5" />
                        
                        {location.pathname === "/account" && (
                            <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-primary rounded-r-full" />
                        )}
                    </Button>
                </Link>
                
                <Button
                    variant="ghost"
                    size="icon"
                    className="w-12 h-12 rounded-xl text-destructive hover:bg-destructive/10 hover:text-destructive"
                    title="Logout"
                    onClick={handleLogout}
                >
                    <LogOut className="w-5 h-5" />
                </Button>
            </div>
        </div>
    );
};

export default memo(NavigationSidebar);