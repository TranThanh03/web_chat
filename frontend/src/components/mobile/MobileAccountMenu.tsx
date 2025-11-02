import { User, LogOut, ChevronRight } from "lucide-react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
    Sheet,
    SheetContent,
    SheetHeader,
    SheetTitle,
    SheetTrigger,
} from "@/components/ui/sheet";
import { toast } from "sonner";
import { memo } from "react";

interface MobileAccountMenuProps {
    isActive: boolean;
}

const MobileAccountMenu = ({ isActive }: MobileAccountMenuProps) => {
    const handleLogout = () => {
        toast.success('Logged out successfully!');
    };

    return (
        <Sheet>
            <SheetTrigger asChild>
                <Button
                    variant="ghost"
                    size="icon"
                    className={`flex flex-col gap-1 h-auto py-2 px-4 rounded-xl relative ${
                        isActive ? 'bg-primary/10 text-primary hover:bg-primary/20 hover:text-primary' : ''
                    }`}
                >
                    <User className="w-5 h-5" />
                    <span className="text-[10px] font-medium">Account</span>
                    {isActive && (
                        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-8 h-1 bg-primary rounded-b-full" />
                    )}
                </Button>
            </SheetTrigger>

            <SheetContent side="bottom" className="rounded-t-3xl">
                <SheetHeader>
                    <SheetTitle>Account</SheetTitle>
                </SheetHeader>
                <div className="mt-6 space-y-2">
                    <Link to="/account">
                        <Button
                            variant="ghost"
                            className="w-full justify-between h-14 text-base"
                        >
                            <div className="flex items-center gap-3">
                                <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center">
                                    <User className="w-5 h-5 text-primary" />
                                </div>
                                <span>Manage Account</span>
                            </div>
                            <ChevronRight className="w-5 h-5 text-muted-foreground" />
                        </Button>
                    </Link>
                    <Button
                        variant="ghost"
                        className="w-full justify-between h-14 text-base text-destructive hover:bg-destructive/10 hover:text-destructive"
                        onClick={handleLogout}
                    >
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-lg bg-destructive/10 flex items-center justify-center">
                                <LogOut className="w-5 h-5 text-destructive" />
                            </div>
                            <span>Logout</span>
                        </div>
                    </Button>
                </div>
            </SheetContent>
        </Sheet>
    );
};

export default memo(MobileAccountMenu);