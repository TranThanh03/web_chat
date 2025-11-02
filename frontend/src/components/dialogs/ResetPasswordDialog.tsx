import { memo, useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { Eye, EyeOff } from "lucide-react";
import { useDialog } from "@/hooks/useDialog";
import { emailRegex } from "@/utils/emailRegex";
import { ERROR_MESSAGE } from "@/utils/errorMessage";

type ResetStep = "email" | "otp" | "password";

const ResetPasswordDialog = () => {
    const [currentStep, setCurrentStep] = useState<ResetStep>("email");
    const { dialog, closeDialog, openDialog } = useDialog();
    const [email, setEmail] = useState("");
    const [otp, setOtp] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [resendTimer, setResendTimer] = useState(0);
    const initMsgError = {
        email: "",
        otp: "",
        password: "" 
    };
    const [msgError, setMsgError] = useState(initMsgError);

    const handleEmailSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!emailRegex(email)) {
            setMsgError(prev => ({
                ...prev,
                email: ERROR_MESSAGE.EMAIL_INVALID
            }))

            return;
        }

        toast.success("OTP code has been sent to your email!");
        setCurrentStep("otp");
        startResendTimer();
    };

    const handleOtpSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        toast.success("OTP is valid!");
        setCurrentStep("password");
    };

    const handlePasswordSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        
        if (newPassword !== confirmPassword) {
            toast.error("Password confirmation does not match!");
            return;
        }

        if (newPassword.length < 8) {
            toast.error("Password must be at least 8 characters!");
            return;
        }

        toast.success("Password reset successfully!");
        handleClose();
        openDialog("login");
    };

    const handleResendOtp = () => {
        if (resendTimer > 0) return;
        toast.success("A new OTP has been sent!");
        startResendTimer();
    };

    const startResendTimer = () => {
        setResendTimer(60);
        const interval = setInterval(() => {
        setResendTimer((prev) => {
            if (prev <= 1) {
            clearInterval(interval);
            return 0;
            }
            return prev - 1;
        });
        }, 1000);
    };

    const handleBack = () => {
        if (currentStep === "email") {
            handleClose();
            openDialog("login");
        } else if (currentStep === "otp") {
            setCurrentStep("email");
            setOtp("");
        } else if (currentStep === "password") {
            setCurrentStep("otp");
            setNewPassword("");
            setConfirmPassword("");
        }
    };

    const handleClose = () => {
        setCurrentStep("email");
        setEmail("");
        setOtp("");
        setNewPassword("");
        setConfirmPassword("");
        setShowNewPassword(false);
        setShowConfirmPassword(false);
        setResendTimer(0);
        setMsgError(initMsgError);
        closeDialog();
    };

    const getStepTitle = () => {
        switch (currentStep) {
        case "email":
            return "Reset Password";
        case "otp":
            return "OTP Verification";
        case "password":
            return "Create New Password";
        }
    };

    const getStepDescription = () => {
        switch (currentStep) {
        case "email":
            return "Enter your email address to receive an OTP code";
        case "otp":
            return "Enter the OTP code sent to your email";
        case "password":
            return "Enter a new password for your account";
        }
    };

    return (
        <Dialog open={dialog === "resetPassword"} onOpenChange={closeDialog}>
            <DialogContent
                className="sm:max-w-106.25"
                onInteractOutside={(e) => e.preventDefault()}
                onEscapeKeyDown={(e) => e.preventDefault()}
            >
                <DialogHeader>
                    <DialogTitle className="text-2xl font-bold text-center">
                        {getStepTitle()}
                    </DialogTitle>
                    <DialogDescription className="text-center">
                        {getStepDescription()}
                    </DialogDescription>
                </DialogHeader>

                {currentStep === "email" && (
                    <form onSubmit={handleEmailSubmit} className="space-y-4 mt-4">
                        <div className="space-y-2">
                            <Label htmlFor="reset-email">Email Address</Label>
                            <Input
                                id="reset-email"
                                type="text"
                                className="mb-1"
                                placeholder="Enter your email address"
                                value={email}
                                onChange={(e) => {
                                    setEmail(e.target.value);
                                    setMsgError(prev => ({
                                        ...prev,
                                        email: ""
                                    }));
                                }}
                                required
                            />
                            <p className="text-xs text-red-500 font-medium h-4 leading-4">
                                {msgError.email || ""}
                            </p>
                        </div>

                        <Button type="submit" className="w-full" size="lg">
                            Send OTP
                        </Button>

                        <Button
                            type="button"
                            variant="ghost"
                            className="w-full"
                            onClick={handleBack}
                        >
                            Back to Login
                        </Button>
                    </form>
                )}

                {currentStep === "otp" && (
                <form onSubmit={handleOtpSubmit} className="space-y-4 mt-4">
                    <div className="space-y-2">
                        <Label htmlFor="otp">OTP Code</Label>
                        <Input
                            id="otp"
                            type="text"
                            placeholder="Enter OTP code (6 digits)"
                            value={otp}
                            onChange={(e) => setOtp(e.target.value)}
                            required
                            pattern="[0-9]{6}"
                        />
                        <p className="text-xs text-muted-foreground">
                            The OTP code has been sent to: <span className="font-medium">{email}</span>
                        </p>
                    </div>

                    <div className="flex text-sm">
                        <span className="text-muted-foreground me-1">
                            Didn't receive the code?
                        </span>
                        <Button
                            type="button"
                            variant="link"
                            className="p-0 h-auto font-medium"
                            onClick={handleResendOtp}
                            disabled={resendTimer > 0}
                        >
                            {resendTimer > 0 ? `Resend in ${resendTimer}s.` : "Resend Code."}
                        </Button>
                    </div>

                    <Button type="submit" className="w-full" size="lg">
                        Confirm
                    </Button>

                    <Button
                        type="button"
                        variant="ghost"
                        className="w-full"
                        onClick={handleBack}
                    >
                        Back
                    </Button>
                </form>
                )}

                {currentStep === "password" && (
                <form onSubmit={handlePasswordSubmit} className="space-y-4 mt-4">
                    <div className="space-y-2">
                        <Label htmlFor="new-password">New Password</Label>
                        <div className="relative">
                            <Input
                            id="new-password"
                            type={showNewPassword ? "text" : "password"}
                            placeholder="Enter new password (minimum 8 characters)"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                            minLength={8}
                            className="pr-10"
                            />
                            <button
                            type="button"
                            onClick={() => setShowNewPassword(!showNewPassword)}
                            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                            >
                            {showNewPassword ? (
                                <EyeOff className="w-4 h-4" />
                            ) : (
                                <Eye className="w-4 h-4" />
                            )}
                            </button>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="confirm-password">Confirm Password</Label>
                        <div className="relative">
                            <Input
                                id="confirm-password"
                                type={showConfirmPassword ? "text" : "password"}
                                placeholder="Re-enter new password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                                minLength={8}
                                className="pr-10"
                            />
                            <button
                                type="button"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                            >
                            {showConfirmPassword ? (
                                <EyeOff className="w-4 h-4" />
                            ) : (
                                <Eye className="w-4 h-4" />
                            )}
                            </button>
                        </div>
                    </div>

                    <Button type="submit" className="w-full" size="lg">
                        Reset Password
                    </Button>

                    <Button
                        type="button"
                        variant="ghost"
                        className="w-full"
                        onClick={handleBack}
                    >
                        Back
                    </Button>
                </form>
                )}
            </DialogContent>
        </Dialog>
    );
};

export default memo(ResetPasswordDialog);