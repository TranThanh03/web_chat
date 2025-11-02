import { createContext, useState } from "react";
import type { ReactNode } from 'react';

type DialogType = "login" | "signup" | "resetPassword" | null;

export interface DialogContextType {
    dialog: DialogType;
    openDialog: (type: DialogType) => void;
    closeDialog: () => void;
}

export const DialogContext = createContext<DialogContextType | null>(null);

interface Props {
    children: ReactNode;
}

export const DialogProvider = ({ children }: Props) => {
    const [dialog, setDialog] = useState<DialogType>(null)
    const openDialog = (type: DialogType) => setDialog(type)
    const closeDialog = () => setDialog(null)

    return (
        <DialogContext.Provider value={{ dialog, openDialog, closeDialog }}>
            {children}
        </DialogContext.Provider>
    )
}
