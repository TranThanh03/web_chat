import { X, Check, Loader2 } from "lucide-react";
import { memo } from "react";

interface Step {
    id: number;
    label: string;
    duration: number;
}

interface AuthStepListProps {
    steps: Step[];
    completedSteps: number[];
    status: "loading" | "success" | "error";
}

const AuthStepList = ({ steps, completedSteps, status }: AuthStepListProps) => {
    return (
        <div className="space-y-3">
            {steps.map((step) => {
                const isCompleted = completedSteps.includes(step.id);
                const isActive = step.id === 1;
                const isError = status === "error" && step.id === 1;

                return (
                    <div
                        key={step.id}
                        className={`flex items-center gap-3 px-4 py-3 rounded-xl border transition-all duration-500 ${
                            isCompleted ? (
                                isError ? (
                                    "bg-red-500/8 border-red-500/25 opacity-100"
                                ) : (
                                    status === "success"
                                    ? "bg-green-500/8 border-green-500/25 opacity-100" 
                                    : "bg-muted/40 border-border opacity-50"
                                )
                            ) : (
                                isActive
                                ? "bg-primary/8 border-primary/30 shadow-sm"
                                : "bg-muted/40 border-border opacity-50"
                            )
                        }`}
                    >
                        <div
                            className={`shrink-0 w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-all duration-300 ${
                                isCompleted ? (
                                    isError ? (
                                        "bg-red-500 text-white"
                                    ) : (
                                        status === "success"
                                        ? "bg-green-500 text-white"
                                        : "bg-muted-foreground/20 text-muted-foreground"
                                    )
                                ) : (
                                    isActive 
                                    ? "bg-primary text-primary-foreground"
                                    : "bg-muted-foreground/20 text-muted-foreground"
                                )
                            }`}
                        >
                            {isCompleted ? (
                                isError ? (
                                    <X className="w-4 h-4" strokeWidth={3} />
                                ) : (
                                    status === "success"
                                    ? <Check className="w-4 h-4" strokeWidth={3} />
                                    : <span>{step.id}</span>
                                )
                            ) : (
                                isActive
                                ? <Loader2 className="w-4 h-4 animate-spin" />
                                : <span>{step.id}</span>
                            )}
                        </div>

                        <span
                            className={`text-sm font-medium transition-colors duration-300 ${
                                isCompleted ? (
                                    isError ? (
                                        "text-red-600 dark:text-red-400"
                                    ) : (
                                        status === "success"
                                        ? "text-green-600 dark:text-green-400"
                                        : "text-muted-foreground"
                                    )
                                ) : (
                                    isActive
                                    ? "text-foreground"
                                    : "text-muted-foreground"
                                )
                            }`}
                        >
                            {step.label}
                        </span>
                    </div>
                );
            })}
        </div>
    );
};

export default memo(AuthStepList);