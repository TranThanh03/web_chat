import { Button } from "@/components/ui/button";
import TextareaAutosize from "react-textarea-autosize";
import { Send, Paperclip, Smile } from "lucide-react";
import { memo, useState, type KeyboardEvent } from "react";

interface ChatInputProps {
    onSendMessage: (content: string) => void;
}

const ChatInput = ({ onSendMessage }: ChatInputProps) => {
    const [message, setMessage] = useState("");

    const handleSend = () => {
        if (message.trim()) {
            onSendMessage(message.trim());
            setMessage("");
        }
    };

    const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
        <div className="border-t border-border bg-card p-4 pb-20 md:pb-4">
            <div className="flex gap-2 items-center">
                <Button
                    variant="ghost"
                    size="icon"
                    className="rounded-full shrink-0 mb-1"
                >
                    <Paperclip className="w-5 h-5" />
                </Button>

                <div className="flex-1 relative items-center">
                    <TextareaAutosize
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="Type a message..."
                        minRows={1}
                        maxRows={6}
                        className="
                            w-full
                            resize-none
                            rounded-3xl
                            border
                            border-secondary
                            bg-secondary
                            px-4
                            py-3
                            text-sm
                            outline-none
                        "
                    />

                    <Button
                        variant="ghost"
                        size="icon"
                        className="
                            absolute
                            right-2
                            bottom-1.5
                            h-8
                            w-8
                            rounded-full
                        "
                    >
                        <Smile className="w-5 h-5" />
                    </Button>
                </div>

                <Button
                    onClick={handleSend}
                    size="icon"
                    className="
                        rounded-full
                        shrink-0
                        mb-1
                        bg-primary
                        hover:bg-primary/90
                    "
                    disabled={!message.trim()}
                >
                    <Send className="w-5 h-5" />
                </Button>
            </div>
        </div>
    );
};

export default memo(ChatInput);