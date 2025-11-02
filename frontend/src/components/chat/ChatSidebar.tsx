import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Search } from "lucide-react";
import type { Conversation } from "@/services/chatService";
import { formatDistanceToNow } from "date-fns";
import { vi } from "date-fns/locale";
import { cn } from "@/lib/utils";
import { memo } from "react";

interface ChatSidebarProps {
    conversations: Conversation[];
    selectedConversationId: string | null;
    onSelectConversation: (id: string) => void;
}

const ChatSidebar = ({
    conversations,
    selectedConversationId,
    onSelectConversation,
}: ChatSidebarProps) => {
    return (
        <div className="hidden md:flex md:flex-col h-full bg-card border-r border-border">
            <div className="px-3 pt-4 pb-1">
                <div className="flex items-center gap-3 mb-5">
                    <h1 className="text-2xl text-foreground font-bold">Messages</h1>
                </div>
                <div className="relative">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                    <Input
                        placeholder="Search conversations..."
                        className="pl-10 bg-secondary border-secondary"
                    />
                </div>
            </div>

            <ScrollArea className="flex-1">
                <div className="p-2">
                    {conversations.map((conversation) => (
                        <button
                            key={conversation.id}
                            onClick={() => onSelectConversation(conversation.id)}
                            className={cn(
                                "w-full p-3 rounded-lg flex items-start gap-3 hover:bg-accent transition-colors text-left mb-1",
                                selectedConversationId === conversation.id && "bg-accent"
                            )}
                        >
                            <div className="relative">
                                <Avatar className="w-12 h-12">
                                    <AvatarImage src={conversation.avatar} alt={conversation.name} />
                                    <AvatarFallback>{conversation.name.charAt(0)}</AvatarFallback>
                                </Avatar>
                                {conversation.isOnline && (
                                    <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-card rounded-full" />
                                )}
                            </div>

                            <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between mb-1">
                                    <h3 className="font-medium text-foreground truncate">
                                        {conversation.name}
                                    </h3>
                                    <span className="text-xs text-muted-foreground whitespace-nowrap ml-2">
                                        {formatDistanceToNow(conversation.timestamp, {
                                        addSuffix: true,
                                        locale: vi,
                                        })}
                                    </span>
                                </div>

                                <div className="flex items-center justify-between gap-2">
                                    <p className="text-sm text-muted-foreground truncate">
                                        {conversation.lastMessage}
                                    </p>
                                    {conversation.unreadCount > 0 && (
                                        <Badge className="bg-primary text-primary-foreground rounded-full px-2 py-0 text-xs font-medium min-w-5 h-5 flex items-center justify-center">
                                        {conversation.unreadCount}
                                        </Badge>
                                    )}
                                </div>
                            </div>
                        </button>
                    ))}
                </div>
            </ScrollArea>
        </div>
    );
};

export default memo(ChatSidebar);