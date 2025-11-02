import { memo } from "react";

const AuthBackground = () => {
    return (
        <>
            <div className="absolute inset-0 bg-linear-to-br from-primary/8 via-background to-accent/10" />
            <div className="absolute top-1/4 -left-24 w-72 h-72 rounded-full bg-primary/15 blur-3xl animate-pulse" />
            <div
                className="absolute bottom-1/4 -right-24 w-96 h-96 rounded-full bg-primary/10 blur-3xl"
                style={{ animation: "pulse 3s ease-in-out infinite 1s" }}
            />
            <div
                className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-125 h-125 rounded-full bg-accent/20 blur-3xl"
                style={{ animation: "pulse 4s ease-in-out infinite 0.5s" }}
            />
            <div
                className="absolute inset-0 opacity-[0.03]"
                style={{
                    backgroundImage: "radial-gradient(circle, currentColor 1px, transparent 1px)",
                    backgroundSize: "28px 28px",
                }}
            />
        </>
    );
};

export default memo(AuthBackground);