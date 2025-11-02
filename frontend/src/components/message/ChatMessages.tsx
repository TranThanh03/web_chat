import { ScrollArea } from "@/components/ui/scroll-area";
import MessageBubble from "./MessageBubble";
import type { Message } from "@/services/chatService";
import { memo, useEffect, useRef } from "react";

interface ChatMessagesProps {
    messages: Message[];
}

const ChatMessages = ({ messages }: ChatMessagesProps) => {
    const scrollRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [messages]);

    return (
        <ScrollArea className="flex-1 px-6">
            <div ref={scrollRef} className="py-4">
                {messages.map((message) => (
                    <MessageBubble key={message.id} message={message} />
                ))}
            </div>
        </ScrollArea>
    );
};

export default memo(ChatMessages);