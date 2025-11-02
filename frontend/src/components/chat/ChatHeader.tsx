import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { MoreVertical } from "lucide-react";
import type { Conversation } from "@/services/chatService";
import { memo } from "react";

interface ChatHeaderProps {
    conversation: Conversation | undefined;
    onOpenSettings: () => void;
}

const ChatHeader = ({ conversation, onOpenSettings }: ChatHeaderProps) => {
    return (
        <div className="h-16 border-b border-border bg-card px-6 flex items-center justify-between">
            <div className="flex items-center gap-3">
                <div className="relative">
                    <Avatar className="w-10 h-10">
                        <AvatarImage src={conversation?.avatar} alt={conversation?.name} />
                        <AvatarFallback>{conversation?.name.charAt(0)}</AvatarFallback>
                    </Avatar>

                    {conversation?.isOnline && (
                        <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-card rounded-full" />
                    )}
                </div>
                <div>
                    <h2 className="font-semibold text-foreground">{conversation?.name}</h2>
                    <p className="text-sm text-muted-foreground">
                        {conversation?.isOnline ? "Active now" : "Offline"}
                    </p>
                </div>
            </div>

            <div className="flex items-center gap-2">
                <Button 
                    variant="ghost" 
                    size="icon" 
                    className="rounded-full"
                    onClick={onOpenSettings}
                >
                    <MoreVertical className="w-5 h-5" />
                </Button>
            </div>
        </div>
    );
};

export default memo(ChatHeader);