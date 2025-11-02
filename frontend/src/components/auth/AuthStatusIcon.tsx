import { CheckCircle2, XCircle } from "lucide-react";
import { memo } from "react";

interface AuthStatusIconProps {
    status: "loading" | "success" | "error";
}

const AuthStatusIcon = ({ status }: AuthStatusIconProps) => {
    if (status === "success") {
        return (
            <div className="flex justify-center">
                <div className="relative flex items-center justify-center w-16 h-16">
                    <div className="absolute inset-0 rounded-full bg-green-500/20 animate-ping" />
                    <CheckCircle2 className="w-12 h-12 text-green-500 relative z-10" strokeWidth={1.5} />
                </div>
            </div>
        )
    }

    if (status === "error") {
        return (
            <div className="flex justify-center">
                <XCircle className="w-12 h-12 text-destructive" strokeWidth={1.5} />
            </div>
        )
    }

    return (
        <div className="flex justify-center">
            <div className="relative w-16 h-16 flex items-center justify-center">
                <div className="absolute inset-0 rounded-full border-4 border-border" />
                <div
                    className="absolute inset-0 rounded-full border-4 border-transparent border-t-primary border-r-primary/50"
                    style={{ animation: "spin 1s linear infinite" }}
                />
                <div className="w-4 h-4 rounded-full bg-primary/70 animate-pulse" />
            </div>
        </div>
    );
};

export default memo(AuthStatusIcon);