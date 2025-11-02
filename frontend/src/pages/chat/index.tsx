import ChatHeader from "@/components/chat/ChatHeader";
import { memo, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
    getConversations,
    getMessages,
    sendMessage,
} from "@/services/chatService";

import ChatSidebar from "@/components/chat/ChatSidebar";
import { Button } from "@/components/ui/button";
import { ArrowLeft, MoreVertical } from "lucide-react";
import {
    Avatar,
    AvatarFallback,
    AvatarImage,
} from "@/components/ui/avatar";

import MobileChatSidebar from "@/components/mobile/MobileChatSidebar";
import EmptyChatState from "@/components/chat/EmptyChatState";
import ChatSettingsSidebar from "@/components/chat/ChatSettingsSidebar";
import ChatMessages from "@/components/message/ChatMessages";
import ChatInput from "@/components/chat/ChatInput";
import { toast } from "sonner";

const ChatPage = () => {
    const [selectedConversationId, setSelectedConversationId] =
        useState<string | null>(null);

    const [showMobileSidebar, setShowMobileSidebar] = useState(false);
    const [showChatSettings, setShowChatSettings] = useState(false);

    const showMobileList = !selectedConversationId;

    const queryClient = useQueryClient();

    const { data: conversations = [] } = useQuery({
        queryKey: ["conversations"],
        queryFn: getConversations,
    });

    const selectedConversation = conversations.find(
        (c) => c.id === selectedConversationId
    );

    const { data: messages = [] } = useQuery({
        queryKey: ["messages", selectedConversationId],
        queryFn: () => getMessages(selectedConversationId!),
        enabled: !!selectedConversationId,
    });

    const sendMessageMutation = useMutation({
        mutationFn: (content: string) =>
            sendMessage(selectedConversationId!, content),

        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: ["messages", selectedConversationId],
            });

            queryClient.invalidateQueries({
                queryKey: ["conversations"],
            });
        },

        onError: () => {
            toast.error("Failed to send message. Please try again!");
        },
    });

    const handleSendMessage = (content: string) => {
        sendMessageMutation.mutate(content);
    };

    return (
        <div className="h-full flex overflow-hidden bg-background relative">
            <div className="hidden md:block w-80 shrink-0">
                <ChatSidebar
                    conversations={conversations}
                    selectedConversationId={selectedConversationId}
                    onSelectConversation={setSelectedConversationId}
                />
            </div>

            {showMobileSidebar && (
                <div className="md:hidden fixed inset-0 bg-background z-50">
                    <MobileChatSidebar
                        conversations={conversations}
                        selectedConversationId={selectedConversationId}
                        onSelectConversation={setSelectedConversationId}
                        onClose={() => setShowMobileSidebar(false)}
                    />
                </div>
            )}

            <div className="flex-1 flex flex-col overflow-hidden">
                {showMobileList && (
                    <div className="md:hidden h-full">
                        <MobileChatSidebar
                            conversations={conversations}
                            selectedConversationId={selectedConversationId}
                            onSelectConversation={setSelectedConversationId}
                            onClose={() => {}}
                        />
                    </div>
                )}

                {selectedConversation ? (
                    <>
                        <div className="md:hidden">
                            <div className="h-16 border-b border-border bg-card px-4 flex items-center gap-3">
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    className="rounded-full"
                                    onClick={() =>
                                        setSelectedConversationId(null)
                                    }
                                >
                                    <ArrowLeft className="w-5 h-5" />
                                </Button>

                                <div className="flex items-center gap-3 flex-1">
                                    <div className="relative">
                                        <Avatar className="w-10 h-10">
                                            <AvatarImage
                                                src={
                                                    selectedConversation.avatar
                                                }
                                                alt={
                                                    selectedConversation.name
                                                }
                                            />

                                            <AvatarFallback>
                                                {selectedConversation.name.charAt(
                                                    0
                                                )}
                                            </AvatarFallback>
                                        </Avatar>

                                        {selectedConversation.isOnline && (
                                            <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-card rounded-full" />
                                        )}
                                    </div>

                                    <div>
                                        <h2 className="font-semibold text-foreground">
                                            {selectedConversation.name}
                                        </h2>

                                        <p className="text-sm text-muted-foreground">
                                            {selectedConversation.isOnline
                                                ? "Active now"
                                                : "Offline"
                                            }
                                        </p>
                                    </div>
                                </div>

                                <Button
                                    variant="ghost"
                                    size="icon"
                                    className="rounded-full"
                                    onClick={() =>
                                        setShowChatSettings(
                                            !showChatSettings
                                        )
                                    }
                                >
                                    <MoreVertical className="w-5 h-5" />
                                </Button>
                            </div>
                        </div>

                        <div className="hidden md:block">
                            <ChatHeader
                                conversation={selectedConversation}
                                onOpenSettings={() =>
                                    setShowChatSettings(!showChatSettings)
                                }
                            />
                        </div>

                        <div className="flex-1 flex overflow-hidden">
                            <div className="flex-1 flex flex-col">
                                <ChatMessages messages={messages} />

                                <ChatInput
                                    onSendMessage={handleSendMessage}
                                />
                            </div>

                            <ChatSettingsSidebar
                                conversation={selectedConversation}
                                isOpen={showChatSettings}
                                onClose={() =>
                                    setShowChatSettings(false)
                                }
                            />
                        </div>
                    </>
                ) : (
                    <div className="hidden md:flex flex-1">
                        <EmptyChatState />
                    </div>
                )}
            </div>
        </div>
    );
};

export default memo(ChatPage);