import { memo, useEffect, useRef, useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { toast } from "sonner";
import { useDialog } from "@/hooks/useDialog";
import { Eye, EyeOff } from "lucide-react";
import AuthService from "@/services/AuthService";
import { useAuth } from "@/hooks/useAuth";
import { ERROR_MESSAGE } from "@/utils/errorMessage";
import RecaptchaCb from "../recaptcha/RecaptchaCb";
import ReCAPTCHA from "react-google-recaptcha";
import { useSocialLogin } from "@/hooks/useSocialLogin";

const LoginDialog = () => {
    const { login } = useAuth();
    const { dialog, closeDialog, openDialog } = useDialog();
    const initFormData = {
        username: "",
        password: "",
        recaptcha: ""
    };
    const [formData, setFormData] = useState(initFormData);
    const [captchaToken, setCaptchaToken] = useState<string | null>(null);
    const [showPassword, setShowPassword] = useState(false);
    const [msgError, setMsgError] = useState<string | null>(null);
    const [disabled, setDisabled] = useState(true);
    const recaptchaRef = useRef<ReCAPTCHA>(null);

    useEffect(() => {
        if (formData.username && formData.password && captchaToken) {
            setDisabled(false);
        } else {
            setDisabled(true);
        }
    }, [formData, captchaToken])

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name] : value
        }));
        setMsgError("");
    };

    const handleCloseDialog = () => {
        setFormData(initFormData);
        closeDialog();
    }

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setMsgError(null);

        try {
            const response = await AuthService.login({
                ...formData,
                recaptcha: captchaToken
            });

            if (response?.code === 0) {
                login(response?.result?.token);
                setFormData(initFormData);
            } else {
                setMsgError(response?.message || ERROR_MESSAGE.LOGIN_FAILED);
            }
        } catch (error: any) {
            setMsgError(error?.data?.message || ERROR_MESSAGE.UNKNOWN_ERROR);
        } finally {
            recaptchaRef?.current?.reset();
            setCaptchaToken(null);
        }
    };

    const handleSocialLogin = () => {
        useSocialLogin();
    };

    return (
        <Dialog open={dialog === "login"} onOpenChange={handleCloseDialog}>
            <DialogContent
                className="sm:max-w-106.25"
                onInteractOutside={(e) => e.preventDefault()}
                onEscapeKeyDown={(e) => e.preventDefault()}
            >
                <DialogHeader>
                    <DialogTitle className="text-2xl font-bold text-center">Login</DialogTitle>
                    <DialogDescription className="text-center">
                        Login to start chatting with your friends
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleLogin} className="space-y-4 mt-4">
                    <div className="space-y-2">
                        <Label htmlFor="username">Username</Label>
                        <Input
                            id="username"
                            name="username"
                            type="email"
                            placeholder="Enter your email"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            tabIndex={1}
                        />
                    </div>
                    
                    <div className="space-y-2">
                        <div className="flex items-center justify-between">
                            <Label htmlFor="password">Password</Label>
                            <button
                                type="button"
                                onClick={() => openDialog("resetPassword")}
                                className="text-xs text-primary hover:underline"
                            >
                                Forgot password?
                            </button>
                        </div>
                        <div className="relative">
                            <Input
                                id="password"
                                name="password"
                                type={showPassword ? "text" : "password"}
                                placeholder="Enter your password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                className="pr-10"
                                tabIndex={2}
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

                        <p className="text-xs text-red-500 font-medium h-4 leading-4">
                            {msgError || ""}
                        </p>
                    </div>

                    <RecaptchaCb ref={recaptchaRef} onChange={setCaptchaToken} tabIndex={3}/>

                    <Button type="submit" className="w-full hover:bg-blue-600 transition" size="lg" tabIndex={4} disabled={disabled}>
                        Login
                    </Button>
                </form>

                <div className="relative my-4">
                    <Separator />
                    <span className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 bg-background px-2 text-xs text-muted-foreground">
                        Or continue with
                    </span>
                </div>

                <div className="grid">
                    <Button
                        type="button"
                        variant="outline"
                        size="lg"
                        onClick={handleSocialLogin}
                        tabIndex={5}
                    >
                        <svg className="w-5 h-5 mr-2" viewBox="0 0 24 24">
                        <path
                            fill="currentColor"
                            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                        />
                        <path
                            fill="currentColor"
                            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                        />
                        <path
                            fill="currentColor"
                            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                        />
                        <path
                            fill="currentColor"
                            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                        />
                        </svg>
                        Google
                    </Button>
                </div>

                <p className="text-center text-sm text-muted-foreground mt-4">
                    Don't have an account?{" "}
                    <button
                        type="button"
                        onClick={() => openDialog("signup")}
                        className="text-primary hover:underline font-medium"
                    >
                        Sign up
                    </button>
                </p>
            </DialogContent>
        </Dialog>
    );
};

export default memo(LoginDialog);
