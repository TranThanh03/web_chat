import { MessageSquare } from "lucide-react";
import { memo } from "react";

const EmptyChatState = () => {
    return (
        <div className="flex-1 flex items-center justify-center bg-background">
            <div className="text-center">
                <div className="w-20 h-20 rounded-full bg-primary/10 flex items-center justify-center mx-auto mb-4">
                    <MessageSquare className="w-10 h-10 text-primary" />
                </div>
                <h2 className="text-2xl font-semibold text-foreground mb-2">
                    Welcome to ChatApp
                </h2>
                <p className="text-muted-foreground">
                    Select a conversation to start messaging
                </p>
            </div>
        </div>
    );
};

export default memo(EmptyChatState);