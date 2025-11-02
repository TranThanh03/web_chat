import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import type { Message } from "@/services/chatService";
import { format } from "date-fns";
import { vi } from "date-fns/locale";
import { cn } from "@/lib/utils";
import { memo } from "react";

interface MessageBubbleProps {
    message: Message;
}

const MessageBubble = ({ message }: MessageBubbleProps) => {
    return (
        <div
            className={cn(
                "flex gap-3 mb-4 animate-in fade-in slide-in-from-bottom-2 duration-300",
                message.isCurrentUser ? "flex-row-reverse" : "flex-row"
            )}
        >
            <Avatar className="w-8 h-8">
                <AvatarImage src={message.senderAvatar} alt={message.senderName} />
                <AvatarFallback>{message.senderName.charAt(0)}</AvatarFallback>
            </Avatar>

            <div
                className={cn(
                    "flex flex-col gap-1 max-w-[70%]",
                    message.isCurrentUser ? "items-end" : "items-start"
                )}
            >
                <div
                    className={cn(
                        "px-4 py-2.5 rounded-2xl",
                        message.isCurrentUser
                        ? "bg-primary text-primary-foreground rounded-tr-sm"
                        : "bg-secondary text-foreground rounded-tl-sm"
                    )}
                >
                    <p className="text-sm leading-relaxed wrap-break-word">{message.content}</p>
                </div>
                
                <span className="text-xs text-muted-foreground px-1">
                    {format(message.timestamp, "HH:mm", { locale: vi })}
                </span>
            </div>
        </div>
    );
};

export default memo(MessageBubble);