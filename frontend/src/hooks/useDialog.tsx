import { useContext } from "react";
import { DialogContext } from "@/providers/DialogProvider";
import type { DialogContextType } from "@/providers/DialogProvider";

export const useDialog = (): DialogContextType => {
    const context = useContext(DialogContext);

    if (!context) {
        throw new Error("useDialog must be used within DialogProvider");
    }

    return context;
}