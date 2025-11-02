import { Eye, EyeOff } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState } from "react";

interface PasswordInputProps {
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    id: string;
    name: string;
    className: string,
    label: string;
    placeholder?: string;
    tabIndex: number;
}

export const PasswordInput = ({ value, onChange, id, name, className, label, placeholder, tabIndex }: PasswordInputProps) => {
    const [showPassword, setShowPassword] = useState(false);

    return (
        <>
            <Label htmlFor={id}>{label}</Label>
            <div className={className + " relative"}>
                <Input
                    id={id}
                    name={name}
                    type={showPassword ? "text" : "password"}
                    placeholder={placeholder}
                    value={value}
                    onChange={(e) => onChange(e)}
                    required
                    className="pr-10"
                    tabIndex={tabIndex}
                />
                <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                >
                    {showPassword ? (
                        <EyeOff className="w-5 h-5" />
                    ) : (
                        <Eye className="w-5 h-5" />
                    )}
                </button>
            </div>
        </>
    )
}