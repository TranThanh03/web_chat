import { Button } from "@/components/ui/button";
import { useDialog } from "@/hooks/useDialog";
import { MessageCircle } from "lucide-react";
import { memo } from "react";

const Header = () => {
    const { openDialog } = useDialog();

    return (
        <header className="sticky top-0 z-50 bg-background/80 backdrop-blur-md border-b border-border">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
                <div className="flex items-center justify-between h-16 sm:h-20">
                    <div className="flex items-center gap-2 sm:gap-3">
                        <div className="bg-primary rounded-xl p-2 sm:p-2.5">
                            <MessageCircle className="w-5 h-5 sm:w-6 sm:h-6 text-primary-foreground" />
                        </div>
                        <h1 className="text-xl sm:text-2xl font-bold bg-linear-to-r from-primary to-primary/70 bg-clip-text text-transparent">
                            Chat
                        </h1>
                    </div>
                
                    <div className="flex items-center gap-2 sm:gap-4">
                        <Button 
                            variant="ghost" 
                            onClick={() => openDialog("login")}
                            className="text-sm sm:text-base"
                        >
                            Login
                        </Button>
                        <Button 
                            onClick={() => openDialog("signup")}
                            className="text-sm sm:text-base shadow-lg hover:shadow-xl transition-shadow"
                        >
                            Sign Up
                        </Button>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default memo(Header);