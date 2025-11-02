import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { Switch } from "@/components/ui/switch";
import { X, Bell, BellOff, Image, Search, Trash2, Archive, UserMinus, Ban } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import type { Conversation } from "@/services/chatService";
import { memo, useState } from "react";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

interface ChatSettingsSidebarProps {
    conversation: Conversation | undefined;
    isOpen: boolean;
    onClose: () => void;
}

const ChatSettingsSidebar = ({ conversation, isOpen, onClose }: ChatSettingsSidebarProps) => {
    const [isMuted, setIsMuted] = useState(false);

    if (!isOpen) return null;

    return (
        <>
            <div 
                className={`${isOpen ? 'fixed' : 'hidden'} inset-0 bg-black/50 z-40 animate-in fade-in duration-200 lg:hidden`}
                onClick={onClose}
            />

            <div className={`${isOpen ? 'translate-x-0' : 'translate-x-full'} fixed lg:static top-0 right-0 lg:top-auto lg:right-auto h-screen lg:h-auto w-full md:w-96 lg:w-80 bg-card border-l border-border z-50 flex flex-col transition-transform duration-300 ease-in-out lg:transition-none`}>
                <div className="h-16 border-b border-border px-4 flex items-center justify-between shrink-0">
                    <h2 className="text-lg font-semibold text-foreground">Settings</h2>
                    <Button
                        variant="ghost"
                        size="icon"
                        className="rounded-full"
                        onClick={onClose}
                    >
                        <X className="w-5 h-5" />
                    </Button>
                </div>

                <ScrollArea className="flex-1">
                    <div className="p-4 space-y-6 pr-6">
                        <div className="flex flex-col items-center text-center">
                            <Avatar className="w-20 h-20 mb-3">
                                <AvatarImage src={conversation.avatar} alt={conversation.name} />
                                <AvatarFallback className="text-2xl">{conversation.name.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <h3 className="text-lg font-semibold text-foreground">{conversation.name}</h3>
                            <p className="text-sm text-muted-foreground">
                                {conversation.isOnline ? "Active now" : "Offline"}
                            </p>
                        </div>

                        <Separator />

                        <div className="space-y-4">
                            <h4 className="text-sm font-semibold text-foreground">Notifications</h4>
                            <div className="flex items-center justify-between">
                                <div className="flex items-center gap-3">
                                    {isMuted ? (
                                        <BellOff className="w-5 h-5 text-muted-foreground" />
                                    ) : (
                                        <Bell className="w-5 h-5 text-muted-foreground" />
                                    )}

                                    <div>
                                        <p className="text-sm font-medium text-foreground">Mute notifications</p>
                                        <p className="text-xs text-muted-foreground">Stop receiving alerts</p>
                                    </div>
                                </div>
                                <Switch checked={isMuted} onCheckedChange={setIsMuted} />
                            </div>
                        </div>

                        <Separator />

                        <div className="space-y-2">
                            <h4 className="text-sm font-semibold text-foreground mb-3">Actions</h4>
                        
                            <Button
                                variant="ghost"
                                className="w-full justify-start gap-3 h-12"
                            >
                                <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                                    <Search className="w-4 h-4 text-primary" />
                                </div>
                                <span className="text-sm">Search in conversation</span>
                            </Button>

                            <Button
                                variant="ghost"
                                className="w-full justify-start gap-3 h-12"
                            >
                                <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                                    <Image className="w-4 h-4 text-primary" />
                                </div>
                                <span className="text-sm">View media & files</span>
                            </Button>

                            <Button
                                variant="ghost"
                                className="w-full justify-start gap-3 h-12"
                            >
                                <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                                    <Archive className="w-4 h-4 text-primary" />
                                </div>
                                <span className="text-sm">Archive conversation</span>
                            </Button>
                        </div>

                        <Separator />

                        <div className="space-y-2 pb-6">
                            <h4 className="text-sm font-semibold text-destructive mb-3">Danger Zone</h4>
                        
                            <AlertDialog>
                                <AlertDialogTrigger asChild>
                                    <Button
                                        variant="ghost"
                                        className="w-full justify-start gap-3 h-12 text-destructive hover:bg-destructive/10 hover:text-destructive"
                                    >
                                        <div className="w-8 h-8 rounded-lg bg-destructive/10 flex items-center justify-center">
                                            <Trash2 className="w-4 h-4 text-destructive" />
                                        </div>
                                        <span className="text-sm">Delete conversation</span>
                                    </Button>
                                </AlertDialogTrigger>

                                <AlertDialogContent>
                                    <AlertDialogHeader>
                                        <AlertDialogTitle>Delete conversation</AlertDialogTitle>
                                        <AlertDialogDescription>
                                            Are you sure you want to delete this conversation? This action cannot be undone and all messages will be permanently deleted.
                                        </AlertDialogDescription>
                                    </AlertDialogHeader>

                                    <AlertDialogFooter>
                                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                                        <AlertDialogAction className="bg-destructive hover:bg-destructive/90">
                                            Delete
                                        </AlertDialogAction>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialog>

                            <AlertDialog>
                                <AlertDialogTrigger asChild>
                                    <Button
                                        variant="ghost"
                                        className="w-full justify-start gap-3 h-12 text-destructive hover:bg-destructive/10 hover:text-destructive"
                                    >
                                        <div className="w-8 h-8 rounded-lg bg-destructive/10 flex items-center justify-center">
                                            <UserMinus className="w-4 h-4 text-destructive" />
                                        </div>
                                        <span className="text-sm">Remove friend</span>
                                    </Button>
                                </AlertDialogTrigger>
                                <AlertDialogContent>
                                    <AlertDialogHeader>
                                        <AlertDialogTitle>Remove friend</AlertDialogTitle>
                                        <AlertDialogDescription>
                                            Are you sure you want to remove {conversation.name} from your friends? You can always send a friend request again later.
                                        </AlertDialogDescription>
                                    </AlertDialogHeader>

                                    <AlertDialogFooter>
                                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                                        <AlertDialogAction className="bg-destructive hover:bg-destructive/90">
                                            Remove
                                        </AlertDialogAction>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialog>

                            <AlertDialog>
                                <AlertDialogTrigger asChild>
                                    <Button
                                        variant="ghost"
                                        className="w-full justify-start gap-3 h-12 text-destructive hover:bg-destructive/10 hover:text-destructive"
                                    >
                                        <div className="w-8 h-8 rounded-lg bg-destructive/10 flex items-center justify-center">
                                            <Ban className="w-4 h-4 text-destructive" />
                                        </div>
                                        <span className="text-sm">Block user</span>
                                    </Button>
                                </AlertDialogTrigger>

                                <AlertDialogContent>
                                    <AlertDialogHeader>
                                        <AlertDialogTitle>Block user</AlertDialogTitle>
                                        <AlertDialogDescription>
                                            Are you sure you want to block {conversation.name}? They won't be able to message you or see your profile.
                                        </AlertDialogDescription>
                                    </AlertDialogHeader>

                                    <AlertDialogFooter>
                                        <AlertDialogCancel>Hủy</AlertDialogCancel>
                                        <AlertDialogAction className="bg-destructive hover:bg-destructive/90">
                                            Block
                                        </AlertDialogAction>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialog>
                        </div>
                    </div>
                </ScrollArea>
            </div>
        </>
    );
};

export default memo(ChatSettingsSidebar);